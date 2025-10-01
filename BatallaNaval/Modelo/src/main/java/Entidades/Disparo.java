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
    
}
