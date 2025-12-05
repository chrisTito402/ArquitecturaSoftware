package cliente.negocio.builder;

import compartido.entidades.Nave;
import compartido.enums.ColorJugador;
import compartido.enums.EstadoJugador;
import java.util.List;
import compartido.entidades.Jugador;
import compartido.entidades.Tablero;

/**
 * Interfaz del Builder para armar Jugadores.
 * Tiene los setters para ponerle nombre, color, sus naves, tablero
 * y en que estado esta. Al final con getResult() sacas el Jugador.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public interface IJugadorBuilder {

    public void setNombre(String nombre);

    public void setColor(ColorJugador color);

    public void setNaves(List<Nave> naves);

    public void setTablero(Tablero tablero);

    public void setEstado(EstadoJugador estado);

    public Jugador getResult();

}
