package compartido.comunicacion.dto;

/**
 * DTO para solicitar unirse a una partida.
 * Incluye la informacion del jugador y el codigo de la partida.
 *
 * @author Equipo
 */
public class SolicitudUnirseDTO {

    private JugadorDTO jugador;
    private String codigoPartida;
    private boolean esHost;

    public SolicitudUnirseDTO() {
    }

    public SolicitudUnirseDTO(JugadorDTO jugador, String codigoPartida, boolean esHost) {
        this.jugador = jugador;
        this.codigoPartida = codigoPartida;
        this.esHost = esHost;
    }

    public JugadorDTO getJugador() {
        return jugador;
    }

    public void setJugador(JugadorDTO jugador) {
        this.jugador = jugador;
    }

    public String getCodigoPartida() {
        return codigoPartida;
    }

    public void setCodigoPartida(String codigoPartida) {
        this.codigoPartida = codigoPartida;
    }

    public boolean isEsHost() {
        return esHost;
    }

    public void setEsHost(boolean esHost) {
        this.esHost = esHost;
    }

    @Override
    public String toString() {
        return "SolicitudUnirseDTO{" +
                "jugador=" + jugador +
                ", codigoPartida='" + codigoPartida + '\'' +
                ", esHost=" + esHost +
                '}';
    }
}
