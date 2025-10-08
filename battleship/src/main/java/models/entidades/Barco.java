package models.entidades;

import models.enums.OrientacionNave;

/**
 *
 * @author daniel
 */
public class Barco extends Nave{

    public Barco(OrientacionNave orientacion) {
        super(orientacion);
    }

    @Override
    public int getTamanio() {
        return 1;
    }
    
}
