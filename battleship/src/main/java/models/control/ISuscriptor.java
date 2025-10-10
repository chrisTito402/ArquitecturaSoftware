package models.control;

import models.entidades.Disparo;
import models.enums.EstadoPartida;

/**
 *
 * @author daniel
 */
public interface ISuscriptor {
    
    public void notificar(Disparo disparo, EstadoPartida ePartida);
}
