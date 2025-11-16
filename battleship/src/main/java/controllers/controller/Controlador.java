package controllers.controller;

import buseventos.Mensaje;
import clientesocket.IClienteSocket;
import com.google.gson.Gson;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.builder.Director;
import models.control.IModelo;
import java.util.List;
import models.builder.PartidaBuilder;
import models.observador.ISuscriptor;

public class Controlador implements IControlador, ManejadorRespuestaCliente{
    
    private IModelo partida;
    private IClienteSocket cliente;

    public Controlador() {
    }

    public Controlador(IModelo partida) {
        this.partida = partida;
    }

    // Metodo para enviar mensaje por la red.
    private void enviarMensaje(String json) {
        cliente.enviarMensaje(json);
    }
    
    // Metodo para manejar el mensaje recibido por la red.
    @Override
    public void manejarMensaje(String json) {
        Gson gson = new Gson();
        Mensaje mensaje = gson.fromJson(json, Mensaje.class);
        System.out.println((String) mensaje.getData());
    }
    
    @Override
    public String crearPartida(Jugador j) {
        Director d = new Director();
        IModelo modelo = d.makePartida(new PartidaBuilder());
        this.partida = modelo;
        return "Partida creada correctamente";
    }

    @Override
    public void realizarDisparo(Coordenadas coordenadas, Jugador jugador) {
        partida.realizarDisparo(coordenadas, jugador);
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
}
