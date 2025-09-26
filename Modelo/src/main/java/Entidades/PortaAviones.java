package Entidades;

import Enums.OrientacionNave;

/**
 *
 * @author daniel
 */
public class PortaAviones extends Nave{

    public PortaAviones(OrientacionNave orientacion) {
        super(orientacion);
    }

    @Override
    public int getTamanio() {
        return 4;
    }
    
}