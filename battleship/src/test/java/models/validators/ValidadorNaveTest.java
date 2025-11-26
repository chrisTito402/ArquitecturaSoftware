package models.validators;

import models.builder.Director;
import models.builder.TableroBuilder;
import models.entidades.*;
import models.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para ValidadorNave.
 * Caso de Uso: Cargar Tablero - Validaciones
 */
@DisplayName("Pruebas de ValidadorNave")
class ValidadorNaveTest {

    private ValidadorNave validador;
    private Tablero tablero;
    private Jugador jugador;
    private List<Jugador> jugadores;

    @BeforeEach
    void setUp() {
        validador = new ValidadorNave();

        // Crear tablero usando el builder
        Director director = new Director();
        TableroBuilder builder = new TableroBuilder();
        director.makeTablero(builder);
        tablero = builder.getResult();

        // Crear jugador
        jugador = new Jugador("TestPlayer", ColorJugador.ROJO, EstadoJugador.JUGANDO);
        jugador.setTablero(tablero);
        jugador.setNaves(new ArrayList<>());

        jugadores = new ArrayList<>();
        jugadores.add(jugador);
    }

    @Nested
    @DisplayName("Pruebas de validarJugador")
    class ValidarJugadorTests {

        @Test
        @DisplayName("Jugador valido retorna null")
        void jugadorValidoRetornaNull() {
            ResultadoAddNave resultado = validador.validarJugador(jugador, jugadores);

            assertNull(resultado);
        }

        @Test
        @DisplayName("Jugador null retorna JUGADOR_NULL")
        void jugadorNullRetornaError() {
            ResultadoAddNave resultado = validador.validarJugador(null, jugadores);

            assertEquals(ResultadoAddNave.JUGADOR_NULL, resultado);
        }

        @Test
        @DisplayName("Jugador con nombre null retorna JUGADOR_NULL")
        void jugadorConNombreNullRetornaError() {
            Jugador jugadorSinNombre = new Jugador(null, ColorJugador.ROJO, EstadoJugador.JUGANDO);

            ResultadoAddNave resultado = validador.validarJugador(jugadorSinNombre, jugadores);

            assertEquals(ResultadoAddNave.JUGADOR_NULL, resultado);
        }

        @Test
        @DisplayName("Jugador con nombre vacio retorna JUGADOR_NULL")
        void jugadorConNombreVacioRetornaError() {
            Jugador jugadorNombreVacio = new Jugador("   ", ColorJugador.ROJO, EstadoJugador.JUGANDO);

            ResultadoAddNave resultado = validador.validarJugador(jugadorNombreVacio, jugadores);

            assertEquals(ResultadoAddNave.JUGADOR_NULL, resultado);
        }

        @Test
        @DisplayName("Jugador no encontrado en lista retorna JUGADOR_NO_ENCONTRADO")
        void jugadorNoEncontradoRetornaError() {
            Jugador otroJugador = new Jugador("OtroJugador", ColorJugador.AZUL, EstadoJugador.JUGANDO);

            ResultadoAddNave resultado = validador.validarJugador(otroJugador, jugadores);

            assertEquals(ResultadoAddNave.JUGADOR_NO_ENCONTRADO, resultado);
        }
    }

    @Nested
    @DisplayName("Pruebas de validarNave")
    class ValidarNaveTests {

        @Test
        @DisplayName("Nave valida retorna null")
        void naveValidaRetornaNull() {
            Nave nave = new Barco(OrientacionNave.HORIZONTAL);

            ResultadoAddNave resultado = validador.validarNave(nave);

            assertNull(resultado);
        }

        @Test
        @DisplayName("Nave null retorna NAVE_NULL")
        void naveNullRetornaError() {
            ResultadoAddNave resultado = validador.validarNave(null);

            assertEquals(ResultadoAddNave.NAVE_NULL, resultado);
        }
    }

    @Nested
    @DisplayName("Pruebas de validarCoordenadas")
    class ValidarCoordenadasTests {

