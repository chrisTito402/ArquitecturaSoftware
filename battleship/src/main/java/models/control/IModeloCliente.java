package models.control;

import models.entidades.Coordenadas;
import models.entidades.Jugador;
import java.util.List;
import models.observador.ISuscriptor;
import views.DTOs.AddNaveDTO;
import views.DTOs.DisparoDTO;
import views.DTOs.JugadorDTO;
import views.DTOs.NaveDTO;

/**
 *
 * @author daniel
 */
public interface IModeloCliente {
    
    public DisparoDTO realizarDisparo(Coordenadas coordenadas);
    public AddNaveDTO addNave(NaveDTO nave, List<Coordenadas> coordenadas);
    public void addJugador(Jugador j);
    public void crearTableros();
    public void suscribirAPartida(ISuscriptor suscriptor);
    public void notificarAllSuscriptores(String contexto, Object datos);
    public JugadorDTO getJugador();
    
    // Caso de Uso: Unirse Partida
    public void unirsePartida(Jugador jugador);
    public void empezarPartida();
    public void abandonarLobby(Jugador jugador);
    
    // Manejadores de Resultados
    public void manejarResultadoDisparo(DisparoDTO disparo);
}
