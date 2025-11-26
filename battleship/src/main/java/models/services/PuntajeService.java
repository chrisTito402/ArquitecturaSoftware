package models.services;

import models.entidades.Jugador;
import models.entidades.Puntaje;
import models.enums.ResultadoDisparo;
import java.util.Comparator;
import java.util.List;

public class PuntajeService implements IPuntajeService {

    @Override
    public int registrarDisparo(Puntaje puntaje, ResultadoDisparo resultado) {
        if (puntaje == null) {
            throw new IllegalArgumentException("El puntaje no puede ser null");
        }

        return puntaje.calcularPuntos(resultado);
    }

    @Override
    public void registrarVictoria(Puntaje puntaje) {
        if (puntaje == null) {
            throw new IllegalArgumentException("El puntaje no puede ser null");
        }

        puntaje.sumarVictoria();
    }

    @Override
    public void reiniciarPuntaje(Puntaje puntaje) {
        if (puntaje == null) {
            throw new IllegalArgumentException("El puntaje no puede ser null");
        }

        puntaje.resetear();
    }

    @Override
    public int compararPuntajes(Puntaje puntaje1, Puntaje puntaje2) {
        if (puntaje1 == null || puntaje2 == null) {
            throw new IllegalArgumentException("Los puntajes no pueden ser null");
        }

        return Integer.compare(puntaje1.getPuntosTotales(), puntaje2.getPuntosTotales());
    }

    @Override
    public Jugador obtenerJugadorConMayorPuntaje(List<Jugador> jugadores) {
        if (jugadores == null || jugadores.isEmpty()) {
            return null;
        }

        return jugadores.stream()
                .filter(j -> j.getPuntaje() != null)
                .max(Comparator.comparingInt(j -> j.getPuntaje().getPuntosTotales()))
                .orElse(null);
    }

    @Override
    public String generarReporteComparativo(List<Jugador> jugadores) {
        if (jugadores == null || jugadores.isEmpty()) {
            return "No hay jugadores para comparar";
        }

        StringBuilder reporte = new StringBuilder();
        reporte.append("===== REPORTE DE PUNTAJES =====\n\n");

        jugadores.stream()
                .filter(j -> j.getPuntaje() != null)
                .sorted((j1, j2) -> Integer.compare(
                        j2.getPuntaje().getPuntosTotales(),
                        j1.getPuntaje().getPuntosTotales()
                ))
                .forEach(jugador -> {
                    Puntaje p = jugador.getPuntaje();
                    reporte.append(String.format(
                            "Jugador: %s\n" +
                            "  Puntos: %d\n" +
                            "  Precision: %.2f%%\n" +
                            "  Aciertos: %d\n" +
                            "  Fallos: %d\n" +
                            "  Naves Hundidas: %d\n\n",
                            jugador.getNombre(),
                            p.getPuntosTotales(),
                            p.getPrecision(),
                            p.getDisparosAcertados(),
                            p.getDisparosFallados(),
                            p.getNavesHundidas()
                    ));
                });

        reporte.append("===============================");
        return reporte.toString();
    }

    @Override
    public double calcularPrecisionPromedio(List<Jugador> jugadores) {
        if (jugadores == null || jugadores.isEmpty()) {
            return 0.0;
        }

        return jugadores.stream()
                .filter(j -> j.getPuntaje() != null && j.getPuntaje().tieneDatos())
                .mapToDouble(j -> j.getPuntaje().getPrecision())
                .average()
                .orElse(0.0);
    }

    @Override
    public boolean tienePuntajeValido(Jugador jugador) {
        return jugador != null &&
               jugador.getPuntaje() != null &&
               jugador.getPuntaje().tieneDatos();
    }
}
