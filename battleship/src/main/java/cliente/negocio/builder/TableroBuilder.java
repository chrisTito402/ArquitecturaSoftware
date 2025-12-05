package cliente.negocio.builder;

import compartido.entidades.Casilla;
import compartido.entidades.Tablero;

/**
 * Builder para crear el Tablero de 10x10.
 * Le pones las casillas y los limites y con getResult() te da el Tablero.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
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
