package controllers.controller;

import dtos.JugadorDTO;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.observador.ISuscriptor;
import java.util.List;

public interface IControlador {

    String crearPartida(Jugador j);

    void addJugador(Jugador j);

    void crearTableros();

    void suscribirAPartida(ISuscriptor suscriptor);

    void realizarDisparo(Coordenadas c);

    JugadorDTO getJugador();

    boolean addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas);

    void unirsePartida(Jugador jugador);

    void empezarPartida();

    void abandonarLobby(Jugador jugador);

    List<Jugador> getJugadores();

    boolean esMiTurno();
}
