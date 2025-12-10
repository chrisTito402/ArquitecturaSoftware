package servidor.negocio;

import java.util.List;
import compartido.entidades.Coordenadas;
import compartido.entidades.Disparo;
import compartido.entidades.Jugador;
import compartido.entidades.Nave;
import compartido.enums.ResultadoAddNave;

/**
 * Interfaz del modelo en el servidor.
 * Define las operaciones basicas: disparar, agregar naves, unirse, etc.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
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
