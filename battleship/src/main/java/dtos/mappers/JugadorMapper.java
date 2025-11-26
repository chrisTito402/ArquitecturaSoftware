package dtos.mappers;

import models.entidades.Jugador;
import dtos.JugadorDTO;

/**
 * Mapper para convertir entre entidades de Jugador y JugadorDTO.
 *
 * Arquitectura en Capas: Facilita la separación entre la capa de dominio
 * y la capa de transferencia de datos.
 *
 * @author Fred
 */
public class JugadorMapper {

    /**
     * Convierte una entidad Jugador a un JugadorDTO.
     *
     * @param jugador La entidad Jugador del dominio
     * @return El DTO correspondiente
     */
    public static JugadorDTO toDTO(Jugador jugador) {
        if (jugador == null) {
            return null;
        }

        return new JugadorDTO(
                jugador.getNombre(),
                jugador.getColor(),
                jugador.getEstado()
        );
    }

    /**
     * Crea una entidad Jugador básica desde un JugadorDTO.
     * Nota: Este método crea un Jugador sin tablero ni naves,
     * útil para operaciones de transferencia de datos.
     *
     * @param dto El DTO con los datos básicos
     * @return Una nueva instancia de Jugador
     */
    public static Jugador toEntity(JugadorDTO dto) {
        if (dto == null) {
            return null;
        }

        return new Jugador(
                dto.getNombre(),
                dto.getColor(),
                dto.getEstado()
        );
    }
}
