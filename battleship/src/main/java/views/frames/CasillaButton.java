package views.frames;

import views.DTOs.CoordenadasDTO;
import models.entidades.Coordenadas;
import javax.swing.JButton;

/**
 *
 * @author daniel
 */
public class CasillaButton extends JButton{
    
    private CoordenadasDTO coordenadas;

    public CasillaButton(CoordenadasDTO coordenadas) {
        this.coordenadas = coordenadas;
    }

    public CoordenadasDTO getCoordenadas() {
        return coordenadas;
    }
    
}
