package control;

import Entidades.Coordenadas;
import Entidades.Jugador;
import Entidades.Nave;
import Enums.ResultadoDisparo;
import java.util.List;

/**
 *
 * @author daniel
 */
public interface IModelo {
    
    public ResultadoDisparo realizarDisparo(Coordenadas coordenadas, Jugador jugador);
    public boolean addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas);
    public void addJugador(Jugador j);
    public void crearTableros();
    
}
