package dtos.mappers;

import dtos.JugadorDTO;
import models.entidades.Jugador;

public class JugadorMapper {

    public static JugadorDTO toDTO(Jugador jugador) {
        if (jugador == null) {
            return null;
        }

        return new JugadorDTO(
                jugador.getNombre(),
                ColorJugadorMapper.toDTO(jugador.getColor()),
                EstadoJugadorMapper.toDTO(jugador.getEstado())
        );
    }

    public static Jugador toEntity(JugadorDTO dto) {
        if (dto == null) {
            return null;
        }

        return new Jugador(
                dto.getNombre(),
                ColorJugadorMapper.toEntity(dto.getColor()),
                EstadoJugadorMapper.toEntity(dto.getEstado())
        );
    }
}
