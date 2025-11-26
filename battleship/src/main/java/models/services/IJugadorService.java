package models.services;

import models.entidades.Jugador;
import models.enums.ColorJugador;
import models.enums.EstadoJugador;

public interface IJugadorService {

    Jugador crearJugador(String nombre, ColorJugador color);

    boolean validarNombre(String nombre);

    void actualizarEstado(Jugador jugador, EstadoJugador nuevoEstado);

    void reiniciarJugador(Jugador jugador);
}
