package servidor.controlador;

import compartido.comunicacion.Mensaje;
import compartido.comunicacion.TipoAccion;
import compartido.comunicacion.socket.IClienteSocket;
import com.google.gson.Gson;
import compartido.ManejadorRespuestaCliente;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import compartido.entidades.Barco;
import compartido.entidades.Coordenadas;
import compartido.entidades.Crucero;
import compartido.entidades.Disparo;
import compartido.entidades.Jugador;
import compartido.entidades.Nave;
import compartido.entidades.PortaAviones;
import compartido.entidades.Submarino;
import compartido.enums.ResultadoAddNave;
import servidor.negocio.IModeloServidor;
import compartido.comunicacion.dto.AddNaveDTO;
import compartido.comunicacion.dto.CoordenadasDTO;
import compartido.comunicacion.dto.DisparoDTO;
import compartido.comunicacion.dto.JugadorDTO;
import compartido.comunicacion.dto.NaveDTO;
import compartido.comunicacion.dto.RespuestaUnirseDTO;
import compartido.comunicacion.dto.TurnoDTO;
import compartido.enums.EstadoPartida;

/**
 *
 * @author daniel
 */
public class ControladorServidor implements ManejadorRespuestaCliente {

    private IModeloServidor servidor;
    private IClienteSocket cliente;
    private Map<String, Consumer<Mensaje>> manejadoresEventos;

    // Control de tableros confirmados
    private static Set<String> tablerosConfirmados = new HashSet<>();
    private static final Object lockTableros = new Object();

    public ControladorServidor(IModeloServidor servidor, IClienteSocket cliente, Map<String, Consumer<Mensaje>> mapa) {
        this.servidor = servidor;
        this.cliente = cliente;
        this.manejadoresEventos = mapa;

        mapa.put("DISPARO", this::realizarDisparo);
        mapa.put("ADD_NAVE", this::addNave);
        mapa.put("UNIRSE_PARTIDA", this::manejarUnirsePartida);
        mapa.put("ABANDONAR_PARTIDA", this::manejarAbandonarPartidaSv);
        mapa.put("CONFIRMAR_TABLERO", this::manejarConfirmarTablero);
    }

    // Metodo para enviar mensaje por la red.
    private void enviarMensaje(String evento, Object datos) {
        Gson gson = new Gson();

        String id = cliente.getId();
        if (id == null) {
            System.out.println("Error, id vacio.");
            return;
        }

        Mensaje mensaje = new Mensaje(TipoAccion.PUBLICAR, evento, gson.toJsonTree(datos), id);
        String json = gson.toJson(mensaje);

        cliente.enviarMensaje(json);
    }

    // Metodo para enviar mensaje por la red (con subEvento incluido).
    private void enviarMensaje(String evento, String subEvento, Object datos) {
        Gson gson = new Gson();

        String id = cliente.getId();
        if (id == null) {
            System.out.println("Error, id vacio.");
            return;
        }

        Mensaje mensaje = new Mensaje(TipoAccion.PUBLICAR, evento, gson.toJsonTree(datos), id, subEvento);
        String json = gson.toJson(mensaje);

        cliente.enviarMensaje(json);
    }

    // Metodo para manejar el mensaje recibido por la red.
    @Override
    public void manejarMensaje(String json) {
        Gson gson = new Gson();
        Mensaje mensaje = gson.fromJson(json, Mensaje.class);

        manejadoresEventos.get(mensaje.getEvento()).accept(mensaje);
    }

    // Metodo para asignar un metodo al Map cuando se asigne un id al Cliente.
    @Override
    public void onIdSet(String id) {
        manejadoresEventos.put("MENSAJE_CLIENTE_" + id, this::manejarEventoPrivado);
    }

    private void manejarEventoPrivado(Mensaje mensaje) {

    }

