package dtos;

import dtos.enums.EstadoNaveDTO;
import dtos.enums.OrientacionNaveDTO;
import dtos.enums.TipoNaveDTO;

public class NaveDTO {

    private EstadoNaveDTO estado;
    private OrientacionNaveDTO orientacion;
    private TipoNaveDTO tipo;
    private int tamanio;

    public NaveDTO() {
    }

    public NaveDTO(EstadoNaveDTO estado, OrientacionNaveDTO orientacion, TipoNaveDTO tipo, int tamanio) {
        this.estado = estado;
        this.orientacion = orientacion;
        this.tipo = tipo;
        this.tamanio = tamanio;
    }

    public EstadoNaveDTO getEstado() {
        return estado;
    }

    public void setEstado(EstadoNaveDTO estado) {
        this.estado = estado;
    }

    public OrientacionNaveDTO getOrientacion() {
        return orientacion;
    }

    public void setOrientacion(OrientacionNaveDTO orientacion) {
        this.orientacion = orientacion;
    }

    public TipoNaveDTO getTipo() {
        return tipo;
    }

    public void setTipo(TipoNaveDTO tipo) {
        this.tipo = tipo;
    }

    public int getTamanio() {
        return tamanio;
    }

    public void setTamanio(int tamanio) {
        this.tamanio = tamanio;
    }

    @Override
    public String toString() {
        return "NaveDTO{" + "estado=" + estado + ", orientacion=" + orientacion + ", tipo=" + tipo + ", tamanio=" + tamanio + '}';
    }
}
