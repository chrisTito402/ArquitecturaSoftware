package compartido.comunicacion.dto;

import java.util.List;
import compartido.enums.ResultadoAddNave;

/**
 * DTO para transferir informacion de agregar nave entre capas.
 * Capa compartida - no pertenece a Vista ni Modelo.
 *
 * CORRECCION MVC: Usa CoordenadasDTO en lugar de models.entidades.Coordenadas
 *
 * @author daniel
 */
public class AddNaveDTO {

    private JugadorDTO jugador;
    private NaveDTO nave;
    private List<CoordenadasDTO> coordenadas;
    private ResultadoAddNave resultado;

    public AddNaveDTO() {
    }

    public AddNaveDTO(JugadorDTO jugador, NaveDTO nave, List<CoordenadasDTO> coordenadas, ResultadoAddNave resultado) {
        this.jugador = jugador;
        this.nave = nave;
        this.coordenadas = coordenadas;
        this.resultado = resultado;
    }

    public AddNaveDTO(JugadorDTO jugador, NaveDTO nave, List<CoordenadasDTO> coordenadas) {
        this.jugador = jugador;
        this.nave = nave;
        this.coordenadas = coordenadas;
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

    public ResultadoAddNave getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoAddNave resultado) {
        this.resultado = resultado;
    }

    @Override
    public String toString() {
        return "AddNaveDTO{" + "jugador=" + jugador + ", nave=" + nave + ", coordenadas=" + coordenadas + ", resultado=" + resultado + '}';
    }
}
