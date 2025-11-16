package controllers.controller;

import models.entidades.Jugador;
import models.entidades.Nave;
import models.observador.ISuscriptor;
import java.util.List;
import models.entidades.Coordenadas;

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
    public boolean addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas);
}
