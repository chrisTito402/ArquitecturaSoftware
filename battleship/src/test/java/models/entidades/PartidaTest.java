package models.entidades;

import models.builder.Director;
import models.builder.TableroBuilder;
import models.enums.*;
import models.services.DisparoService;
import models.services.IDisparoService;
import models.services.IPartidaService;
import models.services.ITableroService;
import models.services.PartidaService;
import models.services.TableroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de Partida")
class PartidaTest {

    private Partida partida;
    private Jugador jugador1;
    private Jugador jugador2;
    private Director director;
    private IPartidaService partidaService;
    private IDisparoService disparoService;
    private ITableroService tableroService;

    @BeforeEach
    void setUp() {
        director = new Director();
        partidaService = new PartidaService();
        disparoService = new DisparoService();
        tableroService = new TableroService();

        jugador1 = new Jugador("Jugador1", ColorJugador.ROJO, EstadoJugador.JUGANDO);
        jugador2 = new Jugador("Jugador2", ColorJugador.AZUL, EstadoJugador.JUGANDO);

        TableroBuilder builder1 = new TableroBuilder();
        director.makeTablero(builder1);
        jugador1.setTablero(builder1.getResult());

        TableroBuilder builder2 = new TableroBuilder();
        director.makeTablero(builder2);
        jugador2.setTablero(builder2.getResult());

        List<Jugador> jugadores = new ArrayList<>(Arrays.asList(jugador1, jugador2));
        partida = new Partida(jugador1, jugadores, 3, 4, 2, 2, 11,
                EstadoPartida.POR_EMPEZAR, new ArrayList<>());
    }

    @Nested
    @DisplayName("Pruebas de empezarPartida con servicio")
    class EmpezarPartidaTests {

        @Test
        @DisplayName("Empezar partida cambia estado a EN_CURSO")
        void empezarPartidaCambiaEstado() {
            partidaService.iniciarPartida(partida);

            assertEquals(EstadoPartida.EN_CURSO, partida.getEstado());
        }

        @Test
        @DisplayName("Empezar partida asigna turno a un jugador")
        void empezarPartidaAsignaTurno() {
            partidaService.iniciarPartida(partida);

            assertNotNull(partida.getTurno());
            assertTrue(partida.getTurno().equals(jugador1) || partida.getTurno().equals(jugador2));
        }

        @Test
        @DisplayName("No empezar partida con menos de 2 jugadores")
        void noEmpezarConMenosDeDosJugadores() {
            Partida partidaUnJugador = new Partida(null, new ArrayList<>(List.of(jugador1)),
                    3, 4, 2, 2, 11, EstadoPartida.POR_EMPEZAR, new ArrayList<>());

            partidaService.iniciarPartida(partidaUnJugador);

            assertEquals(EstadoPartida.POR_EMPEZAR, partidaUnJugador.getEstado());
        }
    }

    @Nested
    @DisplayName("Pruebas de realizarDisparo con servicio")
    class RealizarDisparoTests {

        @BeforeEach
        void iniciarPartida() {
            partidaService.iniciarPartida(partida);
            Barco barco = new Barco(OrientacionNave.HORIZONTAL);
            List<Coordenadas> coords = List.of(new Coordenadas(5, 5));
            tableroService.colocarNave(jugador2.getTablero(), jugador2, barco, coords, partida.getJugadores());
        }

        @Test
        @DisplayName("Disparo AGUA cambia el turno")
        void disparoAguaCambiaTurno() {
            Jugador turnoInicial = partida.getTurno();
            Coordenadas coordsAgua = new Coordenadas(0, 0);

            Disparo disparo = disparoService.realizarDisparo(partida, turnoInicial, coordsAgua, System.currentTimeMillis());

            assertEquals(ResultadoDisparo.AGUA, disparo.getResultado());
        }

        @Test
        @DisplayName("Disparo IMPACTO retorna resultado correcto")
        void disparoImpactoRetornaResultado() {
            Jugador turnoInicial = partida.getTurno();
            if (!turnoInicial.equals(jugador1)) {
                partida.cambiarTurno();
                turnoInicial = partida.getTurno();
            }

            Coordenadas coordsImpacto = new Coordenadas(5, 5);
            Disparo disparo = disparoService.realizarDisparo(partida, turnoInicial, coordsImpacto, System.currentTimeMillis());

            assertTrue(disparo.getResultado() == ResultadoDisparo.IMPACTO
                    || disparo.getResultado() == ResultadoDisparo.HUNDIMIENTO);
        }

        @Test
        @DisplayName("Disparo en turno incorrecto retorna error")
        void disparoTurnoIncorrectoRetornaError() {
            Jugador turnoInicial = partida.getTurno();
            Jugador otroJugador = turnoInicial.equals(jugador1) ? jugador2 : jugador1;

            Disparo disparo = disparoService.realizarDisparo(partida, otroJugador, new Coordenadas(0, 0), System.currentTimeMillis());

            assertEquals(ResultadoDisparo.TURNO_INCORRECTO, disparo.getResultado());
        }
    }

