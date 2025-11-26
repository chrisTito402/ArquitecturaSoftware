package dtos.mappers;

import dtos.enums.EstadoPartidaDTO;
import models.enums.EstadoPartida;

public class EstadoPartidaMapper {

    public static EstadoPartidaDTO toDTO(EstadoPartida estado) {
        if (estado == null) {
            return null;
        }
        return EstadoPartidaDTO.valueOf(estado.name());
    }

    public static EstadoPartida toEntity(EstadoPartidaDTO dto) {
        if (dto == null) {
            return null;
        }
        return EstadoPartida.valueOf(dto.name());
    }
}
