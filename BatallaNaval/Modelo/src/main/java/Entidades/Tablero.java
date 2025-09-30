package Entidades;

import Enums.EstadoCasilla;
import Enums.EstadoNave;
import Enums.ResultadoDisparo;

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
        if (c.getX() > limiteX || c.getX() < 0) {
            System.out.println("Error, se excedio el limite del Tablero: " + c.getX());
        }
        if (c.getY() > limiteY || c.getY() < 0) {
            System.out.println("Error, se excedio el limite del Tablero." + c.getY());
        }
        
        Casilla casilla = casillas[c.getX()][c.getY()];
        Nave nave = casilla.getNave();
        EstadoCasilla eC = casilla.getEstado();
        
        if (eC == EstadoCasilla.YA_DISPARADO) {
            return ResultadoDisparo.YA_DISPARADO;
        }
        
        if (nave != null) {
            EstadoNave estadoNave = nave.addDisparo();
            
            if (estadoNave == EstadoNave.AVERIADO) {
                return ResultadoDisparo.IMPACTO;
            }
            if (estadoNave == EstadoNave.HUNDIDO) {
                return ResultadoDisparo.HUNDIMIENTO;
            }
        }
        
        if (eC == EstadoCasilla.AGUA) {
            return ResultadoDisparo.AGUA;
        }
        if (eC == EstadoCasilla.YA_DISPARADO) {
            return ResultadoDisparo.YA_DISPARADO;
        }
        
        return null;
    }
}
