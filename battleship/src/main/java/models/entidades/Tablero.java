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
        if (c.getX() >= limiteX || c.getX() < 0 || c.getY() >= limiteY || c.getY() < 0) {
            System.out.println("Error: coordenada fuera de rango (" + c.getX() + ", " + c.getY() + ")");
            return null;
        }

        Casilla casilla = casillas[c.getX()][c.getY()];
        Nave nave = casilla.getNave();
        EstadoCasilla eC = casilla.getEstado();

        if (eC == EstadoCasilla.YA_DISPARADO) {
            return ResultadoDisparo.YA_DISPARADO;
        }

        if (nave != null) {
            EstadoNave estadoNave = nave.addDisparo();

            if (estadoNave == EstadoNave.AVERIADO || estadoNave == EstadoNave.SIN_DAÑOS) {
                return ResultadoDisparo.IMPACTO;
            } else if (estadoNave == EstadoNave.HUNDIDO) {
                return ResultadoDisparo.HUNDIMIENTO;
            }
        }

        if (eC == EstadoCasilla.AGUA) {
            return ResultadoDisparo.AGUA;
        }

        casilla.setEstado(EstadoCasilla.YA_DISPARADO);
        
        return ResultadoDisparo.AGUA;
    }
    
    public boolean addNave(Nave nave, List<Coordenadas> coordenadas) {
        coordenadas.forEach(c -> casillas[c.getX()][c.getY()].setNave(nave));
        
        return true;
    }

}
