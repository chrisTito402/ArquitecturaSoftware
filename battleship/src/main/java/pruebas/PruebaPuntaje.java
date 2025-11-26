package pruebas;

import models.entidades.Puntaje;
import models.enums.ResultadoDisparo;

/**
 * Prueba unitaria de la clase Puntaje.
 * Caso de Uso: Gestionar Puntaje
 *
 * Esta clase demuestra que todas las funcionalidades de Puntaje funcionan correctamente.
 *
 * @author Fred
 */
public class PruebaPuntaje {

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("  PRUEBA UNITARIA - CASO DE USO: GESTIONAR PUNTAJE");
        System.out.println("==============================================\n");

        // Crear una instancia de Puntaje
        Puntaje puntaje = new Puntaje();

        // ===== PRUEBA 1: Estado inicial =====
        System.out.println("--- PRUEBA 1: Estado Inicial ---");
        System.out.println("Puntos totales: " + puntaje.getPuntosTotales() + " (esperado: 0)");
        System.out.println("Disparos acertados: " + puntaje.getDisparosAcertados() + " (esperado: 0)");
        System.out.println("Disparos fallados: " + puntaje.getDisparosFallados() + " (esperado: 0)");
        System.out.println("Naves hundidas: " + puntaje.getNavesHundidas() + " (esperado: 0)");
        System.out.println("Precisión: " + puntaje.getPrecision() + "% (esperado: 0.0)");
        System.out.println("Tiene datos: " + puntaje.tieneDatos() + " (esperado: false)");
        verificar(puntaje.getPuntosTotales() == 0, "Estado inicial - puntos");
        verificar(!puntaje.tieneDatos(), "Estado inicial - sin datos");
        System.out.println("✅ PRUEBA 1 PASADA\n");

        // ===== PRUEBA 2: Disparo en agua =====
        System.out.println("--- PRUEBA 2: Disparo en Agua ---");
        int puntosObtenidos = puntaje.calcularPuntos(ResultadoDisparo.AGUA);
        System.out.println("Puntos obtenidos: " + puntosObtenidos + " (esperado: 0)");
        System.out.println("Puntos totales: " + puntaje.getPuntosTotales() + " (esperado: 0)");
        System.out.println("Disparos fallados: " + puntaje.getDisparosFallados() + " (esperado: 1)");
        System.out.println("Disparos acertados: " + puntaje.getDisparosAcertados() + " (esperado: 0)");
        System.out.println("Precisión: " + puntaje.getPrecision() + "% (esperado: 0.0)");
        verificar(puntosObtenidos == 0, "Agua - puntos obtenidos");
        verificar(puntaje.getPuntosTotales() == 0, "Agua - puntos totales");
        verificar(puntaje.getDisparosFallados() == 1, "Agua - fallos");
        verificar(puntaje.getPrecision() == 0.0, "Agua - precisión");
        System.out.println("✅ PRUEBA 2 PASADA\n");

        // ===== PRUEBA 3: Disparo con impacto =====
        System.out.println("--- PRUEBA 3: Disparo con Impacto ---");
        puntosObtenidos = puntaje.calcularPuntos(ResultadoDisparo.IMPACTO);
        System.out.println("Puntos obtenidos: " + puntosObtenidos + " (esperado: 10)");
        System.out.println("Puntos totales: " + puntaje.getPuntosTotales() + " (esperado: 10)");
        System.out.println("Disparos acertados: " + puntaje.getDisparosAcertados() + " (esperado: 1)");
        System.out.println("Precisión: " + puntaje.getPrecision() + "% (esperado: 50.0)");
        verificar(puntosObtenidos == 10, "Impacto - puntos obtenidos");
        verificar(puntaje.getPuntosTotales() == 10, "Impacto - puntos totales");
        verificar(puntaje.getDisparosAcertados() == 1, "Impacto - aciertos");
        verificar(puntaje.getPrecision() == 50.0, "Impacto - precisión");
        System.out.println("✅ PRUEBA 3 PASADA\n");