    private void addNave(Mensaje mensaje) {
        Gson gson = new Gson();
        AddNaveDTO dto = gson.fromJson(mensaje.getData(), AddNaveDTO.class);

        // Convertir CoordenadasDTO a Coordenadas (entidad del modelo)
        List<Coordenadas> coordenadas = dto.getCoordenadas().stream()
                .map(c -> new Coordenadas(c.getX(), c.getY()))
                .toList();

        JugadorDTO jugadorDTO = dto.getJugador();
        Jugador jugador = new Jugador(
                jugadorDTO.getNombre(),
                jugadorDTO.getColor(),
                jugadorDTO.getEstado()
        );
        NaveDTO naveDTO = dto.getNave();
        Nave nave = null;

        if (null != naveDTO.getTipo()) {
            switch (naveDTO.getTipo()) {
                case BARCO ->
                    nave = new Barco(naveDTO.getOrientacion());
                case SUBMARINO ->
                    nave = new Submarino(naveDTO.getOrientacion());
                case CRUCERO ->
                    nave = new Crucero(naveDTO.getOrientacion());
                case PORTAAVIONES ->
                    nave = new PortaAviones(naveDTO.getOrientacion());
                default -> {
                }
            }
        }

        ResultadoAddNave resultado = servidor.addNave(jugador, nave, coordenadas);

        enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(), "RESULTADO_ADD_NAVE", resultado);
    }

    private void realizarDisparo(Mensaje mensaje) {
        Gson gson = new Gson();
        DisparoDTO disparoDTO = gson.fromJson(mensaje.getData(), DisparoDTO.class);

        // Convertir CoordenadasDTO a Coordenadas (entidad del modelo)
        CoordenadasDTO coordDTO = disparoDTO.getCoordenadas();
        Coordenadas coordenadas = new Coordenadas(coordDTO.getX(), coordDTO.getY());

        JugadorDTO jugadorDTO = disparoDTO.getJugador();
        Jugador jugador = new Jugador(
                jugadorDTO.getNombre(),
                jugadorDTO.getColor(),
                jugadorDTO.getEstado()
        );

        Disparo disparo = servidor.realizarDisparo(coordenadas, jugador, disparoDTO.getTiempo());

        // Convertir Coordenadas (entidad) a CoordenadasDTO para el resultado
        Coordenadas dispCoord = disparo.getCoordenadas();
        CoordenadasDTO resultCoordDTO = new CoordenadasDTO(dispCoord.getX(), dispCoord.getY());

        DisparoDTO resultado = new DisparoDTO(
                new JugadorDTO(
                        disparo.getJugador().getNombre(),
                        disparo.getJugador().getColor(),
                        disparo.getJugador().getEstado()),
                resultCoordDTO,
                disparo.getResultadoDisparo(),
                disparo.getEstadoPartida()
        );

        // Copiar informacion del tipo de nave para el marcador
        resultado.setTipoNaveImpactada(disparo.getTipoNaveImpactada());
        resultado.setTipoNaveHundida(disparo.getTipoNaveHundida());

        enviarMensaje("RESULTADO_DISPARO", resultado);

        // Si la partida termino, enviar evento FIN_PARTIDA
        if (disparo.getEstadoPartida() == EstadoPartida.FINALIZADA) {
            System.out.println("[SERVIDOR] Partida finalizada - Ganador: " + jugador.getNombre());
            enviarMensaje("FIN_PARTIDA", jugadorDTO);
        }
    }

    private void manejarUnirsePartida(Mensaje mensaje) {
        System.out.println("[SERVIDOR] Recibio 'UNIRSE_PARTIDA'.");

        Gson gson = new Gson();
        JugadorDTO jugadorDTO = gson.fromJson(mensaje.getData(), JugadorDTO.class);

        // Validar que el nombre no este duplicado
        List<Jugador> jugadoresActuales = servidor.getJugadores();
        boolean nombreDuplicado = jugadoresActuales.stream()
                .anyMatch(j -> j.getNombre().equalsIgnoreCase(jugadorDTO.getNombre()));

        if (nombreDuplicado) {
            System.out.println("[SERVIDOR] ERROR: Nombre duplicado - " + jugadorDTO.getNombre());
            RespuestaUnirseDTO respuesta = RespuestaUnirseDTO.errorNombreDuplicado(jugadorDTO.getNombre());
            enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(), "RESPUESTA_UNIRSE", respuesta);
            return;
        }

        // Validar que la partida no este llena
        if (jugadoresActuales.size() >= 2) {
            System.out.println("[SERVIDOR] ERROR: Partida llena");
            RespuestaUnirseDTO respuesta = RespuestaUnirseDTO.errorPartidaLlena();
            enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(), "RESPUESTA_UNIRSE", respuesta);
            return;
        }

        // Todo bien, notificar a todos que un jugador se unio
        System.out.println("[SERVIDOR] Jugador aceptado: " + jugadorDTO.getNombre());
        RespuestaUnirseDTO respuestaExito = RespuestaUnirseDTO.exito(jugadorDTO);
        enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(), "RESPUESTA_UNIRSE", respuestaExito);
        enviarMensaje("JUGADOR_UNIDO", jugadorDTO);
    }

    //Recibe el mensaje enviado por el cliente
    private void manejarAbandonarPartidaSv(Mensaje mensaje) {
        Gson gson = new Gson();
        JugadorDTO jugadorDTO = gson.fromJson(mensaje.getData(), JugadorDTO.class);

        // Convertir DTO a entidad real
        Jugador jugador = new Jugador(
                jugadorDTO.getNombre(),
                jugadorDTO.getColor(),
                jugadorDTO.getEstado()
        );

        // 1. Lógica REAL del servidor
        servidor.abandonarPartida(jugador);

        // 2. Notificar al otro jugador
        enviarMensaje("JUGADOR_ABANDONO", jugadorDTO);

        // Limpiar tableros confirmados si abandona
        synchronized (lockTableros) {
            tablerosConfirmados.remove(jugadorDTO.getNombre());
        }
    }

    /**
     * Maneja la confirmacion del tablero de un jugador.
     * La logica de sincronizacion (TABLEROS_LISTOS, TURNO_INICIAL)
     * se maneja en BusEventos para evitar duplicados.
     */
    private void manejarConfirmarTablero(Mensaje mensaje) {
        Gson gson = new Gson();
        JugadorDTO jugadorDTO = gson.fromJson(mensaje.getData(), JugadorDTO.class);

        System.out.println("[SERVIDOR] Tablero confirmado por: " + jugadorDTO.getNombre());

        // Solo reenviar el evento CONFIRMAR_TABLERO al bus
        // BusEventos se encarga de la logica de sincronizacion
        enviarMensaje("CONFIRMAR_TABLERO", jugadorDTO);

        // Iniciar partida en el modelo del servidor si es necesario
        synchronized (lockTableros) {
            tablerosConfirmados.add(jugadorDTO.getNombre());
            if (tablerosConfirmados.size() >= 2) {
                servidor.empezarPartida();
                tablerosConfirmados.clear();
            }
        }
    }

    /**
     * Reinicia el estado de tableros confirmados (para nueva partida).
     */
    public static void resetTablerosConfirmados() {
        synchronized (lockTableros) {
            tablerosConfirmados.clear();
        }
    }

}
