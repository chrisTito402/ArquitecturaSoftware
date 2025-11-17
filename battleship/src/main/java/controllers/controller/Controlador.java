package controllers.controller;

import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.builder.Director;
import models.control.IModelo;
import java.util.List;
import models.builder.PartidaBuilder;
import models.observador.ISuscriptor;

public class Controlador implements IControlador {
    
    private IModelo partida;

    public Controlador() {
    }

    public Controlador(IModelo partida) {
        this.partida = partida;
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
    @Override
    public void unirsePartida(Jugador jugador) {
        partida.unirsePartida(jugador);
    }
    
    @Override
    public void empezarPartida() {
        partida.empezarPartida();
    }
    
    @Override
    public void abandonarLobby(Jugador jugador) {
        partida.abandonarLobby(jugador);
    }
}
