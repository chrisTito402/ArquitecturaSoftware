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
 * Bus de Eventos central para la comunicacion entre clientes.
 *
 * Implementa el patron Publish-Subscribe con soporte para:
 * - PUBLICAR: Broadcast a todos los suscriptores de un evento
 * - SUSCRIBIR: Registrarse para recibir un tipo de evento
 * - SEND_UNICAST: Enviar mensaje a un cliente especifico
 *
 * PRINCIPIO DE RESPONSABILIDAD UNICA (SRP):
 * Esta clase SOLO maneja el routing de mensajes.
 * La logica de negocio se delega a GestorPartida.
 *
 * Thread-safe mediante ConcurrentHashMap y CopyOnWriteArraySet.
 *
 * @author Equipo
 */
public class BusEventos implements IPublicadorEventos {

    // Mapa de eventos a suscriptores
    private final ConcurrentHashMap<String, Set<UserServerThread>> eventos;

    // Mapa de ID de cliente a su thread
    private final ConcurrentHashMap<String, UserServerThread> clientesPorId;

    // Mapa inverso: thread a ID de cliente (para detectar desconexiones)
    private final ConcurrentHashMap<UserServerThread, String> idPorCliente;

    // Gestor de logica de negocio (Capa de Negocio)
    private final GestorPartida gestorPartida;

    // Serializador JSON
    private final Gson gson;

    /**
     * Constructor del BusEventos.
     * Inicializa las estructuras de datos y el gestor de partida.
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
     * Constructor para compatibilidad (ignora el mapa pasado).
     *
     * @param mapa Parametro ignorado, mantenido por compatibilidad
     */
    public BusEventos(java.util.Map mapa) {
        this();
    }

    // =========================================================================
    // OPERACIONES DE ROUTING (Patron Publish-Subscribe)
    // =========================================================================

    /**
     * Publica un mensaje a todos los suscriptores de un evento.
     *
     * @param evento Nombre del evento/canal
     * @param mensaje Mensaje a publicar
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
     * Suscribe un cliente a un evento especifico.
     *
     * @param evento Nombre del evento
     * @param suscriptor Thread del cliente suscriptor
     */
    private void suscribirse(String evento, UserServerThread suscriptor) {
        System.out.println("[BUS] SUSCRIBIR cliente a evento: " + evento);
        eventos.computeIfAbsent(evento, k -> new CopyOnWriteArraySet<>()).add(suscriptor);
        System.out.println("[BUS] Total suscriptores en " + evento + ": " + eventos.get(evento).size());
    }

    /**
     * Desuscribe un cliente de un evento especifico.
     *
     * @param evento Nombre del evento
     * @param suscriptor Thread del cliente a desuscribir
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
     *
     * @param idDestino ID del cliente destino
     * @param mensaje Mensaje a enviar
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
    // PUNTO DE ENTRADA PRINCIPAL
    // =========================================================================

    /**
     * Punto de entrada principal para manejar eventos del cliente.
     * Parsea el mensaje y lo enruta segun la accion solicitada.
     *
     * @param json Mensaje JSON recibido del cliente
     * @param cliente Thread del cliente que envia el mensaje
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
     * Procesa una solicitud de publicacion.
     * Delega la logica de negocio al GestorPartida.
     *
     * @param mensaje Mensaje a procesar
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
     * Procesa una solicitud de envio unicast.
     *
     * @param mensaje Mensaje a enviar
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
     * Remueve un suscriptor de todos los eventos (cuando se desconecta).
     * Notifica al GestorPartida para limpiar la partida si es necesario.
     *
     * @param user Thread del usuario a remover
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
     * Registra un nuevo cliente con su evento privado y lo guarda por ID.
     *
     * @param event Evento privado del cliente (formato: MENSAJE_CLIENTE_X)
     * @param client Thread del cliente
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
    // UTILIDADES DE CONSULTA
    // =========================================================================

    /**
     * Obtiene la cantidad de suscriptores de un evento.
     *
     * @param evento Nombre del evento
     * @return Cantidad de suscriptores
     */
    public int contarSuscriptores(String evento) {
        Set<UserServerThread> suscriptores = eventos.get(evento);
        return suscriptores != null ? suscriptores.size() : 0;
    }

    /**
     * Verifica si un evento tiene suscriptores.
     *
     * @param evento Nombre del evento
     * @return true si tiene al menos un suscriptor
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

    /**
     * Reinicia el estado de la partida en el gestor.
     */
    public void resetPartida() {
        gestorPartida.resetPartida();
    }

    /**
     * Obtiene el gestor de partida (para pruebas o consultas).
     *
     * @return Instancia del GestorPartida
     */
    public GestorPartida getGestorPartida() {
        return gestorPartida;
    }
}
