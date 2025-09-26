package Entidades;

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
    
}
