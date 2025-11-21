package controllers.controller;

import buseventos.Mensaje;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.observador.ISuscriptor;
import java.util.List;
import models.entidades.Coordenadas;
import views.DTOs.JugadorDTO;

/**
 *
 * @author daniel
 */
public interface IControlador {
    public String crearPartida(Jugador j);
    public void addJugador(Jugador j);
    public void realizarDisparo(Coordenadas c);
    public JugadorDTO getJugador();
    public boolean addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas);
    
    // Caso de Uso: Unirse Partida
    public void unirsePartida(Jugador jugador);
    public void empezarPartida();
    public void abandonarLobby(Jugador jugador);
    
    

}
