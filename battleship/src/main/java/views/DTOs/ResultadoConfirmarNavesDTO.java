package views.DTOs;

import java.util.List;
import models.entidades.Coordenadas;
import models.enums.ColorJugador;
import models.enums.ResultadoConfirmarNaves;

/**
 *
 * @author daniel
 */
public class ResultadoConfirmarNavesDTO {
    
    private ResultadoConfirmarNaves resultado;
    private List<Coordenadas> coordenadas;
    private ColorJugador color;

    public ResultadoConfirmarNavesDTO() {
    }

    public ResultadoConfirmarNavesDTO(ResultadoConfirmarNaves resultado, List<Coordenadas> coordenadas, ColorJugador color) {
        this.resultado = resultado;
        this.coordenadas = coordenadas;
        this.color = color;
    }

    public ResultadoConfirmarNaves getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoConfirmarNaves resultado) {
        this.resultado = resultado;
    }

    public List<Coordenadas> getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(List<Coordenadas> coordenadas) {
        this.coordenadas = coordenadas;
    }

    public ColorJugador getColor() {
        return color;
    }

    public void setColor(ColorJugador color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "ResultadoConfirmarNavesDTO{" + "resultado=" + resultado + ", coordenadas=" + coordenadas + ", color=" + color + '}';
    }
    
}
