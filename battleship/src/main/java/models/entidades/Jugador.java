package models.entidades;

import models.enums.ColorJugador;
import models.enums.EstadoJugador;
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
    private Puntaje puntaje;

    public Jugador(String nombre, ColorJugador color, List<Nave> naves, Tablero tablero, EstadoJugador estado) {
        this.nombre = nombre;
        this.color = color;
        this.naves = naves;
        this.tablero = tablero;
        this.estado = estado;
        this.puntaje = new Puntaje();
    }


    public Jugador(String nombre, ColorJugador color, EstadoJugador estado) {
        this.nombre = nombre;
        this.color = color;
        this.estado = estado;
        this.puntaje = new Puntaje();
    }

    public Puntaje getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(Puntaje puntaje) {
        this.puntaje = puntaje;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ColorJugador getColor() {
        return color;
    }

    public void setColor(ColorJugador color) {
        this.color = color;
    }

    public List<Nave> getNaves() {
        return naves;
    }

    public void setNaves(List<Nave> naves) {
        this.naves = naves;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public void setTablero(Tablero tablero) {
        this.tablero = tablero;
    }

    public EstadoJugador getEstado() {
        return estado;
    }

    public void setEstado(EstadoJugador estado) {
        this.estado = estado;
    }
    
}
