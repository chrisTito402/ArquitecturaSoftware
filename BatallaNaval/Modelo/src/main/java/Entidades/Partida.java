package Entidades;

import Enums.EstadoPartida;
import Enums.ResultadoDisparo;
import control.IModelo;
import control.IObervable;
import control.ISuscriptor;
import java.util.List;
import java.util.Random;

/**
 *
 * @author daniel
 */
public class Partida implements IModelo, IObervable {

    private Jugador turno;
    private List<Jugador> jugadores;
    private int cantBarcos;
    private int cantSubmarinos;
    private int cantCruceros;
    private int cantPortaAviones;
    private int totalNaves;
    private EstadoPartida estado;
    private Disparo disparo;
    private List<ISuscriptor> suscriptores;

    public Partida(Jugador turno, List<Jugador> jugadores, int cantBarcos, int cantSubmarinos, int cantCruceros, int cantPortaAviones, int totalNaves, EstadoPartida estado, List<ISuscriptor> suscriptores) {
        this.turno = turno;
        this.jugadores = jugadores;
        this.cantBarcos = cantBarcos;
        this.cantSubmarinos = cantSubmarinos;
        this.cantCruceros = cantCruceros;
        this.cantPortaAviones = cantPortaAviones;
        this.totalNaves = totalNaves;
        this.estado = estado;
        this.suscriptores = suscriptores;
    }
    
    public void setSuscriptor(ISuscriptor suscriptor) {
        suscriptores.add(suscriptor);
    }
    
    public void notificarAllSuscriptores() {
        suscriptores.forEach(s -> s.notificar());
    }
    
    @Override
    public ResultadoDisparo realizarDisparo(Coordenadas coordenadas, Jugador jugador) {
        if (jugador.getNombre() != turno.getNombre()) {
            System.out.println("Error, no es el turno del jugador seleccionado");
            return null;
        }

        //Obtener al oponente
        Jugador j2 = jugadores.stream().filter(e -> e != turno)
                .findFirst()
                .orElse(null);

        if (j2 == null) {
            System.out.println("Error, no se encontró al oponente.");
            return null;
        }

        //Disparo del jugador actual
        Tablero tablero = j2.getTablero();
        ResultadoDisparo resultadoDisparo = tablero.realizarDisparo(coordenadas);

        // Si falla, pasa turno
        if (resultadoDisparo == ResultadoDisparo.AGUA) {

            turno = j2;

            //Si el siguiente es un Bot, dispara automáticamente
            if (j2 instanceof Bot) {
                System.out.println("ES UN BOT");
                Bot bot = (Bot) j2;
//                ResultadoDisparo resultadoBot = bot.dispararAutomatico(jugador.getTablero());
                Random r = new Random();
                Coordenadas c = new Coordenadas(r.nextInt(10), r.nextInt(10));
                realizarDisparo(c, j2);

//                // Si el Bot falla, regresa turno al jugador humano
//                if (resultadoBot == ResultadoDisparo.AGUA) {
//                    turno = jugador;
//                }
            }
        }

        disparo = new Disparo(jugador, coordenadas, resultadoDisparo);
        notificarAllSuscriptores();
        
        return resultadoDisparo;
    }

    @Override
    public Disparo getDisparo() {
        return disparo;
    }
    
}
