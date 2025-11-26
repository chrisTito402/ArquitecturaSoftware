package controllers.controller;

import buseventos.Mensaje;
import buseventos.util.MensajeriaHelper;
import clientesocket.IClienteSocket;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.builder.Director;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import models.builder.PartidaBuilder;
import models.observador.ISuscriptor;
import dtos.DisparoDTO;
import models.control.IModeloCliente;
import models.enums.ColorJugador;
import models.enums.EstadoJugador;
import dtos.JugadorDTO;

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

        manejadorEventos.put("RESULTADO_DISPARO", this::manejarResultadoDisparo);
        manejadorEventos.put("JUGADOR_UNIDO", this::manejarJugadorUnido);
        manejadorEventos.put("JUGADOR_ABANDONO", this::manejarAbandonarPartida);

    }

    private void enviarMensaje(String evento, Object datos) {
        String json = MensajeriaHelper.crearMensajeJSON(evento, datos, "1");
        cliente.enviarMensaje(json);
    }

    @Override
    public void manejarMensaje(String json) {
        Mensaje mensaje = MensajeriaHelper.parsearMensaje(json);

        Consumer<Mensaje> handler = manejadorEventos.get(mensaje.getEvento());
        if (handler != null) {
            handler.accept(mensaje);
        }
    }

    private void manejarResultadoDisparo(Mensaje mensaje) {
        DisparoDTO d = MensajeriaHelper.extraerDatos(mensaje, DisparoDTO.class);
        partida.manejarResultadoDisparo(d);
    }

    public void manejarAbandonarPartida(Mensaje mensaje) {
        JugadorDTO jugadorDTO = MensajeriaHelper.extraerDatos(mensaje, JugadorDTO.class);
        partida.notificarAllSuscriptores("ABANDONO_PARTIDA", jugadorDTO);
    }
    @Override
    public void abandonarLobby(Jugador jugador) {
        partida.abandonarLobby(jugador);
        JugadorDTO dto = new JugadorDTO(jugador.getNombre(), jugador.getColor(), jugador.getEstado());
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
    public void realizarDisparo(Coordenadas coordenadas) {
        DisparoDTO disparo = partida.realizarDisparo(coordenadas);
        if (disparo != null) {
            enviarMensaje("DISPARO", disparo);
        }
    }

    private void manejarJugadorUnido(Mensaje mensaje) {
        JugadorDTO jugadorDTO = MensajeriaHelper.extraerDatos(mensaje, JugadorDTO.class);
        partida.notificarAllSuscriptores("JUGADOR_UNIDO", jugadorDTO);
    }

    @Override
    public boolean addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas) {
        if (jugador == null || nave == null || coordenadas == null || coordenadas.isEmpty()) {
            return false;
        }

        dtos.NaveDTO naveDTO = dtos.mappers.NaveMapper.toDTO(nave);
        JugadorDTO jugadorDTO = dtos.mappers.JugadorMapper.toDTO(jugador);

        dtos.AddNaveDTO addNaveDTO = new dtos.AddNaveDTO(
            jugadorDTO,
            naveDTO,
            coordenadas,
            null
        );

        enviarMensaje("ADD_NAVE", addNaveDTO);
        return true;
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

    // Caso de Uso: Unirse Partida
    @Override
    public void unirsePartida(Jugador jugador) {
        partida.unirsePartida(jugador);

        JugadorDTO jugadorDTO = new JugadorDTO(jugador.getNombre(), jugador.getColor(), jugador.getEstado());
        enviarMensaje("UNIRSE_PARTIDA", jugadorDTO);
    }

    @Override
    public void empezarPartida() {
        partida.empezarPartida();
    }

    @Override
    public List<Jugador> getJugadores() {
        return partida.getJugadores();
    }

    @Override
    public JugadorDTO getJugador() {
        return partida.getJugador();
    }
}
