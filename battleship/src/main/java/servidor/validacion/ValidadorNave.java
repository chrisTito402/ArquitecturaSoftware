package servidor.validacion;

import java.util.Comparator;
import java.util.List;
import models.entidades.Casilla;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.entidades.Tablero;
import models.enums.OrientacionNave;
import models.enums.ResultadoAddNave;

/**
 * Servicio de validacion centralizado para naves.
 * Contiene TODA la logica de validacion de negocio para agregar naves.
 * Esta clase elimina la duplicacion entre Partida y ControlModelo.
 *
 * @author Equipo
 */
public class ValidadorNave {

    /**
     * Valida completamente si una nave puede ser agregada al tablero.
     * Esta es la validacion COMPLETA que debe usarse en el servidor.
     *
     * @param jugador jugador que agrega la nave
     * @param nave nave a agregar
     * @param coordenadas coordenadas donde colocar la nave
     * @param jugadores lista de jugadores en la partida
     * @return resultado de la validacion
     */
    public static ResultadoAddNave validarAddNave(Jugador jugador, Nave nave,
            List<Coordenadas> coordenadas, List<Jugador> jugadores) {

        // Validar jugador
        if (jugador == null || jugador.getNombre() == null || jugador.getNombre().isBlank()) {
            System.out.println("Error: Informacion insuficiente del Jugador.");
            return ResultadoAddNave.JUGADOR_NULL;
        }

        // Validar nave
        if (nave == null) {
            System.out.println("Error: Informacion insuficiente de la Nave");
            return ResultadoAddNave.NAVE_NULL;
        }

        // Validar coordenadas
        if (coordenadas == null || coordenadas.isEmpty()) {
            System.out.println("Error: Informacion insuficiente de las Coordenadas");
            return ResultadoAddNave.COORDENADAS_NULL;
        }

        // Buscar al jugador en la lista
        Jugador j = jugadores.stream()
                .filter(e -> e.getNombre().equals(jugador.getNombre()))
                .findFirst()
                .orElse(null);

        if (j == null) {
            System.out.println("Error: No se encontro al Jugador.");
            return ResultadoAddNave.JUGADOR_NO_ENCONTRADO;
        }

        // Validar numero de coordenadas
        if (coordenadas.size() != nave.getTamanio()) {
            System.out.println("Error: Coordenadas extra o insuficientes para la nave.");
            return ResultadoAddNave.COORDENADAS_EXTRA;
        }

        // Validar limites del tablero
        Tablero t = j.getTablero();
        for (Coordenadas coordenada : coordenadas) {
            if (coordenada.getY() < 0 || coordenada.getY() > t.getLimiteY()
                    || coordenada.getX() < 0 || coordenada.getX() > t.getLimiteX()) {
                System.out.println("Error: La nave se sale de los limites del tablero.");
                return ResultadoAddNave.COORDENADAS_FUERA_LIMITE;
            }
        }

        // Validar orientacion
        ResultadoAddNave resultadoOrientacion = validarOrientacion(nave, coordenadas);
        if (resultadoOrientacion != null) {
            return resultadoOrientacion;
        }

        // Ordenar coordenadas
        coordenadas.sort(Comparator.comparingInt(Coordenadas::getX)
                .thenComparingInt(Coordenadas::getY));

        // Validar consecutividad
        ResultadoAddNave resultadoConsecutividad = validarConsecutividad(nave, coordenadas);
        if (resultadoConsecutividad != null) {
            return resultadoConsecutividad;
        }

        // Validar que no haya naves adyacentes
        ResultadoAddNave resultadoEspacio = validarEspacioLibre(nave, coordenadas, t);
        if (resultadoEspacio != null) {
            return resultadoEspacio;
        }

        return ResultadoAddNave.NAVE_AÑADIDA;
    }

    /**
     * Validacion basica para el cliente (solo formato, sin acceso al tablero).
     * Esta validacion se usa en ControlModelo para feedback rapido al usuario.
     *
     * @param nave nave a validar
     * @param coordenadas coordenadas propuestas
     * @param limiteX limite X del tablero
     * @param limiteY limite Y del tablero
     * @return true si pasa las validaciones basicas
     */
    public static boolean validacionBasicaCliente(Nave nave, List<Coordenadas> coordenadas,
            int limiteX, int limiteY) {

        if (nave == null || coordenadas == null || coordenadas.isEmpty()) {
            return false;
        }

        if (coordenadas.size() != nave.getTamanio()) {
            return false;
        }

        for (Coordenadas c : coordenadas) {
            if (c.getX() < 0 || c.getX() > limiteX || c.getY() < 0 || c.getY() > limiteY) {
                return false;
            }
        }

        return true;
    }

    private static ResultadoAddNave validarOrientacion(Nave nave, List<Coordenadas> coordenadas) {
        if (nave.getOrientacion() == OrientacionNave.VERTICAL) {
            int y = coordenadas.get(0).getY();
            for (Coordenadas coordenada : coordenadas) {
                if (coordenada.getY() != y) {
                    System.out.println("Error: La nave no esta ordenada Verticalmente");
                    return ResultadoAddNave.NO_ORDENADA_VERTICLMENTE;
                }
            }
        } else if (nave.getOrientacion() == OrientacionNave.HORIZONTAL) {
            int x = coordenadas.get(0).getX();
            for (Coordenadas coordenada : coordenadas) {
                if (coordenada.getX() != x) {
                    System.out.println("Error: La nave no esta ordenada Horizontalmente.");
                    return ResultadoAddNave.NO_ORDENADA_HORIZONTALMENTE;
                }
            }
        }
        return null;
    }

    private static ResultadoAddNave validarConsecutividad(Nave nave, List<Coordenadas> coordenadas) {
        if (nave.getOrientacion() == OrientacionNave.VERTICAL) {
            for (int i = coordenadas.size() - 1; i > 0; i--) {
                if (coordenadas.get(i - 1).getX() != coordenadas.get(i).getX() - 1) {
                    System.out.println("Error: Coordenadas no consecutivas en 'X'");
                    return ResultadoAddNave.NO_CONSECUTIVO_X;
                }
            }
        } else if (nave.getOrientacion() == OrientacionNave.HORIZONTAL) {
            for (int i = coordenadas.size() - 1; i > 0; i--) {
                if (coordenadas.get(i - 1).getY() != coordenadas.get(i).getY() - 1) {
                    System.out.println("Error: Coordenadas no consecutivas en 'Y'");
                    return ResultadoAddNave.NO_CONSECUTIVO_Y;
                }
            }
        }
        return null;
    }

    private static ResultadoAddNave validarEspacioLibre(Nave nave, List<Coordenadas> coordenadas, Tablero t) {
        Casilla[][] casillas = t.getCasillas();
        for (Coordenadas c : coordenadas) {
            for (int i = c.getX() - 1; i < c.getX() + 2; i++) {
                for (int k = c.getY() - 1; k < c.getY() + 2; k++) {
                    if (i >= 0 && k >= 0 && i < casillas.length && k < casillas[0].length) {
                        Nave n = casillas[i][k].getNave();
                        if (n != null && n != nave) {
                            System.out.println("Error: Nave encima de otra o adyacente.");
                            return ResultadoAddNave.ESPACIO_YA_OCUPADO;
                        }
                    }
                }
            }
        }
        return null;
    }
}
