package models.services;

import dtos.PuntajeDTO;
import models.entidades.Jugador;
import models.entidades.Puntaje;
import models.enums.ColorJugador;
import models.enums.EstadoJugador;
import models.enums.ResultadoDisparo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para PuntajeService.
 * Caso de Uso: Gestionar Puntaje - Capa de Servicios
 */
@DisplayName("Pruebas de PuntajeService")
class PuntajeServiceTest {

    private PuntajeService puntajeService;
    private Puntaje puntaje;

    @BeforeEach
    void setUp() {
        puntajeService = new PuntajeService();
        puntaje = new Puntaje();
    }

    @Nested
    @DisplayName("Pruebas de registrarDisparo")
    class RegistrarDisparoTests {

        @Test
        @DisplayName("Registrar disparo IMPACTO retorna puntos correctos")
        void registrarDisparoImpacto() {
            int puntos = puntajeService.registrarDisparo(puntaje, ResultadoDisparo.IMPACTO);

            assertEquals(10, puntos);
            assertEquals(10, puntaje.getPuntosTotales());
        }

        @Test
        @DisplayName("Registrar disparo HUNDIMIENTO retorna 50 puntos")
        void registrarDisparoHundimiento() {
            int puntos = puntajeService.registrarDisparo(puntaje, ResultadoDisparo.HUNDIMIENTO);

            assertEquals(50, puntos);
        }

        @Test
        @DisplayName("Registrar disparo AGUA retorna 0 puntos")
        void registrarDisparoAgua() {
            int puntos = puntajeService.registrarDisparo(puntaje, ResultadoDisparo.AGUA);

            assertEquals(0, puntos);
        }

