package controllers.controller;

import models.entidades.Jugador;
import models.observador.ISuscriptor;
import java.util.List;
import models.entidades.Coordenadas;
import views.DTOs.JugadorDTO;
import views.DTOs.NaveDTO;

/**
 *
 * @author daniel
 */
public interface IControlador {
    public String crearPartida(Jugador j);
    public void addJugador(Jugador j);
    public void crearTableros();
    public void suscribirAPartida(ISuscriptor suscriptor);
    public void realizarDisparo(Coordenadas c);
    public JugadorDTO getJugador();
    public void addNave(NaveDTO nave, List<Coordenadas> coordenadas);
    
    // Caso de Uso: Unirse Partida
    public void unirsePartida(Jugador jugador);
    public void empezarPartida();
    public void abandonarLobby(Jugador jugador);
    public List<Jugador> getJugadores();
    
}
