package realizarDisparo;

import Entidades.Coordenadas;
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
