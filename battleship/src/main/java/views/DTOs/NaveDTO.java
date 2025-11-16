package views.DTOs;

import models.enums.EstadoNave;
import models.enums.OrientacionNave;

/**
 *
 * @author daniel
 */
public class NaveDTO {
    
    private EstadoNave estado;
    private OrientacionNave orientacion;
    private TipoNaveDTO tipo;

    public NaveDTO() {
    }

    public NaveDTO(EstadoNave estado, OrientacionNave orientacion, TipoNaveDTO tipo) {
        this.estado = estado;
        this.orientacion = orientacion;
        this.tipo = tipo;
    }

    public EstadoNave getEstado() {
        return estado;
    }

    public void setEstado(EstadoNave estado) {
        this.estado = estado;
    }

    public OrientacionNave getOrientacion() {
        return orientacion;
    }

    public void setOrientacion(OrientacionNave orientacion) {
        this.orientacion = orientacion;
    }

    public TipoNaveDTO getTipo() {
        return tipo;
    }

    public void setTipo(TipoNaveDTO tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "NaveDTO{" + "estado=" + estado + ", orientacion=" + orientacion + ", tipo=" + tipo + '}';
    }
    
}
