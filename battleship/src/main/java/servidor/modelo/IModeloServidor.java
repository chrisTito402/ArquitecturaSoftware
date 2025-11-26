package servidor.modelo;

import java.util.List;
import models.entidades.Coordenadas;
import models.entidades.Disparo;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.enums.ResultadoAddNave;

/**
 *
 * @author daniel
 */
public interface IModeloServidor {
    Disparo realizarDisparo(Coordenadas coordenadas, Jugador jugador, long tiempo);
    ResultadoAddNave addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas);
    void addJugador(Jugador j);
    List<Jugador> getJugadores();
    void unirsePartida(Jugador jugador);
    void crearTableros();
    void empezarPartida();
    void abandonarPartida(Jugador jugadorQueSeVa);
    long getTiempoRestante();
    Jugador getTurnoActual();
    void cambiarTurno();
}
