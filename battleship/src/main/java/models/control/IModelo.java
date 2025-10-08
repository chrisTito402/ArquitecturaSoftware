package models.control;

import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.enums.ResultadoDisparo;
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
