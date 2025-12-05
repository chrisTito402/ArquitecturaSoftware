package compartido.enums;

/**
 * Enum que representa los posibles resultados al agregar una nave al tablero.
 *
 * Incluye tanto los casos de exito como los diferentes tipos de errores
 * que pueden ocurrir durante la validacion de la colocacion de naves.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public enum ResultadoAddNave {
    JUGADOR_NULL,
    NAVE_NULL,
    COORDENADAS_NULL,
    JUGADOR_NO_ENCONTRADO,
    COORDENADAS_EXTRA,
    COORDENADAS_FUERA_LIMITE,
    NO_ORDENADA_VERTICLMENTE,
    NO_ORDENADA_HORIZONTALMENTE,
    NO_CONSECUTIVO_X,
    NO_CONSECUTIVO_Y,
    ESPACIO_YA_OCUPADO,
    NAVE_ADYACENTE,
    NAVE_AÑADIDA;
}
