package dtos.mappers;

import dtos.PuntajeDTO;
import models.entidades.Puntaje;
import models.enums.ResultadoDisparo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para PuntajeMapper.
 * Caso de Uso: Gestionar Puntaje - Capa de DTOs/Mappers
 */
@DisplayName("Pruebas de PuntajeMapper")
class PuntajeMapperTest {

    @Nested
    @DisplayName("Pruebas de toDTO")
    class ToDTOTests {

        @Test
        @DisplayName("Convertir Puntaje a DTO correctamente")
        void convertirPuntajeADTO() {
            Puntaje puntaje = new Puntaje();
            puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);      // +10, 1 acierto
            puntaje.calcularPuntos(ResultadoDisparo.AGUA);         // +0, 1 fallo
            puntaje.calcularPuntos(ResultadoDisparo.HUNDIMIENTO);  // +50, 1 acierto, 1 hundido

            PuntajeDTO dto = PuntajeMapper.toDTO(puntaje);

            assertNotNull(dto);
            assertEquals(60, dto.getPuntosTotales());
            assertEquals(2, dto.getDisparosAcertados());
            assertEquals(1, dto.getDisparosFallados());
            assertEquals(1, dto.getNavesHundidas());
        }

        @Test
        @DisplayName("Precision se calcula correctamente en DTO")
        void precisionCalculadaCorrectamente() {
            Puntaje puntaje = new Puntaje();
            puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);
            puntaje.calcularPuntos(ResultadoDisparo.AGUA);

            PuntajeDTO dto = PuntajeMapper.toDTO(puntaje);

            assertEquals(50.0, dto.getPrecision());
        }

        @Test
        @DisplayName("Puntaje null retorna DTO con valores cero")
        void puntajeNullRetornaDTOVacio() {
            PuntajeDTO dto = PuntajeMapper.toDTO(null);

            assertNotNull(dto);
            assertEquals(0, dto.getPuntosTotales());
            assertEquals(0, dto.getDisparosAcertados());
            assertEquals(0, dto.getDisparosFallados());
            assertEquals(0, dto.getNavesHundidas());
            assertEquals(0.0, dto.getPrecision());
        }

        @Test
        @DisplayName("Puntaje vacio se convierte correctamente")
        void puntajeVacioSeConvierteCorrectamente() {
            Puntaje puntaje = new Puntaje();

            PuntajeDTO dto = PuntajeMapper.toDTO(puntaje);

            assertEquals(0, dto.getPuntosTotales());
            assertEquals(0, dto.getDisparosAcertados());
            assertEquals(0.0, dto.getPrecision());
        }

        @Test
        @DisplayName("DTO preserva todos los campos del Puntaje")
        void dtoPreservaTodosLosCampos() {
            Puntaje puntaje = new Puntaje();
            // Simular una partida completa
            puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);
            puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);
            puntaje.calcularPuntos(ResultadoDisparo.AGUA);
            puntaje.calcularPuntos(ResultadoDisparo.HUNDIMIENTO);
            puntaje.calcularPuntos(ResultadoDisparo.HUNDIMIENTO);
            puntaje.sumarVictoria();

            PuntajeDTO dto = PuntajeMapper.toDTO(puntaje);

            // 10 + 10 + 0 + 50 + 50 + 100 = 220
            assertEquals(220, dto.getPuntosTotales());
            assertEquals(4, dto.getDisparosAcertados());  // 2 impactos + 2 hundimientos
            assertEquals(1, dto.getDisparosFallados());
            assertEquals(2, dto.getNavesHundidas());
            // 4 aciertos de 5 disparos = 80%
            assertEquals(80.0, dto.getPrecision());
        }
    }

    @Nested
    @DisplayName("Pruebas de consistencia")
    class ConsistenciaTests {

        @Test
        @DisplayName("Multiples conversiones dan el mismo resultado")
        void multiplesConversionesMismoResultado() {
            Puntaje puntaje = new Puntaje();
            puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);
            puntaje.calcularPuntos(ResultadoDisparo.HUNDIMIENTO);

            PuntajeDTO dto1 = PuntajeMapper.toDTO(puntaje);
            PuntajeDTO dto2 = PuntajeMapper.toDTO(puntaje);

            assertEquals(dto1.getPuntosTotales(), dto2.getPuntosTotales());
            assertEquals(dto1.getDisparosAcertados(), dto2.getDisparosAcertados());
            assertEquals(dto1.getPrecision(), dto2.getPrecision());
        }

        @Test
        @DisplayName("Conversion no modifica el Puntaje original")
        void conversionNoModificaOriginal() {
            Puntaje puntaje = new Puntaje();
            puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);

            int puntosAntes = puntaje.getPuntosTotales();
            PuntajeMapper.toDTO(puntaje);
            int puntosDespues = puntaje.getPuntosTotales();

            assertEquals(puntosAntes, puntosDespues);
        }
    }

    @Nested
    @DisplayName("Pruebas de fromDTO")
    class FromDTOTests {

        @Test
        @DisplayName("fromDTO con null no lanza excepcion")
        void fromDTOConNullNoLanzaExcepcion() {
            Puntaje puntaje = new Puntaje();

            assertDoesNotThrow(() -> {
                PuntajeMapper.fromDTO(puntaje, null);
            });
        }

        @Test
        @DisplayName("fromDTO con puntaje null no lanza excepcion")
        void fromDTOConPuntajeNullNoLanzaExcepcion() {
            PuntajeDTO dto = new PuntajeDTO(100, 5, 2, 1, 71.4);

            assertDoesNotThrow(() -> {
                PuntajeMapper.fromDTO(null, dto);
            });
        }
    }
}
