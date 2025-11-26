package models.entidades;

import models.builder.Director;
import models.builder.TableroBuilder;
import models.enums.*;
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

    @BeforeEach
    void setUp() {
        director = new Director();

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
    @DisplayName("Pruebas de empezarPartida")
    class EmpezarPartidaTests {

        @Test
        @DisplayName("Empezar partida cambia estado a EN_CURSO")
        void empezarPartidaCambiaEstado() {
            partida.empezarPartida();

            assertEquals(EstadoPartida.EN_CURSO, partida.getEstado());
        }

        @Test
        @DisplayName("Empezar partida asigna turno a un jugador")
        void empezarPartidaAsignaTurno() {
            partida.empezarPartida();

            assertNotNull(partida.getTurno());
            assertTrue(partida.getTurno().equals(jugador1) || partida.getTurno().equals(jugador2));
        }

        @Test
        @DisplayName("No empezar partida con menos de 2 jugadores")
        void noEmpezarConMenosDeDosJugadores() {
            Partida partidaUnJugador = new Partida(null, new ArrayList<>(List.of(jugador1)),
                    3, 4, 2, 2, 11, EstadoPartida.POR_EMPEZAR, new ArrayList<>());

            partidaUnJugador.empezarPartida();

            assertEquals(EstadoPartida.POR_EMPEZAR, partidaUnJugador.getEstado());
        }
    }

    @Nested
    @DisplayName("Pruebas de realizarDisparo")
    class RealizarDisparoTests {

        @BeforeEach
        void iniciarPartida() {
            partida.empezarPartida();
            Barco barco = new Barco(OrientacionNave.HORIZONTAL);
            List<Coordenadas> coords = List.of(new Coordenadas(5, 5));
            partida.addNave(jugador2, barco, coords);
        }

        @Test
        @DisplayName("Disparo AGUA cambia el turno")
        void disparoAguaCambiaTurno() {
            Jugador turnoInicial = partida.getTurno();
            Coordenadas coordsAgua = new Coordenadas(0, 0);

            Disparo disparo = partida.realizarDisparo(coordsAgua, turnoInicial, System.currentTimeMillis());

            assertEquals(ResultadoDisparo.AGUA, disparo.getResultadoDisparo());
            assertNotEquals(turnoInicial, partida.getTurno());
        }

        @Test
        @DisplayName("Disparo IMPACTO NO cambia el turno")
        void disparoImpactoNoCambiaTurno() {
            Jugador turnoInicial = partida.getTurno();
            if (!turnoInicial.equals(jugador1)) {
                partida.cambiarTurno();
                turnoInicial = partida.getTurno();
            }

            Coordenadas coordsImpacto = new Coordenadas(5, 5);
            Disparo disparo = partida.realizarDisparo(coordsImpacto, turnoInicial, System.currentTimeMillis());

            assertTrue(disparo.getResultadoDisparo() == ResultadoDisparo.IMPACTO
                    || disparo.getResultadoDisparo() == ResultadoDisparo.HUNDIMIENTO);
            assertEquals(turnoInicial, partida.getTurno());
        }

        @Test
        @DisplayName("Disparo a coordenadas ya disparadas retorna YA_DISPARADO")
        void disparoYaDisparadoCambiaTurno() {
            Jugador turnoInicial = partida.getTurno();
            Coordenadas coords = new Coordenadas(0, 0);

            Disparo primerDisparo = partida.realizarDisparo(coords, turnoInicial, System.currentTimeMillis());
            assertEquals(ResultadoDisparo.AGUA, primerDisparo.getResultadoDisparo());

            Jugador segundoTurno = partida.getTurno();
            assertNotEquals(turnoInicial, segundoTurno);

            Coordenadas otraCoord = new Coordenadas(1, 1);
            partida.realizarDisparo(otraCoord, segundoTurno, System.currentTimeMillis());

            assertEquals(turnoInicial, partida.getTurno());
            Disparo disparoRepetido = partida.realizarDisparo(coords, turnoInicial, System.currentTimeMillis());

            assertEquals(ResultadoDisparo.YA_DISPARADO, disparoRepetido.getResultadoDisparo());
        }

        @Test
        @DisplayName("Disparo en turno incorrecto retorna error")
        void disparoTurnoIncorrectoRetornaError() {
            Jugador turnoInicial = partida.getTurno();
            Jugador otroJugador = turnoInicial.equals(jugador1) ? jugador2 : jugador1;

            Disparo disparo = partida.realizarDisparo(new Coordenadas(0, 0), otroJugador, System.currentTimeMillis());

            assertEquals(ResultadoDisparo.TURNO_INCORRECTO, disparo.getResultadoDisparo());
        }
    }

    @Nested
    @DisplayName("Pruebas de abandonarPartida")
    class AbandonarPartidaTests {

        @Test
        @DisplayName("Abandonar partida declara ganador al oponente")
        void abandonarPartidaDeclaraGanador() {
            partida.empezarPartida();

            partida.abandonarPartida(jugador1);

            assertEquals(EstadoPartida.FINALIZADA, partida.getEstado());
            assertEquals(1, partida.getJugadores().size());
            assertEquals(jugador2, partida.getJugadores().get(0));
        }

        @Test
        @DisplayName("Abandonar cambia estado del jugador a ABANDONO")
        void abandonarCambiaEstadoJugador() {
            partida.empezarPartida();

            partida.abandonarPartida(jugador1);

            assertEquals(EstadoJugador.ABANDONO, jugador1.getEstado());
        }
    }

    @Nested
    @DisplayName("Pruebas de unirsePartida")
    class UnirsePartidaTests {

        @Test
        @DisplayName("No unirse si partida EN_CURSO")
        void noUnirsePartidaEnCurso() {
            partida.empezarPartida();
            Jugador jugador3 = new Jugador("Jugador3", ColorJugador.ROJO, EstadoJugador.JUGANDO);

            partida.unirsePartida(jugador3);

            assertEquals(2, partida.getJugadores().size());
        }

        @Test
        @DisplayName("No unirse si ya hay 2 jugadores")
        void noUnirseSiYaHayDosJugadores() {
            Jugador jugador3 = new Jugador("Jugador3", ColorJugador.AZUL, EstadoJugador.JUGANDO);

            partida.unirsePartida(jugador3);

            assertEquals(2, partida.getJugadores().size());
        }
    }

    @Nested
    @DisplayName("Pruebas de addNave")
    class AddNaveTests {

        @Test
        @DisplayName("Agregar nave exitosamente")
        void agregarNaveExitosamente() {
            Submarino submarino = new Submarino(OrientacionNave.VERTICAL);
            List<Coordenadas> coords = Arrays.asList(
                    new Coordenadas(0, 0),
                    new Coordenadas(1, 0)
            );

            ResultadoAddNave resultado = partida.addNave(jugador1, submarino, coords);

            assertEquals(ResultadoAddNave.NAVE_AÑADIDA, resultado);
        }

        @Test
        @DisplayName("No agregar nave con jugador no encontrado")
        void noAgregarNaveJugadorNoEncontrado() {
            Jugador jugadorExterno = new Jugador("Externo", ColorJugador.ROJO, EstadoJugador.JUGANDO);
            Barco barco = new Barco(OrientacionNave.HORIZONTAL);
            List<Coordenadas> coords = List.of(new Coordenadas(0, 0));

            ResultadoAddNave resultado = partida.addNave(jugadorExterno, barco, coords);

            assertEquals(ResultadoAddNave.JUGADOR_NO_ENCONTRADO, resultado);
        }
    }
}
