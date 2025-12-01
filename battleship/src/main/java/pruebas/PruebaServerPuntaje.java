/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pruebas;

import buseventos.Mensaje;
import buseventos.TipoAccion;
import clientesocket.ClienteSocket;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import models.entidades.Barco;
import models.entidades.Casilla;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.entidades.Partida;
import models.entidades.Tablero;
import models.enums.ColorJugador;
import models.enums.EstadoCasilla;
import models.enums.EstadoJugador;
import models.enums.EstadoPartida;
import models.enums.OrientacionNave;
import servidor.controlador.ControladorServidor;
import servidor.cronometro.Cronometro;

public class PruebaServerPuntaje {

    public static void main(String[] args) {
        // JUGADOR 1
        Casilla[][] casilla1 = new Casilla[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                casilla1[i][j] = new Casilla(EstadoCasilla.AGUA, new Coordenadas(i, j));
            }
        }

        Nave n1 = new Barco(OrientacionNave.HORIZONTAL);
        casilla1[0][0].setNave(n1);

        Tablero t1 = new Tablero(casilla1, 10, 10);

        Jugador j1 = new Jugador(
                "Jugador1",
                ColorJugador.AZUL,
                new ArrayList<>(),
                t1,
                EstadoJugador.JUGANDO);
        j1.getNaves().add(n1);

        System.out.println("J1 tiene puntaje: " + (j1.getPuntaje() != null));

        // JUGADOR 2
        Casilla[][] casilla2 = new Casilla[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                casilla2[i][j] = new Casilla(EstadoCasilla.AGUA, new Coordenadas(i, j));
            }
        }

        Nave n2 = new Barco(OrientacionNave.HORIZONTAL);
        casilla2[0][0].setNave(n2);

        Tablero t2 = new Tablero(casilla2, 10, 10);

        Jugador j2 = new Jugador(
                "Jugador2",
                ColorJugador.ROJO,
                new ArrayList<>(),
                t2,
                EstadoJugador.JUGANDO);
        j2.getNaves().add(n2);

        System.out.println("J2 tiene puntaje: " + (j2.getPuntaje() != null));

        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(j1);
        jugadores.add(j2);

        Cronometro cronometro = new Cronometro(30000); // 30 segundos
        Partida p = new Partida(
                j1,
                jugadores,
                1,
                0,
                0,
                0,
                1,
                EstadoPartida.EN_CURSO,
                new ArrayList<>(),
                cronometro
        );
        cronometro.setPartida(p);

        ClienteSocket cliente = new ClienteSocket("localhost", 5000, null);
        ControladorServidor control = new ControladorServidor(p, cliente, new HashMap<>());
        cliente.setControl(control);
        cliente.execute();

        System.out.println("===========================================");
        System.out.println("Servidor iniciado con gestión de puntaje");
        System.out.println("Turno inicial: " + j1.getNombre());
        System.out.println("===========================================");
    }
}
