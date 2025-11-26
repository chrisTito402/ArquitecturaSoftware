package controllers.controller;

import buseventos.Mensaje;
import buseventos.TipoAccion;
import clientesocket.ClienteSocket;
import clientesocket.IClienteSocket;
import com.google.gson.Gson;
import java.util.HashMap;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.builder.Director;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import models.builder.PartidaBuilder;
import models.control.ControlModelo;
import models.observador.ISuscriptor;
import views.DTOs.DisparoDTO;
import models.control.IModeloCliente;
import servidor.modelo.ServidorManager;
import views.DTOs.JugadorDTO;

public class Controlador implements IControlador, ManejadorRespuestaCliente {

    private IModeloCliente partida;
    private IClienteSocket cliente;
    private Map<String, Consumer<Mensaje>> manejadorEventos;

    private ControlModelo modelo;
    private ControlVista vista;
    private ClienteSocket clienteS;

    public Controlador() {
    }

    public Controlador(IModeloCliente partida, IClienteSocket cliente, Map<String, Consumer<Mensaje>> mapa) {
        this.partida = partida;
        this.cliente = cliente;
        this.manejadorEventos = mapa;

        manejadorEventos.put("RESULTADO_DISPARO", this::manejarResultadoDisparo);
        manejadorEventos.put("JUGADOR_UNIDO", this::manejarJugadorUnido);
        manejadorEventos.put("JUGADOR_ABANDONO", this::manejarAbandonarPartida);
        manejadorEventos.put("UNIRSE_PARTIDA", this::manejarUnirsePartida);
        manejadorEventos.put("INICIAR_PARTIDA", this::manejarIniciarPartida);
    }

    public Controlador(ControlModelo modelo, ControlVista vista) {
        this.modelo = modelo;
        this.vista = vista;

        ServidorManager.iniciar();

        this.cliente = new ClienteSocket("localhost", 5000, this);
        clienteS.execute();

        this.manejadorEventos = new HashMap<>();
        registrarEventos();
    }

    private void registrarEventos() {
        manejadorEventos.put("RESULTADO_DISPARO", this::manejarResultadoDisparo);
        manejadorEventos.put("JUGADOR_UNIDO", this::manejarJugadorUnido);
        manejadorEventos.put("JUGADOR_ABANDONO", this::manejarAbandonarPartida);
        manejadorEventos.put("UNIRSE_PARTIDA", this::manejarUnirsePartida);
        manejadorEventos.put("INICIAR_PARTIDA", this::manejarIniciarPartida);
    }

    // Metodo para enviar mensaje por la red.
    private void enviarMensaje(String evento, Object datos) {
        Gson gson = new Gson();
        Mensaje mensaje = new Mensaje(TipoAccion.PUBLICAR, evento, gson.toJsonTree(datos), "1");
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

    //Manda mensaje al servidor
    @Override
    public void abandonarLobby(Jugador jugador) {
        partida.abandonarLobby(jugador);

        //Mandar mensaje al servidor para avisar al ribal
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
        System.out.println("Cliente: Recibido 'JUGADOR_UNIDO'.");
        Gson gson = new Gson();
        JugadorDTO jugadorDTO = gson.fromJson(mensaje.getData(), JugadorDTO.class);

        partida.notificarAllSuscriptores("JUGADOR_UNIDO", jugadorDTO);
    }

    @Override
    public boolean addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas) {
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

    private void manejarUnirsePartida(Mensaje mensaje) {
        JugadorDTO dto = new Gson().fromJson(mensaje.getData(), JugadorDTO.class);
        partida.notificarAllSuscriptores("UNIRSE_PARTIDA", dto);
    }

    private void manejarIniciarPartida(Mensaje mensaje) {
        partida.empezarPartida();
        partida.notificarAllSuscriptores("INICIAR_PARTIDA", null);
    }
}
