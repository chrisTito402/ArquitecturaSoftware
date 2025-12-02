package cliente.controlador;

import compartido.entidades.Jugador;
import compartido.observador.ISuscriptor;
import java.util.List;
import compartido.comunicacion.dto.CoordenadasDTO;
import compartido.comunicacion.dto.JugadorDTO;
import compartido.comunicacion.dto.NaveDTO;

/**
 * Interfaz del controlador principal del juego Batalla Naval.
 * Define todas las operaciones disponibles para la vista.
 *
 * @author daniel
 */
public interface IControlador {

    // === GESTION DE PARTIDA ===

    public String crearPartida(Jugador j);

    public void unirsePartida(JugadorDTO jugadorDTO);

    public void empezarPartida();

    public void abandonarPartida(Jugador jugador);

    public void abandonarLobby(JugadorDTO jugadorDTO);

    // === GESTION DE JUGADORES ===

    public void addJugador(Jugador j);

    public JugadorDTO getJugador();

    public List<JugadorDTO> getJugadores();

    // === GESTION DE TABLERO Y NAVES ===

    public void crearTableros();

    public void addNave(NaveDTO nave, List<CoordenadasDTO> coordenadas);

    public void confirmarTablero();

    // === BATALLA ===

    public void realizarDisparo(CoordenadasDTO c);

    public boolean esMiTurno();

    public void setTurno(boolean esMiTurno);

    public void notificarTiempoAgotado();

    // === OBSERVADORES ===

    public void suscribirAPartida(ISuscriptor suscriptor);

}
