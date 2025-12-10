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
import compartido.comunicacion.dto.SolicitudUnirseDTO;
import compartido.comunicacion.dto.TurnoDTO;
import compartido.ManejadorRespuestaCliente;

/**
 * Este es el controlador principal que conecta todo en el cliente.
 * Basicamente es el que recibe lo que hace el usuario en la pantalla
 * y se lo manda al modelo, y tambien recibe las respuestas del servidor.
 *
 * Usamos un HashMap para manejar los eventos porque si no el switch
 * se hacia gigantesco y era un desmadre leerlo.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public class Controlador implements IControlador, ManejadorRespuestaCliente {

    // el modelo donde esta la logica
    private IModeloCliente partida;

    // socket para hablar con el servidor
    private IClienteSocket cliente;

    // mapa de eventos -> metodo que lo maneja
    private Map<String, Consumer<Mensaje>> manejadorEventos;

    /**
     * Constructor vacio por si se necesita.
     */
    public Controlador() {
    }

    /**
     * Constructor que recibe todas las dependencias.
     */
    public Controlador(IModeloCliente partida, IClienteSocket cliente, Map<String, Consumer<Mensaje>> mapa) {
        this.partida = partida;
        this.cliente = cliente;
        this.manejadorEventos = mapa;

        registrarManejadoresDeEventos();
    }

    /**
     * Registra los manejadores de cada evento en el mapa.
     * Asi cuando llega algo solo buscamos y ejecutamos.
     */
    private void registrarManejadoresDeEventos() {
        manejadorEventos.put("JUGADOR_UNIDO", this::manejarJugadorUnido);
        manejadorEventos.put("UNIRSE_PARTIDA", this::manejarUnirsePartida);
        manejadorEventos.put("ABANDONAR_LOBBY", this::manejarAbandonarLobby);
        manejadorEventos.put("EMPEZAR_PARTIDA", this::manejarEmpezarPartida);
        manejadorEventos.put("RESPUESTA_UNIRSE", this::manejarRespuestaUnirse);
        manejadorEventos.put("JUGADOR_LISTO", this::manejarJugadorListo);
        manejadorEventos.put("RESULTADO_ADD_NAVE", this::manejarResultadoAddNave);
        manejadorEventos.put("CONFIRMAR_TABLERO", this::manejarConfirmarTablero);
        manejadorEventos.put("TABLEROS_LISTOS", this::manejarTablerosListos);
        manejadorEventos.put("RESULTADO_DISPARO", this::manejarResultadoDisparo);
        manejadorEventos.put("CAMBIO_TURNO", this::manejarCambioTurno);
        manejadorEventos.put("TIEMPO_AGOTADO", this::manejarTiempoAgotado);
        manejadorEventos.put("TURNO_INICIAL", this::manejarTurnoInicial);
        manejadorEventos.put("JUGADOR_ABANDONO", this::manejarAbandonarPartida);
        manejadorEventos.put("ABANDONAR_PARTIDA", this::manejarAbandonarPartida);
        manejadorEventos.put("FIN_PARTIDA", this::manejarFinPartida);
        System.out.println("[CONTROLADOR] Registrados " + manejadorEventos.size() + " manejadores de eventos");
    }

    /**
     * Manda un mensaje al servidor.
     * Convierte los datos a JSON y los envia.
     */
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

    /**
     * Recibe mensaje del servidor, lo deserializa y ejecuta el manejador.
     */
    @Override
    public void manejarMensaje(String json) {
        Gson gson = new Gson();
        Mensaje mensaje = gson.fromJson(json, Mensaje.class);

        manejadorEventos.get(mensaje.getEvento()).accept(mensaje);
    }

    /**
     * El servidor nos asigna un ID y registramos el manejador de mensajes privados.
     */
    @Override
    public void onIdSet(String id) {
        manejadorEventos.put("MENSAJE_CLIENTE_" + id, this::manejarEventoPrivado);
    }

    /**
     * Procesa mensajes que son solo para este cliente.
     */
    private void manejarEventoPrivado(Mensaje mensaje) {
        manejadorEventos.get(mensaje.getSubEvento()).accept(mensaje);
    }

    /**
     * Maneja la respuesta de agregar nave.
     */
    private void manejarResultadoAddNave(Mensaje mensaje) {
        Gson gson = new Gson();
        ResultadoAddNave resultado = gson.fromJson(mensaje.getData(), ResultadoAddNave.class);

        partida.manejarResultadoAddNave(resultado);
    }

    /**
     * Maneja el resultado de un disparo.
     */
    private void manejarResultadoDisparo(Mensaje mensaje) {
        Gson gson = new Gson();
        DisparoDTO d = gson.fromJson(mensaje.getData(), DisparoDTO.class);

        partida.manejarResultadoDisparo(d);
    }

    /**
     * Cuando alguien abandona la partida.
     */
    public void manejarAbandonarPartida(Mensaje mensaje) {
        Gson gson = new Gson();
        JugadorDTO jugadorDTO = gson.fromJson(mensaje.getData(), JugadorDTO.class);
        System.out.println("El jugador " + jugadorDTO.getNombre() + " abandono la partida.");
        partida.notificarAllSuscriptores("JUGADOR_ABANDONO", jugadorDTO);

    }

    /**
     * Para abandonar la partida.
     */
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

    /**
     * Crea partida nueva con el Builder.
     */
    @Override
    public String crearPartida(Jugador j) {
        Director d = new Director();
        IModeloCliente modelo = d.makePartida(new PartidaBuilder());
        this.partida = modelo;
        return "Partida creada correctamente";
    }

    /**
     * Dispara a una coordenada y manda al servidor.
     */
    @Override
    public void realizarDisparo(CoordenadasDTO coordenadas) {
        DisparoDTO disparo = partida.realizarDisparo(coordenadas);
        if (disparo != null) {
            enviarMensaje("DISPARO", disparo);
        }
    }

    /**
     * Checa si es nuestro turno.
     */
    @Override
    public boolean esMiTurno() {
        return partida.esMiTurno();
    }

    /**
     * Actualiza el turno.
     */
    @Override
    public void setTurno(boolean esMiTurno) {
        partida.setTurno(esMiTurno);
    }

    /**
     * Avisa al servidor que se acabo el tiempo.
     */
    @Override
    public void notificarTiempoAgotado() {
        JugadorDTO jugador = partida.getJugador();
        if (jugador != null) {
            partida.setTurno(false);
            enviarMensaje("TIEMPO_AGOTADO", jugador.getNombre());
            System.out.println("[CONTROLADOR] Tiempo agotado notificado para: " + jugador.getNombre());
        }
    }

    /**
     * Cuando se une un jugador nuevo.
     */
    private void manejarJugadorUnido(Mensaje mensaje) {
        System.out.println("=== RECIBIDO JUGADOR_UNIDO ===");
        Gson gson = new Gson();
        JugadorDTO jugadorDTO = gson.fromJson(mensaje.getData(), JugadorDTO.class);
        System.out.println("Jugador en mensaje: " + jugadorDTO.getNombre());

        // Agregar el jugador al modelo local si no existe
        if (jugadorDTO != null && jugadorDTO.getNombre() != null) {
            Jugador jugador = new Jugador(jugadorDTO.getNombre(), jugadorDTO.getColor(), jugadorDTO.getEstado());
            partida.addJugador(jugador);
        }

        // Notificar a los suscriptores locales (esto actualiza el lobby)
        System.out.println("Notificando a suscriptores del modelo...");
        partida.notificarAllSuscriptores("JUGADOR_UNIDO", jugadorDTO);
    }

    /**
     * Agrega una nave al tablero.
     */
    @Override
    public void addNave(NaveDTO nave, List<CoordenadasDTO> coordenadas) {
        AddNaveDTO addDTO = partida.addNave(nave, coordenadas);
        if (addDTO != null) {
            enviarMensaje("ADD_NAVE", addDTO);
        }
    }

    /**
     * Limpia las naves del tablero.
     */
    @Override
    public void limpiarNaves() {
        // Limpiar en el modelo local
        partida.limpiarNaves();
        // Notificar al servidor para limpiar las naves registradas
        JugadorDTO jugador = partida.getJugador();
        if (jugador != null) {
            enviarMensaje("LIMPIAR_TABLERO", jugador);
            System.out.println("[CONTROLADOR] Tablero limpiado para: " + jugador.getNombre());
        }
    }

    /**
     * Agrega un jugador al modelo.
     */
    @Override
    public void addJugador(Jugador jugador) {
        partida.addJugador(jugador);
    }

    /**
     * Crea los tableros.
     */
    @Override
    public void crearTableros() {
        partida.crearTableros();
    }

    /**
     * Suscribe un observador al modelo (Observer).
     */
    @Override
    public void suscribirAPartida(ISuscriptor suscriptor) {
        partida.suscribirAPartida(suscriptor);
    }

    /**
     * Quita un observador.
     */
    @Override
    public void desuscribirDePartida(ISuscriptor suscriptor) {
        partida.desuscribirDePartida(suscriptor);
    }

    /**
     * Retorna el jugador local.
     */
    @Override
    public JugadorDTO getJugador() {
        return partida.getJugador();
    }

    /**
     * Unirse a una partida.
     */
    @Override
    public void unirsePartida(JugadorDTO jugadorDTO) {
        Jugador jugador = new Jugador(jugadorDTO.getNombre(), jugadorDTO.getColor(), jugadorDTO.getEstado());
        partida.unirsePartida(jugador);
        enviarMensaje("UNIRSE_PARTIDA", jugadorDTO);
    }

    /**
     * Crea partida con codigo (para el host).
     */
    @Override
    public void crearPartidaConCodigo(JugadorDTO jugador, String codigo) {
        // Registrar jugador en el modelo local
        Jugador j = new Jugador(jugador.getNombre(), jugador.getColor(), jugador.getEstado());
        partida.unirsePartida(j);

        // Enviar al servidor para registrar la partida
        SolicitudUnirseDTO solicitud = new SolicitudUnirseDTO(jugador, codigo, true);
        enviarMensaje("CREAR_PARTIDA", solicitud);
        System.out.println("[CONTROLADOR] Partida creada con codigo: " + codigo);
    }

    /**
     * Solicita unirse a una partida con codigo.
     */
    @Override
    public void unirsePartidaConCodigo(JugadorDTO jugador, String codigo) {
        // NO registrar en modelo local hasta recibir confirmacion
        SolicitudUnirseDTO solicitud = new SolicitudUnirseDTO(jugador, codigo, false);
        enviarMensaje("UNIRSE_PARTIDA", solicitud);
        System.out.println("[CONTROLADOR] Solicitud de union enviada - Codigo: " + codigo);
    }

    /**
     * Cuando alguien se une a la partida.
     */
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

    /**
     * Inicia la partida.
     */
    @Override
    public void empezarPartida() {
        partida.empezarPartida();
        JugadorDTO jugador = partida.getJugador();
        enviarMensaje("EMPEZAR_PARTIDA", jugador);
    }

    /**
     * Avisa que el jugador esta listo.
     */
    @Override
    public void jugadorListo() {
        JugadorDTO jugador = partida.getJugador();
        if (jugador != null) {
            enviarMensaje("JUGADOR_LISTO", jugador);
            System.out.println("[CONTROLADOR] Jugador listo enviado: " + jugador.getNombre());
        }
    }

    /**
     * Recibe que un jugador esta listo.
     */
    private void manejarJugadorListo(Mensaje mensaje) {
        Gson gson = new Gson();
        JugadorDTO jugadorDTO = gson.fromJson(mensaje.getData(), JugadorDTO.class);
        System.out.println("[CONTROLADOR] Jugador listo recibido: " + jugadorDTO.getNombre());
        partida.notificarAllSuscriptores("JUGADOR_LISTO", jugadorDTO);
    }

    /**
     * Recibe que la partida empezo.
     */
    private void manejarEmpezarPartida(Mensaje mensaje) {
        JugadorDTO jugadorDTO = new Gson().fromJson(mensaje.getData(), JugadorDTO.class);
        System.out.println("La partida esta comenzando.");
        partida.notificarAllSuscriptores("EMPEZAR_PARTIDA", jugadorDTO);
    }

    /**
     * Abandonar el lobby.
     */
    @Override
    public void abandonarLobby(JugadorDTO jugadorDTO) {
        Jugador jugador = new Jugador(jugadorDTO.getNombre(), jugadorDTO.getColor(), jugadorDTO.getEstado());
        partida.abandonarLobby(jugador);
        //Mandar mensaje al servidor para avisar al rival
        enviarMensaje("ABANDONAR_LOBBY", jugadorDTO);
    }

    /**
     * Otro jugador abandono el lobby.
     */
    private void manejarAbandonarLobby(Mensaje mensaje) {
        JugadorDTO jugadorDTO = new Gson().fromJson(mensaje.getData(), JugadorDTO.class);
        System.out.println("El jugador " + jugadorDTO.getNombre() + " abandono el lobby.");
        partida.notificarAllSuscriptores("ABANDONAR_LOBBY", jugadorDTO);
    }

    /**
     * Obtiene los jugadores de la partida.
     */
    @Override
    public List<JugadorDTO> getJugadores() {
        return partida.getJugadores()
                .stream()
                .map(jugadorEntidad -> new JugadorDTO(jugadorEntidad.getNombre(), jugadorEntidad.getColor(), jugadorEntidad.getEstado()))
                .toList();
    }

    // =========================================================================
    // EVENTOS DE BATALLA
    // =========================================================================

    /**
     * Confirma el tablero (ya puso todas las naves).
     */
    @Override
    public void confirmarTablero() {
        partida.confirmarTablero();
        JugadorDTO jugador = partida.getJugador();
        enviarMensaje("CONFIRMAR_TABLERO", jugador);
        System.out.println("[CONTROLADOR] Tablero confirmado para: " + jugador.getNombre());
    }

    /**
     * Otro jugador confirmo su tablero.
     */
    private void manejarConfirmarTablero(Mensaje mensaje) {
        Gson gson = new Gson();
        JugadorDTO jugadorDTO = gson.fromJson(mensaje.getData(), JugadorDTO.class);
        System.out.println("[CONTROLADOR] Tablero confirmado por: " + jugadorDTO.getNombre());
        partida.manejarConfirmacionTablero(jugadorDTO);
    }

    /**
     * Ambos tableros listos, empieza la batalla.
     */
    private void manejarTablerosListos(Mensaje mensaje) {
        System.out.println("[CONTROLADOR] Ambos tableros listos - Iniciando batalla");
        partida.manejarTablerosListos();
    }

    /**
     * Cambio de turno.
     */
    private void manejarCambioTurno(Mensaje mensaje) {
        Gson gson = new Gson();
        TurnoDTO turno = gson.fromJson(mensaje.getData(), TurnoDTO.class);
        System.out.println("[CONTROLADOR] Cambio de turno - Ahora juega: " + turno.getNombreJugadorEnTurno());
        partida.manejarCambioTurno(turno);
    }

    /**
     * Turno inicial (quien empieza).
     */
    private void manejarTurnoInicial(Mensaje mensaje) {
        Gson gson = new Gson();
        TurnoDTO turno = gson.fromJson(mensaje.getData(), TurnoDTO.class);
        System.out.println("[CONTROLADOR] Turno inicial - Comienza: " + turno.getNombreJugadorEnTurno());
        partida.manejarCambioTurno(turno);
        partida.notificarAllSuscriptores("TURNO_INICIAL", turno);
    }

    /**
     * Se acabo el tiempo de alguien.
     */
    private void manejarTiempoAgotado(Mensaje mensaje) {
        Gson gson = new Gson();
        String idJugador = gson.fromJson(mensaje.getData(), String.class);
        System.out.println("[CONTROLADOR] Tiempo agotado para jugador: " + idJugador);
        partida.manejarTiempoAgotado(idJugador);
    }

    /**
     * Fin de la partida, alguien gano.
     */
    private void manejarFinPartida(Mensaje mensaje) {
        Gson gson = new Gson();
        JugadorDTO ganador = gson.fromJson(mensaje.getData(), JugadorDTO.class);
        System.out.println("[CONTROLADOR] Fin de partida - Ganador: " +
                          (ganador != null ? ganador.getNombre() : "Ninguno"));
        partida.manejarFinPartida(ganador);
    }

    /**
     * Notifica cambio de turno al servidor.
     */
    public void notificarCambioTurno(TurnoDTO turno) {
        enviarMensaje("CAMBIO_TURNO", turno);
    }

    /**
     * Notifica tiempo agotado.
     */
    public void notificarTiempoAgotado(String idJugador) {
        enviarMensaje("TIEMPO_AGOTADO", idJugador);
    }

    /**
     * Notifica fin de partida.
     */
    public void notificarFinPartida(JugadorDTO ganador) {
        enviarMensaje("FIN_PARTIDA", ganador);
    }

    /**
     * Notifica tableros listos.
     */
    public void notificarTablerosListos() {
        enviarMensaje("TABLEROS_LISTOS", null);
    }

    /**
     * Respuesta de unirse a partida.
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

    /**
     * Reinicia el modelo para nueva partida.
     */
    @Override
    public void reiniciarModelo() {
        partida.reiniciar();
        System.out.println("[CONTROLADOR] Modelo reiniciado");
    }
}
