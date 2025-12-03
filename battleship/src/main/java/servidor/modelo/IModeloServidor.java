package servidor.modelo;

import java.util.List;
import models.entidades.Coordenadas;
import models.entidades.Disparo;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.enums.ResultadoAddNave;
import models.enums.ResultadoConfirmarNaves;

/**
 *
 * @author daniel
 */
public interface IModeloServidor {

    public Disparo realizarDisparo(Coordenadas coordenadas, Jugador jugador, long tiempo);

    public ResultadoAddNave addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas);

    public ResultadoConfirmarNaves setConfirmarNaves(Jugador jugador);
    
    public void addJugador(Jugador j);

    public List<Jugador> getJugadores();

    public void unirsePartida(Jugador jugador);

    public void empezarPartida();

    public void abandonarPartida(Jugador jugadorQueSeVa);
}
