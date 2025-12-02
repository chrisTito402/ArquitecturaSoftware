package cliente.controlador;

import compartido.comunicacion.Mensaje;
import compartido.comunicacion.TipoAccion;
import compartido.comunicacion.socket.IClienteSocket;
import com.google.gson.Gson;
import compartido.entidades.Jugador;
import cliente.negocio.builder.Director;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import cliente.negocio.builder.PartidaBuilder;
import compartido.observador.ISuscriptor;
import cliente.negocio.IModeloCliente;
import compartido.enums.ResultadoAddNave;
import compartido.comunicacion.dto.AddNaveDTO;
import compartido.comunicacion.dto.CoordenadasDTO;
import compartido.comunicacion.dto.DisparoDTO;
import compartido.comunicacion.dto.JugadorDTO;
import compartido.comunicacion.dto.NaveDTO;
import compartido.comunicacion.dto.RespuestaUnirseDTO;
import compartido.comunicacion.dto.TurnoDTO;
import compartido.ManejadorRespuestaCliente;

/**
 * Controlador principal del juego Batalla Naval.
 * Maneja la comunicacion entre la vista, el modelo y el bus de eventos.
 *
 * @author daniel
 */
public class Controlador implements IControlador, ManejadorRespuestaCliente {

    private IModeloCliente partida;
    private IClienteSocket cliente;
    private Map<String, Consumer<Mensaje>> manejadorEventos;

    public Controlador() {
    }

    public Controlador(IModeloCliente partida, IClienteSocket cliente, Map<String, Consumer<Mensaje>> mapa) {
        this.partida = partida;
        this.cliente = cliente;
        this.manejadorEventos = mapa;

        registrarManejadoresDeEventos();
    }

