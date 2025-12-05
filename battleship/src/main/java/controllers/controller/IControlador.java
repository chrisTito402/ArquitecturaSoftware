package controllers.controller;

import models.entidades.Jugador;
import java.util.List;
import models.entidades.Coordenadas;
import models.enums.ColorJugador;
import models.enums.OrientacionNave;
import views.DTOs.JugadorDTO;
import views.DTOs.TipoNaveDTO;

/**
 *
 * @author daniel
 */
public interface IControlador {

    public String crearPartida(Jugador j);

    public void realizarDisparo(Coordenadas c);

    public JugadorDTO getJugador();

    public void addNave(TipoNaveDTO tipo, OrientacionNave orientacion, List<Coordenadas> coordenadas);
    
    public void setConfirmarNaves();

    public void abandonarPartida(Jugador jugador);

    // Caso de Uso: Unirse Partida
    public void unirsePartida(String nombre, ColorJugador color);

    public void empezarPartida();

    public void abandonarLobby(JugadorDTO jugadorDTO);
    
    public void obtenerJugadorEnemigo();
    
    public void obtenerMarcador();

}
