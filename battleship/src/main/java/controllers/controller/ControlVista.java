package controllers.controller;

import views.DTOs.CoordenadasDTO;
import views.DTOs.JugadorDTO;
import models.entidades.Coordenadas;
import models.entidades.Disparo;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.enums.EstadoPartida;
import models.enums.ResultadoDisparo;
import views.builder.IPartidaBuilder;
import models.control.IObervable;
import models.control.ISuscriptor;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
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
    private IObervable modelo;
    private List<CasillaPanel> casillasPropias;
    private List<CasillaButton> casillasEnemigas;
    private JugadorDTO jugador;

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

    public void setModelo(IObervable modelo) {
        this.modelo = modelo;
    }

    public void setJugador(JugadorDTO jugador) {
        this.jugador = jugador;
    }
    
    public void realizarDisparo(CoordenadasDTO c, JugadorDTO j) {
        control.realizarDisparo(c, j);
    }
    
    private void actualizarCasillasPropias(Disparo d) {
        Coordenadas c = d.getCoordenadas();
        
        Component cB = casillasPropias.stream().filter(e -> e.getCoordenadas().getX() == c.getX()
                && e.getCoordenadas().getY() == c.getY())
                .findFirst()
                .orElse(null);
        
        if (d.getResultadoDisparo() == ResultadoDisparo.IMPACTO) {
            cB.setBackground(Color.YELLOW);
        }
        if (d.getResultadoDisparo() == ResultadoDisparo.HUNDIMIENTO) {
            cB.setBackground(Color.RED);
        }
        if (d.getResultadoDisparo() == ResultadoDisparo.AGUA) {
            cB.setBackground(Color.BLUE);
        }
    }
    
    private void actualizarCasillasEnemigas(Disparo d) {
        Coordenadas c = d.getCoordenadas();
        
        Component cB = casillasEnemigas.stream().filter(e -> e.getCoordenadas().getX() == c.getX()
                && e.getCoordenadas().getY() == c.getY())
                .findFirst()
                .orElse(null);
        
        if (d.getResultadoDisparo() == ResultadoDisparo.IMPACTO) {
            cB.setBackground(Color.YELLOW);
        }
        if (d.getResultadoDisparo() == ResultadoDisparo.HUNDIMIENTO) {
            cB.setBackground(Color.RED);
        }
        if (d.getResultadoDisparo() == ResultadoDisparo.AGUA) {
            cB.setBackground(Color.BLUE);
        }
    }
    
    @Override
    public void notificar() {
        Disparo d = modelo.getDisparo();
        Coordenadas c = d.getCoordenadas();
        
        List<CasillaButton> casillas;
        if (d.getJugador().getNombre() == jugador.getNombre()) {
            actualizarCasillasEnemigas(d);
        } else {
            actualizarCasillasPropias(d);
        }
        
        System.out.println(c.getX() + " " + c.getY());
        
        System.out.println(d.getResultadoDisparo().toString());
        
        if (modelo.getEstado() == EstadoPartida.FINALIZADA) {
            casillasEnemigas.forEach(e -> e.setEnabled(false));
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
                    }
                });
                casillasEnemigas.add(cB);
            }
        }
    }
    
    public void crearPartida(IPartidaBuilder builder, Jugador j) {
        control.crearPartida(builder, j);
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
    
    public void mostrarFrmPartidaEnCurso() {
        new FrmPartidaEnCurso().setVisible(true);
    }
    
}
