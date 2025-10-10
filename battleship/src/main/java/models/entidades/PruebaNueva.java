package models.entidades;

import controllers.controller.ControlVista;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.Timer;
import models.enums.ColorJugador;
import models.enums.EstadoJugador;
import models.enums.OrientacionNave;
import controllers.controller.Controlador;

/**
 *
 * @author daniel
 */
public class PruebaNueva {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ControlVista controlVista = ControlVista.getInstancia();
        controlVista.setControl(new Controlador());
        
        Jugador j1 = new Jugador("J1", ColorJugador.AZUL, EstadoJugador.JUGANDO);
        j1.setNaves(new ArrayList<>());
        controlVista.crearPartida(j1);
        controlVista.addJugador(j1);
        
        Bot b = new Bot("Bot");
        b.setNaves(new ArrayList<>());
        controlVista.addJugador(b);
        
        controlVista.crearTableros();
        
        controlVista.setJugador(j1);
        
        controlVista.addNave(
                b,
                new Barco(OrientacionNave.HORIZONTAL),
                Arrays.asList(new Coordenadas(0, 0))
        );
        
        controlVista.addNave(
                j1,
                new Barco(OrientacionNave.HORIZONTAL),
                Arrays.asList(new Coordenadas(0, 0))
        );
        
        controlVista.suscribirAModelo();
        
        controlVista.initTableroEnemigo();
        controlVista.initTableroPropio();
        controlVista.setTimer(new Timer(1000, null));
        controlVista.mostrarFrmPartidaEnCurso();
    }
    
}
