package dtos.mappers;

import dtos.enums.ResultadoDisparoDTO;
import models.enums.ResultadoDisparo;

public class ResultadoDisparoMapper {

    public static ResultadoDisparoDTO toDTO(ResultadoDisparo resultado) {
        if (resultado == null) {
            return null;
        }
        return ResultadoDisparoDTO.valueOf(resultado.name());
    }

    public static ResultadoDisparo toEntity(ResultadoDisparoDTO dto) {
        if (dto == null) {
            return null;
        }
        return ResultadoDisparo.valueOf(dto.name());
    }
}
