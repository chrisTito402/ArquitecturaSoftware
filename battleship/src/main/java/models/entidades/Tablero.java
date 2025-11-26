package models.entidades;

import models.enums.EstadoCasilla;
import models.enums.EstadoNave;
import models.enums.ResultadoDisparo;
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
        // Validar que las coordenadas estén dentro del rango
        if (c.getX() >= limiteX || c.getX() < 0 || c.getY() >= limiteY || c.getY() < 0) {
            System.out.println("Error: coordenada fuera de rango (" + c.getX() + ", " + c.getY() + ")");
            return null;
        }

        Casilla casilla = casillas[c.getX()][c.getY()];
        EstadoCasilla estadoCasilla = casilla.getEstado();

        // Verificar si la casilla ya fue disparada
        if (estadoCasilla == EstadoCasilla.YA_DISPARADO) {
            return ResultadoDisparo.YA_DISPARADO;
        }

        // Marcar la casilla como disparada ANTES de procesar el resultado
        casilla.setEstado(EstadoCasilla.YA_DISPARADO);

        Nave nave = casilla.getNave();

        // Si hay una nave en la casilla, registrar el disparo
        if (nave != null) {
            EstadoNave estadoNave = nave.addDisparo();

            if (estadoNave == EstadoNave.HUNDIDO) {
                return ResultadoDisparo.HUNDIMIENTO;
            } else {
                // AVERIADO o cualquier otro estado
                return ResultadoDisparo.IMPACTO;
            }
        }

        // Si no hay nave, el disparo cayó en agua
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

}
