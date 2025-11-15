package models.observador;

import models.entidades.Disparo;
import models.enums.EstadoPartida;

/**
 *
 * @author daniel
 */
public interface ISuscriptor {
    
    public void notificar(Disparo disparo, EstadoPartida ePartida);
    public void notificar(String contexto, Object datos);
}
