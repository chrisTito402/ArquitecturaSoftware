package controllers.controller;

//import views.DTOs.CoordenadasDTO;
//import views.DTOs.JugadorDTO;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.builder.IPartidaBuilder;
import java.util.List;
import models.entidades.Coordenadas;

/**
 *
 * @author daniel
 */
public interface IControlador {
    String crearPartida(IPartidaBuilder builder, Jugador j);
    void realizarDisparo(Coordenadas c, Jugador j);
    boolean addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas);
    void addJugador(Jugador j);
    void crearTableros();
}
