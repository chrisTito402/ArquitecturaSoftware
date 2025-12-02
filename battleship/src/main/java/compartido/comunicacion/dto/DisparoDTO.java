package compartido.comunicacion.dto;

import compartido.enums.EstadoPartida;
import compartido.enums.ResultadoDisparo;

/**
 * DTO para transferir informacion de disparo entre capas.
 * Capa compartida - no pertenece a Vista ni Modelo.
 *
 * @author daniel
 */
public class DisparoDTO {

    private JugadorDTO jugador;
    private CoordenadasDTO coordenadas;
    private ResultadoDisparo resultadoDisparo;
    private EstadoPartida estadoPartida;
    private long tiempo;
    private String tipoNaveHundida;
    private String tipoNaveImpactada;

    public DisparoDTO() {
    }

    public DisparoDTO(JugadorDTO jugador, CoordenadasDTO coordenadas, ResultadoDisparo resultadoDisparo, EstadoPartida estadoPartida, long tiempo) {
        this.jugador = jugador;
        this.coordenadas = coordenadas;
        this.resultadoDisparo = resultadoDisparo;
        this.estadoPartida = estadoPartida;
        this.tiempo = tiempo;
    }

    public DisparoDTO(JugadorDTO jugador, CoordenadasDTO coordenadas, ResultadoDisparo resultadoDisparo, EstadoPartida estadoPartida) {
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

    public CoordenadasDTO getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(CoordenadasDTO coordenadas) {
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

    public String getTipoNaveHundida() {
        return tipoNaveHundida;
    }

    public void setTipoNaveHundida(String tipoNaveHundida) {
        this.tipoNaveHundida = tipoNaveHundida;
    }

    public String getTipoNaveImpactada() {
        return tipoNaveImpactada;
    }

    public void setTipoNaveImpactada(String tipoNaveImpactada) {
        this.tipoNaveImpactada = tipoNaveImpactada;
    }

    @Override
    public String toString() {
        return "DisparoDTO{"
                + "jugador=" + jugador
                + ", coordenadas=" + coordenadas
                + ", resultadoDisparo=" + resultadoDisparo
                + ", estadoPartida=" + estadoPartida
                + ", tiempo=" + tiempo
                + '}';
    }
}
