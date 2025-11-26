package models.entidades;

import models.enums.OrientacionNave;
import models.enums.TipoNave;

/**
 * Crucero - Nave de tamaño 3
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

    @Override
    public TipoNave getTipo() {
        return TipoNave.CRUCERO;
    }

}