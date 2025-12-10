package compartido.entidades;

import compartido.enums.OrientacionNave;

/**
 * El submarino, ocupa 2 casillas. Es el que mas hay (4 por jugador).
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public class Submarino extends Nave {

    public Submarino(OrientacionNave orientacion) {
        super(orientacion);
    }

    @Override
    public int getTamanio() {
        return 2;
    }

}
