package compartido.entidades;

import compartido.enums.OrientacionNave;

/**
 * El crucero, ocupa 3 casillas. Cada jugador tiene 2.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public class Crucero extends Nave {

    public Crucero(OrientacionNave orientacion) {
        super(orientacion);
    }

    @Override
    public int getTamanio() {
        return 3;
    }

}
