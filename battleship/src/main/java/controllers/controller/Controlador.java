package controllers.controller;

import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.builder.Director;
import models.builder.IPartidaBuilder;
import models.control.IModelo;
import java.util.ArrayList;
import java.util.List;

public class Controlador implements IControlador {
    
    private IModelo partida;

    public Controlador(IModelo partida) {
        this.partida = partida;
    }

    @Override
    public String crearPartida(IPartidaBuilder builder, Jugador j) {
        Director director = new Director();
        IModelo modelo = director.makePartida(builder);
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
}
