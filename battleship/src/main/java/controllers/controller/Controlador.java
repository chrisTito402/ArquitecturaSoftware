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

//    private ControlModelo modelo;
//    private ControlVista vista;
//    private ClienteSocket clienteS;

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
        manejadorEventos.put("EMPEZAR_PARTIDA", this::manejarEmpezarPartida);
        manejadorEventos.put("ABANDONAR_LOBBY", this::manejarAbandonarLobby);
        manejadorEventos.put("RESULTADO_ADD_NAVE", this::manejarResultadoAddNave);
    }

//    public Controlador(ControlModelo modelo, ControlVista vista) {
//        this.modelo = modelo;
//        this.vista = vista;
//
//        this.cliente = new ClienteSocket("localhost", 5000, this);
//        clienteS.execute();
//
//        this.manejadorEventos = new HashMap<>();
//        registrarEventos();
//    }

//    private void registrarEventos() {
//        manejadorEventos.put("RESULTADO_DISPARO", this::manejarResultadoDisparo);
//        manejadorEventos.put("JUGADOR_UNIDO", this::manejarJugadorUnido);
//        manejadorEventos.put("JUGADOR_ABANDONO", this::manejarAbandonarPartida);
//        manejadorEventos.put("UNIRSE_PARTIDA", this::manejarUnirsePartida);
//        manejadorEventos.put("INICIAR_PARTIDA", this::manejarIniciarPartida);
//    }

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
//    @Override
//    public void abandonarLobby(Jugador jugador) {
//        partida.abandonarLobby(jugador);
//        //Mandar mensaje al servidor para avisar al ribal
//        JugadorDTO dto = new JugadorDTO(jugador.getNombre(), jugador.getColor(), jugador.getEstado());
//        enviarMensaje("ABANDONAR_PARTIDA", dto);
//    }

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
    
    @Override
    public JugadorDTO getJugador() {
        return partida.getJugador();
    }

    // Caso de Uso: Unirse Partida
    @Override
    public void unirsePartida(Jugador jugador) {
        partida.unirsePartida(jugador);
        JugadorDTO jugadorDTO = new JugadorDTO(jugador.getNombre(), jugador.getColor(), jugador.getEstado());
        enviarMensaje("UNIRSE_PARTIDA", jugadorDTO);
    }
    
    private void manejarUnirsePartida(Mensaje mensaje) {
        JugadorDTO dto = new Gson().fromJson(mensaje.getData(), JugadorDTO.class);
        System.out.println("El jugador " + dto.getNombre() + " se unio a la partida.");
        partida.notificarAllSuscriptores("UNIRSE_PARTIDA", dto);
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
    public void abandonarLobby(Jugador jugador) {
        partida.abandonarLobby(jugador);
        //Mandar mensaje al servidor para avisar al ribal
        JugadorDTO dto = new JugadorDTO(jugador.getNombre(), jugador.getColor(), jugador.getEstado());
        enviarMensaje("ABANDONAR_LOBBY", dto);
    }
    
    private void manejarAbandonarLobby(Mensaje mensaje) {
        JugadorDTO jugadorDTO = new Gson().fromJson(mensaje.getData(), JugadorDTO.class);
        System.out.println("El jugador " + jugadorDTO.getNombre() + " abandono el lobby.");
        partida.notificarAllSuscriptores("ABANDONAR_LOBBY", jugadorDTO);
    }

    @Override
    public List<Jugador> getJugadores() {
        return partida.getJugadores();
    }
}
