package dtos.mappers;

import models.entidades.Nave;
import models.enums.TipoNave;
import dtos.NaveDTO;
import dtos.TipoNaveDTO;

/**
 * Mapper para convertir entre entidades de Nave y NaveDTO.
 *
 * Arquitectura en Capas: Este mapper actúa como puente entre la capa de
 * negocio (entidades) y la capa de presentación/transferencia (DTOs).
 *
 * Patrón de Diseño: Mapper/Converter Pattern
 *
 * @author Fred
 */
public class NaveMapper {

    /**
     * Convierte una entidad Nave a un NaveDTO.
     *
     * @param nave La entidad Nave del dominio
     * @return El DTO correspondiente
     */
    public static NaveDTO toDTO(Nave nave) {
        if (nave == null) {
            return null;
        }

        return new NaveDTO(
                nave.getEstado(),
                nave.getOrientacion(),
                toTipoNaveDTO(nave.getTipo()),
                nave.getTamanio()
        );
    }

    /**
     * Convierte TipoNave (dominio) a TipoNaveDTO (transferencia).
     *
     * @param tipoNave El enum del dominio
     * @return El enum DTO correspondiente
     */
    public static TipoNaveDTO toTipoNaveDTO(TipoNave tipoNave) {
        if (tipoNave == null) {
            return null;
        }

        return switch (tipoNave) {
            case BARCO -> TipoNaveDTO.BARCO;
            case SUBMARINO -> TipoNaveDTO.SUBMARINO;
            case CRUCERO -> TipoNaveDTO.CRUCERO;
            case PORTAAVIONES -> TipoNaveDTO.PORTAAVIONES;
        };
    }

    /**
     * Convierte TipoNaveDTO (transferencia) a TipoNave (dominio).
     *
     * @param tipoNaveDTO El enum DTO
     * @return El enum del dominio correspondiente
     */
    public static TipoNave toTipoNave(TipoNaveDTO tipoNaveDTO) {
        if (tipoNaveDTO == null) {
            return null;
        }

        return switch (tipoNaveDTO) {
            case BARCO -> TipoNave.BARCO;
            case SUBMARINO -> TipoNave.SUBMARINO;
            case CRUCERO -> TipoNave.CRUCERO;
            case PORTAAVIONES -> TipoNave.PORTAAVIONES;
        };
    }
}