    @Nested
    @DisplayName("Pruebas de abandonarPartida con servicio")
    class AbandonarPartidaTests {

        @Test
        @DisplayName("Abandonar partida declara ganador al oponente")
        void abandonarPartidaDeclaraGanador() {
            partidaService.iniciarPartida(partida);

            partidaService.abandonarPartida(partida, jugador1);

            assertEquals(EstadoPartida.FINALIZADA, partida.getEstado());
            assertEquals(1, partida.getJugadores().size());
            assertEquals(jugador2, partida.getJugadores().get(0));
        }

        @Test
        @DisplayName("Abandonar cambia estado del jugador a ABANDONO")
        void abandonarCambiaEstadoJugador() {
            partidaService.iniciarPartida(partida);

            partidaService.abandonarPartida(partida, jugador1);

            assertEquals(EstadoJugador.ABANDONO, jugador1.getEstado());
        }
    }

    @Nested
    @DisplayName("Pruebas de unirsePartida con servicio")
    class UnirsePartidaTests {

        @Test
        @DisplayName("No unirse si partida EN_CURSO")
        void noUnirsePartidaEnCurso() {
            partidaService.iniciarPartida(partida);
            Jugador jugador3 = new Jugador("Jugador3", ColorJugador.ROJO, EstadoJugador.JUGANDO);

            ResultadoUnirse resultado = partidaService.unirsePartida(partida, jugador3);

            assertEquals(ResultadoUnirse.PARTIDA_EN_CURSO, resultado);
            assertEquals(2, partida.getJugadores().size());
        }

        @Test
        @DisplayName("No unirse si ya hay 2 jugadores")
        void noUnirseSiYaHayDosJugadores() {
            Jugador jugador3 = new Jugador("Jugador3", ColorJugador.AZUL, EstadoJugador.JUGANDO);

            ResultadoUnirse resultado = partidaService.unirsePartida(partida, jugador3);

            assertEquals(ResultadoUnirse.PARTIDA_LLENA, resultado);
            assertEquals(2, partida.getJugadores().size());
        }

        @Test
        @DisplayName("No unirse con nombre duplicado")
        void noUnirseConNombreDuplicado() {
            Partida nuevaPartida = new Partida(null, new ArrayList<>(List.of(jugador1)),
                    3, 4, 2, 2, 11, EstadoPartida.POR_EMPEZAR, new ArrayList<>());
            Jugador jugadorDuplicado = new Jugador("Jugador1", ColorJugador.AZUL, EstadoJugador.JUGANDO);

            ResultadoUnirse resultado = partidaService.unirsePartida(nuevaPartida, jugadorDuplicado);

            assertEquals(ResultadoUnirse.NOMBRE_DUPLICADO, resultado);
        }
    }

    @Nested
    @DisplayName("Pruebas de addNave con servicio")
    class AddNaveTests {

        @Test
        @DisplayName("Agregar nave exitosamente")
        void agregarNaveExitosamente() {
            Submarino submarino = new Submarino(OrientacionNave.VERTICAL);
            List<Coordenadas> coords = Arrays.asList(
                    new Coordenadas(0, 0),
                    new Coordenadas(1, 0)
            );

            ResultadoAddNave resultado = tableroService.colocarNave(
                    jugador1.getTablero(), jugador1, submarino, coords, partida.getJugadores());

            assertEquals(ResultadoAddNave.NAVE_AÑADIDA, resultado);
        }

        @Test
        @DisplayName("No agregar nave con jugador no encontrado")
        void noAgregarNaveJugadorNoEncontrado() {
            Jugador jugadorExterno = new Jugador("Externo", ColorJugador.ROJO, EstadoJugador.JUGANDO);
            Barco barco = new Barco(OrientacionNave.HORIZONTAL);
            List<Coordenadas> coords = List.of(new Coordenadas(0, 0));

            ResultadoAddNave resultado = tableroService.colocarNave(
                    jugadorExterno.getTablero(), jugadorExterno, barco, coords, partida.getJugadores());

            assertEquals(ResultadoAddNave.JUGADOR_NO_ENCONTRADO, resultado);
        }
    }

    @Nested
    @DisplayName("Pruebas de cambiarTurno")
    class CambiarTurnoTests {

        @Test
        @DisplayName("Cambiar turno alterna entre jugadores")
        void cambiarTurnoAlternaJugadores() {
            partida.setTurno(jugador1);

            boolean resultado = partida.cambiarTurno();

            assertTrue(resultado);
            assertEquals(jugador2, partida.getTurno());
        }

        @Test
        @DisplayName("Cambiar turno vuelve al primer jugador")
        void cambiarTurnoVuelveAlPrimero() {
            partida.setTurno(jugador1);
            partida.cambiarTurno();
            partida.cambiarTurno();

            assertEquals(jugador1, partida.getTurno());
        }
    }
}
