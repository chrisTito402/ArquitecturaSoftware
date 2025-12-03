package controllers.controller;

import models.entidades.Jugador;
import models.observador.ISuscriptor;
import java.util.List;
import models.entidades.Coordenadas;
import models.enums.OrientacionNave;
import views.DTOs.JugadorDTO;
import views.DTOs.TipoNaveDTO;

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

    public void addNave(TipoNaveDTO tipo, OrientacionNave orientacion, List<Coordenadas> coordenadas);
    
    public void setConfirmarNaves();

    public void abandonarPartida(Jugador jugador);

    // Caso de Uso: Unirse Partida
    public void unirsePartida(JugadorDTO jugadorDTO);

    public void empezarPartida();

    public void abandonarLobby(JugadorDTO jugadorDTO);

    public List<JugadorDTO> getJugadores();

}
