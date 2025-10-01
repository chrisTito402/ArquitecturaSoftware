package Entidades;

import Enums.EstadoPartida;
import Enums.ResultadoDisparo;
import control.IModelo;
import java.util.List;

/**
 *
 * @author daniel
 */
public class Partida implements IModelo {
    
    private Jugador turno;
    private List<Jugador> jugadores;
    private int cantBarcos;
    private int cantSubmarinos;
    private int cantCruceros;
    private int cantPortaAviones;
    private int totalNaves;
    private EstadoPartida estado;
    private Disparo disparo;

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
    
    @Override
    public ResultadoDisparo realizarDisparo(Coordenadas coordenadas, Jugador jugador) {
        if (jugador == turno) {
            Jugador j2 = jugadores.stream().filter(e -> e != turno)
                    .findFirst()
                    .orElse(null);
            
            ResultadoDisparo resultadoDisparo = null;
            if (j2 != null) {
                Tablero tablero = j2.getTablero();
                resultadoDisparo = tablero.realizarDisparo(coordenadas);
            } else {
                System.out.println("Error, no se encontro al jugador.");
            }
            
            if (resultadoDisparo != ResultadoDisparo.IMPACTO &&
                    resultadoDisparo != ResultadoDisparo.HUNDIMIENTO) {
                turno = j2;
            }
            
            return resultadoDisparo;
        } else {
            System.out.println("Error, no es el turno del jugador seleccionado");
        }
        
        return null;
    }

    @Override
    public Coordenadas getCoordenadasDisparada() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ResultadoDisparo getResultadoDisparo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
