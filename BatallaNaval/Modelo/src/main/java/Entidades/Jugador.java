package Entidades;

import Enums.ColorJugador;
import Enums.EstadoJugador;
import java.util.List;

/**
 *
 * @author daniel
 */
public class Jugador {
    
    private String nombre;
    private ColorJugador color;
    private List<Nave> naves;
    private Tablero tablero;
    private EstadoJugador estado;

    public Jugador(String nombre, ColorJugador color, List<Nave> naves, Tablero tablero, EstadoJugador estado) {
        this.nombre = nombre;
        this.color = color;
        this.naves = naves;
        this.tablero = tablero;
        this.estado = estado;
    }

    public Jugador(String nombre, ColorJugador color, EstadoJugador estado) {
        this.nombre = nombre;
        this.color = color;
        this.estado = estado;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public String getNombre() {
        return nombre;
    }

    public ColorJugador getColor() {
        return color;
    }

    public EstadoJugador getEstado() {
        return estado;
    }

    public List<Nave> getNaves() {
        return naves;
    }
    
}
