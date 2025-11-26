package models.services;

import models.entidades.Jugador;
import models.entidades.Puntaje;
import models.enums.ColorJugador;
import models.enums.EstadoJugador;
import java.util.ArrayList;

public class JugadorService implements IJugadorService {

    private static final int MIN_NOMBRE_LENGTH = 2;
    private static final int MAX_NOMBRE_LENGTH = 20;

    @Override
    public Jugador crearJugador(String nombre, ColorJugador color) {
        if (!validarNombre(nombre)) {
            return null;
        }

        if (color == null) {
            color = ColorJugador.AZUL;
        }

        Jugador jugador = new Jugador(nombre.trim(), color, EstadoJugador.EN_ESPERA);
        jugador.setNaves(new ArrayList<>());
        jugador.setPuntaje(new Puntaje());

        return jugador;
    }

    @Override
    public boolean validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return false;
        }

        String nombreTrimmed = nombre.trim();
        return nombreTrimmed.length() >= MIN_NOMBRE_LENGTH
                && nombreTrimmed.length() <= MAX_NOMBRE_LENGTH;
    }

    @Override
    public void actualizarEstado(Jugador jugador, EstadoJugador nuevoEstado) {
        if (jugador == null || nuevoEstado == null) {
            return;
        }
        jugador.setEstado(nuevoEstado);
    }

    @Override
    public void reiniciarJugador(Jugador jugador) {
        if (jugador == null) {
            return;
        }

        jugador.setEstado(EstadoJugador.EN_ESPERA);
        jugador.setNaves(new ArrayList<>());
        jugador.setTablero(null);

        if (jugador.getPuntaje() != null) {
            jugador.getPuntaje().resetear();
        } else {
            jugador.setPuntaje(new Puntaje());
        }
    }
}
