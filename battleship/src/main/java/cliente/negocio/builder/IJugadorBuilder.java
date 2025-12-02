package cliente.negocio.builder;

import compartido.entidades.Nave;
import compartido.enums.ColorJugador;
import compartido.enums.EstadoJugador;
import java.util.List;
import compartido.entidades.Jugador;
import compartido.entidades.Tablero;

/**
 *
 * @author daniel
 */
public interface IJugadorBuilder {

    public void setNombre(String nombre);

    public void setColor(ColorJugador color);

    public void setNaves(List<Nave> naves);

    public void setTablero(Tablero tablero);

    public void setEstado(EstadoJugador estado);

    public Jugador getResult();

}
