package controllers.controller;

import views.DTOs.CoordenadasDTO;
import views.DTOs.JugadorDTO;
import models.entidades.Jugador;
import models.entidades.Nave;
import views.builder.IPartidaBuilder;
import java.util.List;

/**
 *
 * @author daniel
 */
public interface IControlador {
    public void realizarDisparo(CoordenadasDTO c, JugadorDTO j);
    public String crearPartida(IPartidaBuilder builder, Jugador j);
    public boolean addNave(JugadorDTO jugador, Nave nave, List<CoordenadasDTO> coordenadas);
    public void addJugador(Jugador j);
    public void crearTableros();
}
