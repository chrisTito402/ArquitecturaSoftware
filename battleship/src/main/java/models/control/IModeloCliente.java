package models.control;

import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import java.util.List;
import models.observador.ISuscriptor;
import views.DTOs.DisparoDTO;
import views.DTOs.JugadorDTO;

/**
 *
 * @author daniel
 */
public interface IModeloCliente {
    
    public DisparoDTO realizarDisparo(Coordenadas coordenadas);
    public boolean addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas);
    public void addJugador(Jugador j);
    public void crearTableros();
    public void suscribirAPartida(ISuscriptor suscriptor);
    public void notificarAllSuscriptores(String contexto, Object datos);
    public JugadorDTO getJugador();
    
    // Caso de Uso: Unirse Partida
    public void unirsePartida(Jugador jugador);
    public void empezarPartida();
    public void abandonarLobby(Jugador jugador);
}
