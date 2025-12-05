package views.DTOs;

import models.enums.ResultadoAddJugador;

/**
 *
 * @author daniel
 */
public class AddJugadorDTO {
    
    private ResultadoAddJugador resultado;
    private JugadorDTO jugador;

    public AddJugadorDTO() {
    }

    public AddJugadorDTO(ResultadoAddJugador resultado, JugadorDTO jugador) {
        this.resultado = resultado;
        this.jugador = jugador;
    }

    public ResultadoAddJugador getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoAddJugador resultado) {
        this.resultado = resultado;
    }

    public JugadorDTO getJugador() {
        return jugador;
    }

    public void setJugador(JugadorDTO jugador) {
        this.jugador = jugador;
    }

    @Override
    public String toString() {
        return "AddJugadorDTO{" + "resultado=" + resultado + ", jugador=" + jugador + '}';
    }
    
}
