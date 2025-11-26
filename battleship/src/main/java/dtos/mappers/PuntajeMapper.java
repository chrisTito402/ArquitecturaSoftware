package dtos.mappers;

import models.entidades.Puntaje;
import dtos.PuntajeDTO;

/**
 * Mapper para convertir entre la entidad Puntaje y PuntajeDTO.
 *
 * Arquitectura en Capas: Separa la capa de dominio (Puntaje) de la capa
 * de presentación/transferencia (PuntajeDTO).
 *
 * Caso de Uso: Gestionar Puntaje - Componente de conversión
 *
 * @author Fred
 */
public class PuntajeMapper {

    /**
     * Convierte una entidad Puntaje a un PuntajeDTO.
     *
     * Arquitectura: Este método pertenece a la capa de transferencia,
     * permitiendo que la capa de presentación reciba datos sin conocer
     * la entidad de dominio interna.
     *
     * @param puntaje La entidad Puntaje del dominio
     * @return El DTO correspondiente, o un DTO vacío si puntaje es null
     */
    public static PuntajeDTO toDTO(Puntaje puntaje) {
        if (puntaje == null) {
            return new PuntajeDTO(0, 0, 0, 0, 0.0);
        }

        return new PuntajeDTO(
                puntaje.getPuntosTotales(),
                puntaje.getDisparosAcertados(),
                puntaje.getDisparosFallados(),
                puntaje.getNavesHundidas(),
                puntaje.getPrecision()
        );
    }

    /**
     * Actualiza una entidad Puntaje con los datos de un PuntajeDTO.
     * Nota: En general, los DTOs fluyen del dominio hacia la presentación,
     * no al revés. Este método existe por completitud pero debe usarse con precaución.
     *
     * @param puntaje La entidad a actualizar
     * @param dto El DTO con los datos
     */
    public static void fromDTO(Puntaje puntaje, PuntajeDTO dto) {
        if (puntaje == null || dto == null) {
            return;
        }

        // Nota: Puntaje no tiene setters públicos para mantener la integridad,
        // por lo que este método tiene funcionalidad limitada.
        // El puntaje debe modificarse a través de sus métodos de negocio.
        // Este método existe para casos especiales donde se necesita sincronización.
    }
}
