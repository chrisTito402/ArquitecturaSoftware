package compartido.entidades;

import compartido.enums.EstadoNave;
import compartido.enums.OrientacionNave;

/**
 * Clase padre de todas las naves. Es abstracta porque no se usa directo,
 * sino las hijas (PortaAviones, Crucero, Submarino, Barco).
 * Tiene el estado (sin danio, averiada, hundida) y cuantos disparos lleva.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public abstract class Nave {

    private EstadoNave estado;
    private Integer cantDisparos;
    private OrientacionNave orientacion;

    public Nave(OrientacionNave orientacion) {
        this.estado = EstadoNave.SIN_DAÑOS;
        this.cantDisparos = 0;
        this.orientacion = orientacion;
    }

    public abstract int getTamanio();

    public EstadoNave addDisparo() {
        cantDisparos++;
        if (cantDisparos >= this.getTamanio()) {
            estado = EstadoNave.HUNDIDO;
        } else if (cantDisparos > 0) {
            estado = EstadoNave.AVERIADO;
        }
        return estado;
    }

    public EstadoNave getEstado() {
        return estado;
    }

    public OrientacionNave getOrientacion() {
        return orientacion;
    }

}
