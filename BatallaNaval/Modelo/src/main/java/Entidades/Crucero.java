package Entidades;

import Enums.OrientacionNave;

/**
 *
 * @author daniel
 */
public class Crucero extends Nave{

    public Crucero(OrientacionNave orientacion) {
        super(orientacion);
    }

    @Override
    public int getTamanio() {
        return 3;
    }
    
}