package cliente.presentacion.componentes;

import compartido.comunicacion.dto.CoordenadasDTO;
import javax.swing.JPanel;

/**
 * Panel que representa una casilla del tablero propio.
 * Usa CoordenadasDTO para cumplir con MVC (Vista no importa del Modelo).
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