        // ===== PRUEBA 4: Hundimiento =====
        System.out.println("--- PRUEBA 4: Hundimiento de Nave ---");
        puntosObtenidos = puntaje.calcularPuntos(ResultadoDisparo.HUNDIMIENTO);
        System.out.println("Puntos obtenidos: " + puntosObtenidos + " (esperado: 50)");
        System.out.println("Puntos totales: " + puntaje.getPuntosTotales() + " (esperado: 60)");
        System.out.println("Disparos acertados: " + puntaje.getDisparosAcertados() + " (esperado: 2)");
        System.out.println("Naves hundidas: " + puntaje.getNavesHundidas() + " (esperado: 1)");
        System.out.println("Precisión: " + String.format("%.2f", puntaje.getPrecision()) + "% (esperado: 66.67)");
        verificar(puntosObtenidos == 50, "Hundimiento - puntos obtenidos");
        verificar(puntaje.getPuntosTotales() == 60, "Hundimiento - puntos totales");
        verificar(puntaje.getNavesHundidas() == 1, "Hundimiento - naves hundidas");
        verificar(Math.abs(puntaje.getPrecision() - 66.67) < 0.1, "Hundimiento - precisión");
        System.out.println("✅ PRUEBA 4 PASADA\n");

        // ===== PRUEBA 5: Victoria =====
        System.out.println("--- PRUEBA 5: Victoria ---");
        puntaje.sumarVictoria();
        System.out.println("Puntos totales: " + puntaje.getPuntosTotales() + " (esperado: 160)");
        verificar(puntaje.getPuntosTotales() == 160, "Victoria - bonus sumado");
        System.out.println("✅ PRUEBA 5 PASADA\n");

        // ===== PRUEBA 6: Casos especiales (no suman a estadísticas) =====
        System.out.println("--- PRUEBA 6: Casos Especiales ---");
        int disparosAntesEspeciales = puntaje.getTotalDisparos();

        puntaje.calcularPuntos(ResultadoDisparo.YA_DISPARADO);
        puntaje.calcularPuntos(ResultadoDisparo.TURNO_INCORRECTO);
        puntaje.calcularPuntos(ResultadoDisparo.DISPARO_FUERA_TIEMPO);

        int disparosDespuesEspeciales = puntaje.getTotalDisparos();
        System.out.println("Disparos antes: " + disparosAntesEspeciales);
        System.out.println("Disparos después: " + disparosDespuesEspeciales);
        System.out.println("Los casos especiales NO deben sumar a estadísticas");
        verificar(disparosAntesEspeciales == disparosDespuesEspeciales, "Casos especiales - no suman");
        System.out.println("✅ PRUEBA 6 PASADA\n");

        // ===== PRUEBA 7: Obtener resumen =====
        System.out.println("--- PRUEBA 7: Resumen del Puntaje ---");
        String resumen = puntaje.obtenerResumen();
        System.out.println(resumen);
        verificar(resumen.contains("Puntos Totales: 160"), "Resumen - contiene puntos");
        verificar(resumen.contains("Precisión:"), "Resumen - contiene precisión");
        System.out.println("✅ PRUEBA 7 PASADA\n");

        // ===== PRUEBA 8: Validación null =====
        System.out.println("--- PRUEBA 8: Validación Null ---");
        puntosObtenidos = puntaje.calcularPuntos(null);
        System.out.println("Puntos con null: " + puntosObtenidos + " (esperado: 0)");
        verificar(puntosObtenidos == 0, "Validación null - retorna 0");
        System.out.println("✅ PRUEBA 8 PASADA\n");

        // ===== PRUEBA 9: Resetear puntaje =====
        System.out.println("--- PRUEBA 9: Resetear Puntaje ---");
        puntaje.resetear();
        System.out.println("Después de resetear:");
        System.out.println("Puntos totales: " + puntaje.getPuntosTotales() + " (esperado: 0)");
        System.out.println("Disparos acertados: " + puntaje.getDisparosAcertados() + " (esperado: 0)");
        System.out.println("Disparos fallados: " + puntaje.getDisparosFallados() + " (esperado: 0)");
        System.out.println("Naves hundidas: " + puntaje.getNavesHundidas() + " (esperado: 0)");
        verificar(puntaje.getPuntosTotales() == 0, "Resetear - puntos");
        verificar(puntaje.getDisparosAcertados() == 0, "Resetear - aciertos");
        verificar(puntaje.getDisparosFallados() == 0, "Resetear - fallos");
        verificar(puntaje.getNavesHundidas() == 0, "Resetear - hundidos");
        System.out.println("✅ PRUEBA 9 PASADA\n");

