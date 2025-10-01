package control;

import Entidades.Casilla;
import Entidades.Coordenadas;
import Entidades.Jugador;
import Enums.ResultadoDisparo;

/**
 *
 * @author daniel
 */
public interface IModelo {
    
    public ResultadoDisparo realizarDisparo(Coordenadas coordenadas, Jugador jugador);
    
    public Coordenadas getCoordenadasDisparada();
    
    public ResultadoDisparo getResultadoDisparo();
}
