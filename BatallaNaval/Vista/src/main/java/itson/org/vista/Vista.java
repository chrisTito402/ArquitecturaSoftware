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
import Entidades.Crucero;
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
        
        List<Nave> naves1 = new ArrayList<>();
        List<Nave> naves2 = new ArrayList<>();
        Nave nJ1 = new Barco(OrientacionNave.HORIZONTAL);
        Nave nJ2 = new Barco(OrientacionNave.HORIZONTAL);
        
        Nave nB1 = new Barco(OrientacionNave.HORIZONTAL);
        Nave nB2 = new Barco(OrientacionNave.HORIZONTAL);
        Nave nB3 = new Crucero(OrientacionNave.HORIZONTAL);
        
        naves1.add(nJ1);
        naves1.add(nJ2);
        
        naves2.add(nB1);
        naves2.add(nB2);
        naves2.add(nB3);
        
        Coordenadas co = new Coordenadas(1, 1);
        Casilla cJ1 = new Casilla(nJ1, EstadoCasilla.NO_DISPARADO, co);
        Casilla cJ2 = new Casilla(nJ2, EstadoCasilla.NO_DISPARADO, co);
        
        Casilla cB1 = new Casilla(nB1, EstadoCasilla.NO_DISPARADO, co);
        Casilla cB2 = new Casilla(nB2, EstadoCasilla.NO_DISPARADO, co);
        Casilla cB3 = new Casilla(nB3, EstadoCasilla.NO_DISPARADO, co);
        Casilla cB4 = new Casilla(nB3, EstadoCasilla.NO_DISPARADO, co);
        Casilla cB5 = new Casilla(nB3, EstadoCasilla.NO_DISPARADO, co);
        
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
        
        casillas2[1][1] = cB1;
        casillas2[1][3] = cB2;
        casillas2[2][0] = cB3;
        casillas2[2][1] = cB4;
        casillas2[2][2] = cB5;
        
        casillas1[0][0] = cJ1;
        casillas1[0][2] = cJ2;
        
        Tablero t1 = new Tablero(casillas1, 10, 10);
        Tablero t2 = new Tablero(casillas2, 10, 10);
        
        Jugador j1 = new Jugador("j1", ColorJugador.ROJO, naves1, t1, EstadoJugador.JUGANDO);
        boolean[][] cBO = new boolean[10][10];
        Jugador j2 = new Bot(cBO, "bot", ColorJugador.AZUL, naves2, t2, EstadoJugador.JUGANDO);
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
