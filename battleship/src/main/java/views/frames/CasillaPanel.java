package views.frames;

import views.DTOs.CoordenadasDTO;
import javax.swing.JPanel;

/**
 *
 * @author daniel
 */
public class CasillaPanel extends JPanel {

    private CoordenadasDTO coordenadas;

    public CasillaPanel(CoordenadasDTO coordenadas) {
        this.coordenadas = coordenadas;
    }

    public CoordenadasDTO getCoordenadas() {
        return coordenadas;
    }

}
