package controllers.controller;

import java.util.List;
import models.entidades.Coordenadas;
import models.enums.ColorJugador;
import models.enums.OrientacionNave;
import views.DTOs.TipoNaveDTO;

/**
 *
 * @author daniel
 */
public interface IControlador {

    public void realizarDisparo(Coordenadas c);

    public void addNave(TipoNaveDTO tipo, OrientacionNave orientacion, List<Coordenadas> coordenadas);
    
    public void setConfirmarNaves();

    public void abandonarPartida();

    // Caso de Uso: Unirse Partida
    public void unirsePartida(String nombre, ColorJugador color);

    public void empezarPartida();

    public void abandonarLobby();
    
    public void obtenerJugadorEnemigo();
    
    public void obtenerMarcador();

}
