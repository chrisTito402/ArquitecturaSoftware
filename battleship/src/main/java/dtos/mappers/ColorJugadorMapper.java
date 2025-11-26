package dtos.mappers;

import dtos.enums.ColorJugadorDTO;
import models.enums.ColorJugador;

public class ColorJugadorMapper {

    public static ColorJugadorDTO toDTO(ColorJugador color) {
        if (color == null) {
            return null;
        }
        return ColorJugadorDTO.valueOf(color.name());
    }

    public static ColorJugador toEntity(ColorJugadorDTO dto) {
        if (dto == null) {
            return null;
        }
        return ColorJugador.valueOf(dto.name());
    }
}
