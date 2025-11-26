package models.entidades;

import models.enums.OrientacionNave;
import models.enums.TipoNave;

/**
 * Barco - Nave de tamaño 1
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

    @Override
    public TipoNave getTipo() {
        return TipoNave.BARCO;
    }

}
