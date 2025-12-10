package cliente.presentacion.componentes;

import compartido.comunicacion.dto.CoordenadasDTO;
import javax.swing.JButton;

/**
 * Boton para las casillas del tablero enemigo.
 * Cuando le das click es para disparar a esa coordenada.
 * Usamos CoordenadasDTO y no Coordenadas directo para que la Vista
 * no dependa del Modelo (asi nos dijeron que era el MVC).
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
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
