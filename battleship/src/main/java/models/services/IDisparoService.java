package models.services;

import models.entidades.Coordenadas;
import models.entidades.Disparo;
import models.entidades.Jugador;
import models.entidades.Partida;

public interface IDisparoService {

    Disparo realizarDisparo(Partida partida, Jugador jugador, Coordenadas coordenadas, long tiempo);

    boolean esTurnoValido(Partida partida, Jugador jugador);

    boolean sonCoordenadasValidas(Coordenadas coordenadas, int limiteX, int limiteY);
}
