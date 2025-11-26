package models.builder;

import models.entidades.Disparo;
import models.entidades.Jugador;
import models.entidades.Partida;
import models.enums.EstadoPartida;
import models.observador.ISuscriptor;
import java.util.List;

public interface IPartidaBuilder {

    void setTurno(Jugador turno);

    void setJugadores(List<Jugador> jugadores);

    void setCantBarcos(int cantBarcos);

    void setCantSubmarinos(int cantSubmarinos);

    void setCantCruceros(int cantCruceros);

    void setCantPortaAviones(int cantPortaAviones);

    void setTotalNaves(int totalNaves);

    void setEstado(EstadoPartida estado);

    void setDisparo(Disparo disparo);

    void setSuscriptores(List<ISuscriptor> suscriptores);

    Partida getResult();
}
