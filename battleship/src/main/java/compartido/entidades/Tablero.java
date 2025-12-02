package compartido.entidades;

import compartido.enums.EstadoCasilla;
import compartido.enums.EstadoNave;
import compartido.enums.ResultadoDisparo;
import java.util.List;

/**
 *
 * @author daniel
 */
public class Tablero {

    private Casilla[][] casillas;
    private int limiteX;
    private int limiteY;

    public Tablero(Casilla[][] casillas, int limiteX, int limiteY) {
        this.casillas = casillas;
        this.limiteX = limiteX;
        this.limiteY = limiteY;
    }

    public ResultadoDisparo realizarDisparo(Coordenadas c) {
        if (c.getX() >= limiteX || c.getX() < 0 || c.getY() >= limiteY || c.getY() < 0) {
            System.out.println("Error: coordenada fuera de rango (" + c.getX() + ", " + c.getY() + ")");
            return null;
        }

        Casilla casilla = casillas[c.getX()][c.getY()];
        Nave nave = casilla.getNave();
        EstadoCasilla eC = casilla.getEstado();

        // Verificar si ya se disparó a esta casilla
        if (eC == EstadoCasilla.YA_DISPARADO) {
            return ResultadoDisparo.YA_DISPARADO;
        }

        // Marcar la casilla como ya disparada
        casilla.setEstado(EstadoCasilla.YA_DISPARADO);

        // Si hay una nave en la casilla
        if (nave != null) {
            EstadoNave estadoNave = nave.addDisparo();

            if (estadoNave == EstadoNave.HUNDIDO) {
                return ResultadoDisparo.HUNDIMIENTO;
            } else {
                return ResultadoDisparo.IMPACTO;
            }
        }

        // No hay nave, es agua
        return ResultadoDisparo.AGUA;
    }

    public boolean addNave(Nave nave, List<Coordenadas> coordenadas) {
        coordenadas.forEach(c -> casillas[c.getX()][c.getY()].setNave(nave));

        return true;
    }

    public Casilla[][] getCasillas() {
        return casillas;
    }

    public int getLimiteX() {
        return limiteX;
    }

    public int getLimiteY() {
        return limiteY;
    }

    /**
     * Obtiene la nave ubicada en las coordenadas especificadas.
     * @param c Coordenadas a consultar
     * @return La nave en esa posicion, o null si no hay nave
     */
    public Nave getNaveEnCoordenadas(Coordenadas c) {
        if (c.getX() >= limiteX || c.getX() < 0 || c.getY() >= limiteY || c.getY() < 0) {
            return null;
        }
        return casillas[c.getX()][c.getY()].getNave();
    }

}
