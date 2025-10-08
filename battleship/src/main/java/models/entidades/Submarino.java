package models.entidades;

import models.enums.OrientacionNave;

/**
 *
 * @author daniel
 */
public class Submarino extends Nave{

    public Submarino(OrientacionNave orientacion) {
        super(orientacion);
    }

    @Override
    public int getTamanio() {
        return 2;
    }
    
}
