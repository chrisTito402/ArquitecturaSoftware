package dtos;

import dtos.enums.EstadoPartidaDTO;
import dtos.enums.ResultadoDisparoDTO;

public class DisparoDTO {

    private JugadorDTO jugador;
    private CoordenadasDTO coordenadas;
    private ResultadoDisparoDTO resultadoDisparo;
    private EstadoPartidaDTO estadoPartida;
    private long tiempo;
    private PuntajeDTO puntaje;

    public DisparoDTO() {
    }

    public DisparoDTO(JugadorDTO jugador, CoordenadasDTO coordenadas, ResultadoDisparoDTO resultadoDisparo, EstadoPartidaDTO estadoPartida, long tiempo, PuntajeDTO puntaje) {
        this.jugador = jugador;
        this.coordenadas = coordenadas;
        this.resultadoDisparo = resultadoDisparo;
        this.estadoPartida = estadoPartida;
        this.tiempo = tiempo;
        this.puntaje = puntaje;
    }

    public DisparoDTO(JugadorDTO jugador, CoordenadasDTO coordenadas, ResultadoDisparoDTO resultadoDisparo, EstadoPartidaDTO estadoPartida, long tiempo) {
        this.jugador = jugador;
        this.coordenadas = coordenadas;
        this.resultadoDisparo = resultadoDisparo;
        this.estadoPartida = estadoPartida;
        this.tiempo = tiempo;
    }

    public DisparoDTO(JugadorDTO jugador, CoordenadasDTO coordenadas, ResultadoDisparoDTO resultadoDisparo, EstadoPartidaDTO estadoPartida) {
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

    public CoordenadasDTO getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(CoordenadasDTO coordenadas) {
        this.coordenadas = coordenadas;
    }

    public ResultadoDisparoDTO getResultadoDisparo() {
        return resultadoDisparo;
    }

    public void setResultadoDisparo(ResultadoDisparoDTO resultadoDisparo) {
        this.resultadoDisparo = resultadoDisparo;
    }

    public EstadoPartidaDTO getEstadoPartida() {
        return estadoPartida;
    }

    public void setEstadoPartida(EstadoPartidaDTO estadoPartida) {
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
