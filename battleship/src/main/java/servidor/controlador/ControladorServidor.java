package servidor.controlador;

import buseventos.Mensaje;
import buseventos.TipoAccion;
import clientesocket.IClienteSocket;
import com.google.gson.Gson;
import controllers.controller.ManejadorRespuestaCliente;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import models.builder.Director;
import models.builder.PartidaBuilder;
import models.entidades.Barco;
import models.entidades.Coordenadas;
import models.entidades.Crucero;
import models.entidades.Disparo;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.entidades.Partida;
import models.entidades.PortaAviones;
import models.entidades.Puntaje;
import models.entidades.Submarino;
import models.enums.EstadoPartida;
import models.enums.ResultadoAddNave;
import models.enums.ResultadoUnirsePartida;
import servidor.modelo.GestorPartidas;
import servidor.modelo.IModeloServidor;
import servidor.validacion.ValidadorJugador;
import shared.dto.AddNaveDTO;
import shared.dto.CrearPartidaDTO;
import shared.dto.DisparoDTO;
import shared.dto.JugadorDTO;
import shared.dto.NaveDTO;
import shared.dto.PuntajeDTO;
import shared.dto.UnirsePartidaDTO;

/**
 *
 * @author daniel
 */
public class ControladorServidor implements ManejadorRespuestaCliente {

    private IModeloServidor servidor;
    private IClienteSocket cliente;
    private Map<String, Consumer<Mensaje>> manejadoresEventos;
    private GestorPartidas gestorPartidas;

    public ControladorServidor(IModeloServidor servidor, IClienteSocket cliente, Map<String, Consumer<Mensaje>> mapa) {
        this.servidor = servidor;
        this.cliente = cliente;
        this.manejadoresEventos = mapa;
        this.gestorPartidas = GestorPartidas.getInstancia();

        mapa.put("DISPARO", this::realizarDisparo);
        mapa.put("ADD_NAVE", this::addNave);
        mapa.put("CREAR_PARTIDA", this::manejarCrearPartida);
        mapa.put("UNIRSE_PARTIDA", this::manejarUnirsePartida);
        mapa.put("VALIDAR_CODIGO", this::manejarValidarCodigo);
        mapa.put("ABANDONAR_PARTIDA", this::manejarAbandonarPartidaSv);
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

        List<Coordenadas> coordenadas = dto.getCoordenadases();
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

        Coordenadas coordenadas = disparoDTO.getCoordenadas();
        JugadorDTO jugadorDTO = disparoDTO.getJugador();
        Jugador jugador = new Jugador(
                jugadorDTO.getNombre(),
                jugadorDTO.getColor(),
                jugadorDTO.getEstado()
        );

        Disparo disparo = servidor.realizarDisparo(coordenadas, jugador, disparoDTO.getTiempo());

        PuntajeDTO puntajeDTO = null;
        Jugador jugadorConPuntaje = servidor.getJugadores().stream()
                .filter(j -> j.getNombre().equals(jugador.getNombre()))
                .findFirst()
                .orElse(null);

        if (jugadorConPuntaje != null && jugadorConPuntaje.getPuntaje() != null) {
            Puntaje p = jugadorConPuntaje.getPuntaje();
            puntajeDTO = new PuntajeDTO(
                    p.getPuntosTotales(),
                    p.getDisparosAcertados(),
                    p.getDisparosFallados(),
                    p.getNavesHundidas(),
                    p.getPrecision()
            );
        }

        DisparoDTO resultado = new DisparoDTO(
                new JugadorDTO(
                        disparo.getJugador().getNombre(),
                        disparo.getJugador().getColor(),
                        disparo.getJugador().getEstado()),
                disparo.getCoordenadas(),
                disparo.getResultadoDisparo(),
                disparo.getEstadoPartida()
        );

        resultado.setPuntaje(puntajeDTO);

        enviarMensaje("RESULTADO_DISPARO", resultado);
    }

