package models.builder;

import models.entidades.Casilla;
import models.entidades.Tablero;

/**
 *
 * @author daniel
 */
public class TableroBuilder implements ITableroBuilder {
    
    private Casilla[][] casillas;
    private int limiteX;
    private int limiteY;

    @Override
    public void setCasillas(Casilla[][] casillas) {
        this.casillas = casillas;
    }

    @Override
    public void setLimiteX(int limiteX) {
        this.limiteX = limiteX;
    }

    @Override
    public void setLimiteY(int limiteY) {
        this.limiteY = limiteY;
    }

    public Tablero getResult() {
        return new Tablero(casillas, limiteX, limiteY);
    }
    
}