        @Test
        @DisplayName("Coordenadas validas retornan null")
        void coordenadasValidasRetornanNull() {
            List<Coordenadas> coords = Arrays.asList(
                    new Coordenadas(0, 0),
                    new Coordenadas(1, 0)
            );

            ResultadoAddNave resultado = validador.validarCoordenadas(coords);

            assertNull(resultado);
        }

        @Test
        @DisplayName("Coordenadas null retorna COORDENADAS_NULL")
        void coordenadasNullRetornaError() {
            ResultadoAddNave resultado = validador.validarCoordenadas(null);

            assertEquals(ResultadoAddNave.COORDENADAS_NULL, resultado);
        }

        @Test
        @DisplayName("Lista vacia retorna COORDENADAS_NULL")
        void listaVaciaRetornaError() {
            ResultadoAddNave resultado = validador.validarCoordenadas(new ArrayList<>());

            assertEquals(ResultadoAddNave.COORDENADAS_NULL, resultado);
        }
    }

    @Nested
    @DisplayName("Pruebas de validarTamanioCoordenadas")
    class ValidarTamanioCoordenadasTests {

        @Test
        @DisplayName("Tamanio correcto retorna null")
        void tamanioCorrectoRetornaNull() {
            Nave barco = new Barco(OrientacionNave.HORIZONTAL);  // Tamanio 1
            List<Coordenadas> coords = Arrays.asList(
                    new Coordenadas(0, 0)
            );

            ResultadoAddNave resultado = validador.validarTamanioCoordenadas(barco, coords);

            assertNull(resultado);
        }

        @Test
        @DisplayName("Coordenadas extra retorna COORDENADAS_EXTRA")
        void coordenadasExtraRetornaError() {
            Nave barco = new Barco(OrientacionNave.HORIZONTAL);  // Tamanio 1
            List<Coordenadas> coords = Arrays.asList(
                    new Coordenadas(0, 0),
                    new Coordenadas(1, 0)  // Extra
            );

            ResultadoAddNave resultado = validador.validarTamanioCoordenadas(barco, coords);

            assertEquals(ResultadoAddNave.COORDENADAS_EXTRA, resultado);
        }

        @Test
        @DisplayName("Coordenadas insuficientes retorna COORDENADAS_EXTRA")
        void coordenadasInsuficientesRetornaError() {
            Nave crucero = new Crucero(OrientacionNave.HORIZONTAL);  // Tamanio 4
            List<Coordenadas> coords = Arrays.asList(
                    new Coordenadas(0, 0),
                    new Coordenadas(1, 0)
            );

            ResultadoAddNave resultado = validador.validarTamanioCoordenadas(crucero, coords);

            assertEquals(ResultadoAddNave.COORDENADAS_EXTRA, resultado);
        }
    }

    @Nested
    @DisplayName("Pruebas de validarLimites")
    class ValidarLimitesTests {

        @Test
        @DisplayName("Coordenadas dentro de limites retorna null")
        void coordenadasDentroLimitesRetornaNull() {
            List<Coordenadas> coords = Arrays.asList(
                    new Coordenadas(5, 5),
                    new Coordenadas(6, 5)
            );

            ResultadoAddNave resultado = validador.validarLimites(tablero, coords);

            assertNull(resultado);
        }

        @Test
        @DisplayName("Coordenada X negativa retorna COORDENADAS_FUERA_LIMITE")
        void coordenadaXNegativaRetornaError() {
            List<Coordenadas> coords = Arrays.asList(
                    new Coordenadas(-1, 0),
                    new Coordenadas(0, 0)
            );

            ResultadoAddNave resultado = validador.validarLimites(tablero, coords);

            assertEquals(ResultadoAddNave.COORDENADAS_FUERA_LIMITE, resultado);
        }

        @Test
        @DisplayName("Coordenada Y negativa retorna COORDENADAS_FUERA_LIMITE")
        void coordenadaYNegativaRetornaError() {
            List<Coordenadas> coords = Arrays.asList(
                    new Coordenadas(0, -1),
                    new Coordenadas(0, 0)
            );

            ResultadoAddNave resultado = validador.validarLimites(tablero, coords);

            assertEquals(ResultadoAddNave.COORDENADAS_FUERA_LIMITE, resultado);
        }
    }