        // ===== PRUEBA 10: Simulación de partida completa =====
        System.out.println("--- PRUEBA 10: Simulación de Partida Completa ---");
        Puntaje partidaCompleta = new Puntaje();

        // Simulamos 20 disparos variados
        partidaCompleta.calcularPuntos(ResultadoDisparo.AGUA);      // 0 puntos
        partidaCompleta.calcularPuntos(ResultadoDisparo.IMPACTO);   // 10 puntos
        partidaCompleta.calcularPuntos(ResultadoDisparo.AGUA);      // 0 puntos
        partidaCompleta.calcularPuntos(ResultadoDisparo.IMPACTO);   // 10 puntos
        partidaCompleta.calcularPuntos(ResultadoDisparo.HUNDIMIENTO); // 50 puntos
        partidaCompleta.calcularPuntos(ResultadoDisparo.AGUA);      // 0 puntos
        partidaCompleta.calcularPuntos(ResultadoDisparo.IMPACTO);   // 10 puntos
        partidaCompleta.calcularPuntos(ResultadoDisparo.IMPACTO);   // 10 puntos
        partidaCompleta.calcularPuntos(ResultadoDisparo.HUNDIMIENTO); // 50 puntos
        partidaCompleta.calcularPuntos(ResultadoDisparo.IMPACTO);   // 10 puntos
        partidaCompleta.calcularPuntos(ResultadoDisparo.AGUA);      // 0 puntos
        partidaCompleta.calcularPuntos(ResultadoDisparo.HUNDIMIENTO); // 50 puntos
        partidaCompleta.calcularPuntos(ResultadoDisparo.IMPACTO);   // 10 puntos
        partidaCompleta.calcularPuntos(ResultadoDisparo.AGUA);      // 0 puntos
        partidaCompleta.calcularPuntos(ResultadoDisparo.IMPACTO);   // 10 puntos

        partidaCompleta.sumarVictoria(); // 100 puntos

        System.out.println("Estadísticas finales:");
        System.out.println("  Puntos totales: " + partidaCompleta.getPuntosTotales());
        System.out.println("  Disparos acertados: " + partidaCompleta.getDisparosAcertados());
        System.out.println("  Disparos fallados: " + partidaCompleta.getDisparosFallados());
        System.out.println("  Total disparos: " + partidaCompleta.getTotalDisparos());
        System.out.println("  Naves hundidas: " + partidaCompleta.getNavesHundidas());
        System.out.println("  Precisión: " + String.format("%.2f%%", partidaCompleta.getPrecision()));

        // Verificaciones
        int esperadoTotal = (7 * 10) + (3 * 50) + 100; // 7 impactos + 3 hundimientos + victoria = 320
        verificar(partidaCompleta.getPuntosTotales() == esperadoTotal, "Partida completa - puntos");
        verificar(partidaCompleta.getDisparosAcertados() == 10, "Partida completa - aciertos");
        verificar(partidaCompleta.getDisparosFallados() == 5, "Partida completa - fallos");
        verificar(partidaCompleta.getNavesHundidas() == 3, "Partida completa - hundidos");
        verificar(partidaCompleta.getTotalDisparos() == 15, "Partida completa - total");

        double precisionEsperada = (10.0 / 15.0) * 100;
        verificar(Math.abs(partidaCompleta.getPrecision() - precisionEsperada) < 0.1, "Partida completa - precisión");

        System.out.println("✅ PRUEBA 10 PASADA\n");

        // ===== RESUMEN FINAL =====
        System.out.println("==============================================");
        System.out.println("  ✅ TODAS LAS PRUEBAS PASARON EXITOSAMENTE");
        System.out.println("  CASO DE USO: GESTIONAR PUNTAJE - FUNCIONANDO");
        System.out.println("==============================================");
    }

    /**
     * Método auxiliar para verificar condiciones.
     */
    private static void verificar(boolean condicion, String nombrePrueba) {
        if (!condicion) {
            System.err.println("❌ FALLO: " + nombrePrueba);
            throw new AssertionError("Prueba fallida: " + nombrePrueba);
        }
    }
}
