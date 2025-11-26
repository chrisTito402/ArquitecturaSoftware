package controllers.controller;

import buseventos.EventoJuego;
import buseventos.Mensaje;
import buseventos.util.MensajeriaHelper;
import clientesocket.IClienteSocket;
import dtos.AddNaveDTO;
import dtos.CoordenadasDTO;
import dtos.DisparoDTO;
import dtos.JugadorDTO;
import dtos.NaveDTO;
import dtos.TiempoDTO;
import dtos.mappers.CoordenadasMapper;
import dtos.mappers.JugadorMapper;
import dtos.mappers.NaveMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import models.control.ControlModelo;
import models.control.IModeloCliente;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.observador.ISuscriptor;

public class Controlador implements IControlador, ManejadorRespuestaCliente {

    private IModeloCliente modelo;
    private IClienteSocket cliente;
    private Map<String, Consumer<Mensaje>> manejadorEventos;

    public Controlador() {
    }

    public Controlador(IModeloCliente modelo, IClienteSocket cliente, Map<String, Consumer<Mensaje>> mapa) {
        this.modelo = modelo;
        this.cliente = cliente;
        this.manejadorEventos = mapa;

        registrarManejadores();
    }

    private void registrarManejadores() {
        manejadorEventos.put(EventoJuego.RESULTADO_DISPARO.getValor(), this::manejarResultadoDisparo);
        manejadorEventos.put(EventoJuego.JUGADOR_UNIDO.getValor(), this::manejarJugadorUnido);
        manejadorEventos.put(EventoJuego.JUGADOR_ABANDONO.getValor(), this::manejarAbandonarPartida);
        manejadorEventos.put(EventoJuego.TIEMPO_ACTUALIZADO.getValor(), this::manejarTiempoActualizado);
        manejadorEventos.put(EventoJuego.CAMBIO_TURNO.getValor(), this::manejarCambioTurno);
    }

    private void enviarMensaje(String evento, Object datos) {
        if (cliente == null) {
            return;
        }
        String idJugador = obtenerIdJugadorLocal();
        String json = MensajeriaHelper.crearMensajeJSON(evento, datos, idJugador);
        cliente.enviarMensaje(json);
    }

    private void enviarMensaje(EventoJuego evento, Object datos) {
        enviarMensaje(evento.getValor(), datos);
    }

    private String obtenerIdJugadorLocal() {
        if (modelo != null && modelo.getJugadores() != null && !modelo.getJugadores().isEmpty()) {
            Jugador jugadorLocal = modelo.getJugadores().get(0);
            if (jugadorLocal != null && jugadorLocal.getNombre() != null) {
                return jugadorLocal.getNombre();
            }
        }
        return "cliente-" + System.currentTimeMillis();
    }

    @Override
    public void manejarMensaje(String json) {
        Mensaje mensaje = MensajeriaHelper.parsearMensaje(json);
        if (mensaje == null) {
            return;
        }

        Consumer<Mensaje> handler = manejadorEventos.get(mensaje.getEvento());
        if (handler != null) {
            handler.accept(mensaje);
        }
    }

    private void manejarResultadoDisparo(Mensaje mensaje) {
        DisparoDTO disparoDTO = MensajeriaHelper.extraerDatos(mensaje, DisparoDTO.class);
        if (disparoDTO != null) {
            modelo.notificarAllSuscriptores(EventoJuego.RESULTADO_DISPARO.getValor(), disparoDTO);
        }
    }

    public void manejarAbandonarPartida(Mensaje mensaje) {
        JugadorDTO jugadorDTO = MensajeriaHelper.extraerDatos(mensaje, JugadorDTO.class);
        if (jugadorDTO != null) {
            modelo.notificarAllSuscriptores(EventoJuego.ABANDONAR_PARTIDA.getValor(), jugadorDTO);
        }
    }

