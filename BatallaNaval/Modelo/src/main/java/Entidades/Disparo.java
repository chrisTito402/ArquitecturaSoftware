package Entidades;

import Enums.ResultadoDisparo;

/**
 *
 * @author daniel
 */
public class Disparo {
    
    private Jugador jugador;
    private Coordenadas coordenadas;
    private ResultadoDisparo resultadoDisparo;

    public Disparo(Jugador jugador, Coordenadas coordenadas, ResultadoDisparo resultadoDisparo) {
        this.jugador = jugador;
        this.coordenadas = coordenadas;
        this.resultadoDisparo = resultadoDisparo;
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
    
}
