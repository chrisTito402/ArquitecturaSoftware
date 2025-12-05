package cliente.negocio.builder;

import compartido.entidades.Jugador;
import compartido.entidades.Nave;
import compartido.entidades.Tablero;
import compartido.enums.ColorJugador;
import compartido.enums.EstadoJugador;
import java.util.List;

/**
 * Builder para crear Jugadores. Vas poniendo el nombre, color,
 * naves, tablero y estado con los setters, y al final
 * getResult() te regresa el Jugador ya armado.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public class JugadorBuilder implements IJugadorBuilder {

    private String nombre;
    private ColorJugador color;
    private List<Nave> naves;
    private Tablero tablero;
    private EstadoJugador estado;

    @Override
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void setColor(ColorJugador color) {
        this.color = color;
    }

    @Override
    public void setNaves(List<Nave> naves) {
        this.naves = naves;
    }

    @Override
    public void setTablero(Tablero tablero) {
        this.tablero = tablero;
    }

    @Override
    public void setEstado(EstadoJugador estado) {
        this.estado = estado;
    }

    public Jugador getResult() {
        return new Jugador(nombre, color, naves, tablero, estado);
    }

}
