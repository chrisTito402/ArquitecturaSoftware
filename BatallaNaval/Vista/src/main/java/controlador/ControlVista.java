package controlador;

import DTOs.CoordenadasDTO;
import DTOs.JugadorDTO;
import Entidades.Coordenadas;
import Entidades.Disparo;
import Enums.ResultadoDisparo;
import control.IModelo;
import control.IObervable;
import control.ISuscriptor;
import java.awt.Color;
import java.util.List;
import realizarDisparo.CasillaButton;
import realizarDisparo.FrmPartidaEnCurso;

/**
 *
 * @author daniel
 */
public class ControlVista implements ISuscriptor{
    
    private IControlador control;
    private IObervable modelo;
    private List<CasillaButton> casillas;

    public ControlVista(IControlador control, IObervable modelo, List<CasillaButton> casillas) {
        this.control = control;
        this.modelo = modelo;
        this.casillas = casillas;
    }
    
    public void realizarDisparo(CoordenadasDTO c, JugadorDTO j) {
        control.realizarDisparo(c, j);
    }

    @Override
    public void notificar() {
        Disparo d = modelo.getDisparo();
        Coordenadas c = d.getCoordenadas();
        for (CasillaButton casilla : casillas) {
            
        }
        
        CasillaButton cB = casillas.stream().filter(e -> e.getCoordenadas().getX() == c.getX()
                && e.getCoordenadas().getY() == c.getY())
                .findFirst()
                .orElse(null);
        
        System.out.println(cB.getX() + " " + cB.getY());
        
        if (d.getResultadoDisparo() == ResultadoDisparo.IMPACTO) {
            cB.setBackground(Color.YELLOW);
        }
        if (d.getResultadoDisparo() == ResultadoDisparo.HUNDIMIENTO) {
            cB.setBackground(Color.RED);
        }
        if (d.getResultadoDisparo() == ResultadoDisparo.AGUA) {
            cB.setBackground(Color.BLUE);
        }
        
        System.out.println(d.getResultadoDisparo().toString());
    }

    public List<CasillaButton> getCasillas() {
        return casillas;
    }
    
    public void mostrarFrmPartidaEnCurso() {
        new FrmPartidaEnCurso(this);
    }
    
}
