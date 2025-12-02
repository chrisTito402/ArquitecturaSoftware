package compartido.entidades;

import java.time.Instant;
import compartido.enums.EstadoPartida;
import compartido.enums.ResultadoDisparo;

/**
 *
 * @author daniel
 */
public class Disparo {

    private Jugador jugador;
    private Coordenadas coordenadas;
    private ResultadoDisparo resultadoDisparo;
    private EstadoPartida estadoPartida;
    private Instant tiempo;
    private String tipoNaveImpactada;
    private String tipoNaveHundida;

    public Disparo() {
    }

    public Disparo(Jugador jugador, Coordenadas coordenadas, ResultadoDisparo resultadoDisparo, EstadoPartida estadoPartida, Instant tiempo) {
        this.jugador = jugador;
        this.coordenadas = coordenadas;
        this.resultadoDisparo = resultadoDisparo;
        this.estadoPartida = estadoPartida;
        this.tiempo = tiempo;
    }

    public Disparo(Jugador jugador, Coordenadas coordenadas, ResultadoDisparo resultadoDisparo, EstadoPartida estadoPartida) {
        this.jugador = jugador;
        this.coordenadas = coordenadas;
        this.resultadoDisparo = resultadoDisparo;
        this.estadoPartida = estadoPartida;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public void setJugador(Jugador jugador) {
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

    public Instant getTiempo() {
        return tiempo;
    }

    public void setTiempo(Instant tiempo) {
        this.tiempo = tiempo;
    }

    public String getTipoNaveImpactada() {
        return tipoNaveImpactada;
    }

    public void setTipoNaveImpactada(String tipoNaveImpactada) {
        this.tipoNaveImpactada = tipoNaveImpactada;
    }

    public String getTipoNaveHundida() {
        return tipoNaveHundida;
    }

    public void setTipoNaveHundida(String tipoNaveHundida) {
        this.tipoNaveHundida = tipoNaveHundida;
    }

}
