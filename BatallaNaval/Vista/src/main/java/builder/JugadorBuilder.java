package builder;

import Entidades.Jugador;
import Entidades.Nave;
import Entidades.Tablero;
import Enums.ColorJugador;
import Enums.EstadoJugador;
import java.util.List;

/**
 *
 * @author daniel
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
    public void setTablero() {
        
    }

    @Override
    public void setEstado(EstadoJugador estado) {
        this.estado = estado;
    }

    public Jugador getResult() {
        return new Jugador(nombre, color, naves, tablero, estado);
    }

}
