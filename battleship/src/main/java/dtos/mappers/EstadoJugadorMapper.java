package dtos.mappers;

import dtos.enums.EstadoJugadorDTO;
import models.enums.EstadoJugador;

public class EstadoJugadorMapper {

    public static EstadoJugadorDTO toDTO(EstadoJugador estado) {
        if (estado == null) {
            return null;
        }
        return EstadoJugadorDTO.valueOf(estado.name());
    }

    public static EstadoJugador toEntity(EstadoJugadorDTO dto) {
        if (dto == null) {
            return null;
        }
        return EstadoJugador.valueOf(dto.name());
    }
}
