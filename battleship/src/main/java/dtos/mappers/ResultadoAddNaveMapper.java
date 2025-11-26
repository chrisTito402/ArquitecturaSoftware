package dtos.mappers;

import dtos.enums.ResultadoAddNaveDTO;
import models.enums.ResultadoAddNave;

public class ResultadoAddNaveMapper {

    public static ResultadoAddNaveDTO toDTO(ResultadoAddNave resultado) {
        if (resultado == null) {
            return null;
        }
        return ResultadoAddNaveDTO.valueOf(resultado.name());
    }

    public static ResultadoAddNave toEntity(ResultadoAddNaveDTO dto) {
        if (dto == null) {
            return null;
        }
        return ResultadoAddNave.valueOf(dto.name());
    }
}
