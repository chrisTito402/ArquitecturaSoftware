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

    /**
     * Crea una partida y la registra en el servidor con el codigo dado.
     */
    public void crearPartidaConCodigo(JugadorDTO jugador, String codigo);

    public void unirsePartida(JugadorDTO jugadorDTO);

    /**
     * Solicita unirse a una partida con un codigo especifico.
     */
    public void unirsePartidaConCodigo(JugadorDTO jugador, String codigo);

    public void empezarPartida();

    public void abandonarPartida(Jugador jugador);

    public void abandonarLobby(JugadorDTO jugadorDTO);

    public void jugadorListo();

    // === GESTION DE JUGADORES ===

    public void addJugador(Jugador j);

    public JugadorDTO getJugador();

    public List<JugadorDTO> getJugadores();

    // === GESTION DE TABLERO Y NAVES ===

    public void crearTableros();

    public void addNave(NaveDTO nave, List<CoordenadasDTO> coordenadas);

    public void limpiarNaves();

    public void confirmarTablero();

    // === BATALLA ===

    public void realizarDisparo(CoordenadasDTO c);

    public boolean esMiTurno();

    public void setTurno(boolean esMiTurno);

    public void notificarTiempoAgotado();

    // === OBSERVADORES ===

    public void suscribirAPartida(ISuscriptor suscriptor);

    public void desuscribirDePartida(ISuscriptor suscriptor);

    // === REINICIO ===

    /**
     * Reinicia el modelo para una nueva partida.
     */
    public void reiniciarModelo();

}
