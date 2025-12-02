package Entidades;

import Enums.EstadoCasilla;

/**
 *
 * @author daniel
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
