package controlador;

import DTOs.CoordenadasDTO;
import DTOs.JugadorDTO;
import control.IModelo;
import realizarDisparo.CasillaButton;
import realizarDisparo.FrmPartidaEnCurso;

/**
 *
 * @author daniel
 */
public class ControlVista implements ISuscriptor{
    
    private IControlador control;
    private IModelo modelo;
    private CasillaButton casillas;

    public ControlVista(IControlador control, IModelo modelo, CasillaButton casillas) {
        this.control = control;
        this.modelo = modelo;
        this.casillas = casillas;
    }
    
    public void realizarDisparo(CoordenadasDTO c, JugadorDTO j) {
        control.realizarDisparo(c, j);
    }

    @Override
    public void notificar() {
        
    }
    
    public void mostrarFrmPartidaEnCurso() {
        new FrmPartidaEnCurso();
    }
    
}
