package views.frames;

import shared.dto.CoordenadasDTO;
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
