package servidor.bus;

import compartido.comunicacion.socket.UserServerThread;
import com.google.gson.Gson;
import compartido.comunicacion.dto.JugadorDTO;
import compartido.comunicacion.dto.TurnoDTO;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import compartido.comunicacion.Mensaje;
import compartido.comunicacion.TipoAccion;

/**
 * Bus de Eventos central para la comunicacion entre clientes.
 * Implementa el patron Publish-Subscribe con soporte para:
 * - PUBLICAR: Broadcast a todos los suscriptores de un evento
 * - SUSCRIBIR: Registrarse para recibir un tipo de evento
 * - SEND_UNICAST: Enviar mensaje a un cliente especifico
 *
 * Thread-safe mediante ConcurrentHashMap y CopyOnWriteArraySet.
 *
 * @author daniel
 */
public class BusEventos {

    private final ConcurrentHashMap<String, Set<UserServerThread>> eventos;
    private final ConcurrentHashMap<String, UserServerThread> clientesPorId;

    // Control de tableros confirmados (lógica del servidor)
    private final Set<String> tablerosConfirmados = new HashSet<>();
    private final List<JugadorDTO> jugadoresEnPartida = new ArrayList<>();
    private final Object lockTableros = new Object();

    public BusEventos() {
        this.eventos = new ConcurrentHashMap<>();
        this.clientesPorId = new ConcurrentHashMap<>();
    }

    /**
     * Constructor para compatibilidad (ignora el mapa pasado, usa ConcurrentHashMap).
     */
    public BusEventos(java.util.Map mapa) {
        this.eventos = new ConcurrentHashMap<>();
        this.clientesPorId = new ConcurrentHashMap<>();
    }

    /**
     * Publica un mensaje a todos los suscriptores de un evento.
     */
    private void publicar(String evento, Mensaje mensaje) {
        System.out.println("[BUS] PUBLICAR evento: " + evento);
        Gson gson = new Gson();
        String jsonMensaje = gson.toJson(mensaje);

        Set<UserServerThread> suscriptores = eventos.get(evento);
        if (suscriptores != null && !suscriptores.isEmpty()) {
            suscriptores.forEach(s -> {
                if (s != null) {
                    s.sendMessage(jsonMensaje);
                }
            });
            System.out.println("[BUS] Mensaje enviado a " + suscriptores.size() + " suscriptores");
        } else {
            System.out.println("[BUS] No hay suscriptores para el evento: " + evento);
        }
    }

    /**
     * Suscribe un cliente a un evento especifico.
     */
    private void suscribirse(String evento, UserServerThread suscriptor) {
        System.out.println("[BUS] SUSCRIBIR cliente a evento: " + evento);
        eventos.computeIfAbsent(evento, k -> new CopyOnWriteArraySet<>()).add(suscriptor);
        System.out.println("[BUS] Total suscriptores en " + evento + ": " + eventos.get(evento).size());
    }

    /**
     * Desuscribe un cliente de un evento especifico.
     */
    private void desuscribirse(String evento, UserServerThread suscriptor) {
        System.out.println("[BUS] DESUSCRIBIR cliente de evento: " + evento);
        Set<UserServerThread> suscriptores = eventos.get(evento);
        if (suscriptores != null) {
            suscriptores.remove(suscriptor);
        }
    }

    /**
     * Envia un mensaje unicast a un cliente especifico por su ID.
     */
    private void enviarUnicast(String idDestino, Mensaje mensaje) {
        System.out.println("[BUS] SEND_UNICAST a cliente: " + idDestino);
        Gson gson = new Gson();
        String jsonMensaje = gson.toJson(mensaje);

        UserServerThread destinatario = clientesPorId.get(idDestino);
        if (destinatario != null) {
            destinatario.sendMessage(jsonMensaje);
            System.out.println("[BUS] Mensaje unicast enviado a: " + idDestino);
        } else {
            // Intentar buscar en el evento MENSAJE_CLIENTE_X
            String eventoPrivado = "MENSAJE_CLIENTE_" + idDestino;
            Set<UserServerThread> suscriptores = eventos.get(eventoPrivado);
            if (suscriptores != null && !suscriptores.isEmpty()) {
                suscriptores.forEach(s -> s.sendMessage(jsonMensaje));
                System.out.println("[BUS] Mensaje unicast enviado via evento privado: " + eventoPrivado);
            } else {
                System.out.println("[BUS] ERROR: Cliente destino no encontrado: " + idDestino);
            }
        }
    }

