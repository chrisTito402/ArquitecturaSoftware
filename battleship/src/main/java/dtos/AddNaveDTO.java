package dtos;

import dtos.enums.ResultadoAddNaveDTO;
import java.util.List;

public class AddNaveDTO {

    private JugadorDTO jugador;
    private NaveDTO nave;
    private List<CoordenadasDTO> coordenadas;
    private ResultadoAddNaveDTO resultado;

    public AddNaveDTO() {
    }

    public AddNaveDTO(JugadorDTO jugador, NaveDTO nave, List<CoordenadasDTO> coordenadas, ResultadoAddNaveDTO resultado) {
        this.jugador = jugador;
        this.nave = nave;
        this.coordenadas = coordenadas;
        this.resultado = resultado;
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

    public List<CoordenadasDTO> getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(List<CoordenadasDTO> coordenadas) {
        this.coordenadas = coordenadas;
    }

    public ResultadoAddNaveDTO getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoAddNaveDTO resultado) {
        this.resultado = resultado;
    }

    @Override
    public String toString() {
        return "AddNaveDTO{" + "jugador=" + jugador + ", nave=" + nave + ", coordenadas=" + coordenadas + ", resultado=" + resultado + '}';
    }
}
