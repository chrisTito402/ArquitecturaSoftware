package shared.dto;

import models.enums.ResultadoUnirsePartida;

/**
 * DTO para la solicitud y respuesta de unirse a partida.
 *
 * @author Equipo
 */
public class UnirsePartidaDTO {

    private String codigoPartida;
    private JugadorDTO jugador;
    private ResultadoUnirsePartida resultado;
    private String mensaje;

    public UnirsePartidaDTO() {
    }

    public UnirsePartidaDTO(String codigoPartida, JugadorDTO jugador) {
        this.codigoPartida = codigoPartida;
        this.jugador = jugador;
    }

    public UnirsePartidaDTO(String codigoPartida, JugadorDTO jugador, ResultadoUnirsePartida resultado, String mensaje) {
        this.codigoPartida = codigoPartida;
        this.jugador = jugador;
        this.resultado = resultado;
        this.mensaje = mensaje;
    }

    public String getCodigoPartida() {
        return codigoPartida;
    }

    public void setCodigoPartida(String codigoPartida) {
        this.codigoPartida = codigoPartida;
    }

    public JugadorDTO getJugador() {
        return jugador;
    }

    public void setJugador(JugadorDTO jugador) {
        this.jugador = jugador;
    }

    public ResultadoUnirsePartida getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoUnirsePartida resultado) {
        this.resultado = resultado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isExito() {
        return resultado == ResultadoUnirsePartida.EXITO;
    }

    @Override
    public String toString() {
        return "UnirsePartidaDTO{" +
                "codigoPartida='" + codigoPartida + '\'' +
                ", jugador=" + jugador +
                ", resultado=" + resultado +
                ", mensaje='" + mensaje + '\'' +
                '}';
    }
}
