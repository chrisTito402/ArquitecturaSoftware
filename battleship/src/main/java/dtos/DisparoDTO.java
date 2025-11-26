package dtos;

import models.entidades.Coordenadas;
import models.enums.EstadoPartida;
import models.enums.ResultadoDisparo;

/**
 * Data Transfer Object para Disparo.
 * Incluye información del puntaje obtenido en el disparo.
 *
 * @author daniel
 */
public class DisparoDTO {

    private JugadorDTO jugador;
    private Coordenadas coordenadas;
    private ResultadoDisparo resultadoDisparo;
    private EstadoPartida estadoPartida;
    private long tiempo;
    private PuntajeDTO puntaje;

    public DisparoDTO() {
    }

    public DisparoDTO(JugadorDTO jugador, Coordenadas coordenadas, ResultadoDisparo resultadoDisparo, EstadoPartida estadoPartida, long tiempo, PuntajeDTO puntaje) {
        this.jugador = jugador;
        this.coordenadas = coordenadas;
        this.resultadoDisparo = resultadoDisparo;
        this.estadoPartida = estadoPartida;
        this.tiempo = tiempo;
        this.puntaje = puntaje;
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

    public PuntajeDTO getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(PuntajeDTO puntaje) {
        this.puntaje = puntaje;
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
        return "DisparoDTO{" +
                "jugador=" + jugador +
                ", coordenadas=" + coordenadas +
                ", resultadoDisparo=" + resultadoDisparo +
                ", estadoPartida=" + estadoPartida +
                ", tiempo=" + tiempo +
                ", puntaje=" + puntaje +
                '}';
    }
}
