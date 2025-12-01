package models.enums;

import java.awt.Color;

/**
 * Colores disponibles para los jugadores.
 * Segun el PDF: Rojo y Azul.
 *
 * @author daniel
 */
public enum ColorJugador {

    ROJO(new Color(220, 20, 60)),      // Crimson
    AZUL(new Color(30, 144, 255));     // DodgerBlue

    private final Color colorAWT;

    ColorJugador(Color colorAWT) {
        this.colorAWT = colorAWT;
    }

    /**
     * Obtiene el color AWT para usar en componentes Swing.
     * @return Color AWT correspondiente
     */
    public Color getColorAWT() {
        return colorAWT;
    }

    /**
     * Obtiene el nombre en español del color.
     * @return nombre legible del color
     */
    public String getNombreEspanol() {
        switch (this) {
            case ROJO: return "Rojo";
            case AZUL: return "Azul";
            default: return this.name();
        }
    }
}
