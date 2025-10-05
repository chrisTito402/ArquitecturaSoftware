package control;

import Entidades.Disparo;
import Enums.EstadoPartida;

/**
 *
 * @author daniel
 */
public interface IObervable {
    
    public Disparo getDisparo();
    
    public EstadoPartida getEstado();
}
