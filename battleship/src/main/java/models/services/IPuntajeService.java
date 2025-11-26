package models.services;

import models.entidades.Jugador;
import models.entidades.Puntaje;
import models.enums.ResultadoDisparo;
import java.util.List;

public interface IPuntajeService {

    int registrarDisparo(Puntaje puntaje, ResultadoDisparo resultado);

    void registrarVictoria(Puntaje puntaje);

    void reiniciarPuntaje(Puntaje puntaje);

    int compararPuntajes(Puntaje puntaje1, Puntaje puntaje2);

    Jugador obtenerJugadorConMayorPuntaje(List<Jugador> jugadores);

    String generarReporteComparativo(List<Jugador> jugadores);

    double calcularPrecisionPromedio(List<Jugador> jugadores);

    boolean tienePuntajeValido(Jugador jugador);
}
