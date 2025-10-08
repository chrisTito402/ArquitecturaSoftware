/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package models.entidades;

import models.enums.EstadoCasilla;
import models.enums.OrientacionNave;

/**
 *
 * @author chris
 */
public class PruebaBot {

    public static void main(String[] args) {
        // Crear tablero del jugador con 10x10 casillas vacías
        Casilla[][] casillas = new Casilla[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                casillas[i][j] = new Casilla(null, EstadoCasilla.AGUA, new Coordenadas(i, j));
            }
        }

        // Crear un submarino horizontal
        Nave submarino = new Submarino(OrientacionNave.HORIZONTAL);

        // Lo colocamos en la fila 2, columnas 2 y 3
        for (int j = 2; j < 2 + submarino.getTamanio(); j++) {
            casillas[2][j] = new Casilla(submarino, EstadoCasilla.NO_DISPARADO, new Coordenadas(2, j));
        }

        Tablero tableroJugador = new Tablero(casillas, 10, 10);

        // Crear Bot
        Bot bot = new Bot("Botito");

        // Bot dispara varias veces
        for (int i = 0; i < 30; i++) {
            bot.dispararAutomatico(tableroJugador);
        }
    }
}
