package models.enums;

/**
 * Enum que representa los posibles resultados al intentar unirse a una partida.
 *
 * @author Equipo
 */
public enum ResultadoUnirsePartida {
    EXITO,
    CODIGO_INVALIDO,
    PARTIDA_NO_ENCONTRADA,
    PARTIDA_LLENA,
    PARTIDA_YA_INICIADA,
    JUGADOR_YA_EN_PARTIDA,
    NOMBRE_DUPLICADO,
    ERROR_SERVIDOR
}
