package Entidades;

import Enums.EstadoNave;
import Enums.OrientacionNave;

/**
 *
 * @author daniel
 */
public abstract class Nave {
    
    private EstadoNave estado;
    private Integer cantDisparos;
    private OrientacionNave orientacion;
    
    public Nave(OrientacionNave orientacion) {
        this.estado = EstadoNave.SIN_DAÑOS;
        this.cantDisparos = 0;
        this.orientacion = orientacion;
    }
    
    public abstract int getTamanio();
    
    public EstadoNave addDisparo() {
        cantDisparos++;
        if (cantDisparos == 0) {
            estado = EstadoNave.AVERIADO;
        } else {
            if (cantDisparos == this.getTamanio()) {
                estado = EstadoNave.HUNDIDO;
            }
        }
        
        return estado;
    }
    
    
}
