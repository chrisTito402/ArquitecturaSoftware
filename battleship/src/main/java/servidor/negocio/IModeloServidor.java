package servidor.negocio;

import java.util.List;
import compartido.entidades.Coordenadas;
import compartido.entidades.Disparo;
import compartido.entidades.Jugador;
import compartido.entidades.Nave;
import compartido.enums.ResultadoAddNave;

/**
 *
 * @author daniel
 */
public interface IModeloServidor {

    public Disparo realizarDisparo(Coordenadas coordenadas, Jugador jugador, long tiempo);

    public ResultadoAddNave addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas);

    public void addJugador(Jugador j);

    public List<Jugador> getJugadores();

    public void unirsePartida(Jugador jugador);

    public void empezarPartida();

    public void abandonarPartida(Jugador jugadorQueSeVa);
}
