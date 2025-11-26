package dtos.mappers;

import dtos.CoordenadasDTO;
import models.entidades.Coordenadas;
import java.util.List;
import java.util.stream.Collectors;

public class CoordenadasMapper {

    public static CoordenadasDTO toDTO(Coordenadas coordenadas) {
        if (coordenadas == null) {
            return null;
        }

        return new CoordenadasDTO(
                coordenadas.getX(),
                coordenadas.getY()
        );
    }

    public static Coordenadas toEntity(CoordenadasDTO dto) {
        if (dto == null) {
            return null;
        }

        return new Coordenadas(
                dto.getX(),
                dto.getY()
        );
    }

    public static List<CoordenadasDTO> toDTOList(List<Coordenadas> coordenadas) {
        if (coordenadas == null) {
            return null;
        }

        return coordenadas.stream()
                .map(CoordenadasMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static List<Coordenadas> toEntityList(List<CoordenadasDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(CoordenadasMapper::toEntity)
                .collect(Collectors.toList());
    }
}
