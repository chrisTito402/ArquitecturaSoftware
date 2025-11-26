package models.entidades;

import models.enums.OrientacionNave;
import models.enums.TipoNave;

/**
 * PortaAviones - Nave de tamaño 4
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

    @Override
    public TipoNave getTipo() {
        return TipoNave.PORTAAVIONES;
    }

}