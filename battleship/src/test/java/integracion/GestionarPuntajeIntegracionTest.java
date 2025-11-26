package integracion;

import dtos.CoordenadasDTO;
import dtos.DisparoDTO;
import dtos.JugadorDTO;
import dtos.PuntajeDTO;
import dtos.enums.EstadoPartidaDTO;
import dtos.enums.ResultadoDisparoDTO;
import dtos.mappers.JugadorMapper;
import dtos.mappers.PuntajeMapper;
import models.builder.Director;
import models.builder.TableroBuilder;
import models.entidades.*;
import models.enums.*;
import models.services.DisparoService;
import models.services.IDisparoService;
import models.services.IPuntajeService;
import models.services.PuntajeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de Integracion - Gestionar Puntaje")
class GestionarPuntajeIntegracionTest {

    private Partida partida;
    private Jugador jugador1;
    private Jugador jugador2;
    private IPuntajeService puntajeService;
    private IDisparoService disparoService;
    private Director director;

    @BeforeEach
    void setUp() {
        director = new Director();
        puntajeService = new PuntajeService();
        disparoService = new DisparoService();

        jugador1 = new Jugador("Jugador1", ColorJugador.ROJO, EstadoJugador.JUGANDO);
        jugador2 = new Jugador("Jugador2", ColorJugador.AZUL, EstadoJugador.JUGANDO);

        TableroBuilder tableroBuilder1 = new TableroBuilder();
        director.makeTablero(tableroBuilder1);
        jugador1.setTablero(tableroBuilder1.getResult());
        jugador1.setNaves(new ArrayList<>());

        TableroBuilder tableroBuilder2 = new TableroBuilder();
        director.makeTablero(tableroBuilder2);
        jugador2.setTablero(tableroBuilder2.getResult());
        jugador2.setNaves(new ArrayList<>());

        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(jugador1);
        jugadores.add(jugador2);

        partida = new Partida(
                jugador1,
                jugadores,
                1, 1, 1, 1, 4,
                EstadoPartida.EN_CURSO,
                new ArrayList<>()
        );
    }

    @Nested
    @DisplayName("Flujo completo de puntaje en disparo")
    class FlujoDisparoTests {

        @BeforeEach
        void colocarNavesParaPrueba() {
            Nave crucero = new Crucero(OrientacionNave.HORIZONTAL);
            List<Coordenadas> coordsCrucero = List.of(
                    new Coordenadas(0, 0),
                    new Coordenadas(0, 1),
                    new Coordenadas(0, 2),
                    new Coordenadas(0, 3)
            );

            jugador2.getTablero().addNave(crucero, coordsCrucero);
            jugador2.getNaves().add(crucero);
        }

        @Test
        @DisplayName("Disparo exitoso actualiza puntaje del jugador")
        void disparoExitosoActualizaPuntaje() {
            Coordenadas coordDisparo = new Coordenadas(0, 0);

            Disparo disparo = disparoService.realizarDisparo(partida, jugador1, coordDisparo, System.currentTimeMillis());

            assertNotNull(disparo);
            assertEquals(ResultadoDisparo.IMPACTO, disparo.getResultado());
            assertEquals(10, jugador1.getPuntaje().getPuntosTotales());
            assertEquals(1, jugador1.getPuntaje().getDisparosAcertados());
        }

        @Test
        @DisplayName("Disparo al agua no suma puntos")
        void disparoAguaNoSumaPuntos() {
            Coordenadas coordDisparo = new Coordenadas(5, 5);

            Disparo disparo = disparoService.realizarDisparo(partida, jugador1, coordDisparo, System.currentTimeMillis());

            assertNotNull(disparo);
            assertEquals(ResultadoDisparo.AGUA, disparo.getResultado());
            assertEquals(0, jugador1.getPuntaje().getPuntosTotales());
            assertEquals(1, jugador1.getPuntaje().getDisparosFallados());
        }

        @Test
        @DisplayName("Puntaje se convierte correctamente a DTO")
        void puntajeSeConvierteADTO() {
            jugador1.getPuntaje().calcularPuntos(ResultadoDisparo.IMPACTO);

            PuntajeDTO dto = PuntajeMapper.toDTO(jugador1.getPuntaje());

            assertEquals(10, dto.getPuntosTotales());
            assertEquals(1, dto.getDisparosAcertados());
            assertEquals(100.0, dto.getPrecision());
        }
    }

    @Nested
    @DisplayName("Flujo de victoria con puntaje")
    class FlujoVictoriaTests {

        @BeforeEach
        void colocarNaveUnica() {
            Nave barco = new Barco(OrientacionNave.HORIZONTAL);
            List<Coordenadas> coordsBarco = List.of(
                    new Coordenadas(0, 0)
            );

            jugador2.getTablero().addNave(barco, coordsBarco);
            jugador2.getNaves().add(barco);
        }

