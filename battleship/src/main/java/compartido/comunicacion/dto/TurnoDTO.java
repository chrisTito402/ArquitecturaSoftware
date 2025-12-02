package compartido.comunicacion.dto;

/**
 * DTO para transferir informacion del turno entre capas.
 * Capa compartida - no pertenece a Vista ni Modelo.
 *
 * @author Equipo
 */
public class TurnoDTO {

    private String idJugadorEnTurno;
    private String nombreJugadorEnTurno;
    private int tiempoRestante;
    private int numeroTurno;

    public TurnoDTO() {
    }

    public TurnoDTO(String idJugadorEnTurno, String nombreJugadorEnTurno, int tiempoRestante) {
        this.idJugadorEnTurno = idJugadorEnTurno;
        this.nombreJugadorEnTurno = nombreJugadorEnTurno;
        this.tiempoRestante = tiempoRestante;
    }

    public TurnoDTO(String idJugadorEnTurno, String nombreJugadorEnTurno, int tiempoRestante, int numeroTurno) {
        this.idJugadorEnTurno = idJugadorEnTurno;
        this.nombreJugadorEnTurno = nombreJugadorEnTurno;
        this.tiempoRestante = tiempoRestante;
        this.numeroTurno = numeroTurno;
    }

    public String getIdJugadorEnTurno() {
        return idJugadorEnTurno;
    }

    public void setIdJugadorEnTurno(String idJugadorEnTurno) {
        this.idJugadorEnTurno = idJugadorEnTurno;
    }

    public String getNombreJugadorEnTurno() {
        return nombreJugadorEnTurno;
    }

    public void setNombreJugadorEnTurno(String nombreJugadorEnTurno) {
        this.nombreJugadorEnTurno = nombreJugadorEnTurno;
    }

    public int getTiempoRestante() {
        return tiempoRestante;
    }

    public void setTiempoRestante(int tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }

    public int getNumeroTurno() {
        return numeroTurno;
    }

    public void setNumeroTurno(int numeroTurno) {
        this.numeroTurno = numeroTurno;
    }

    @Override
    public String toString() {
        return "TurnoDTO{" +
                "idJugadorEnTurno='" + idJugadorEnTurno + '\'' +
                ", nombreJugadorEnTurno='" + nombreJugadorEnTurno + '\'' +
                ", tiempoRestante=" + tiempoRestante +
                ", numeroTurno=" + numeroTurno +
                '}';
    }
}
