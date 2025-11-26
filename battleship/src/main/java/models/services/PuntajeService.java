/*
 * Servicio para la gestión de puntajes en el juego Battleship.
 * Proporciona operaciones de alto nivel sobre puntajes.
 */
package models.services;

import models.entidades.Jugador;
import models.entidades.Puntaje;
import models.enums.ResultadoDisparo;
import dtos.PuntajeDTO;
import java.util.List;
import java.util.Comparator;

/**
 * Servicio de negocio para gestionar operaciones relacionadas con puntajes.
 *
 * Este servicio forma parte de la Arquitectura en Capas (Capa de Negocio)
 * y proporciona funcionalidades de alto nivel para el Caso de Uso: Gestionar Puntaje.
 *
 * @author Fred
 */
public class PuntajeService {

    /**
     * Registra un disparo y actualiza el puntaje correspondiente.
     *
     * @param puntaje El puntaje a actualizar
     * @param resultado El resultado del disparo
     * @return Los puntos obtenidos en este disparo
     */
    public int registrarDisparo(Puntaje puntaje, ResultadoDisparo resultado) {
        if (puntaje == null) {
            throw new IllegalArgumentException("El puntaje no puede ser null");
        }

        return puntaje.calcularPuntos(resultado);
    }

    /**
     * Registra una victoria y suma el bonus correspondiente.
     *
     * @param puntaje El puntaje del ganador
     */
    public void registrarVictoria(Puntaje puntaje) {
        if (puntaje == null) {
            throw new IllegalArgumentException("El puntaje no puede ser null");
        }

        puntaje.sumarVictoria();
    }

    /**
     * Reinicia el puntaje a cero.
     *
     * @param puntaje El puntaje a reiniciar
     */
    public void reiniciarPuntaje(Puntaje puntaje) {
        if (puntaje == null) {
            throw new IllegalArgumentException("El puntaje no puede ser null");
        }

        puntaje.resetear();
    }

    /**
     * Convierte una entidad Puntaje a un DTO para la capa de presentación.
     *
     * Aplicación de Arquitectura en Capas: convierte entidad del dominio
     * a DTO para la vista, manteniendo separación de responsabilidades.
     *
     * @param puntaje La entidad Puntaje
     * @return El DTO correspondiente
     */
    public PuntajeDTO convertirADTO(Puntaje puntaje) {
        // Delegar al PuntajeMapper para mantener la conversión centralizada
        return dtos.mappers.PuntajeMapper.toDTO(puntaje);
    }

    /**
     * Compara dos puntajes y determina cuál es mayor.
     *
     * @param puntaje1 Primer puntaje
     * @param puntaje2 Segundo puntaje
     * @return Número positivo si puntaje1 > puntaje2, negativo si puntaje1 < puntaje2, 0 si son iguales
     */
    public int compararPuntajes(Puntaje puntaje1, Puntaje puntaje2) {
        if (puntaje1 == null || puntaje2 == null) {
            throw new IllegalArgumentException("Los puntajes no pueden ser null");
        }

        return Integer.compare(puntaje1.getPuntosTotales(), puntaje2.getPuntosTotales());
    }

    /**
     * Determina el jugador con mayor puntaje de una lista.
     *
     * @param jugadores Lista de jugadores
     * @return El jugador con mayor puntaje, o null si la lista está vacía
     */
    public Jugador obtenerJugadorConMayorPuntaje(List<Jugador> jugadores) {
        if (jugadores == null || jugadores.isEmpty()) {
            return null;
        }

        return jugadores.stream()
                .filter(j -> j.getPuntaje() != null)
                .max(Comparator.comparingInt(j -> j.getPuntaje().getPuntosTotales()))
                .orElse(null);
    }

    /**
     * Genera un reporte comparativo de puntajes entre jugadores.
     *
     * @param jugadores Lista de jugadores
     * @return String con el reporte comparativo
     */
    public String generarReporteComparativo(List<Jugador> jugadores) {
        if (jugadores == null || jugadores.isEmpty()) {
            return "No hay jugadores para comparar";
        }

        StringBuilder reporte = new StringBuilder();
        reporte.append("===== REPORTE DE PUNTAJES =====\n\n");

        // Ordenar jugadores por puntaje (mayor a menor)
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
                            "  Precisión: %.2f%%\n" +
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

    /**
     * Calcula el promedio de precisión de una lista de jugadores.
     *
     * @param jugadores Lista de jugadores
     * @return El promedio de precisión, o 0.0 si no hay jugadores
     */
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

    /**
     * Verifica si un jugador tiene un puntaje válido y con datos.
     *
     * @param jugador El jugador a verificar
     * @return true si tiene puntaje válido con datos, false en caso contrario
     */
    public boolean tienePuntajeValido(Jugador jugador) {
        return jugador != null &&
               jugador.getPuntaje() != null &&
               jugador.getPuntaje().tieneDatos();
    }
}
