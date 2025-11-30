package views.DTOs;

import java.util.List;
import models.entidades.Coordenadas;
import models.enums.ResultadoAddNave;

/**
 *
 * @author daniel
 */
public class AddNaveDTO {
    
    private JugadorDTO jugador;
    private NaveDTO nave;
    private List<Coordenadas> coordenadases;
    private ResultadoAddNave resultado;

    public AddNaveDTO() {
    }

    public AddNaveDTO(JugadorDTO jugador, NaveDTO nave, List<Coordenadas> coordenadases, ResultadoAddNave resultado) {
        this.jugador = jugador;
        this.nave = nave;
        this.coordenadases = coordenadases;
        this.resultado = resultado;
    }

    public AddNaveDTO(JugadorDTO jugador, NaveDTO nave, List<Coordenadas> coordenadases) {
        this.jugador = jugador;
        this.nave = nave;
        this.coordenadases = coordenadases;
    }
    
    public JugadorDTO getJugador() {
        return jugador;
    }

    public void setJugador(JugadorDTO jugador) {
        this.jugador = jugador;
    }

    public NaveDTO getNave() {
        return nave;
    }

    public void setNave(NaveDTO nave) {
        this.nave = nave;
    }

    public List<Coordenadas> getCoordenadases() {
        return coordenadases;
    }

    public void setCoordenadases(List<Coordenadas> coordenadases) {
        this.coordenadases = coordenadases;
    }

    public ResultadoAddNave getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoAddNave resultado) {
        this.resultado = resultado;
    }

    @Override
    public String toString() {
        return "AddNaveDTO{" + "jugador=" + jugador + ", nave=" + nave + ", coordenadases=" + coordenadases + ", resultado=" + resultado + '}';
    }
    
}
