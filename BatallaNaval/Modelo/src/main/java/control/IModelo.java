package control;

import Entidades.Coordenadas;
import Entidades.Disparo;
import Entidades.Jugador;
import Enums.ResultadoDisparo;

/**
 *
 * @author daniel
 */
public interface IModelo {
    
    public ResultadoDisparo realizarDisparo(Coordenadas coordenadas, Jugador jugador);
    
}
