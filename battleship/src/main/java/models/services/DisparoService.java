package models.services;

import models.entidades.Coordenadas;
import models.entidades.Disparo;
import models.entidades.Jugador;
import models.entidades.Partida;
import models.entidades.Tablero;
import models.enums.EstadoJugador;
import models.enums.EstadoNave;
import models.enums.EstadoPartida;
import models.enums.ResultadoDisparo;

public class DisparoService implements IDisparoService {

    private final IPuntajeService puntajeService;

    public DisparoService() {
        this.puntajeService = new PuntajeService();
    }

    public DisparoService(IPuntajeService puntajeService) {
        this.puntajeService = puntajeService;
    }

    @Override
    public Disparo realizarDisparo(Partida partida, Jugador jugador, Coordenadas coordenadas, long tiempo) {
        if (partida == null || jugador == null || coordenadas == null) {
            return new Disparo(jugador, coordenadas, ResultadoDisparo.COORDENADAS_INVALIDAS, EstadoPartida.POR_EMPEZAR);
        }

        if (!esTurnoValido(partida, jugador)) {
            return new Disparo(jugador, coordenadas, ResultadoDisparo.TURNO_INCORRECTO, partida.getEstado());
        }

        Jugador oponente = obtenerOponente(partida, jugador);
        if (oponente == null) {
            return new Disparo(jugador, coordenadas, ResultadoDisparo.COORDENADAS_INVALIDAS, partida.getEstado());
        }

        Tablero tableroOponente = oponente.getTablero();
        if (tableroOponente == null) {
            return new Disparo(jugador, coordenadas, ResultadoDisparo.COORDENADAS_INVALIDAS, partida.getEstado());
        }

        ResultadoDisparo resultado = tableroOponente.realizarDisparo(coordenadas);

        Jugador jugadorActual = partida.getJugadores().stream()
                .filter(j -> j.equals(jugador))
                .findFirst()
                .orElse(null);

        if (jugadorActual != null && jugadorActual.getPuntaje() != null) {
            puntajeService.registrarDisparo(jugadorActual.getPuntaje(), resultado);
        }

        EstadoPartida estadoPartida = partida.getEstado();

        if (resultado == ResultadoDisparo.HUNDIMIENTO) {
            boolean todasHundidas = oponente.getNaves().stream()
                    .allMatch(n -> n.getEstado() == EstadoNave.HUNDIDO);

            if (todasHundidas) {
                estadoPartida = EstadoPartida.FINALIZADA;
                partida.setEstado(estadoPartida);

                if (jugadorActual != null) {
                    jugadorActual.setEstado(EstadoJugador.GANADOR);
                    if (jugadorActual.getPuntaje() != null) {
                        puntajeService.registrarVictoria(jugadorActual.getPuntaje());
                    }
                }
                oponente.setEstado(EstadoJugador.PERDEDOR);
            }
        }

        return new Disparo(jugador, coordenadas, resultado, estadoPartida);
    }

    @Override
    public boolean esTurnoValido(Partida partida, Jugador jugador) {
        if (partida.getTurno() == null || jugador == null) {
            return false;
        }
        return partida.getTurno().equals(jugador);
    }

    @Override
    public boolean sonCoordenadasValidas(Coordenadas coordenadas, int limiteX, int limiteY) {
        if (coordenadas == null) {
            return false;
        }
        return coordenadas.getX() >= 0 && coordenadas.getX() < limiteX
                && coordenadas.getY() >= 0 && coordenadas.getY() < limiteY;
    }

    private Jugador obtenerOponente(Partida partida, Jugador jugador) {
        return partida.getJugadores().stream()
                .filter(j -> !j.equals(jugador))
                .findFirst()
                .orElse(null);
    }
}
