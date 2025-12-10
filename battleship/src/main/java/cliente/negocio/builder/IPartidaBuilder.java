package cliente.negocio.builder;

import compartido.entidades.Disparo;
import compartido.entidades.Jugador;
import compartido.enums.EstadoPartida;
import compartido.observador.ISuscriptor;
import java.util.List;
import cliente.negocio.IModeloCliente;

/**
 * Interfaz del Builder para armar la Partida.
 * Tiene todos los setters para configurar turno, jugadores,
 * cantidad de cada tipo de nave, estado, etc. Cuando ya esta
 * todo listo llamas getResult() y te da el modelo.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
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