    /**
     * Punto de entrada principal para manejar eventos del cliente.
     */
    public void manejarEvento(String json, UserServerThread cliente) {
        if (json == null || json.isEmpty()) {
            System.out.println("[BUS] ERROR: JSON vacio o nulo");
            return;
        }

        Gson gson = new Gson();
        Mensaje mensaje;
        try {
            mensaje = gson.fromJson(json, Mensaje.class);
        } catch (Exception e) {
            System.out.println("[BUS] ERROR: JSON invalido: " + e.getMessage());
            return;
        }

        String idCliente = mensaje.getIdPublicador();
        System.out.println("[BUS] Evento recibido - Accion: " + mensaje.getAccion() +
                          ", Evento: " + mensaje.getEvento() +
                          ", Cliente: " + idCliente);

        switch (mensaje.getAccion()) {
            case SUSCRIBIR:
                suscribirse(mensaje.getEvento(), cliente);
                break;
            case PUBLICAR:
                // Ejecutar lógica del servidor antes de publicar
                manejarLogicaServidor(mensaje.getEvento(), mensaje);
                publicar(mensaje.getEvento(), mensaje);
                break;
            case SEND_UNICAST:
                String idDestino = mensaje.getSubEvento();
                if (idDestino != null && !idDestino.isEmpty()) {
                    enviarUnicast(idDestino, mensaje);
                } else {
                    System.out.println("[BUS] ERROR: SEND_UNICAST sin ID destino");
                }
                break;
            default:
                System.out.println("[BUS] ERROR: Accion desconocida: " + mensaje.getAccion());
        }
    }

    /**
     * Remueve un suscriptor de todos los eventos (cuando se desconecta).
     */
    public void removeSuscriptor(UserServerThread user) {
        if (user != null) {
            eventos.values().forEach(suscriptores -> suscriptores.remove(user));
            // Remover del mapa de clientes por ID
            clientesPorId.values().removeIf(u -> u.equals(user));
            System.out.println("[BUS] Suscriptor removido de todos los eventos");
        }
    }

    /**
     * Registra un nuevo cliente con su evento privado y lo guarda por ID.
     */
    public void addNewClient(String event, UserServerThread client) {
        System.out.println("[BUS] Registrando nuevo cliente con evento: " + event);
        eventos.computeIfAbsent(event, k -> new CopyOnWriteArraySet<>()).add(client);

        // Extraer ID del evento (formato: MENSAJE_CLIENTE_X)
        if (event.startsWith("MENSAJE_CLIENTE_")) {
            String id = event.substring("MENSAJE_CLIENTE_".length());
            clientesPorId.put(id, client);
            System.out.println("[BUS] Cliente registrado con ID: " + id);
        }
    }

    /**
     * Obtiene la cantidad de suscriptores de un evento.
     */
    public int contarSuscriptores(String evento) {
        Set<UserServerThread> suscriptores = eventos.get(evento);
        return suscriptores != null ? suscriptores.size() : 0;
    }

    /**
     * Verifica si un evento tiene suscriptores.
     */
    public boolean tieneSubscriptores(String evento) {
        return contarSuscriptores(evento) > 0;
    }

    /**
     * Lista todos los eventos registrados (para debug).
     */
    public void listarEventos() {
        System.out.println("[BUS] === Eventos registrados ===");
        eventos.forEach((evento, suscriptores) -> {
            System.out.println("  - " + evento + ": " + suscriptores.size() + " suscriptores");
        });
        System.out.println("[BUS] ============================");
    }

    // =========================================================================
    // LOGICA DEL SERVIDOR
    // =========================================================================

