package models.services;

import models.builder.Director;
import models.builder.PartidaBuilder;
import models.entidades.Jugador;
import models.entidades.Partida;
import models.enums.EstadoJugador;
import models.enums.EstadoPartida;
import models.enums.ResultadoUnirse;
import java.util.Random;

public class PartidaService implements IPartidaService {

    private static final int MAX_JUGADORES = 2;

    @Override
    public Partida crearPartida() {
        Director director = new Director();
        PartidaBuilder builder = new PartidaBuilder();
        director.makePartida(builder);
        return builder.getResult();
    }

    @Override
    public ResultadoUnirse unirsePartida(Partida partida, Jugador jugador) {
        if (jugador == null || jugador.getNombre() == null || jugador.getNombre().isBlank()) {
            return ResultadoUnirse.JUGADOR_INVALIDO;
        }

        if (partida.getEstado() == EstadoPartida.EN_CURSO) {
            return ResultadoUnirse.PARTIDA_EN_CURSO;
        }

        if (partida.getJugadores().size() >= MAX_JUGADORES) {
            return ResultadoUnirse.PARTIDA_LLENA;
        }

        boolean nombreDuplicado = partida.getJugadores().stream()
                .anyMatch(j -> j.getNombre().equalsIgnoreCase(jugador.getNombre()));

        if (nombreDuplicado) {
            return ResultadoUnirse.NOMBRE_DUPLICADO;
        }

        partida.addJugador(jugador);
        return ResultadoUnirse.EXITO;
    }

    @Override
    public void iniciarPartida(Partida partida) {
        if (!puedeIniciar(partida)) {
            return;
        }

        partida.setEstado(EstadoPartida.EN_CURSO);

        Random random = new Random();
        Jugador primerTurno = partida.getJugadores().get(random.nextInt(partida.getJugadores().size()));
        partida.setTurno(primerTurno);

        partida.getJugadores().forEach(j -> j.setEstado(EstadoJugador.JUGANDO));
    }

    @Override
    public void abandonarPartida(Partida partida, Jugador jugadorQueSeVa) {
        Jugador jugadorEnPartida = partida.getJugadores().stream()
                .filter(j -> j.equals(jugadorQueSeVa))
                .findFirst()
                .orElse(null);

        if (jugadorEnPartida == null) {
            return;
        }

        jugadorEnPartida.setEstado(EstadoJugador.ABANDONO);
        partida.getJugadores().removeIf(j -> j.equals(jugadorQueSeVa));

        if (partida.getJugadores().size() == 1) {
            Jugador ganador = partida.getJugadores().get(0);
            ganador.setEstado(EstadoJugador.GANADOR);
            partida.setEstado(EstadoPartida.FINALIZADA);

            if (ganador.getPuntaje() != null) {
                ganador.getPuntaje().sumarVictoria();
            }
        } else if (partida.getJugadores().isEmpty()) {
            partida.setEstado(EstadoPartida.POR_EMPEZAR);
        }
    }

    @Override
    public boolean puedeIniciar(Partida partida) {
        return partida.getJugadores().size() == MAX_JUGADORES
                && partida.getEstado() == EstadoPartida.POR_EMPEZAR;
    }

    @Override
    public Jugador obtenerGanador(Partida partida) {
        if (partida.getEstado() != EstadoPartida.FINALIZADA) {
            return null;
        }

        return partida.getJugadores().stream()
                .filter(j -> j.getEstado() == EstadoJugador.GANADOR)
                .findFirst()
                .orElse(null);
    }
}
