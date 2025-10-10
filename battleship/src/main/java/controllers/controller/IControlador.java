package controllers.controller;

import views.DTOs.CoordenadasDTO;
import views.DTOs.JugadorDTO;
import models.entidades.Jugador;
import models.entidades.Nave;
import java.util.List;
import models.control.ISuscriptor;

/**
 *
 * @author daniel
 */
public interface IControlador {
    public void realizarDisparo(CoordenadasDTO c, JugadorDTO j);
    public String crearPartida(Jugador j);
    public boolean addNave(JugadorDTO jugador, Nave nave, List<CoordenadasDTO> coordenadas);
    public void addJugador(Jugador j);
    public void crearTableros();
    public void suscribirAPartida(ISuscriptor suscriptor);
}
