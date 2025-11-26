package models.entidades;

import models.enums.OrientacionNave;
import models.enums.TipoNave;

/**
 * Submarino - Nave de tamaño 2
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

    @Override
    public TipoNave getTipo() {
        return TipoNave.SUBMARINO;
    }

}
