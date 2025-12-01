package controllers.controller;

import models.entidades.Jugador;
import models.observador.ISuscriptor;
import java.util.List;
import models.entidades.Coordenadas;
import shared.dto.JugadorDTO;
import shared.dto.NaveDTO;

/**
 *
 * @author daniel
 */
public interface IControlador {

    /**
     * Crea una nueva partida en el servidor.
     * @param jugadorDTO datos del jugador que crea la partida
     * @param codigoPartida codigo de la partida (puede ser generado por el cliente)
     */
    public void crearPartida(JugadorDTO jugadorDTO, String codigoPartida);

    /**
     * Valida si un codigo de partida existe y puede recibir jugadores.
     * @param codigo codigo a validar
     */
    public void validarCodigoPartida(String codigo);

    /**
     * Se une a una partida existente usando el codigo.
     * @param jugadorDTO datos del jugador
     * @param codigoPartida codigo de la partida a unirse
     */
    public void unirsePartida(JugadorDTO jugadorDTO, String codigoPartida);

    // Metodo legacy para compatibilidad
    @Deprecated
    public String crearPartida(Jugador j);

    // Metodo legacy para compatibilidad
    @Deprecated
    public void unirsePartida(JugadorDTO jugadorDTO);

    public void addJugador(Jugador j);

    public void crearTableros();

    public void suscribirAPartida(ISuscriptor suscriptor);

    public void realizarDisparo(Coordenadas c);

    public JugadorDTO getJugador();

    public void addNave(NaveDTO nave, List<Coordenadas> coordenadas);

    public void abandonarPartida(Jugador jugador);

    public void empezarPartida();

    public void abandonarLobby(JugadorDTO jugadorDTO);

    public List<JugadorDTO> getJugadores();

    /**
     * Notifica al guest que debe ir a la pantalla de colocar naves.
     */
    public void notificarIrAColocarNaves();

    /**
     * Notifica que el jugador (guest) esta listo con sus naves.
     */
    public void notificarJugadorListo();

}
