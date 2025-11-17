package controllers.controller;

import buseventos.Mensaje;
import buseventos.TipoAccion;
import clientesocket.IClienteSocket;
import com.google.gson.Gson;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.builder.Director;
import java.util.List;
import models.builder.PartidaBuilder;
import models.entidades.Disparo;
import models.observador.ISuscriptor;
import views.DTOs.DisparoDTO;
import models.control.IModeloCliente;
import views.DTOs.JugadorDTO;

public class Controlador implements IControlador, ManejadorRespuestaCliente{
    
    private IModeloCliente partida;
    private IClienteSocket cliente;

    public Controlador() {
    }

    public Controlador(IModeloCliente partida, IClienteSocket cliente) {
        this.partida = partida;
        this.cliente = cliente;
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
        // PROVISIONAL, hay que agregar un patron que maneje los diferentes eventos.
        // Lo que hay ahora es solo para probar el DISPARO
        Gson gson = new Gson();
        Mensaje mensaje = gson.fromJson(json, Mensaje.class);
        DisparoDTO d = gson.fromJson(mensaje.getData(), DisparoDTO.class);
        System.out.println(d.getResultadoDisparo());
        System.out.println(json);
        
        partida.notificarAllSuscriptores("DISPARO", d);
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

    @Override
    public boolean addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas) {
        partida.addNave(jugador, nave, coordenadas);
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
    public void unirsePartida() {
        partida.unirsePartida();
    }
    
    public void empezarPartida() {
        partida.empezarPartida();
    }
    
    public void abandonarLobby() {
        partida.abandonarLobby();
    }

    @Override
    public JugadorDTO getJugador() {
        return partida.getJugador();
    }
}
