package compartido.comunicacion.dto;

import compartido.enums.ColorJugador;
import compartido.enums.EstadoJugador;

/**
 * DTO para transferir datos del jugador entre capas.
 *
 * Se usa para enviar informacion del jugador por la red sin
 * exponer la entidad completa. Contiene solo los datos necesarios:
 * nombre, color y estado.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
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
