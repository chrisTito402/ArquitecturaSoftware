package views.builder;

import models.entidades.Jugador;
import models.entidades.Nave;
import models.entidades.Tablero;
import models.enums.ColorJugador;
import models.enums.EstadoJugador;
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
