package compartido.enums;

/**
 * Estados posibles de una nave durante la partida.
 *
 * SIN_DAÑOS indica que no ha sido impactada, AVERIADO que tiene
 * impactos pero aun flota, y HUNDIDO cuando todas sus casillas
 * fueron impactadas.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public enum EstadoNave {

    AVERIADO,
    HUNDIDO,
    SIN_DAÑOS;
}
