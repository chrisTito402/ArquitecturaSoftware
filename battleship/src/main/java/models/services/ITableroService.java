package models.services;

import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.entidades.Tablero;
import models.enums.ResultadoAddNave;
import java.util.List;

public interface ITableroService {

    Tablero crearTablero(int filas, int columnas);

    ResultadoAddNave colocarNave(Tablero tablero, Jugador jugador, Nave nave, List<Coordenadas> coordenadas, List<Jugador> jugadores);

    boolean tableroCompleto(Jugador jugador, int totalNavesRequeridas);

    void inicializarTablerosJugadores(List<Jugador> jugadores);
}
