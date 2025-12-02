package controllers.controller;

import buseventos.Mensaje;
import buseventos.TipoAccion;
import clientesocket.ClienteSocket;
import clientesocket.IClienteSocket;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.ArrayList;
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
import models.enums.ResultadoAddNave;
import servidor.modelo.ServidorManager;
import views.DTOs.AddNaveDTO;
import views.DTOs.JugadorDTO;
import views.DTOs.NaveDTO;

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
        manejadorEventos.put("ACTUALIZAR_LOBBY", this::actualizarLobby);
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
        Gson gson = new Gson();
        AddNaveDTO resultado = gson.fromJson(mensaje.getData(), AddNaveDTO.class);

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
    public void realizarDisparo(Coordenadas coordenadas) {
        DisparoDTO disparo = partida.realizarDisparo(coordenadas);
        if (disparo != null) {
            enviarMensaje("DISPARO", disparo);
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
    public void addNave(NaveDTO nave, List<Coordenadas> coordenadas) {
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
        
        Gson gson = new Gson();
        JsonElement data = gson.toJsonTree(jugador);
        Mensaje mensaje = new Mensaje(TipoAccion.PUBLICAR, "UNIRSE_PARTIDA", data, "ID_CLIENTE");
        String mensajeJSON = gson.toJson(mensaje);
        cliente.enviarMensaje(mensajeJSON);
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
    
    private void actualizarLobby(Mensaje mensaje) {
    Gson gson = new Gson();
    JugadorDTO jugador = gson.fromJson(mensaje.getData(), JugadorDTO.class);
    
    partida.notificarAllSuscriptores("NUEVO_JUGADOR_LOBBY", jugador);
}
}
