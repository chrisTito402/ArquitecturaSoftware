package cliente.presentacion.componentes;

import compartido.comunicacion.dto.CoordenadasDTO;
import javax.swing.JButton;

/**
 * Boton que representa una casilla del tablero enemigo.
 * Usa CoordenadasDTO para cumplir con MVC (Vista no importa del Modelo).
 *
 * @author daniel
 */
public class CasillaButton extends JButton {

    private CoordenadasDTO coordenadas;

    public CasillaButton(CoordenadasDTO coordenadas) {
        this.coordenadas = coordenadas;
    }

    public CoordenadasDTO getCoordenadas() {
        return coordenadas;
    }

}
