package shared.dto;

/**
 * DTO para la solicitud y respuesta de crear partida.
 *
 * @author Equipo
 */
public class CrearPartidaDTO {

    private String codigoPartida;
    private JugadorDTO jugador;
    private boolean exito;
    private String mensaje;

    public CrearPartidaDTO() {
    }

    public CrearPartidaDTO(String codigoPartida, JugadorDTO jugador) {
        this.codigoPartida = codigoPartida;
        this.jugador = jugador;
        this.exito = true;
        this.mensaje = "Partida creada exitosamente";
    }

    public CrearPartidaDTO(boolean exito, String mensaje) {
        this.exito = exito;
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

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        return "CrearPartidaDTO{" +
                "codigoPartida='" + codigoPartida + '\'' +
                ", jugador=" + jugador +
                ", exito=" + exito +
                ", mensaje='" + mensaje + '\'' +
                '}';
    }
}
