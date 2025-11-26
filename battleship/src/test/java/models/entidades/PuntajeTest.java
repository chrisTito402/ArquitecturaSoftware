package models.entidades;

import models.enums.ResultadoDisparo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la entidad Puntaje.
 * Caso de Uso: Gestionar Puntaje
 */
@DisplayName("Pruebas de la entidad Puntaje")
class PuntajeTest {

    private Puntaje puntaje;

    @BeforeEach
    void setUp() {
        puntaje = new Puntaje();
    }

    @Nested
    @DisplayName("Pruebas de inicializacion")
    class InicializacionTests {

        @Test
        @DisplayName("Puntaje inicial debe ser cero")
        void puntajeInicialEsCero() {
            assertEquals(0, puntaje.getPuntosTotales());
            assertEquals(0, puntaje.getDisparosAcertados());
            assertEquals(0, puntaje.getDisparosFallados());
            assertEquals(0, puntaje.getNavesHundidas());
        }

        @Test
        @DisplayName("Precision inicial debe ser cero")
        void precisionInicialEsCero() {
            assertEquals(0.0, puntaje.getPrecision());
        }

        @Test
        @DisplayName("Total disparos inicial debe ser cero")
        void totalDisparosInicialEsCero() {
            assertEquals(0, puntaje.getTotalDisparos());
        }

        @Test
        @DisplayName("No tiene datos inicialmente")
        void noTieneDatosInicialmente() {
            assertFalse(puntaje.tieneDatos());
        }
    }

    @Nested
    @DisplayName("Pruebas de calcularPuntos")
    class CalcularPuntosTests {

        @Test
        @DisplayName("Disparo al AGUA suma 0 puntos y cuenta como fallo")
        void disparoAguaSumaCeroPuntos() {
            int puntos = puntaje.calcularPuntos(ResultadoDisparo.AGUA);

            assertEquals(Puntaje.PUNTOS_AGUA, puntos);
            assertEquals(0, puntaje.getPuntosTotales());
            assertEquals(0, puntaje.getDisparosAcertados());
            assertEquals(1, puntaje.getDisparosFallados());
        }

        @Test
        @DisplayName("IMPACTO suma 10 puntos y cuenta como acierto")
        void impactoSuma10Puntos() {
            int puntos = puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);

            assertEquals(Puntaje.PUNTOS_IMPACTO, puntos);
            assertEquals(10, puntaje.getPuntosTotales());
            assertEquals(1, puntaje.getDisparosAcertados());
            assertEquals(0, puntaje.getDisparosFallados());
        }

        @Test
        @DisplayName("HUNDIMIENTO suma 50 puntos, cuenta acierto y nave hundida")
        void hundimientoSuma50Puntos() {
            int puntos = puntaje.calcularPuntos(ResultadoDisparo.HUNDIMIENTO);

            assertEquals(Puntaje.PUNTOS_HUNDIMIENTO, puntos);
            assertEquals(50, puntaje.getPuntosTotales());
            assertEquals(1, puntaje.getDisparosAcertados());
            assertEquals(1, puntaje.getNavesHundidas());
        }

        @Test
        @DisplayName("YA_DISPARADO no suma puntos ni estadisticas")
        void yaDisparadoNoSumaNada() {
            int puntos = puntaje.calcularPuntos(ResultadoDisparo.YA_DISPARADO);

            assertEquals(0, puntos);
            assertEquals(0, puntaje.getPuntosTotales());
            assertEquals(0, puntaje.getDisparosAcertados());
            assertEquals(0, puntaje.getDisparosFallados());
        }

        @Test
        @DisplayName("TURNO_INCORRECTO no suma puntos ni estadisticas")
        void turnoIncorrectoNoSumaNada() {
            int puntos = puntaje.calcularPuntos(ResultadoDisparo.TURNO_INCORRECTO);

            assertEquals(0, puntos);
            assertEquals(0, puntaje.getPuntosTotales());
        }

        @Test
        @DisplayName("DISPARO_FUERA_TIEMPO no suma puntos ni estadisticas")
        void disparoFueraTiempoNoSumaNada() {
            int puntos = puntaje.calcularPuntos(ResultadoDisparo.DISPARO_FUERA_TIEMPO);

            assertEquals(0, puntos);
            assertEquals(0, puntaje.getPuntosTotales());
        }

        @Test
        @DisplayName("ResultadoDisparo null retorna 0")
        void resultadoNullRetornaCero() {
            int puntos = puntaje.calcularPuntos(null);

            assertEquals(0, puntos);
            assertEquals(0, puntaje.getPuntosTotales());
        }

