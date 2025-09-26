package Entidades;

import Enums.EstadoPartida;
import Enums.ResultadoDisparo;
import java.util.List;

/**
 *
 * @author daniel
 */
public class Partida {
    
    private Jugador turno;
    private List<Jugador> jugadores;
    private int cantBarcos;
    private int cantSubmarinos;
    private int cantCruceros;
    private int cantPortaAviones;
    private int totalNaves;
    private EstadoPartida estado;

    public Partida(Jugador turno, List<Jugador> jugadores, int cantBarcos, int cantSubmarinos, int cantCruceros, int cantPortaAviones, int totalNaves, EstadoPartida estado) {
        this.turno = turno;
        this.jugadores = jugadores;
        this.cantBarcos = cantBarcos;
        this.cantSubmarinos = cantSubmarinos;
        this.cantCruceros = cantCruceros;
        this.cantPortaAviones = cantPortaAviones;
        this.totalNaves = totalNaves;
        this.estado = estado;
    }
    
    public ResultadoDisparo realizarDisparo(Coordenadas coordenadas, Jugador jugador) {
        return null;
    }
}
