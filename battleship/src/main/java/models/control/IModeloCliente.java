package models.control;

import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import java.util.List;
import models.observador.ISuscriptor;
import views.DTOs.DisparoDTO;

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
    
    // Caso de Uso: Unirse Partida
    public void unirsePartida();
    public void empezarPartida();
    public void abandonarLobby();
}