    /**
     * Maneja la lógica del servidor para eventos específicos.
     * Se ejecuta ANTES de publicar el evento a los clientes.
     */
    private void manejarLogicaServidor(String evento, Mensaje mensaje) {
        Gson gson = new Gson();

        switch (evento) {
            case "JUGADOR_UNIDO":
            case "UNIRSE_PARTIDA":
                // Registrar jugador para el turno inicial
                JugadorDTO jugador = gson.fromJson(mensaje.getData(), JugadorDTO.class);
                if (jugador != null) {
                    synchronized (lockTableros) {
                        // Evitar duplicados
                        boolean existe = jugadoresEnPartida.stream()
                                .anyMatch(j -> j.getNombre().equals(jugador.getNombre()));
                        if (!existe) {
                            jugadoresEnPartida.add(jugador);
                            System.out.println("[BUS-SERVER] Jugador registrado: " + jugador.getNombre() +
                                              " (Total: " + jugadoresEnPartida.size() + ")");
                        }
                    }
                }
                break;

            case "CONFIRMAR_TABLERO":
                manejarConfirmarTablero(mensaje);
                break;

            case "ABANDONAR_LOBBY":
            case "JUGADOR_ABANDONO":
                // Limpiar jugador que abandona
                JugadorDTO jugadorAbandono = gson.fromJson(mensaje.getData(), JugadorDTO.class);
                if (jugadorAbandono != null) {
                    synchronized (lockTableros) {
                        tablerosConfirmados.remove(jugadorAbandono.getNombre());
                        jugadoresEnPartida.removeIf(j -> j.getNombre().equals(jugadorAbandono.getNombre()));
                        System.out.println("[BUS-SERVER] Jugador removido: " + jugadorAbandono.getNombre());
                    }
                }
                break;

            default:
                // Otros eventos no requieren lógica del servidor
                break;
        }
    }

    /**
     * Maneja la confirmación del tablero de un jugador.
     * Cuando ambos jugadores confirman, envía TABLEROS_LISTOS y TURNO_INICIAL.
     */
    private void manejarConfirmarTablero(Mensaje mensaje) {
        Gson gson = new Gson();
        JugadorDTO jugadorDTO = gson.fromJson(mensaje.getData(), JugadorDTO.class);

        System.out.println("[BUS-SERVER] Tablero confirmado por: " + jugadorDTO.getNombre());

        boolean ambosListos = false;
        TurnoDTO turnoInicial = null;

        synchronized (lockTableros) {
            // Registrar el jugador con su DTO completo
            boolean existeJugador = jugadoresEnPartida.stream()
                    .anyMatch(j -> j.getNombre().equals(jugadorDTO.getNombre()));
            if (!existeJugador) {
                jugadoresEnPartida.add(jugadorDTO);
                System.out.println("[BUS-SERVER] Jugador registrado: " + jugadorDTO.getNombre());
            }

            // Agregar a tableros confirmados
            tablerosConfirmados.add(jugadorDTO.getNombre());

            System.out.println("[BUS-SERVER] Tableros confirmados: " + tablerosConfirmados.size() + "/2 -> " + tablerosConfirmados);
            System.out.println("[BUS-SERVER] Jugadores registrados: " + jugadoresEnPartida.size());

            // Verificar si ambos jugadores han confirmado (solo necesitamos 2 tableros)
            if (tablerosConfirmados.size() >= 2) {
                System.out.println("[BUS-SERVER] *** AMBOS TABLEROS LISTOS - INICIANDO PARTIDA ***");
                ambosListos = true;

                // Seleccionar jugador inicial aleatoriamente
                List<String> nombres = new ArrayList<>(tablerosConfirmados);
                Random random = new Random();
                String nombreInicial = nombres.get(random.nextInt(nombres.size()));

                // Crear DTO del turno inicial
                turnoInicial = new TurnoDTO(
                        nombreInicial,
                        nombreInicial,
                        30, // segundos por turno
                        1   // numero de turno
                );

                System.out.println("[BUS-SERVER] Turno inicial asignado a: " + nombreInicial);

                // Limpiar para proxima partida
                tablerosConfirmados.clear();
                jugadoresEnPartida.clear();
            }
        }

        // Publicar FUERA del synchronized para evitar deadlocks
        if (ambosListos && turnoInicial != null) {
            // Notificar que los tableros estan listos (enviar TRUE para que no sea null)
            Mensaje msgTablerosListos = new Mensaje(
                    TipoAccion.PUBLICAR,
                    "TABLEROS_LISTOS",
                    gson.toJsonTree(Boolean.TRUE),
                    "SERVER"
            );
            publicar("TABLEROS_LISTOS", msgTablerosListos);

            // Enviar el turno inicial
            Mensaje msgTurnoInicial = new Mensaje(
                    TipoAccion.PUBLICAR,
                    "TURNO_INICIAL",
                    gson.toJsonTree(turnoInicial),
                    "SERVER"
            );
            publicar("TURNO_INICIAL", msgTurnoInicial);
        }
    }

    /**
     * Reinicia el estado del servidor para una nueva partida.
     */
    public void resetPartida() {
        synchronized (lockTableros) {
            tablerosConfirmados.clear();
            jugadoresEnPartida.clear();
            System.out.println("[BUS-SERVER] Estado de partida reiniciado");
        }
    }
}
