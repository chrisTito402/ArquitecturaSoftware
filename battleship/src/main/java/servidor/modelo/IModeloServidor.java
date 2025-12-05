package servidor.modelo;

import java.util.List;
import models.entidades.AddNave;
import models.entidades.Coordenadas;
import models.entidades.Disparo;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.enums.ResultadoAddJugador;
import models.enums.ResultadoConfirmarNaves;
import models.enums.ResultadoEmpezarPartida;

/**
 *
 * @author daniel
 */
public interface IModeloServidor {

    public Disparo realizarDisparo(Coordenadas coordenadas, Jugador jugador, long tiempo);

    public AddNave addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas);

    public ResultadoConfirmarNaves setConfirmarNaves(Jugador jugador);

    public List<Jugador> getJugadores();

    public ResultadoAddJugador unirsePartida(Jugador jugador);

    public ResultadoEmpezarPartida empezarPartida();

    public Jugador abandonarPartida(Jugador jugadorQueSeVa);
    
    public Jugador obtenerJugadorEnemigo(Jugador jugador);
    
}