        @Test
        @DisplayName("Hundir nave con un disparo")
        void hundirNaveConUnDisparo() {
            Disparo disparo = disparoService.realizarDisparo(partida, jugador1, new Coordenadas(0, 0), System.currentTimeMillis());

            assertNotNull(disparo);
            assertEquals(ResultadoDisparo.HUNDIMIENTO, disparo.getResultado());
        }
    }

    @Nested
    @DisplayName("Integracion con DTOs y Mappers")
    class IntegracionDTOsTests {

        @Test
        @DisplayName("JugadorDTO preserva puntaje")
        void jugadorDTOPreservaPuntaje() {
            jugador1.getPuntaje().calcularPuntos(ResultadoDisparo.IMPACTO);
            jugador1.getPuntaje().calcularPuntos(ResultadoDisparo.HUNDIMIENTO);

            JugadorDTO dto = JugadorMapper.toDTO(jugador1);

            assertNotNull(dto);
            assertEquals("Jugador1", dto.getNombre());
            assertEquals(dtos.enums.ColorJugadorDTO.ROJO, dto.getColor());
        }

        @Test
        @DisplayName("DisparoDTO incluye puntaje actualizado")
        void disparoDTOIncluyePuntaje() {
            jugador1.getPuntaje().calcularPuntos(ResultadoDisparo.IMPACTO);

            PuntajeDTO puntajeDTO = PuntajeMapper.toDTO(jugador1.getPuntaje());
            JugadorDTO jugadorDTO = JugadorMapper.toDTO(jugador1);

            DisparoDTO disparoDTO = new DisparoDTO(
                    jugadorDTO,
                    new CoordenadasDTO(0, 0),
                    ResultadoDisparoDTO.IMPACTO,
                    EstadoPartidaDTO.EN_CURSO
            );
            disparoDTO.setPuntaje(puntajeDTO);

            assertNotNull(disparoDTO.getPuntaje());
            assertEquals(10, disparoDTO.getPuntaje().getPuntosTotales());
        }
    }

    @Nested
    @DisplayName("Integracion con PuntajeService")
    class IntegracionServiceTests {

        @Test
        @DisplayName("Service registra disparo correctamente")
        void serviceRegistraDisparoCorrectamente() {
            int puntos = puntajeService.registrarDisparo(jugador1.getPuntaje(), ResultadoDisparo.HUNDIMIENTO);

            assertEquals(50, puntos);
            assertEquals(50, jugador1.getPuntaje().getPuntosTotales());
        }

        @Test
        @DisplayName("Service obtiene jugador con mayor puntaje")
        void serviceObtieneJugadorMayorPuntaje() {
            jugador1.getPuntaje().calcularPuntos(ResultadoDisparo.IMPACTO);
            jugador2.getPuntaje().calcularPuntos(ResultadoDisparo.HUNDIMIENTO);

            List<Jugador> jugadores = List.of(jugador1, jugador2);

            Jugador ganador = puntajeService.obtenerJugadorConMayorPuntaje(jugadores);

            assertEquals("Jugador2", ganador.getNombre());
        }

        @Test
        @DisplayName("Service genera reporte comparativo")
        void serviceGeneraReporteComparativo() {
            jugador1.getPuntaje().calcularPuntos(ResultadoDisparo.IMPACTO);
            jugador2.getPuntaje().calcularPuntos(ResultadoDisparo.AGUA);

            List<Jugador> jugadores = List.of(jugador1, jugador2);

            String reporte = puntajeService.generarReporteComparativo(jugadores);

            assertTrue(reporte.contains("Jugador1"));
            assertTrue(reporte.contains("Jugador2"));
            assertTrue(reporte.contains("10"));
        }
    }

    @Nested
    @DisplayName("Escenarios de partida completa")
    class EscenariosPartidaTests {

        @Test
        @DisplayName("Precision se calcula correctamente tras multiples disparos")
        void precisionMultiplesDisparos() {
            jugador1.getPuntaje().calcularPuntos(ResultadoDisparo.IMPACTO);
            jugador1.getPuntaje().calcularPuntos(ResultadoDisparo.IMPACTO);
            jugador1.getPuntaje().calcularPuntos(ResultadoDisparo.AGUA);
            jugador1.getPuntaje().calcularPuntos(ResultadoDisparo.HUNDIMIENTO);

            assertEquals(75.0, jugador1.getPuntaje().getPrecision());
            assertEquals(70, jugador1.getPuntaje().getPuntosTotales());
        }

        @Test
        @DisplayName("Puntaje se puede reiniciar para nueva partida")
        void puntajeSeReinicia() {
            jugador1.getPuntaje().calcularPuntos(ResultadoDisparo.IMPACTO);
            jugador1.getPuntaje().sumarVictoria();

            puntajeService.reiniciarPuntaje(jugador1.getPuntaje());

            assertEquals(0, jugador1.getPuntaje().getPuntosTotales());
            assertFalse(jugador1.getPuntaje().tieneDatos());
        }
    }
}
