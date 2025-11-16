package views.DTOs;

import models.entidades.Casilla;

/**
 *
 * @author daniel
 */
public class TableroDTO {
    
    private Casilla[][] casillas;
    private int limiteX;
    private int limiteY;

    public TableroDTO() {
    }

    public TableroDTO(Casilla[][] casillas, int limiteX, int limiteY) {
        this.casillas = casillas;
        this.limiteX = limiteX;
        this.limiteY = limiteY;
    }

    public Casilla[][] getCasillas() {
        return casillas;
    }

    public void setCasillas(Casilla[][] casillas) {
        this.casillas = casillas;
    }

    public int getLimiteX() {
        return limiteX;
    }

    public void setLimiteX(int limiteX) {
        this.limiteX = limiteX;
    }

    public int getLimiteY() {
        return limiteY;
    }

    public void setLimiteY(int limiteY) {
        this.limiteY = limiteY;
    }

    @Override
    public String toString() {
        return "TableroDTO{" + "casillas=" + casillas + ", limiteX=" + limiteX + ", limiteY=" + limiteY + '}';
    }
    
}
