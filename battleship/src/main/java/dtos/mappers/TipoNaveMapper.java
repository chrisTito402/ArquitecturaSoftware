package dtos.mappers;

import dtos.enums.TipoNaveDTO;
import models.enums.TipoNave;

public class TipoNaveMapper {

    public static TipoNaveDTO toDTO(TipoNave tipo) {
        if (tipo == null) {
            return null;
        }
        return TipoNaveDTO.valueOf(tipo.name());
    }

    public static TipoNave toEntity(TipoNaveDTO dto) {
        if (dto == null) {
            return null;
        }
        return TipoNave.valueOf(dto.name());
    }
}
