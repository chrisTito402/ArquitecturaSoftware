package dtos;

public class TiempoDTO {

    private long tiempoRestante;
    private long tiempoTotal;
    private String nombreJugadorTurno;

    public TiempoDTO() {
    }

    public TiempoDTO(long tiempoRestante, long tiempoTotal, String nombreJugadorTurno) {
        this.tiempoRestante = tiempoRestante;
        this.tiempoTotal = tiempoTotal;
        this.nombreJugadorTurno = nombreJugadorTurno;
    }

    public long getTiempoRestante() {
        return tiempoRestante;
    }

    public void setTiempoRestante(long tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }

    public long getTiempoTotal() {
        return tiempoTotal;
    }

    public void setTiempoTotal(long tiempoTotal) {
        this.tiempoTotal = tiempoTotal;
    }

    public String getNombreJugadorTurno() {
        return nombreJugadorTurno;
    }

    public void setNombreJugadorTurno(String nombreJugadorTurno) {
        this.nombreJugadorTurno = nombreJugadorTurno;
    }
}
