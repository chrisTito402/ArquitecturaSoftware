package views.DTOs;

import java.util.Objects;
import models.enums.ColorJugador;
import models.enums.EstadoJugador;

/**
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

    public JugadorDTO(String nombre, ColorJugador color) {
        this.nombre = nombre;
        this.color = color;
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
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JugadorDTO other = (JugadorDTO) obj;
        return Objects.equals(this.nombre, other.nombre);
    }

    @Override
    public String toString() {
        return "JugadorDTO{" + "nombre=" + nombre + ", color=" + color + ", estado=" + estado + '}';
    }

}
