package controllers.controller;

import views.DTOs.CoordenadasDTO;
import views.DTOs.JugadorDTO;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import views.builder.Director;
import views.builder.IPartidaBuilder;
import models.control.IModelo;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author daniel
 */
public class Controlador implements IControlador{
    
    private IModelo partida;

    public Controlador() {
    }

    public Controlador(IModelo partida) {
        this.partida = partida;
    }

    @Override
    public String crearPartida(IPartidaBuilder builder, Jugador j) {
        Director d = new Director();
        IModelo modelo = d.makePartida(builder);
        this.partida = modelo;
        
        return null;
    }
    
    @Override
    public void realizarDisparo(CoordenadasDTO c, JugadorDTO j) {
        Coordenadas coordenadas = new Coordenadas(
                c.getX(),
                c.getY()
        );
        Jugador jugador = new Jugador(
                j.getNombre(),
                j.getColor(),
                j.getEstado()
        );
        
        partida.realizarDisparo(coordenadas, jugador);
    }
    
    @Override
    public boolean addNave(JugadorDTO jugador, Nave nave, List<CoordenadasDTO> coordenadas) {
        List<Coordenadas> cords = new ArrayList<>();
        coordenadas.forEach(c ->
                cords.add(new Coordenadas(c.getX(), c.getY()))
        );
        Jugador j = new Jugador(
                jugador.getNombre(),
                jugador.getColor(),
                jugador.getEstado()
        );
        
        partida.addNave(j, nave, cords);
        
        return true;
    }

    @Override
    public void addJugador(Jugador j) {
        partida.addJugador(j);
    }

    @Override
    public void crearTableros() {
        
    }
}
