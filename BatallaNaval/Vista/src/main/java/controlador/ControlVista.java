package controlador;

import DTOs.CoordenadasDTO;
import DTOs.JugadorDTO;
import Entidades.Coordenadas;
import Entidades.Disparo;
import Enums.EstadoPartida;
import Enums.ResultadoDisparo;
import control.IObervable;
import control.ISuscriptor;
import java.awt.Color;
import java.awt.Component;
import java.util.List;
import realizarDisparo.CasillaButton;
import realizarDisparo.CasillaPanel;
import realizarDisparo.FrmPartidaEnCurso;

/**
 *
 * @author daniel
 */
public class ControlVista implements ISuscriptor{
    
    private IControlador control;
    private IObervable modelo;
    private List<CasillaPanel> casillasPropias;
    private List<CasillaButton> casillasEnemigas;
    private JugadorDTO jugador;

    public ControlVista(IControlador control, IObervable modelo, List<CasillaPanel> casillasPropias, List<CasillaButton> casillasEnemigas, JugadorDTO jugador) {
        this.control = control;
        this.modelo = modelo;
        this.casillasPropias = casillasPropias;
        this.casillasEnemigas = casillasEnemigas;
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

    public List<CasillaPanel> getCasillasPropias() {
        return casillasPropias;
    }
    
    public List<CasillaButton> getCasillasEnemigas() {
        return casillasEnemigas;
    }
    
    public void mostrarFrmPartidaEnCurso() {
        new FrmPartidaEnCurso(this).setVisible(true);
    }
    
}
