package dtos.mappers;

import dtos.enums.OrientacionNaveDTO;
import models.enums.OrientacionNave;

public class OrientacionNaveMapper {

    public static OrientacionNaveDTO toDTO(OrientacionNave orientacion) {
        if (orientacion == null) {
            return null;
        }
        return OrientacionNaveDTO.valueOf(orientacion.name());
    }

    public static OrientacionNave toEntity(OrientacionNaveDTO dto) {
        if (dto == null) {
            return null;
        }
        return OrientacionNave.valueOf(dto.name());
    }
}