    private void manejarTiempoActualizado(Mensaje mensaje) {
        TiempoDTO tiempoDTO = MensajeriaHelper.extraerDatos(mensaje, TiempoDTO.class);
        if (tiempoDTO != null) {
            modelo.notificarAllSuscriptores(EventoJuego.TIEMPO_ACTUALIZADO.getValor(), tiempoDTO);
        }
    }

    private void manejarCambioTurno(Mensaje mensaje) {
        DisparoDTO disparoDTO = MensajeriaHelper.extraerDatos(mensaje, DisparoDTO.class);
        if (disparoDTO != null) {
            modelo.notificarAllSuscriptores(EventoJuego.CAMBIO_TURNO.getValor(), disparoDTO);
        }
    }

    @Override
    public void abandonarLobby(Jugador jugador) {
        modelo.abandonarLobby(jugador);
        JugadorDTO dto = JugadorMapper.toDTO(jugador);
        enviarMensaje(EventoJuego.ABANDONAR_PARTIDA, dto);
    }

    @Override
    public String crearPartida(Jugador j) {
        this.modelo = new ControlModelo(new ArrayList<>());
        return "Partida creada correctamente";
    }

    @Override
    public void realizarDisparo(Coordenadas coordenadas) {
        if (coordenadas == null || modelo == null) {
            return;
        }

        Jugador turno = modelo.getTurno();
        if (turno == null) {
            return;
        }

        JugadorDTO jugadorDTO = JugadorMapper.toDTO(turno);
        DisparoDTO disparoDTO = new DisparoDTO(
                jugadorDTO,
                CoordenadasMapper.toDTO(coordenadas),
                null,
                null,
                System.currentTimeMillis()
        );

        enviarMensaje(EventoJuego.DISPARO, disparoDTO);
    }

    private void manejarJugadorUnido(Mensaje mensaje) {
        JugadorDTO jugadorDTO = MensajeriaHelper.extraerDatos(mensaje, JugadorDTO.class);
        if (jugadorDTO != null) {
            modelo.notificarAllSuscriptores(EventoJuego.JUGADOR_UNIDO.getValor(), jugadorDTO);
        }
    }

    @Override
    public boolean addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas) {
        if (jugador == null || nave == null || coordenadas == null || coordenadas.isEmpty()) {
            return false;
        }

        NaveDTO naveDTO = NaveMapper.toDTO(nave);
        JugadorDTO jugadorDTO = JugadorMapper.toDTO(jugador);
        List<CoordenadasDTO> coordenadasDTO = CoordenadasMapper.toDTOList(coordenadas);

        AddNaveDTO addNaveDTO = new AddNaveDTO(
                jugadorDTO,
                naveDTO,
                coordenadasDTO,
                null
        );

        enviarMensaje(EventoJuego.ADD_NAVE, addNaveDTO);
        return true;
    }

    @Override
    public void addJugador(Jugador jugador) {
        modelo.addJugador(jugador);
    }

    @Override
    public void crearTableros() {
        modelo.crearTableros();
    }

    @Override
    public void suscribirAPartida(ISuscriptor suscriptor) {
        modelo.suscribirAPartida(suscriptor);
    }

    @Override
    public void unirsePartida(Jugador jugador) {
        modelo.unirsePartida(jugador);
        JugadorDTO jugadorDTO = JugadorMapper.toDTO(jugador);
        enviarMensaje(EventoJuego.UNIRSE_PARTIDA, jugadorDTO);
    }

    @Override
    public void empezarPartida() {
        modelo.empezarPartida();
    }

    @Override
    public List<Jugador> getJugadores() {
        return modelo.getJugadores();
    }

    @Override
    public JugadorDTO getJugador() {
        Jugador turno = modelo.getTurno();
        if (turno != null) {
            return JugadorMapper.toDTO(turno);
        }
        return null;
    }

    @Override
    public boolean esMiTurno() {
        return modelo != null && modelo.getTurno() != null;
    }
}
