package controllers.controller;

import views.DTOs.CoordenadasDTO;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.enums.ResultadoDisparo;
import models.observador.ISuscriptor;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import models.enums.EstadoPartida;
import views.DTOs.DisparoDTO;
import views.DTOs.JugadorDTO;
import views.frames.CasillaButton;
import views.frames.CasillaPanel;
import views.frames.FrmPartidaEnCurso;
import views.frames.TimerPanel;

/**
 *
 * @author daniel
 */
public class ControlVista implements ISuscriptor{
    
    private static ControlVista controlVista;
    
    private IControlador control;
    private List<CasillaPanel> casillasPropias;
    private List<CasillaButton> casillasEnemigas;
    private TimerPanel timer;
    private Map<String, Consumer<Object>> manejadoresNoti;

    private ControlVista() {
        manejadoresNoti = new HashMap<>();
        manejadoresNoti.put("RESULTADO_DISPARO", this::manejarDisparo);
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

    public TimerPanel getTimer() {
        return timer;
    }

    public void setTimer(TimerPanel timer) {
        this.timer = timer;
    }
    
    public void realizarDisparo(Coordenadas c) {
        control.realizarDisparo(c);
    }
    
    private Component getCasillaPropia(Coordenadas c) {
        Component cP = casillasPropias.stream().filter(e -> e.getCoordenadas().getX() == c.getX()
                && e.getCoordenadas().getY() == c.getY())
                .findFirst()
                .orElse(null);
        
        return cP;
    }
    
    private Component getCasillaEnemiga(Coordenadas c) {  
        Component cB = casillasEnemigas.stream().filter(e -> e.getCoordenadas().getX() == c.getX()
                && e.getCoordenadas().getY() == c.getY())
                .findFirst()
                .orElse(null);
        
        return cB;
    }
    
    @Override
    public void notificar(String contexto, Object datos) {
        if (datos == null) {
            System.out.println("Los datos estan vacios.");
            return;
        } else {
            manejadoresNoti.get(contexto).accept(datos);
        }
    }
    
    private void manejarDisparo(Object datos) {
        if (!(datos instanceof DisparoDTO)) {
            System.out.println("Los datos no son un objeto DisparoDTO");
            return;
        }
        
        // Reiniciar Temporizador
        timer.initTimer();
        
        DisparoDTO d = (DisparoDTO) datos;
        Coordenadas c = d.getCoordenadas();
        
        JugadorDTO jugador = control.getJugador();
        
        Component componente;
        if (d.getJugador().getNombre().equals(jugador.getNombre())) {
            componente = getCasillaEnemiga(d.getCoordenadas());
        } else {
            componente = getCasillaPropia(d.getCoordenadas());
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
        
        if (d.getEstadoPartida() == EstadoPartida.FINALIZADA) {
            casillasEnemigas.forEach(e -> e.setEnabled(false));
            timer.stopTimer();
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
                Coordenadas coordenadas = new Coordenadas(i, j);
                CasillaButton cB = new CasillaButton(coordenadas);
                cB.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                cB.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        realizarDisparo(coordenadas);
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
    
    public void addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas) {
        boolean resultado = control.addNave(jugador, nave, coordenadas);
        if (!resultado) {
            System.out.println("No se pudo agregar la Nave.");
        }
    }
    
    public void addJugador(Jugador j) {
        control.addJugador(j);
    }
    
    public void mostrarFrmPartidaEnCurso() {
        new FrmPartidaEnCurso().setVisible(true);
        timer.initTimer();
    }
    
    // Caso de Uso: Unirse Partida
    public void unirsePartida(Jugador jugador) {
        control.unirsePartida(jugador);
    }
    
    public void empezarPartida() {
        control.empezarPartida();
    }
    
    public void abandonarLobby(Jugador jugador) {
        control.abandonarLobby(jugador);
    }
}
