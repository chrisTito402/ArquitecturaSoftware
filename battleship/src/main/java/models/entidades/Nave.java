package models.entidades;

import models.enums.EstadoNave;
import models.enums.OrientacionNave;
import models.enums.TipoNave;

/**
 * Clase abstracta que representa una Nave en el juego Battleship.
 * Define el comportamiento común de todas las naves.
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

    public abstract TipoNave getTipo();
    
    public EstadoNave addDisparo() {
        cantDisparos++;
        if (cantDisparos >= this.getTamanio()) {
            estado = EstadoNave.HUNDIDO;
        } else {
            estado = EstadoNave.AVERIADO;
        }

        return estado;
    }

    public EstadoNave getEstado() {
        return estado;
    }

    public OrientacionNave getOrientacion() {
        return orientacion;
    }
    
}
