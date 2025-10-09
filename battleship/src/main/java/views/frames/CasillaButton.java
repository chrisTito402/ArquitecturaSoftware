package views.frames;

import views.DTOs.CoordenadasDTO;
import models.entidades.Coordenadas;
import javax.swing.JButton;

/**
 *
 * @author daniel
 */
public class CasillaButton extends JButton{
    
    private Coordenadas coordenadas;

    public CasillaButton(Coordenadas coordenadas) {
        this.coordenadas = coordenadas;
    }

    public Coordenadas getCoordenadas() {
        return coordenadas;
    }
    
}