    /**
     * Maneja la creacion de una nueva partida en el servidor.
     */
    private void manejarCrearPartida(Mensaje mensaje) {
        System.out.println("Servidor: Recibio 'CREAR_PARTIDA'.");
        Gson gson = new Gson();
        CrearPartidaDTO solicitud = gson.fromJson(mensaje.getData(), CrearPartidaDTO.class);

        // Validar jugador
        ValidadorJugador.ResultadoValidacion validacion =
            ValidadorJugador.validarJugadorDTO(solicitud.getJugador());

        if (!validacion.isValido()) {
            CrearPartidaDTO respuesta = new CrearPartidaDTO(false, validacion.getMensaje());
            enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(),
                "RESULTADO_CREAR_PARTIDA", respuesta);
            return;
        }

        String codigo = solicitud.getCodigoPartida();

        // Si no viene codigo, generar uno nuevo
        if (codigo == null || codigo.isEmpty()) {
            codigo = gestorPartidas.generarCodigo();
        }

        // Verificar que el codigo no exista
        if (gestorPartidas.existePartida(codigo)) {
            CrearPartidaDTO respuesta = new CrearPartidaDTO(false,
                "El codigo de partida ya existe. Intenta de nuevo.");
            enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(),
                "RESULTADO_CREAR_PARTIDA", respuesta);
            return;
        }

        // Crear la partida usando el Builder existente
        Director director = new Director();
        Partida nuevaPartida = (Partida) director.makePartida(new PartidaBuilder());

        // Registrar la partida
        if (!gestorPartidas.registrarPartida(codigo, nuevaPartida)) {
            CrearPartidaDTO respuesta = new CrearPartidaDTO(false,
                "Error al registrar la partida en el servidor.");
            enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(),
                "RESULTADO_CREAR_PARTIDA", respuesta);
            return;
        }

        // Agregar el jugador creador a la partida
        JugadorDTO jugadorDTO = solicitud.getJugador();
        Jugador jugador = new Jugador(
            jugadorDTO.getNombre(),
            jugadorDTO.getColor(),
            jugadorDTO.getEstado()
        );
        nuevaPartida.addJugador(jugador);

        // Enviar respuesta exitosa
        CrearPartidaDTO respuesta = new CrearPartidaDTO(codigo, jugadorDTO);
        respuesta.setExito(true);
        respuesta.setMensaje("Partida creada exitosamente con codigo: " + codigo);

        enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(),
            "RESULTADO_CREAR_PARTIDA", respuesta);

        System.out.println("Servidor: Partida creada con codigo " + codigo);
    }

    /**
     * Valida si un codigo de partida existe y puede recibir jugadores.
     */
    private void manejarValidarCodigo(Mensaje mensaje) {
        System.out.println("Servidor: Recibio 'VALIDAR_CODIGO'.");
        Gson gson = new Gson();
        UnirsePartidaDTO solicitud = gson.fromJson(mensaje.getData(), UnirsePartidaDTO.class);

        String codigo = solicitud.getCodigoPartida();
        UnirsePartidaDTO respuesta = new UnirsePartidaDTO();
        respuesta.setCodigoPartida(codigo);

        // Validar formato del codigo
        if (!gestorPartidas.validarFormatoCodigo(codigo)) {
            respuesta.setResultado(ResultadoUnirsePartida.CODIGO_INVALIDO);
            respuesta.setMensaje("El codigo debe tener 5 caracteres alfanumericos.");
            enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(),
                "RESULTADO_VALIDAR_CODIGO", respuesta);
            return;
        }

        // Verificar si existe la partida
        if (!gestorPartidas.existePartida(codigo)) {
            respuesta.setResultado(ResultadoUnirsePartida.PARTIDA_NO_ENCONTRADA);
            respuesta.setMensaje("No se encontro una partida con el codigo: " + codigo);
            enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(),
                "RESULTADO_VALIDAR_CODIGO", respuesta);
            return;
        }

        // Verificar si puede unirse
        if (!gestorPartidas.puedeUnirse(codigo)) {
            respuesta.setResultado(ResultadoUnirsePartida.PARTIDA_LLENA);
            respuesta.setMensaje("La partida ya tiene 2 jugadores.");
            enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(),
                "RESULTADO_VALIDAR_CODIGO", respuesta);
            return;
        }

        // Codigo valido
        respuesta.setResultado(ResultadoUnirsePartida.EXITO);
        respuesta.setMensaje("Codigo valido. Puedes unirte a la partida.");
        enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(),
            "RESULTADO_VALIDAR_CODIGO", respuesta);
    }

    private void manejarUnirsePartida(Mensaje mensaje) {
        System.out.println("Servidor: Recibio 'UNIRSE_PARTIDA'.");
        Gson gson = new Gson();
        UnirsePartidaDTO solicitud = gson.fromJson(mensaje.getData(), UnirsePartidaDTO.class);

        String codigo = solicitud.getCodigoPartida();
        JugadorDTO jugadorDTO = solicitud.getJugador();

        UnirsePartidaDTO respuesta = new UnirsePartidaDTO();
        respuesta.setCodigoPartida(codigo);
        respuesta.setJugador(jugadorDTO);

        // Validar jugador
        ValidadorJugador.ResultadoValidacion validacion =
            ValidadorJugador.validarJugadorDTO(jugadorDTO);

        if (!validacion.isValido()) {
            respuesta.setResultado(ResultadoUnirsePartida.ERROR_SERVIDOR);
            respuesta.setMensaje(validacion.getMensaje());
            enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(),
                "RESULTADO_UNIRSE_PARTIDA", respuesta);
            return;
        }

        // Verificar que exista la partida
        Partida partida = gestorPartidas.obtenerPartida(codigo);
        if (partida == null) {
            respuesta.setResultado(ResultadoUnirsePartida.PARTIDA_NO_ENCONTRADA);
            respuesta.setMensaje("No se encontro la partida con codigo: " + codigo);
            enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(),
                "RESULTADO_UNIRSE_PARTIDA", respuesta);
            return;
        }

        // Verificar que no este llena
        if (partida.getJugadores().size() >= 2) {
            respuesta.setResultado(ResultadoUnirsePartida.PARTIDA_LLENA);
            respuesta.setMensaje("La partida ya tiene 2 jugadores.");
            enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(),
                "RESULTADO_UNIRSE_PARTIDA", respuesta);
            return;
        }

        // Verificar nombre duplicado
        boolean nombreDuplicado = partida.getJugadores().stream()
            .anyMatch(j -> j.getNombre().equalsIgnoreCase(jugadorDTO.getNombre()));

        if (nombreDuplicado) {
            respuesta.setResultado(ResultadoUnirsePartida.NOMBRE_DUPLICADO);
            respuesta.setMensaje("Ya hay un jugador con ese nombre en la partida.");
            enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(),
                "RESULTADO_UNIRSE_PARTIDA", respuesta);
            return;
        }

        // Agregar jugador a la partida
        Jugador jugador = new Jugador(
            jugadorDTO.getNombre(),
            jugadorDTO.getColor(),
            jugadorDTO.getEstado()
        );
        partida.addJugador(jugador);

        // Respuesta exitosa
        respuesta.setResultado(ResultadoUnirsePartida.EXITO);
        respuesta.setMensaje("Te uniste exitosamente a la partida.");

        // Notificar al cliente que se unio
        enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(),
            "RESULTADO_UNIRSE_PARTIDA", respuesta);

        // Notificar a TODOS los suscritos sobre CADA jugador de la partida
        // Esto asegura que todos los clientes tengan la lista completa
        for (Jugador j : partida.getJugadores()) {
            JugadorDTO jDTO = new JugadorDTO(j.getNombre(), j.getColor(), j.getEstado());
            enviarMensaje("JUGADOR_UNIDO", jDTO);
        }

        System.out.println("Servidor: " + jugadorDTO.getNombre() + " se unio a partida " + codigo);
        System.out.println("Servidor: Total jugadores en partida: " + partida.getJugadores().size());
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
    }

}
