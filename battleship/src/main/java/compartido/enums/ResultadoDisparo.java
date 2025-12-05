package compartido.enums;

/**
 * Resultados posibles de un disparo en la partida.
 *
 * AGUA si no impacta, IMPACTO si da en una nave, HUNDIMIENTO si la
 * hunde, YA_DISPARADO si repite coordenada, DISPARO_FUERA_TIEMPO
 * si se acabo el turno, TURNO_INCORRECTO si no era su turno.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public enum ResultadoDisparo {

    AGUA,
    IMPACTO,
    YA_DISPARADO,
    HUNDIMIENTO,
    DISPARO_FUERA_TIEMPO,
    TURNO_INCORRECTO;
}