    @Nested
    @DisplayName("Pruebas de validarOrientacion")
    class ValidarOrientacionTests {

        @Test
        @DisplayName("Nave vertical con coordenadas verticales retorna null")
        void naveVerticalCoordenadasVerticalesRetornaNull() {
            Nave nave = new Barco(OrientacionNave.VERTICAL);
            List<Coordenadas> coords = Arrays.asList(
                    new Coordenadas(0, 0),
                    new Coordenadas(1, 0)
            );

            ResultadoAddNave resultado = validador.validarOrientacion(nave, coords);

            assertNull(resultado);
        }

        @Test
        @DisplayName("Nave horizontal con coordenadas horizontales retorna null")
        void naveHorizontalCoordenadasHorizontalesRetornaNull() {
            Nave nave = new Barco(OrientacionNave.HORIZONTAL);
            List<Coordenadas> coords = Arrays.asList(
                    new Coordenadas(0, 0),
                    new Coordenadas(0, 1)
            );

            ResultadoAddNave resultado = validador.validarOrientacion(nave, coords);

            assertNull(resultado);
        }

        @Test
        @DisplayName("Nave vertical con coordenadas no alineadas retorna error")
        void naveVerticalCoordenadasNoAlineadasRetornaError() {
            Nave nave = new Barco(OrientacionNave.VERTICAL);
            List<Coordenadas> coords = Arrays.asList(
                    new Coordenadas(0, 0),
                    new Coordenadas(0, 1)  // Y diferente, deberia ser igual para vertical
            );

            ResultadoAddNave resultado = validador.validarOrientacion(nave, coords);

            assertEquals(ResultadoAddNave.NO_ORDENADA_VERTICLMENTE, resultado);
        }

        @Test
        @DisplayName("Nave horizontal con coordenadas no alineadas retorna error")
        void naveHorizontalCoordenadasNoAlineadasRetornaError() {
            Nave nave = new Barco(OrientacionNave.HORIZONTAL);
            List<Coordenadas> coords = Arrays.asList(
                    new Coordenadas(0, 0),
                    new Coordenadas(1, 0)  // X diferente, deberia ser igual para horizontal
            );

            ResultadoAddNave resultado = validador.validarOrientacion(nave, coords);

            assertEquals(ResultadoAddNave.NO_ORDENADA_HORIZONTALMENTE, resultado);
        }
    }

    @Nested
    @DisplayName("Pruebas de validarCompleto")
    class ValidarCompletoTests {

        @Test
        @DisplayName("Validacion completa exitosa retorna null")
        void validacionCompletaExitosaRetornaNull() {
            Nave nave = new Barco(OrientacionNave.HORIZONTAL);  // Tamanio 1
            List<Coordenadas> coords = Arrays.asList(
                    new Coordenadas(0, 0)
            );

            ResultadoAddNave resultado = validador.validarCompleto(
                    jugador, jugadores, nave, coords, tablero
            );

            assertNull(resultado);
        }

        @Test
        @DisplayName("Validacion falla con jugador invalido")
        void validacionFallaConJugadorInvalido() {
            Nave nave = new Barco(OrientacionNave.HORIZONTAL);  // Tamanio 1
            List<Coordenadas> coords = Arrays.asList(
                    new Coordenadas(0, 0)
            );

            ResultadoAddNave resultado = validador.validarCompleto(
                    null, jugadores, nave, coords, tablero
            );

            assertEquals(ResultadoAddNave.JUGADOR_NULL, resultado);
        }

        @Test
        @DisplayName("Validacion falla con nave invalida")
        void validacionFallaConNaveInvalida() {
            List<Coordenadas> coords = Arrays.asList(
                    new Coordenadas(0, 0)
            );

            ResultadoAddNave resultado = validador.validarCompleto(
                    jugador, jugadores, null, coords, tablero
            );

            assertEquals(ResultadoAddNave.NAVE_NULL, resultado);
        }
    }
}
