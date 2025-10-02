/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package itson.org.vista;

import DTOs.CoordenadasDTO;
import DTOs.JugadorDTO;
import Entidades.Barco;
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
import control.IModelo;
import control.IObervable;
import control.ISuscriptor;
import controlador.ControlVista;
import controlador.Controlador;
import controlador.IControlador;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import realizarDisparo.CasillaButton;
import realizarDisparo.FrmPartidaEnCurso;

/**
 *
 * @author daniel
 */
public class Vista {

    public static void main(String[] args) {
        
        Nave n1 = new Barco(OrientacionNave.HORIZONTAL);
        Coordenadas co = new Coordenadas(1, 1);
        Casilla c = new Casilla(n1, EstadoCasilla.NO_DISPARADO, co);
        Casilla c2 = new Casilla(null, EstadoCasilla.AGUA, co);
        Casilla[][] casillas = new Casilla[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Casilla c3 = new Casilla(null, EstadoCasilla.AGUA, co);
                casillas[i][j] = c3;
            }
        }
        
        casillas[1][1] = c;
        casillas[1][2] = c2;
        Tablero t2 = new Tablero(casillas, 3, 3);
        Jugador j1 = new Jugador("j1", ColorJugador.ROJO, null, t2, EstadoJugador.JUGANDO);
        Jugador j2 = new Jugador("j2", ColorJugador.AZUL, null, t2, EstadoJugador.JUGANDO);
        List<Jugador> jugadores = Arrays.asList(j1, j2);
        
        List<ISuscriptor> suscriptores = new ArrayList<>();
        //suscriptores.add();
        IObervable p = new Partida(j1, jugadores, 1, 0, 0, 0, 1, EstadoPartida.EN_CURSO, suscriptores);
        
        List<CasillaButton> casillasB = new ArrayList<>();
        
        JugadorDTO j1DTO = new JugadorDTO(
                j1.getNombre(),
                j1.getColor(),
                j1.getEstado()
        );
        
        IModelo p2 = (IModelo) p;
        IControlador controlador = new Controlador(p2);
        
        ControlVista cV = new ControlVista(controlador, p, casillasB);
        suscriptores.add(cV);
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                CoordenadasDTO coordenadas = new CoordenadasDTO(i, j);
                CasillaButton cB = new CasillaButton(coordenadas);
                cB.setText(String.valueOf(i) + "," + String.valueOf(j));
                cB.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        cV.realizarDisparo(coordenadas, j1DTO);
                    }
                });
                
                casillasB.add(cB);
            }
        }
        
        
        new FrmPartidaEnCurso(cV);
    }
}
