package realizarDisparo;

import DTOs.CoordenadasDTO;
import Entidades.Coordenadas;
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
