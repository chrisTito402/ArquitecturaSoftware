package models.validators;

import models.entidades.Casilla;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.entidades.Tablero;
import models.enums.OrientacionNave;
import models.enums.ResultadoAddNave;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ValidadorNave {

    public ResultadoAddNave validarJugador(Jugador jugador, List<Jugador> jugadores) {
        if (jugador == null || jugador.getNombre() == null || jugador.getNombre().isBlank()) {
            return ResultadoAddNave.JUGADOR_NULL;
        }

        boolean existe = jugadores.stream().anyMatch(e -> e.equals(jugador));

        if (!existe) {
            return ResultadoAddNave.JUGADOR_NO_ENCONTRADO;
        }

        return null;
    }

    public ResultadoAddNave validarNave(Nave nave) {
        if (nave == null) {
            return ResultadoAddNave.NAVE_NULL;
        }
        return null;
    }

    public ResultadoAddNave validarCoordenadas(List<Coordenadas> coordenadas) {
        if (coordenadas == null || coordenadas.isEmpty()) {
            return ResultadoAddNave.COORDENADAS_NULL;
        }
        return null;
    }

    public ResultadoAddNave validarTamanioCoordenadas(Nave nave, List<Coordenadas> coordenadas) {
        if (coordenadas.size() != nave.getTamanio()) {
            return ResultadoAddNave.COORDENADAS_EXTRA;
        }
        return null;
    }

    public ResultadoAddNave validarLimites(Tablero tablero, List<Coordenadas> coordenadas) {
        for (Coordenadas coordenada : coordenadas) {
            if (coordenada.getY() < 0 || coordenada.getY() >= tablero.getLimiteY()
                    || coordenada.getX() < 0 || coordenada.getX() >= tablero.getLimiteX()) {
                return ResultadoAddNave.COORDENADAS_FUERA_LIMITE;
            }
        }
        return null;
    }

    /**
     * Valida que la nave tenga la orientación correcta según sus coordenadas.
     *
     * VERTICAL: Las coordenadas deben estar en la misma columna (Y constante),
     *           variando en X (filas).
     * HORIZONTAL: Las coordenadas deben estar en la misma fila (X constante),
     *             variando en Y (columnas).
     */
    public ResultadoAddNave validarOrientacion(Nave nave, List<Coordenadas> coordenadas) {
        if (nave.getOrientacion() == OrientacionNave.VERTICAL) {
            // VERTICAL: misma columna (Y constante), diferentes filas (X varía)
            int y = coordenadas.getFirst().getY();
            for (Coordenadas coordenada : coordenadas) {
                if (coordenada.getY() != y) {
                    return ResultadoAddNave.NO_ORDENADA_VERTICALMENTE;
                }
            }
        } else if (nave.getOrientacion() == OrientacionNave.HORIZONTAL) {
            // HORIZONTAL: misma fila (X constante), diferentes columnas (Y varía)
            int x = coordenadas.getFirst().getX();
            for (Coordenadas coordenada : coordenadas) {
                if (coordenada.getX() != x) {
                    return ResultadoAddNave.NO_ORDENADA_HORIZONTALMENTE;
                }
            }
        }
        return null;
    }

    /**
     * Valida que las coordenadas sean consecutivas según la orientación.
     *
     * VERTICAL: Las coordenadas X deben ser consecutivas (0,1,2,3...)
     * HORIZONTAL: Las coordenadas Y deben ser consecutivas (0,1,2,3...)
     */
    public ResultadoAddNave validarConsecutividad(Nave nave, List<Coordenadas> coordenadas) {
        List<Coordenadas> coordenadasOrdenadas = new ArrayList<>(coordenadas);

        if (nave.getOrientacion() == OrientacionNave.VERTICAL) {
            coordenadasOrdenadas.sort(Comparator.comparingInt(Coordenadas::getX));
            for (int i = 1; i < coordenadasOrdenadas.size(); i++) {
                if (coordenadasOrdenadas.get(i).getX() != coordenadasOrdenadas.get(i - 1).getX() + 1) {
                    return ResultadoAddNave.NO_CONSECUTIVO_X;
                }
            }
        } else if (nave.getOrientacion() == OrientacionNave.HORIZONTAL) {
            coordenadasOrdenadas.sort(Comparator.comparingInt(Coordenadas::getY));
            for (int i = 1; i < coordenadasOrdenadas.size(); i++) {
                if (coordenadasOrdenadas.get(i).getY() != coordenadasOrdenadas.get(i - 1).getY() + 1) {
                    return ResultadoAddNave.NO_CONSECUTIVO_Y;
                }
            }
        }
        return null;
    }

    public ResultadoAddNave validarEspacioLibre(Tablero tablero, Nave nave, List<Coordenadas> coordenadas) {
        Casilla[][] casillas = tablero.getCasillas();
        int limiteX = tablero.getLimiteX();
        int limiteY = tablero.getLimiteY();

        for (Coordenadas c : coordenadas) {
            for (int i = c.getX() - 1; i <= c.getX() + 1; i++) {
                for (int k = c.getY() - 1; k <= c.getY() + 1; k++) {
                    if (i >= 0 && k >= 0 && i < limiteX && k < limiteY) {
                        Nave n = casillas[i][k].getNave();
                        if (n != null && n != nave) {
                            return ResultadoAddNave.ESPACIO_YA_OCUPADO;
                        }
                    }
                }
            }
        }
        return null;
    }

    public ResultadoAddNave validarCompleto(Jugador jugador, List<Jugador> jugadores,
                                            Nave nave, List<Coordenadas> coordenadas, Tablero tablero) {
        ResultadoAddNave resultado;

        resultado = validarJugador(jugador, jugadores);
        if (resultado != null) return resultado;

        resultado = validarNave(nave);
        if (resultado != null) return resultado;

        resultado = validarCoordenadas(coordenadas);
        if (resultado != null) return resultado;

        resultado = validarTamanioCoordenadas(nave, coordenadas);
        if (resultado != null) return resultado;

        resultado = validarLimites(tablero, coordenadas);
        if (resultado != null) return resultado;

        resultado = validarOrientacion(nave, coordenadas);
        if (resultado != null) return resultado;

        resultado = validarConsecutividad(nave, coordenadas);
        if (resultado != null) return resultado;

        resultado = validarEspacioLibre(tablero, nave, coordenadas);
        if (resultado != null) return resultado;

        return null;
    }
}