        @Test
        @DisplayName("Registrar disparo con puntaje null lanza excepcion")
        void registrarDisparoConPuntajeNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                puntajeService.registrarDisparo(null, ResultadoDisparo.IMPACTO);
            });
        }
    }

    @Nested
    @DisplayName("Pruebas de registrarVictoria")
    class RegistrarVictoriaTests {

        @Test
        @DisplayName("Registrar victoria suma 100 puntos")
        void registrarVictoriaSumaPuntos() {
            puntajeService.registrarVictoria(puntaje);

            assertEquals(100, puntaje.getPuntosTotales());
        }

        @Test
        @DisplayName("Registrar victoria con puntaje null lanza excepcion")
        void registrarVictoriaConPuntajeNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                puntajeService.registrarVictoria(null);
            });
        }
    }

    @Nested
    @DisplayName("Pruebas de reiniciarPuntaje")
    class ReiniciarPuntajeTests {

        @Test
        @DisplayName("Reiniciar puntaje pone valores en cero")
        void reiniciarPuntaje() {
            puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);
            puntaje.calcularPuntos(ResultadoDisparo.HUNDIMIENTO);

            puntajeService.reiniciarPuntaje(puntaje);

            assertEquals(0, puntaje.getPuntosTotales());
            assertEquals(0, puntaje.getDisparosAcertados());
        }

        @Test
        @DisplayName("Reiniciar con puntaje null lanza excepcion")
        void reiniciarConPuntajeNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                puntajeService.reiniciarPuntaje(null);
            });
        }
    }

    @Nested
    @DisplayName("Pruebas de conversion a DTO usando PuntajeMapper")
    class ConversionDTOTests {

        @Test
        @DisplayName("PuntajeMapper convierte puntaje a DTO correctamente")
        void mapperConviertePuntajeADTO() {
            puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);
            puntaje.calcularPuntos(ResultadoDisparo.AGUA);

            // La conversion se hace mediante el Mapper, no el Service
            // Esto respeta la arquitectura en capas
            PuntajeDTO dto = dtos.mappers.PuntajeMapper.toDTO(puntaje);

            assertNotNull(dto);
            assertEquals(10, dto.getPuntosTotales());
            assertEquals(1, dto.getDisparosAcertados());
            assertEquals(1, dto.getDisparosFallados());
            assertEquals(50.0, dto.getPrecision());
        }

        @Test
        @DisplayName("PuntajeMapper maneja puntaje null")
        void mapperManejaNull() {
            PuntajeDTO dto = dtos.mappers.PuntajeMapper.toDTO(null);

            assertNotNull(dto);
            assertEquals(0, dto.getPuntosTotales());
        }
    }

    @Nested
    @DisplayName("Pruebas de compararPuntajes")
    class CompararPuntajesTests {

        @Test
        @DisplayName("Comparar puntajes - primero mayor")
        void compararPuntajesPrimeroMayor() {
            Puntaje puntaje1 = new Puntaje();
            Puntaje puntaje2 = new Puntaje();

            puntaje1.calcularPuntos(ResultadoDisparo.HUNDIMIENTO);  // 50
            puntaje2.calcularPuntos(ResultadoDisparo.IMPACTO);       // 10

            int resultado = puntajeService.compararPuntajes(puntaje1, puntaje2);

            assertTrue(resultado > 0);
        }

        @Test
        @DisplayName("Comparar puntajes - segundo mayor")
        void compararPuntajesSegundoMayor() {
            Puntaje puntaje1 = new Puntaje();
            Puntaje puntaje2 = new Puntaje();

            puntaje1.calcularPuntos(ResultadoDisparo.IMPACTO);       // 10
            puntaje2.calcularPuntos(ResultadoDisparo.HUNDIMIENTO);  // 50

            int resultado = puntajeService.compararPuntajes(puntaje1, puntaje2);

            assertTrue(resultado < 0);
        }

        @Test
        @DisplayName("Comparar puntajes iguales")
        void compararPuntajesIguales() {
            Puntaje puntaje1 = new Puntaje();
            Puntaje puntaje2 = new Puntaje();

            puntaje1.calcularPuntos(ResultadoDisparo.IMPACTO);
            puntaje2.calcularPuntos(ResultadoDisparo.IMPACTO);

            int resultado = puntajeService.compararPuntajes(puntaje1, puntaje2);

            assertEquals(0, resultado);
        }

        @Test
        @DisplayName("Comparar con puntaje null lanza excepcion")
        void compararConPuntajeNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                puntajeService.compararPuntajes(null, puntaje);
            });
        }
    }

    @Nested
    @DisplayName("Pruebas de obtenerJugadorConMayorPuntaje")
    class ObtenerJugadorConMayorPuntajeTests {

        @Test
        @DisplayName("Obtener jugador con mayor puntaje")
        void obtenerJugadorConMayorPuntaje() {
            Jugador jugador1 = new Jugador("Jugador1", ColorJugador.ROJO, EstadoJugador.JUGANDO);
            Jugador jugador2 = new Jugador("Jugador2", ColorJugador.AZUL, EstadoJugador.JUGANDO);

            jugador1.getPuntaje().calcularPuntos(ResultadoDisparo.IMPACTO);      // 10
            jugador2.getPuntaje().calcularPuntos(ResultadoDisparo.HUNDIMIENTO);  // 50

            List<Jugador> jugadores = Arrays.asList(jugador1, jugador2);

            Jugador ganador = puntajeService.obtenerJugadorConMayorPuntaje(jugadores);

            assertNotNull(ganador);
            assertEquals("Jugador2", ganador.getNombre());
        }

        @Test
        @DisplayName("Lista vacia retorna null")
        void listaVaciaRetornaNull() {
            Jugador resultado = puntajeService.obtenerJugadorConMayorPuntaje(new ArrayList<>());

            assertNull(resultado);
        }

        @Test
        @DisplayName("Lista null retorna null")
        void listaNullRetornaNull() {
            Jugador resultado = puntajeService.obtenerJugadorConMayorPuntaje(null);

            assertNull(resultado);
        }
    }

    @Nested
    @DisplayName("Pruebas de generarReporteComparativo")
    class GenerarReporteComparativoTests {

        @Test
        @DisplayName("Generar reporte con jugadores")
        void generarReporteConJugadores() {
            Jugador jugador1 = new Jugador("Ana", ColorJugador.ROJO, EstadoJugador.JUGANDO);
            Jugador jugador2 = new Jugador("Bob", ColorJugador.AZUL, EstadoJugador.JUGANDO);

            jugador1.getPuntaje().calcularPuntos(ResultadoDisparo.IMPACTO);
            jugador2.getPuntaje().calcularPuntos(ResultadoDisparo.HUNDIMIENTO);

            List<Jugador> jugadores = Arrays.asList(jugador1, jugador2);

            String reporte = puntajeService.generarReporteComparativo(jugadores);

            assertTrue(reporte.contains("Ana"));
            assertTrue(reporte.contains("Bob"));
            assertTrue(reporte.contains("REPORTE"));
        }

        @Test
        @DisplayName("Lista vacia genera mensaje apropiado")
        void listaVaciaGeneraMensaje() {
            String reporte = puntajeService.generarReporteComparativo(new ArrayList<>());

            assertTrue(reporte.contains("No hay jugadores"));
        }
    }

    @Nested
    @DisplayName("Pruebas de calcularPrecisionPromedio")
    class CalcularPrecisionPromedioTests {

        @Test
        @DisplayName("Calcular precision promedio correctamente")
        void calcularPrecisionPromedio() {
            Jugador jugador1 = new Jugador("J1", ColorJugador.ROJO, EstadoJugador.JUGANDO);
            Jugador jugador2 = new Jugador("J2", ColorJugador.AZUL, EstadoJugador.JUGANDO);

            // J1: 100% precision (1 acierto de 1)
            jugador1.getPuntaje().calcularPuntos(ResultadoDisparo.IMPACTO);

            // J2: 50% precision (1 acierto de 2)
            jugador2.getPuntaje().calcularPuntos(ResultadoDisparo.IMPACTO);
            jugador2.getPuntaje().calcularPuntos(ResultadoDisparo.AGUA);

            List<Jugador> jugadores = Arrays.asList(jugador1, jugador2);

            double promedio = puntajeService.calcularPrecisionPromedio(jugadores);

            // (100 + 50) / 2 = 75
            assertEquals(75.0, promedio);
        }

        @Test
        @DisplayName("Lista vacia retorna 0")
        void listaVaciaRetornaCero() {
            double promedio = puntajeService.calcularPrecisionPromedio(new ArrayList<>());

            assertEquals(0.0, promedio);
        }
    }

    @Nested
    @DisplayName("Pruebas de tienePuntajeValido")
    class TienePuntajeValidoTests {

        @Test
        @DisplayName("Jugador con puntaje y datos es valido")
        void jugadorConPuntajeYDatosEsValido() {
            Jugador jugador = new Jugador("Test", ColorJugador.ROJO, EstadoJugador.JUGANDO);
            jugador.getPuntaje().calcularPuntos(ResultadoDisparo.IMPACTO);

            assertTrue(puntajeService.tienePuntajeValido(jugador));
        }

        @Test
        @DisplayName("Jugador sin disparos no es valido")
        void jugadorSinDisparosNoEsValido() {
            Jugador jugador = new Jugador("Test", ColorJugador.ROJO, EstadoJugador.JUGANDO);

            assertFalse(puntajeService.tienePuntajeValido(jugador));
        }

        @Test
        @DisplayName("Jugador null no es valido")
        void jugadorNullNoEsValido() {
            assertFalse(puntajeService.tienePuntajeValido(null));
        }
    }
}