        @Test
        @DisplayName("Multiples disparos acumulan correctamente")
        void multiplesDisparosAcumulan() {
            puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);      // +10
            puntaje.calcularPuntos(ResultadoDisparo.AGUA);         // +0
            puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);      // +10
            puntaje.calcularPuntos(ResultadoDisparo.HUNDIMIENTO);  // +50

            assertEquals(70, puntaje.getPuntosTotales());
            assertEquals(3, puntaje.getDisparosAcertados());
            assertEquals(1, puntaje.getDisparosFallados());
            assertEquals(1, puntaje.getNavesHundidas());
            assertEquals(4, puntaje.getTotalDisparos());
        }
    }

    @Nested
    @DisplayName("Pruebas de sumarVictoria")
    class SumarVictoriaTests {

        @Test
        @DisplayName("Victoria suma 100 puntos bonus")
        void victoriaSuma100Puntos() {
            puntaje.sumarVictoria();

            assertEquals(Puntaje.PUNTOS_VICTORIA, puntaje.getPuntosTotales());
            assertEquals(100, puntaje.getPuntosTotales());
        }

        @Test
        @DisplayName("Victoria se suma a puntos existentes")
        void victoriaSeSumaAPuntosExistentes() {
            puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);  // +10
            puntaje.calcularPuntos(ResultadoDisparo.HUNDIMIENTO);  // +50
            puntaje.sumarVictoria();  // +100

            assertEquals(160, puntaje.getPuntosTotales());
        }
    }

    @Nested
    @DisplayName("Pruebas de precision")
    class PrecisionTests {

        @Test
        @DisplayName("Precision 100% con todos aciertos")
        void precision100ConTodosAciertos() {
            puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);
            puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);
            puntaje.calcularPuntos(ResultadoDisparo.HUNDIMIENTO);

            assertEquals(100.0, puntaje.getPrecision());
        }

        @Test
        @DisplayName("Precision 0% con todos fallos")
        void precision0ConTodosFallos() {
            puntaje.calcularPuntos(ResultadoDisparo.AGUA);
            puntaje.calcularPuntos(ResultadoDisparo.AGUA);

            assertEquals(0.0, puntaje.getPrecision());
        }

        @Test
        @DisplayName("Precision 50% con mitad aciertos")
        void precision50ConMitadAciertos() {
            puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);
            puntaje.calcularPuntos(ResultadoDisparo.AGUA);

            assertEquals(50.0, puntaje.getPrecision());
        }

        @Test
        @DisplayName("Precision se calcula correctamente con varios disparos")
        void precisionCalculadaCorrectamente() {
            puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);      // acierto
            puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);      // acierto
            puntaje.calcularPuntos(ResultadoDisparo.AGUA);         // fallo
            puntaje.calcularPuntos(ResultadoDisparo.HUNDIMIENTO);  // acierto

            // 3 aciertos de 4 disparos = 75%
            assertEquals(75.0, puntaje.getPrecision());
        }
    }

    @Nested
    @DisplayName("Pruebas de resetear")
    class ResetearTests {

        @Test
        @DisplayName("Resetear pone todos los valores en cero")
        void resetearPoneTodoEnCero() {
            puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);
            puntaje.calcularPuntos(ResultadoDisparo.HUNDIMIENTO);
            puntaje.sumarVictoria();

            puntaje.resetear();

            assertEquals(0, puntaje.getPuntosTotales());
            assertEquals(0, puntaje.getDisparosAcertados());
            assertEquals(0, puntaje.getDisparosFallados());
            assertEquals(0, puntaje.getNavesHundidas());
            assertEquals(0.0, puntaje.getPrecision());
        }
    }

    @Nested
    @DisplayName("Pruebas de tieneDatos")
    class TieneDatosTests {

        @Test
        @DisplayName("tieneDatos es true despues de un disparo")
        void tieneDatosDespuesDeDisparo() {
            puntaje.calcularPuntos(ResultadoDisparo.AGUA);

            assertTrue(puntaje.tieneDatos());
        }

        @Test
        @DisplayName("tieneDatos es false despues de resetear")
        void noTieneDatosDespuesDeResetear() {
            puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);
            puntaje.resetear();

            assertFalse(puntaje.tieneDatos());
        }
    }

    @Nested
    @DisplayName("Pruebas de constantes")
    class ConstantesTests {

        @Test
        @DisplayName("Constantes tienen valores correctos")
        void constantesTienenValoresCorrectos() {
            assertEquals(0, Puntaje.PUNTOS_AGUA);
            assertEquals(10, Puntaje.PUNTOS_IMPACTO);
            assertEquals(50, Puntaje.PUNTOS_HUNDIMIENTO);
            assertEquals(100, Puntaje.PUNTOS_VICTORIA);
        }
    }

    @Nested
    @DisplayName("Pruebas de obtenerResumen")
    class ObtenerResumenTests {

        @Test
        @DisplayName("Resumen contiene informacion correcta")
        void resumenContieneInformacionCorrecta() {
            puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);
            puntaje.calcularPuntos(ResultadoDisparo.AGUA);

            String resumen = puntaje.obtenerResumen();

            assertTrue(resumen.contains("10"));  // puntos totales
            assertTrue(resumen.contains("1"));   // aciertos y fallos
            assertTrue(resumen.contains("50"));  // precision 50%
        }
    }

    @Nested
    @DisplayName("Pruebas de toString")
    class ToStringTests {

        @Test
        @DisplayName("toString retorna formato correcto")
        void toStringFormatoCorrecto() {
            puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);

            String resultado = puntaje.toString();

            assertTrue(resultado.contains("Puntaje"));
            assertTrue(resultado.contains("puntosTotales=10"));
            assertTrue(resultado.contains("disparosAcertados=1"));
        }
    }
}
