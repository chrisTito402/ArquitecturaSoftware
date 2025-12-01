package models.control;

import models.entidades.Coordenadas;
import models.entidades.Jugador;
import java.util.List;
import models.enums.ResultadoAddNave;
import models.observador.ISuscriptor;
import shared.dto.AddNaveDTO;
import shared.dto.DisparoDTO;
import shared.dto.JugadorDTO;
import shared.dto.NaveDTO;

/**
 *
 * @author daniel
 */
public interface IModeloCliente {

    public DisparoDTO realizarDisparo(Coordenadas coordenadas);

    public AddNaveDTO addNave(NaveDTO nave, List<Coordenadas> coordenadas);

    public void addJugador(Jugador j);

    public void manejarResultadoAddNave(ResultadoAddNave resultado);

    public void crearTableros();

    public void suscribirAPartida(ISuscriptor suscriptor);

    public void notificarAllSuscriptores(String contexto, Object datos);

    public JugadorDTO getJugador();

    // Caso de Uso: Unirse Partida
    public void unirsePartida(Jugador jugador);

    public void empezarPartida();

    public void abandonarLobby(Jugador jugador);

    public List<Jugador> getJugadores();

    public JugadorDTO abandonarPartida(Jugador jugador);

    // Manejadores de Resultados
    public void manejarResultadoDisparo(DisparoDTO disparo);
}
