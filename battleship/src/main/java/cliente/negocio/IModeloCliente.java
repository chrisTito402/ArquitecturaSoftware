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
 * Interfaz que define lo que debe hacer el Modelo del cliente.
 * Tiene metodos para todo lo del juego: agregar jugadores, unirse
 * a partida, poner naves, disparar, manejar turnos, y un chorro
 * de cosas mas que se necesitan para que funcione el juego.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public interface IModeloCliente {

    public void addJugador(Jugador j);

    public JugadorDTO getJugador();

    public List<Jugador> getJugadores();

    public void unirsePartida(Jugador jugador);

    public void empezarPartida();

    public void abandonarLobby(Jugador jugador);

    public JugadorDTO abandonarPartida(Jugador jugador);

    public void crearTableros();

    public AddNaveDTO addNave(NaveDTO nave, List<CoordenadasDTO> coordenadas);

    public void limpiarNaves();

    public void confirmarTablero();

    public boolean tableroConfirmado();

    public DisparoDTO realizarDisparo(CoordenadasDTO coordenadas);

    public boolean esMiTurno();

    public void setTurno(boolean esMiTurno);

    public void manejarResultadoAddNave(ResultadoAddNave resultado);

    public void manejarResultadoDisparo(DisparoDTO disparo);

    public void manejarCambioTurno(TurnoDTO turno);

    public void manejarTiempoAgotado(String idJugador);

    public void manejarConfirmacionTablero(JugadorDTO jugador);

    public void manejarTablerosListos();

    public void manejarFinPartida(JugadorDTO ganador);

    public void suscribirAPartida(ISuscriptor suscriptor);

    public void desuscribirDePartida(ISuscriptor suscriptor);

    public void notificarAllSuscriptores(String contexto, Object datos);

    /**
     * Reinicia para nueva partida.
     */
    public void reiniciar();
}
