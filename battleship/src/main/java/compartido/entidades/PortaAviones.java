package compartido.entidades;

import compartido.enums.OrientacionNave;

/**
 * El portaaviones, la nave mas grande (4 casillas).
 * Cada jugador tiene 2 de estos.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public class PortaAviones extends Nave {

    public PortaAviones(OrientacionNave orientacion) {
        super(orientacion);
    }

    @Override
    public int getTamanio() {
        return 4;
    }

}
