package compartido.entidades;

import compartido.enums.OrientacionNave;

/**
 * El barquito chiquito de 1 sola casilla. Hay 3 por jugador.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public class Barco extends Nave {

    public Barco(OrientacionNave orientacion) {
        super(orientacion);
    }

    @Override
    public int getTamanio() {
        return 1;
    }

}
