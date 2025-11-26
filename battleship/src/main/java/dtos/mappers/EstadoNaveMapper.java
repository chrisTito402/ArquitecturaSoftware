package dtos.mappers;

import dtos.enums.EstadoNaveDTO;
import models.enums.EstadoNave;

public class EstadoNaveMapper {

    public static EstadoNaveDTO toDTO(EstadoNave estado) {
        if (estado == null) {
            return null;
        }
        return EstadoNaveDTO.valueOf(estado.name());
    }

    public static EstadoNave toEntity(EstadoNaveDTO dto) {
        if (dto == null) {
            return null;
        }
        return EstadoNave.valueOf(dto.name());
    }
}
