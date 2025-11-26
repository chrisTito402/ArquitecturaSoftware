package models.enums;

/**
 * Enum que representa los tipos de nave en el dominio del modelo.
 * Este enum pertenece a la capa de modelo y NO debe confundirse con TipoNaveDTO.
 *
 * Separación de Capas: El modelo usa TipoNave, los DTOs usan TipoNaveDTO.
 *
 * @author daniel
 */
public enum TipoNave {
    BARCO,
    SUBMARINO,
    CRUCERO,
    PORTAAVIONES
}
