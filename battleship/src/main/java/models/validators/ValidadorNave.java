package models.validators;

import models.entidades.Casilla;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.entidades.Tablero;
import models.enums.OrientacionNave;
import models.enums.ResultadoAddNave;
import java.util.Comparator;
import java.util.List;

public class ValidadorNave {

    public ResultadoAddNave validarJugador(Jugador jugador, List<Jugador> jugadores) {
        if (jugador == null || jugador.getNombre() == null || jugador.getNombre().isBlank()) {
            return ResultadoAddNave.JUGADOR_NULL;
        }

        Jugador j = jugadores.stream()
                .filter(e -> e.getNombre().equals(jugador.getNombre()))
                .findFirst()
                .orElse(null);

        if (j == null) {
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
            if (coordenada.getY() < 0 || coordenada.getY() > tablero.getLimiteY()
                    || coordenada.getX() < 0 || coordenada.getX() > tablero.getLimiteX()) {
                return ResultadoAddNave.COORDENADAS_FUERA_LIMITE;
            }
        }
        return null;
    }

    public ResultadoAddNave validarOrientacion(Nave nave, List<Coordenadas> coordenadas) {
        if (nave.getOrientacion() == OrientacionNave.VERTICAL) {
            int y = coordenadas.getFirst().getY();
            for (Coordenadas coordenada : coordenadas) {
                if (coordenada.getY() != y) {
                    return ResultadoAddNave.NO_ORDENADA_VERTICLMENTE;
                }
            }
        } else if (nave.getOrientacion() == OrientacionNave.HORIZONTAL) {
            int x = coordenadas.getFirst().getX();
            for (Coordenadas coordenada : coordenadas) {
                if (coordenada.getX() != x) {
                    return ResultadoAddNave.NO_ORDENADA_HORIZONTALMENTE;
                }
            }
        }
        return null;
    }

    public ResultadoAddNave validarConsecutividad(Nave nave, List<Coordenadas> coordenadas) {
        coordenadas.sort(Comparator.comparingInt(Coordenadas::getX)
                .thenComparingInt(Coordenadas::getY));

        if (nave.getOrientacion() == OrientacionNave.VERTICAL) {
            for (int i = coordenadas.size() - 1; i >= 0; i--) {
                if (i == 0) {
                    break;
                }
                if (coordenadas.get(i - 1).getX() != coordenadas.get(i).getX() - 1) {
                    return ResultadoAddNave.NO_CONSECUTIVO_X;
                }
            }
        } else if (nave.getOrientacion() == OrientacionNave.HORIZONTAL) {
            for (int i = coordenadas.size() - 1; i >= 0; i--) {
                if (i == 0) {
                    break;
                }
                if (coordenadas.get(i - 1).getY() != coordenadas.get(i).getY() - 1) {
                    return ResultadoAddNave.NO_CONSECUTIVO_Y;
                }
            }
        }
        return null;
    }

    public ResultadoAddNave validarEspacioLibre(Tablero tablero, Nave nave, List<Coordenadas> coordenadas) {
        Casilla[][] casillas = tablero.getCasillas();
        for (Coordenadas c : coordenadas) {
            for (int i = c.getX() - 1; i < c.getX() + 2; i++) {
                for (int k = c.getY() - 1; k < c.getY() + 2; k++) {
                    if (i >= 0 && k >= 0) {
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
