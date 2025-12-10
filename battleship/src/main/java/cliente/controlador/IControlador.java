package cliente.controlador;

import compartido.entidades.Jugador;
import compartido.observador.ISuscriptor;
import java.util.List;
import compartido.comunicacion.dto.CoordenadasDTO;
import compartido.comunicacion.dto.JugadorDTO;
import compartido.comunicacion.dto.NaveDTO;

/**
 * Interfaz del controlador para el MVC.
 * Aqui definimos todos los metodos que las vistas van a poder usar
 * para comunicarse con el modelo. Tiene de todo: crear partida,
 * unirse, disparar, poner naves, etc.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public interface IControlador {

    public String crearPartida(Jugador j);

    /**
     * Crea partida con codigo.
     */
    public void crearPartidaConCodigo(JugadorDTO jugador, String codigo);

    public void unirsePartida(JugadorDTO jugadorDTO);

    /**
     * Unirse con codigo.
     */
    public void unirsePartidaConCodigo(JugadorDTO jugador, String codigo);

    public void empezarPartida();

    public void abandonarPartida(Jugador jugador);

    public void abandonarLobby(JugadorDTO jugadorDTO);

    public void jugadorListo();

    public void addJugador(Jugador j);

    public JugadorDTO getJugador();

    public List<JugadorDTO> getJugadores();

    public void crearTableros();

    public void addNave(NaveDTO nave, List<CoordenadasDTO> coordenadas);

    public void limpiarNaves();

    public void confirmarTablero();

    public void realizarDisparo(CoordenadasDTO c);

    public boolean esMiTurno();

    public void setTurno(boolean esMiTurno);

    public void notificarTiempoAgotado();

    public void suscribirAPartida(ISuscriptor suscriptor);

    public void desuscribirDePartida(ISuscriptor suscriptor);

    /**
     * Reinicia para nueva partida.
     */
    public void reiniciarModelo();

}
