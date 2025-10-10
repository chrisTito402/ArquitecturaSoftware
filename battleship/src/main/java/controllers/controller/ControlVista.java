package controllers.controller;

import views.DTOs.CoordenadasDTO;
import views.DTOs.JugadorDTO;
import models.entidades.Coordenadas;
import models.entidades.Disparo;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.enums.ResultadoDisparo;
import models.control.ISuscriptor;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Timer;
import models.enums.EstadoPartida;
import views.frames.CasillaButton;
import views.frames.CasillaPanel;
import views.frames.FrmPartidaEnCurso;

/**
 *
 * @author daniel
 */
public class ControlVista implements ISuscriptor{
    
    private static ControlVista controlVista;
    
    private IControlador control;
    private List<CasillaPanel> casillasPropias;
    private List<CasillaButton> casillasEnemigas;
    private JugadorDTO jugador;
    private Timer timer;

    private ControlVista() {
    }
    
    public static ControlVista getInstancia() {
        if (controlVista == null) {
            controlVista = new ControlVista();
        }
        return controlVista;
    }

    public List<CasillaPanel> getCasillasPropias() {
        return casillasPropias;
    }

    public List<CasillaButton> getCasillasEnemigas() {
        return casillasEnemigas;
    }

    public void setControl(IControlador control) {
        this.control = control;
    }

    public void setJugador(JugadorDTO jugador) {
        this.jugador = jugador;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public Timer getTimer() {
        return timer;
    }
    
    public void realizarDisparo(CoordenadasDTO c, JugadorDTO j) {
        control.realizarDisparo(c, j);
    }
    
    private Component getCasillaPropia(Disparo d) {
        Coordenadas c = d.getCoordenadas();
        
        Component cP = casillasPropias.stream().filter(e -> e.getCoordenadas().getX() == c.getX()
                && e.getCoordenadas().getY() == c.getY())
                .findFirst()
                .orElse(null);
        
        return cP;
    }
    
    private Component getCasillaEnemiga(Disparo d) {
        Coordenadas c = d.getCoordenadas();
        
        Component cB = casillasEnemigas.stream().filter(e -> e.getCoordenadas().getX() == c.getX()
                && e.getCoordenadas().getY() == c.getY())
                .findFirst()
                .orElse(null);
        
        return cB;
    }
    
    @Override
    public void notificar(Disparo disparo, EstadoPartida ePartida) {
        Disparo d = disparo;
        Coordenadas c = d.getCoordenadas();
        
        Component componente;
        if (d.getJugador().getNombre() == jugador.getNombre()) {
            componente = getCasillaEnemiga(d);
        } else {
            componente = getCasillaPropia(d);
        }
        
        if (d.getResultadoDisparo() == ResultadoDisparo.IMPACTO) {
            componente.setBackground(Color.YELLOW);
        }
        if (d.getResultadoDisparo() == ResultadoDisparo.HUNDIMIENTO) {
            componente.setBackground(Color.RED);
        }
        if (d.getResultadoDisparo() == ResultadoDisparo.AGUA) {
            componente.setBackground(Color.BLUE);
        }
        
        System.out.println(c.getX() + " " + c.getY());
        
        System.out.println(d.getResultadoDisparo().toString());
        
        if (ePartida == EstadoPartida.FINALIZADA) {
            casillasEnemigas.forEach(e -> e.setEnabled(false));
            timer.stop();
            System.out.println("EL JUGADOR " + d.getJugador().getNombre() + " GANO LA PARTIDA!!");
        }
    }
    
    public void initTableroPropio() {
        casillasPropias = new ArrayList<>();
        
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                CoordenadasDTO coordenadas = new CoordenadasDTO(i, j);
                CasillaPanel cP = new CasillaPanel(coordenadas);
                cP.setBackground(new Color(242, 242, 242)); // O cualquier color que necesites
                cP.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                casillasPropias.add(cP);
            }
        }
    }
    
    public void initTableroEnemigo() {
        casillasEnemigas = new ArrayList<>();
        
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                CoordenadasDTO coordenadas = new CoordenadasDTO(i, j);
                CasillaButton cB = new CasillaButton(coordenadas);
                cB.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                cB.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        realizarDisparo(coordenadas, jugador);
                        //cB.setEnabled(false);
                    }
                });
                casillasEnemigas.add(cB);
            }
        }
    }
    
    public void crearPartida(Jugador j) {
        control.crearPartida(j);
    }
    
    public void addNave(JugadorDTO jugador, Nave nave, List<CoordenadasDTO> coordenadas) {
        boolean resultado = control.addNave(jugador, nave, coordenadas);
        if (!resultado) {
            System.out.println("No se pudo agregar la Nave.");
        }
    }
    
    public void addJugador(Jugador j) {
        control.addJugador(j);
    }
    
    public void crearTableros() {
        control.crearTableros();
    }
    
    public void suscribirAModelo() {
        control.suscribirAPartida(this);
    }
    
    public void mostrarFrmPartidaEnCurso() {
        new FrmPartidaEnCurso().setVisible(true);
    }
    
}
