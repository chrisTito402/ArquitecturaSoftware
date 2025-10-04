/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package itson.org.vista;

import DTOs.CoordenadasDTO;
import DTOs.JugadorDTO;
import Entidades.Barco;
import Entidades.Bot;
import Entidades.Casilla;
import Entidades.Coordenadas;
import Entidades.Jugador;
import Entidades.Nave;
import Entidades.Partida;
import Entidades.Tablero;
import Enums.ColorJugador;
import Enums.EstadoCasilla;
import Enums.EstadoJugador;
import Enums.EstadoPartida;
import Enums.OrientacionNave;
import control.ISuscriptor;
import controlador.ControlVista;
import controlador.Controlador;
import controlador.IControlador;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import realizarDisparo.CasillaButton;
import realizarDisparo.CasillaPanel;

/**
 *
 * @author daniel
 */
public class Vista {

    public static void main(String[] args) {
        
        Nave n1 = new Barco(OrientacionNave.HORIZONTAL);
        Nave n2 = new Barco(OrientacionNave.HORIZONTAL);
        Coordenadas co = new Coordenadas(1, 1);
        Casilla c = new Casilla(n1, EstadoCasilla.NO_DISPARADO, co);
        Casilla c2 = new Casilla(n2, EstadoCasilla.AGUA, co);
        
        Casilla[][] casillas1 = new Casilla[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Casilla c3 = new Casilla(null, EstadoCasilla.AGUA, co);
                casillas1[i][j] = c3;
            }
        }
        
        Casilla[][] casillas2 = new Casilla[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Casilla c3 = new Casilla(null, EstadoCasilla.AGUA, co);
                casillas2[i][j] = c3;
            }
        }
        
        casillas2[1][1] = c;
        casillas2[1][3] = c2;
        Tablero t1 = new Tablero(casillas1, 10, 10);
        Tablero t2 = new Tablero(casillas2, 10, 10);
        Jugador j1 = new Jugador("j1", ColorJugador.ROJO, null, t1, EstadoJugador.JUGANDO);
        boolean[][] cBO = new boolean[10][10];
        Jugador j2 = new Bot(cBO, "bot", ColorJugador.AZUL, null, t2, EstadoJugador.JUGANDO);
        List<Jugador> jugadores = Arrays.asList(j1, j2);
        
        List<ISuscriptor> suscriptores = new ArrayList<>();
        //suscriptores.add();
        Partida p = new Partida(j1, jugadores, 1, 0, 0, 0, 1, EstadoPartida.EN_CURSO, suscriptores);
        
        List<CasillaButton> casillasBP = new ArrayList<>();
        List<CasillaPanel> casillasPE = new ArrayList<>();
        
        JugadorDTO j1DTO = new JugadorDTO(
                j1.getNombre(),
                j1.getColor(),
                j1.getEstado()
        );
        
        IControlador controlador = new Controlador(p);
        
        ControlVista cV = new ControlVista(controlador, p, casillasPE, casillasBP, j1DTO);
        suscriptores.add(cV);
        
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                CoordenadasDTO coordenadas = new CoordenadasDTO(i, j);
                CasillaButton cB = new CasillaButton(coordenadas);
                cB.setText(String.valueOf(i) + "," + String.valueOf(j));
                cB.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        cV.realizarDisparo(coordenadas, j1DTO);
                    }
                });
                casillasBP.add(cB);
            }
        }
        
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                CoordenadasDTO coordenadas = new CoordenadasDTO(i, j);
                CasillaPanel cP = new CasillaPanel(coordenadas);
                cP.setBackground(Color.WHITE); // O cualquier color que necesites
                cP.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                casillasPE.add(cP);
            }
        }
        
        cV.mostrarFrmPartidaEnCurso();
    }
}