    /**
     * Registra todos los manejadores de eventos del bus.
     */
    private void registrarManejadoresDeEventos() {
        // === EVENTOS DE LOBBY/PARTIDA ===
        manejadorEventos.put("JUGADOR_UNIDO", this::manejarJugadorUnido);
        manejadorEventos.put("UNIRSE_PARTIDA", this::manejarUnirsePartida);
        manejadorEventos.put("ABANDONAR_LOBBY", this::manejarAbandonarLobby);
        manejadorEventos.put("EMPEZAR_PARTIDA", this::manejarEmpezarPartida);
        manejadorEventos.put("RESPUESTA_UNIRSE", this::manejarRespuestaUnirse);

        // === EVENTOS DE COLOCACION DE NAVES ===
        manejadorEventos.put("RESULTADO_ADD_NAVE", this::manejarResultadoAddNave);
        manejadorEventos.put("CONFIRMAR_TABLERO", this::manejarConfirmarTablero);
        manejadorEventos.put("TABLEROS_LISTOS", this::manejarTablerosListos);

        // === EVENTOS DE BATALLA ===
        manejadorEventos.put("RESULTADO_DISPARO", this::manejarResultadoDisparo);
        manejadorEventos.put("CAMBIO_TURNO", this::manejarCambioTurno);
        manejadorEventos.put("TIEMPO_AGOTADO", this::manejarTiempoAgotado);
        manejadorEventos.put("TURNO_INICIAL", this::manejarTurnoInicial);

        // === EVENTOS DE FIN DE PARTIDA ===
        manejadorEventos.put("JUGADOR_ABANDONO", this::manejarAbandonarPartida);
        manejadorEventos.put("FIN_PARTIDA", this::manejarFinPartida);

        System.out.println("[CONTROLADOR] Registrados " + manejadorEventos.size() + " manejadores de eventos");
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

    // Metodo para manejar el mensaje recibido por la red.
    @Override
    public void manejarMensaje(String json) {
        Gson gson = new Gson();
        Mensaje mensaje = gson.fromJson(json, Mensaje.class);

        manejadorEventos.get(mensaje.getEvento()).accept(mensaje);
    }

    // Metodo para asignar un metodo al Map cuando se asigne un id al Cliente.
    @Override
    public void onIdSet(String id) {
        manejadorEventos.put("MENSAJE_CLIENTE_" + id, this::manejarEventoPrivado);
    }

    private void manejarEventoPrivado(Mensaje mensaje) {
        manejadorEventos.get(mensaje.getSubEvento()).accept(mensaje);
    }

    private void manejarResultadoAddNave(Mensaje mensaje) {
        Gson gson = new Gson();
        ResultadoAddNave resultado = gson.fromJson(mensaje.getData(), ResultadoAddNave.class);

        partida.manejarResultadoAddNave(resultado);
    }

    private void manejarResultadoDisparo(Mensaje mensaje) {
        Gson gson = new Gson();
        DisparoDTO d = gson.fromJson(mensaje.getData(), DisparoDTO.class);

        partida.manejarResultadoDisparo(d);
    }

    //Recibe el mensaje del servidor
    public void manejarAbandonarPartida(Mensaje mensaje) {
        Gson gson = new Gson();
        JugadorDTO jugadorDTO = gson.fromJson(mensaje.getData(), JugadorDTO.class);
        System.out.println("El jugador " + jugadorDTO.getNombre() + " abandono la partida.");
        partida.notificarAllSuscriptores("ABANDONO_PARTIDA", jugadorDTO);

    }

    //Manda mensaje* al servidor
    @Override
    public void abandonarPartida(Jugador jugador) {
        // Validaciones del modelo
        JugadorDTO dto = partida.abandonarPartida(jugador);

        if (dto == null) {
            System.out.println("No se pudo abandonar la partida.");
            return;
        }
        enviarMensaje("ABANDONAR_PARTIDA", dto);
    }

    @Override
    public String crearPartida(Jugador j) {
        Director d = new Director();
        IModeloCliente modelo = d.makePartida(new PartidaBuilder());
        this.partida = modelo;
        return "Partida creada correctamente";
    }

    @Override
    public void realizarDisparo(CoordenadasDTO coordenadas) {
        DisparoDTO disparo = partida.realizarDisparo(coordenadas);
        if (disparo != null) {
            enviarMensaje("DISPARO", disparo);
        }
    }

    @Override
    public boolean esMiTurno() {
        return partida.esMiTurno();
    }

    @Override
    public void setTurno(boolean esMiTurno) {
        partida.setTurno(esMiTurno);
    }

    @Override
    public void notificarTiempoAgotado() {
        JugadorDTO jugador = partida.getJugador();
        if (jugador != null) {
            partida.setTurno(false);
            enviarMensaje("TIEMPO_AGOTADO", jugador.getNombre());
            System.out.println("[CONTROLADOR] Tiempo agotado notificado para: " + jugador.getNombre());
        }
    }

    private void manejarJugadorUnido(Mensaje mensaje) {
        System.out.println("=== RECIBIDO JUGADOR_UNIDO ===");
        Gson gson = new Gson();
        JugadorDTO jugadorDTO = gson.fromJson(mensaje.getData(), JugadorDTO.class);
        System.out.println("Jugador en mensaje: " + jugadorDTO.getNombre());

        // Notificar a los suscriptores locales (esto actualiza el lobby)
        System.out.println("Notificando a suscriptores del modelo...");
        partida.notificarAllSuscriptores("JUGADOR_UNIDO", jugadorDTO);
    }

    @Override
    public void addNave(NaveDTO nave, List<CoordenadasDTO> coordenadas) {
        AddNaveDTO addDTO = partida.addNave(nave, coordenadas);
        if (addDTO != null) {
            enviarMensaje("ADD_NAVE", addDTO);
        }
    }

    @Override
    public void addJugador(Jugador jugador) {
        partida.addJugador(jugador);
    }

    @Override
    public void crearTableros() {
        partida.crearTableros();
    }

    @Override
    public void suscribirAPartida(ISuscriptor suscriptor) {
        partida.suscribirAPartida(suscriptor);
    }

    @Override
    public JugadorDTO getJugador() {
        return partida.getJugador();
    }

    // Caso de Uso: Unirse Partida
    @Override
    public void unirsePartida(JugadorDTO jugadorDTO) {
        Jugador jugador = new Jugador(jugadorDTO.getNombre(), jugadorDTO.getColor(), jugadorDTO.getEstado());
        partida.unirsePartida(jugador);
        enviarMensaje("UNIRSE_PARTIDA", jugadorDTO);
    }

    private void manejarUnirsePartida(Mensaje mensaje) {
        JugadorDTO dto = new Gson().fromJson(mensaje.getData(), JugadorDTO.class);
        System.out.println("=== RECIBIDO UNIRSE_PARTIDA ===");
        System.out.println("Jugador que se unio: " + dto.getNombre());

        partida.notificarAllSuscriptores("UNIRSE_PARTIDA", dto);

        // Si soy el HOST, enviar mi info al nuevo jugador para que me vea
        ControlVista cv = ControlVista.getInstancia();
        System.out.println("Soy HOST? " + cv.isEsHost());

        if (cv.isEsHost()) {
            JugadorDTO miJugador = partida.getJugador();
            System.out.println("Mi jugador: " + (miJugador != null ? miJugador.getNombre() : "NULL"));

            // Solo enviar si tengo info y no soy yo mismo
            if (miJugador != null && !miJugador.getNombre().equals(dto.getNombre())) {
                System.out.println(">>> HOST: Enviando JUGADOR_UNIDO con mi info: " + miJugador.getNombre());
                enviarMensaje("JUGADOR_UNIDO", miJugador);
            } else {
                System.out.println("No envio porque es el mismo jugador o miJugador es null");
            }
        }
    }

    @Override
    public void empezarPartida() {
        partida.empezarPartida();
        enviarMensaje("EMPEZAR_PARTIDA", null);
    }

    private void manejarEmpezarPartida(Mensaje mensaje) {
        JugadorDTO jugadorDTO = new Gson().fromJson(mensaje.getData(), JugadorDTO.class);
        System.out.println("La partida esta comenzando.");
        partida.notificarAllSuscriptores("EMPEZAR_PARTIDA", jugadorDTO);
    }

    @Override
    public void abandonarLobby(JugadorDTO jugadorDTO) {
        Jugador jugador = new Jugador(jugadorDTO.getNombre(), jugadorDTO.getColor(), jugadorDTO.getEstado());
        partida.abandonarLobby(jugador);
        //Mandar mensaje al servidor para avisar al rival
        enviarMensaje("ABANDONAR_LOBBY", jugadorDTO);
    }

    private void manejarAbandonarLobby(Mensaje mensaje) {
        JugadorDTO jugadorDTO = new Gson().fromJson(mensaje.getData(), JugadorDTO.class);
        System.out.println("El jugador " + jugadorDTO.getNombre() + " abandono el lobby.");
        partida.notificarAllSuscriptores("ABANDONAR_LOBBY", jugadorDTO);
    }

    @Override
    public List<JugadorDTO> getJugadores() {
        return partida.getJugadores()
                .stream()
                .map(jugadorEntidad -> new JugadorDTO(jugadorEntidad.getNombre(), jugadorEntidad.getColor(), jugadorEntidad.getEstado()))
                .toList();
    }

    // =========================================================================
    // NUEVOS MANEJADORES DE EVENTOS
    // =========================================================================

    /**
     * Confirma el tablero del jugador y notifica al servidor.
     */
    @Override
    public void confirmarTablero() {
        partida.confirmarTablero();
        JugadorDTO jugador = partida.getJugador();
        enviarMensaje("CONFIRMAR_TABLERO", jugador);
        System.out.println("[CONTROLADOR] Tablero confirmado para: " + jugador.getNombre());
    }

    /**
     * Maneja la confirmacion de tablero de otro jugador.
     */
    private void manejarConfirmarTablero(Mensaje mensaje) {
        Gson gson = new Gson();
        JugadorDTO jugadorDTO = gson.fromJson(mensaje.getData(), JugadorDTO.class);
        System.out.println("[CONTROLADOR] Tablero confirmado por: " + jugadorDTO.getNombre());
        partida.manejarConfirmacionTablero(jugadorDTO);
    }

    /**
     * Maneja cuando ambos tableros estan listos para iniciar la batalla.
     */
    private void manejarTablerosListos(Mensaje mensaje) {
        System.out.println("[CONTROLADOR] Ambos tableros listos - Iniciando batalla");
        partida.manejarTablerosListos();
    }

    /**
     * Maneja el cambio de turno entre jugadores.
     */
    private void manejarCambioTurno(Mensaje mensaje) {
        Gson gson = new Gson();
        TurnoDTO turno = gson.fromJson(mensaje.getData(), TurnoDTO.class);
        System.out.println("[CONTROLADOR] Cambio de turno - Ahora juega: " + turno.getNombreJugadorEnTurno());
        partida.manejarCambioTurno(turno);
    }

    /**
     * Maneja el turno inicial al comenzar la partida.
     */
    private void manejarTurnoInicial(Mensaje mensaje) {
        Gson gson = new Gson();
        TurnoDTO turno = gson.fromJson(mensaje.getData(), TurnoDTO.class);
        System.out.println("[CONTROLADOR] Turno inicial - Comienza: " + turno.getNombreJugadorEnTurno());
        partida.manejarCambioTurno(turno);
        partida.notificarAllSuscriptores("TURNO_INICIAL", turno);
    }

    /**
     * Maneja cuando se agota el tiempo del turno.
     */
    private void manejarTiempoAgotado(Mensaje mensaje) {
        Gson gson = new Gson();
        String idJugador = gson.fromJson(mensaje.getData(), String.class);
        System.out.println("[CONTROLADOR] Tiempo agotado para jugador: " + idJugador);
        partida.manejarTiempoAgotado(idJugador);
    }

    /**
     * Maneja el fin de la partida (victoria/derrota).
     */
    private void manejarFinPartida(Mensaje mensaje) {
        Gson gson = new Gson();
        JugadorDTO ganador = gson.fromJson(mensaje.getData(), JugadorDTO.class);
        System.out.println("[CONTROLADOR] Fin de partida - Ganador: " +
                          (ganador != null ? ganador.getNombre() : "Ninguno"));
        partida.manejarFinPartida(ganador);
    }

    /**
     * Envia un evento de cambio de turno al bus.
     */
    public void notificarCambioTurno(TurnoDTO turno) {
        enviarMensaje("CAMBIO_TURNO", turno);
    }

    /**
     * Envia un evento de tiempo agotado al bus.
     */
    public void notificarTiempoAgotado(String idJugador) {
        enviarMensaje("TIEMPO_AGOTADO", idJugador);
    }

    /**
     * Envia un evento de fin de partida al bus.
     */
    public void notificarFinPartida(JugadorDTO ganador) {
        enviarMensaje("FIN_PARTIDA", ganador);
    }

    /**
     * Envia un evento de tableros listos al bus.
     */
    public void notificarTablerosListos() {
        enviarMensaje("TABLEROS_LISTOS", null);
    }

    /**
     * Maneja la respuesta del servidor al intentar unirse a una partida.
     * Verifica si el nombre esta duplicado u otro error.
     */
    private void manejarRespuestaUnirse(Mensaje mensaje) {
        Gson gson = new Gson();
        RespuestaUnirseDTO respuesta = gson.fromJson(mensaje.getData(), RespuestaUnirseDTO.class);

        System.out.println("[CONTROLADOR] Respuesta de unirse: " + respuesta);

        if (!respuesta.isExitoso()) {
            // Notificar error a la vista
            partida.notificarAllSuscriptores("ERROR_UNIRSE", respuesta);
            System.out.println("[CONTROLADOR] Error al unirse: " + respuesta.getMensaje());
        } else {
            System.out.println("[CONTROLADOR] Union exitosa");
        }
    }
}
