package models.builder;

import models.entidades.Disparo;
import models.entidades.Jugador;
import models.enums.EstadoPartida;
import models.observador.ISuscriptor;
import java.util.List;
import models.control.IModeloCliente;

/**
 *
 * @author daniel
 */
public interface IPartidaBuilder {

    public void setTurno(Jugador turno);

    public void setJugadores(List<Jugador> jugadores);

    public void setCantBarcos(int cantBarcos);

    public void setCantSubmarinos(int cantSubmarinos);

    public void setCantCruceros(int cantCruceros);

    public void setCantPortaAviones(int cantPortaAviones);

    public void setTotalNaves(int totalNaves);

    public void setEstado(EstadoPartida estado);

    public void setDisparo(Disparo disparo);

    public void setSuscriptores(List<ISuscriptor> suscriptores);

    public IModeloCliente getResult();
}
