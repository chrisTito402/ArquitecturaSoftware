package compartido.entidades;

import compartido.enums.EstadoCasilla;

/**
 * Una casilla del tablero. Puede tener una nave o estar vacia,
 * y guarda si ya le dispararon o no.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public class Casilla {

    private Nave nave;
    private EstadoCasilla estado;
    private Coordenadas coordenadas;

    public Casilla(Nave nave, EstadoCasilla estado, Coordenadas coordenadas) {
        this.nave = nave;
        this.estado = estado;
        this.coordenadas = coordenadas;
    }

    public Casilla(EstadoCasilla estado, Coordenadas coordenadas) {
        this.estado = estado;
        this.coordenadas = coordenadas;
    }

    public EstadoCasilla getEstado() {
        return estado;
    }

    public void setNave(Nave nave) {
        this.nave = nave;
    }

    public Nave getNave() {
        return nave;
    }

    public void setEstado(EstadoCasilla estado) {
        this.estado = estado;
    }
}
