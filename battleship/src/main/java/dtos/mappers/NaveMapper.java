package dtos.mappers;

import dtos.NaveDTO;
import dtos.enums.TipoNaveDTO;
import models.entidades.Nave;
import models.enums.TipoNave;
import models.factories.NaveFactory;

public class NaveMapper {

    public static NaveDTO toDTO(Nave nave) {
        if (nave == null) {
            return null;
        }

        return new NaveDTO(
                EstadoNaveMapper.toDTO(nave.getEstado()),
                OrientacionNaveMapper.toDTO(nave.getOrientacion()),
                TipoNaveMapper.toDTO(nave.getTipo()),
                nave.getTamanio()
        );
    }

    public static Nave toEntity(NaveDTO dto) {
        if (dto == null) {
            return null;
        }

        TipoNave tipo = TipoNaveMapper.toEntity(dto.getTipo());
        return NaveFactory.crear(tipo, OrientacionNaveMapper.toEntity(dto.getOrientacion()));
    }

    public static TipoNaveDTO toTipoNaveDTO(TipoNave tipoNave) {
        return TipoNaveMapper.toDTO(tipoNave);
    }

    public static TipoNave toTipoNave(TipoNaveDTO tipoNaveDTO) {
        return TipoNaveMapper.toEntity(tipoNaveDTO);
    }
}
