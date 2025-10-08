/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.entidades;

import models.enums.ColorJugador;
import models.enums.EstadoJugador;
import models.enums.ResultadoDisparo;
import java.util.List;
import java.util.Random;

/**
 *
 * @author chris
 */
/**
 * Representa al contrincante automático (bot) que realiza disparos aleatorios.
 */
public class Bot extends Jugador {

    private Random random;
    private boolean[][] disparos; // Para no repetir tiros

    public Bot(String nombre) {
        super(nombre, ColorJugador.ROJO, null, new Tablero(new Casilla[10][10], 10, 10), EstadoJugador.JUGANDO);
        this.random = new Random();
        this.disparos = new boolean[10][10]; // matriz de disparos 10x10
    }

    public Bot(boolean[][] disparos, String nombre, ColorJugador color, List<Nave> naves, Tablero tablero, EstadoJugador estado) {
        super(nombre, color, naves, tablero, estado);
        this.disparos = disparos;
    }
    
    /**
     * El bot realiza un disparo aleatorio sobre el tablero del jugador.
     *
     * @param tableroJugador tablero donde disparará el bot.
     * @return resultado del disparo (IMPACTO, AGUA, etc.)
     */
    public ResultadoDisparo dispararAutomatico(Tablero tableroOponente) {
        int x, y;
        do {
            x = random.nextInt(10); // 0..9
            y = random.nextInt(10); // 0..9
        } while (disparos[x][y]);

        disparos[x][y] = true;

        Coordenadas c = new Coordenadas(x, y);
        ResultadoDisparo resultado = tableroOponente.realizarDisparo(c);

        System.out.println("🤖 Bot dispara en (" + x + ", " + y + ") -> " + resultado);
        return resultado;
    }
    
    public Coordenadas getCoordenadas() {
        Random r = new Random();
        
        int x, y;
        do {
            x = r.nextInt(10); // 0..9
            y = r.nextInt(10); // 0..9
        } while (disparos[x][y]);

        disparos[x][y] = true;
        Coordenadas c = new Coordenadas(x, y);
        
        return c;
    }
}

