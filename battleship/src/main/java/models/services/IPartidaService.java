package models.services;

import models.entidades.Jugador;
import models.entidades.Partida;
import models.enums.ResultadoUnirse;

public interface IPartidaService {

    Partida crearPartida();

    ResultadoUnirse unirsePartida(Partida partida, Jugador jugador);

    void iniciarPartida(Partida partida);

    void abandonarPartida(Partida partida, Jugador jugador);

    boolean puedeIniciar(Partida partida);

    Jugador obtenerGanador(Partida partida);
}
