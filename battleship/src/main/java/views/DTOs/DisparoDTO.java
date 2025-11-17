package views.DTOs;

import java.time.Instant;
import models.entidades.Coordenadas;
import models.enums.EstadoPartida;
import models.enums.ResultadoDisparo;

/**
 *
 * @author daniel
 */
public class DisparoDTO {
    
    private JugadorDTO jugador;
    private Coordenadas coordenadas;
    private ResultadoDisparo resultadoDisparo;
    private EstadoPartida estadoPartida;
    private long tiempo;

    public DisparoDTO() {
    }

    public DisparoDTO(JugadorDTO jugador, Coordenadas coordenadas, ResultadoDisparo resultadoDisparo, EstadoPartida estadoPartida, long tiempo) {
        this.jugador = jugador;
        this.coordenadas = coordenadas;
        this.resultadoDisparo = resultadoDisparo;
        this.estadoPartida = estadoPartida;
        this.tiempo = tiempo;
    }
    
    public DisparoDTO(JugadorDTO jugador, Coordenadas coordenadas, ResultadoDisparo resultadoDisparo, EstadoPartida estadoPartida) {
        this.jugador = jugador;
        this.coordenadas = coordenadas;
        this.resultadoDisparo = resultadoDisparo;
        this.estadoPartida = estadoPartida;
    }

    public JugadorDTO getJugador() {
        return jugador;
    }

    public void setJugador(JugadorDTO jugador) {
        this.jugador = jugador;
    }

    public Coordenadas getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(Coordenadas coordenadas) {
        this.coordenadas = coordenadas;
    }

    public ResultadoDisparo getResultadoDisparo() {
        return resultadoDisparo;
    }

    public void setResultadoDisparo(ResultadoDisparo resultadoDisparo) {
        this.resultadoDisparo = resultadoDisparo;
    }

    public EstadoPartida getEstadoPartida() {
        return estadoPartida;
    }

    public void setEstadoPartida(EstadoPartida estadoPartida) {
        this.estadoPartida = estadoPartida;
    }

    public long getTiempo() {
        return tiempo;
    }

    public void setTiempo(long tiempo) {
        this.tiempo = tiempo;
    }

    @Override
    public String toString() {
        return "DisparoDTO{" + "jugador=" + jugador + ", coordenadas=" + coordenadas + ", resultadoDisparo=" + resultadoDisparo + ", estadoPartida=" + estadoPartida + ", tiempo=" + tiempo + '}';
    }
    
}
