package models.observador;

import models.entidades.Disparo;
import models.enums.EstadoPartida;

/**
 *
 * @author daniel
 */
public interface IObervable {
    
    public Disparo getDisparo();
    
    public EstadoPartida getEstado();
}
