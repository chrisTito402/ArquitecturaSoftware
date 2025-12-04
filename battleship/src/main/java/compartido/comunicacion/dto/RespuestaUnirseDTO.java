package compartido.comunicacion.dto;

/**
 * DTO para la respuesta del servidor al intentar unirse a una partida.
 * Indica si el intento fue exitoso o si hubo un error (nombre duplicado, etc.).
 *
 * @author Equipo
 */
public class RespuestaUnirseDTO {

    private boolean exitoso;
    private String mensaje;
    private String codigoError;
    private JugadorDTO jugador;

    public RespuestaUnirseDTO() {
    }

    public RespuestaUnirseDTO(boolean exitoso, String mensaje) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
    }

    public RespuestaUnirseDTO(boolean exitoso, String mensaje, String codigoError) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
        this.codigoError = codigoError;
    }

    public RespuestaUnirseDTO(boolean exitoso, String mensaje, JugadorDTO jugador) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
        this.jugador = jugador;
    }

    // Metodos estaticos para crear respuestas comunes
    public static RespuestaUnirseDTO exito(JugadorDTO jugador) {
        return new RespuestaUnirseDTO(true, "Union exitosa", jugador);
    }

    public static RespuestaUnirseDTO errorNombreDuplicado(String nombre) {
        return new RespuestaUnirseDTO(false,
                "El nombre '" + nombre + "' ya esta en uso. Por favor elige otro nombre.",
                "NOMBRE_DUPLICADO");
    }

    public static RespuestaUnirseDTO errorPartidaLlena() {
        return new RespuestaUnirseDTO(false,
                "La partida ya tiene 2 jugadores.",
                "PARTIDA_LLENA");
    }

    public static RespuestaUnirseDTO errorPartidaEnCurso() {
        return new RespuestaUnirseDTO(false,
                "No puedes unirte, la partida ya esta en curso.",
                "PARTIDA_EN_CURSO");
    }

    public static RespuestaUnirseDTO errorCodigoInvalido(String codigo) {
        return new RespuestaUnirseDTO(false,
                "El codigo '" + codigo + "' no corresponde a ninguna partida activa.",
                "CODIGO_INVALIDO");
    }

    // Getters y Setters
    public boolean isExitoso() {
        return exitoso;
    }

    public void setExitoso(boolean exitoso) {
        this.exitoso = exitoso;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getCodigoError() {
        return codigoError;
    }

    public void setCodigoError(String codigoError) {
        this.codigoError = codigoError;
    }

    public JugadorDTO getJugador() {
        return jugador;
    }

    public void setJugador(JugadorDTO jugador) {
        this.jugador = jugador;
    }

    @Override
    public String toString() {
        return "RespuestaUnirseDTO{" +
                "exitoso=" + exitoso +
                ", mensaje='" + mensaje + '\'' +
                ", codigoError='" + codigoError + '\'' +
                '}';
    }
}
