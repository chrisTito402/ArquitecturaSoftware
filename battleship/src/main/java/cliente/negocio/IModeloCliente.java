package cliente.negocio;

import compartido.entidades.Jugador;
import java.util.List;
import compartido.enums.ResultadoAddNave;
import compartido.observador.ISuscriptor;
import compartido.comunicacion.dto.AddNaveDTO;
import compartido.comunicacion.dto.CoordenadasDTO;
import compartido.comunicacion.dto.DisparoDTO;
import compartido.comunicacion.dto.JugadorDTO;
import compartido.comunicacion.dto.NaveDTO;
import compartido.comunicacion.dto.TurnoDTO;

/**
 * Interfaz del modelo del cliente para el juego Batalla Naval.
 * Define todas las operaciones del modelo accesibles desde el controlador.
 *
 * @author daniel
 */
public interface IModeloCliente {

    // === GESTION DE JUGADORES ===

    public void addJugador(Jugador j);

    public JugadorDTO getJugador();

    public List<Jugador> getJugadores();

    // === GESTION DE PARTIDA ===

    public void unirsePartida(Jugador jugador);

    public void empezarPartida();

    public void abandonarLobby(Jugador jugador);

    public JugadorDTO abandonarPartida(Jugador jugador);

    // === GESTION DE TABLERO Y NAVES ===

    public void crearTableros();

    public AddNaveDTO addNave(NaveDTO nave, List<CoordenadasDTO> coordenadas);

    public void confirmarTablero();

    public boolean tableroConfirmado();

    // === BATALLA ===

    public DisparoDTO realizarDisparo(CoordenadasDTO coordenadas);

    public boolean esMiTurno();

    public void setTurno(boolean esMiTurno);

    // === MANEJADORES DE EVENTOS RECIBIDOS ===

    public void manejarResultadoAddNave(ResultadoAddNave resultado);

    public void manejarResultadoDisparo(DisparoDTO disparo);

    public void manejarCambioTurno(TurnoDTO turno);

    public void manejarTiempoAgotado(String idJugador);

    public void manejarConfirmacionTablero(JugadorDTO jugador);

    public void manejarTablerosListos();

    public void manejarFinPartida(JugadorDTO ganador);

    // === OBSERVADORES ===

    public void suscribirAPartida(ISuscriptor suscriptor);

    public void notificarAllSuscriptores(String contexto, Object datos);
}
