package dtos;

import models.enums.ColorJugador;
import models.enums.EstadoJugador;

/**
 * Data Transfer Object para Jugador.
 *
 * @author daniel
 */
public class JugadorDTO {

    private String nombre;
    private ColorJugador color;
    private EstadoJugador estado;

    public JugadorDTO() {
    }

    public JugadorDTO(String nombre, ColorJugador color, EstadoJugador estado) {
        this.nombre = nombre;
        this.color = color;
        this.estado = estado;
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

    public EstadoJugador getEstado() {
        return estado;
    }

    public void setEstado(EstadoJugador estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "JugadorDTO{" + "nombre=" + nombre + ", color=" + color + ", estado=" + estado + '}';
    }

}
