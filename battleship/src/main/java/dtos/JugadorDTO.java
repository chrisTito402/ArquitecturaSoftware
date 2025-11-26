package dtos;

import dtos.enums.ColorJugadorDTO;
import dtos.enums.EstadoJugadorDTO;

public class JugadorDTO {

    private String nombre;
    private ColorJugadorDTO color;
    private EstadoJugadorDTO estado;

    public JugadorDTO() {
    }

    public JugadorDTO(String nombre, ColorJugadorDTO color, EstadoJugadorDTO estado) {
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

    public ColorJugadorDTO getColor() {
        return color;
    }

    public void setColor(ColorJugadorDTO color) {
        this.color = color;
    }

    public EstadoJugadorDTO getEstado() {
        return estado;
    }

    public void setEstado(EstadoJugadorDTO estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "JugadorDTO{" + "nombre=" + nombre + ", color=" + color + ", estado=" + estado + '}';
    }
}
