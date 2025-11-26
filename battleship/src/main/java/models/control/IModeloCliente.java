package models.control;

import models.entidades.Jugador;
import java.util.List;
import models.observador.ISuscriptor;

public interface IModeloCliente {

    Jugador getTurno();

    void addJugador(Jugador j);

    void crearTableros();

    void suscribirAPartida(ISuscriptor suscriptor);

    void notificarAllSuscriptores(String contexto, Object datos);

    void unirsePartida(Jugador jugador);

    void empezarPartida();

    void abandonarLobby(Jugador jugador);

    List<Jugador> getJugadores();
}
