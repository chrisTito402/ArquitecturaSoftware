package servidor.modelo;

import java.util.List;
import models.entidades.Coordenadas;
import models.entidades.Disparo;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.observador.ISuscriptor;

/**
 *
 * @author daniel
 */
public interface IModeloServidor {
    public Disparo realizarDisparo(Coordenadas coordenadas, Jugador jugador);
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
