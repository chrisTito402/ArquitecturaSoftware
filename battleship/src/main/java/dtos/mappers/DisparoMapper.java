package dtos.mappers;

import dtos.DisparoDTO;
import models.entidades.Disparo;

public class DisparoMapper {

    public static DisparoDTO toDTO(Disparo disparo) {
        if (disparo == null) {
            return null;
        }

        return new DisparoDTO(
                JugadorMapper.toDTO(disparo.getJugador()),
                CoordenadasMapper.toDTO(disparo.getCoordenadas()),
                ResultadoDisparoMapper.toDTO(disparo.getResultadoDisparo()),
                EstadoPartidaMapper.toDTO(disparo.getEstadoPartida())
        );
    }
}
