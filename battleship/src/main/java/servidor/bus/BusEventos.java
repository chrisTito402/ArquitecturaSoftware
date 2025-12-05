package servidor.bus;

import compartido.comunicacion.socket.UserServerThread;
import com.google.gson.Gson;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import compartido.comunicacion.Mensaje;
import compartido.comunicacion.TipoAccion;
import servidor.negocio.GestorPartida;
import servidor.negocio.IPublicadorEventos;

/**
 * El famoso Bus de Eventos! Es como un "cartero" que reparte mensajes.
 * Los clientes se suscriben a eventos que les interesan y cuando alguien
 * publica algo, el bus se lo manda a todos los que estan suscritos.
 *
 * Puede hacer tres cosas:
 * - PUBLICAR: manda a todos los suscritos de ese evento
 * - SUSCRIBIR: apunta a un cliente para que reciba cierto evento
 * - UNICAST: manda a un cliente especifico nomas
 *
 * Importante: esta clase SOLO manda mensajes, no tiene logica del juego.
 * Eso va en GestorPartida. Asi respetamos el SRP.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public class BusEventos implements IPublicadorEventos {

    // evento -> clientes suscritos
    private final ConcurrentHashMap<String, Set<UserServerThread>> eventos;

    // id -> cliente
    private final ConcurrentHashMap<String, UserServerThread> clientesPorId;

    // cliente -> id (para desconexiones)
    private final ConcurrentHashMap<UserServerThread, String> idPorCliente;

    // logica del juego
    private final GestorPartida gestorPartida;

    // para convertir a JSON
    private final Gson gson;

    /**
     * Constructor principal.
     */
    public BusEventos() {
        this.eventos = new ConcurrentHashMap<>();
        this.clientesPorId = new ConcurrentHashMap<>();
        this.idPorCliente = new ConcurrentHashMap<>();
        this.gson = new Gson();
        // Inyeccion de dependencia: el GestorPartida recibe este bus como publicador
        this.gestorPartida = new GestorPartida(this);
    }

    /**
     * Constructor alternativo (compatibilidad).
     */
    public BusEventos(java.util.Map mapa) {
        this();
    }

    // =========================================================================
    // ROUTING - Publish-Subscribe
    // =========================================================================

    /**
     * Manda mensaje a todos los suscritos de un evento.
     */
    @Override
    public void publicar(String evento, Mensaje mensaje) {
        System.out.println("[BUS] PUBLICAR evento: " + evento);
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
     * Registra un cliente para recibir un evento.
     */
    private void suscribirse(String evento, UserServerThread suscriptor) {
        System.out.println("[BUS] SUSCRIBIR cliente a evento: " + evento);
        eventos.computeIfAbsent(evento, k -> new CopyOnWriteArraySet<>()).add(suscriptor);
        System.out.println("[BUS] Total suscriptores en " + evento + ": " + eventos.get(evento).size());
    }

    /**
     * Quita un cliente de un evento.
     */
    private void desuscribirse(String evento, UserServerThread suscriptor) {
        System.out.println("[BUS] DESUSCRIBIR cliente de evento: " + evento);
        Set<UserServerThread> suscriptores = eventos.get(evento);
        if (suscriptores != null) {
            suscriptores.remove(suscriptor);
        }
    }

    /**
     * Manda mensaje a un solo cliente (unicast).
     */
    @Override
    public void enviarUnicast(String idDestino, Mensaje mensaje) {
        System.out.println("[BUS] SEND_UNICAST a cliente: " + idDestino);
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

    // =========================================================================
    // PUNTO DE ENTRADA - aqui llegan los mensajes
    // =========================================================================

    /**
     * Recibe mensaje del cliente y lo procesa segun el tipo de accion.
     */
    public void manejarEvento(String json, UserServerThread cliente) {
        if (json == null || json.isEmpty()) {
            System.out.println("[BUS] ERROR: JSON vacio o nulo");
            return;
        }

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
                procesarPublicacion(mensaje);
                break;

            case SEND_UNICAST:
                procesarUnicast(mensaje);
                break;

            default:
                System.out.println("[BUS] ERROR: Accion desconocida: " + mensaje.getAccion());
        }
    }

    /**
     * Procesa publicacion, delega al GestorPartida.
     */
    private void procesarPublicacion(Mensaje mensaje) {
        String evento = mensaje.getEvento();

        // Delegar logica de negocio al GestorPartida
        boolean debePublicar = gestorPartida.procesarEvento(evento, mensaje);

        // Si el gestor indica que debe publicarse, hacer broadcast
        if (debePublicar) {
            publicar(evento, mensaje);
        }
    }

    /**
     * Procesa unicast.
     */
    private void procesarUnicast(Mensaje mensaje) {
        String idDestino = mensaje.getSubEvento();
        if (idDestino != null && !idDestino.isEmpty()) {
            enviarUnicast(idDestino, mensaje);
        } else {
            System.out.println("[BUS] ERROR: SEND_UNICAST sin ID destino");
        }
    }

    // =========================================================================
    // GESTION DE CLIENTES
    // =========================================================================

    /**
     * Quita un cliente de todos los eventos (desconexion).
     */
    public void removeSuscriptor(UserServerThread user) {
        if (user != null) {
            // Obtener el ID del cliente antes de removerlo
            String idCliente = idPorCliente.get(user);

            // Remover de todas las estructuras
            eventos.values().forEach(suscriptores -> suscriptores.remove(user));
            clientesPorId.values().removeIf(u -> u.equals(user));
            idPorCliente.remove(user);

            System.out.println("[BUS] Suscriptor removido de todos los eventos (ID: " + idCliente + ")");

            // Notificar al GestorPartida para que limpie la partida fantasma
            if (idCliente != null) {
                gestorPartida.manejarDesconexionCliente(idCliente);
            }
        }
    }

    /**
     * Registra un cliente nuevo.
     */
    public void addNewClient(String event, UserServerThread client) {
        System.out.println("[BUS] Registrando nuevo cliente con evento: " + event);
        eventos.computeIfAbsent(event, k -> new CopyOnWriteArraySet<>()).add(client);

        // Extraer ID del evento (formato: MENSAJE_CLIENTE_X)
        if (event.startsWith("MENSAJE_CLIENTE_")) {
            String id = event.substring("MENSAJE_CLIENTE_".length());
            clientesPorId.put(id, client);
            idPorCliente.put(client, id);
            System.out.println("[BUS] Cliente registrado con ID: " + id);
        }
    }

    // =========================================================================
    // UTILIDADES
    // =========================================================================

    /**
     * Cuenta suscriptores de un evento.
     */
    public int contarSuscriptores(String evento) {
        Set<UserServerThread> suscriptores = eventos.get(evento);
        return suscriptores != null ? suscriptores.size() : 0;
    }

    /**
     * Checa si hay suscriptores.
     */
    public boolean tieneSubscriptores(String evento) {
        return contarSuscriptores(evento) > 0;
    }

    /**
     * Lista eventos (debug).
     */
    public void listarEventos() {
        System.out.println("[BUS] === Eventos registrados ===");
        eventos.forEach((evento, suscriptores) -> {
            System.out.println("  - " + evento + ": " + suscriptores.size() + " suscriptores");
        });
        System.out.println("[BUS] ============================");
    }

    /**
     * Reinicia la partida.
     */
    public void resetPartida() {
        gestorPartida.resetPartida();
    }

    /**
     * Obtiene el gestor de partida.
     */
    public GestorPartida getGestorPartida() {
        return gestorPartida;
    }
}
