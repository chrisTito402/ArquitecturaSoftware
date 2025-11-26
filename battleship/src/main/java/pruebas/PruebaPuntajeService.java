package pruebas;

import models.entidades.Jugador;
import models.entidades.Puntaje;
import models.enums.ColorJugador;
import models.enums.EstadoJugador;
import models.enums.ResultadoDisparo;
import models.services.PuntajeService;
import dtos.PuntajeDTO;
import java.util.ArrayList;
import java.util.List;

/**
 * Prueba del servicio PuntajeService.
 * Caso de Uso: Gestionar Puntaje - Servicio de Alto Nivel
 *
 * Demuestra que todas las operaciones del servicio funcionan correctamente.
 *
 * @author Fred
 */
public class PruebaPuntajeService {

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("  PRUEBA - SERVICIO PUNTAJE");
        System.out.println("  Caso de Uso: Gestionar Puntaje");
        System.out.println("==============================================\n");

        PuntajeService service = new PuntajeService();

        // ===== PRUEBA 1: Registrar disparo =====
        System.out.println("--- PRUEBA 1: Registrar Disparo ---");
        Puntaje puntaje = new Puntaje();
        int puntos = service.registrarDisparo(puntaje, ResultadoDisparo.IMPACTO);
        System.out.println("Puntos obtenidos: " + puntos + " (esperado: 10)");
        System.out.println("Puntos totales: " + puntaje.getPuntosTotales() + " (esperado: 10)");
        verificar(puntos == 10, "Registrar disparo - puntos");
        System.out.println("✅ PRUEBA 1 PASADA\n");

        // ===== PRUEBA 2: Registrar victoria =====
        System.out.println("--- PRUEBA 2: Registrar Victoria ---");
        int puntosAntes = puntaje.getPuntosTotales();
        service.registrarVictoria(puntaje);
        int puntajeDespues = puntaje.getPuntosTotales();
        System.out.println("Puntos antes: " + puntosAntes);
        System.out.println("Puntos después: " + puntajeDespues);
        System.out.println("Diferencia: " + (puntajeDespues - puntosAntes) + " (esperado: 100)");
        verificar(puntajeDespues - puntosAntes == 100, "Registrar victoria - bonus");
        System.out.println("✅ PRUEBA 2 PASADA\n");

        // ===== PRUEBA 3: Convertir a DTO =====
        System.out.println("--- PRUEBA 3: Convertir Puntaje a DTO ---");
        Puntaje puntajeCompleto = new Puntaje();
        puntajeCompleto.calcularPuntos(ResultadoDisparo.IMPACTO);
        puntajeCompleto.calcularPuntos(ResultadoDisparo.AGUA);
        puntajeCompleto.calcularPuntos(ResultadoDisparo.HUNDIMIENTO);

        PuntajeDTO dto = service.convertirADTO(puntajeCompleto);
        System.out.println("DTO generado:");
        System.out.println("  Puntos totales: " + dto.getPuntosTotales() + " (esperado: 60)");
        System.out.println("  Aciertos: " + dto.getDisparosAcertados() + " (esperado: 2)");
        System.out.println("  Fallos: " + dto.getDisparosFallados() + " (esperado: 1)");
        System.out.println("  Hundidos: " + dto.getNavesHundidas() + " (esperado: 1)");
        System.out.println("  Precisión: " + String.format("%.2f%%", dto.getPrecision()));

        verificar(dto.getPuntosTotales() == 60, "DTO - puntos totales");
        verificar(dto.getDisparosAcertados() == 2, "DTO - aciertos");
        verificar(dto.getDisparosFallados() == 1, "DTO - fallos");
        verificar(dto.getNavesHundidas() == 1, "DTO - hundidos");
        System.out.println("✅ PRUEBA 3 PASADA\n");

        // ===== PRUEBA 4: Convertir null a DTO =====
        System.out.println("--- PRUEBA 4: Convertir null a DTO (caso borde) ---");
        PuntajeDTO dtoNull = service.convertirADTO(null);
        System.out.println("DTO de null:");
        System.out.println("  Puntos: " + dtoNull.getPuntosTotales() + " (esperado: 0)");
        System.out.println("  Aciertos: " + dtoNull.getDisparosAcertados() + " (esperado: 0)");
        verificar(dtoNull.getPuntosTotales() == 0, "DTO null - puntos");
        verificar(dtoNull.getPrecision() == 0.0, "DTO null - precisión");
        System.out.println("✅ PRUEBA 4 PASADA\n");

        // ===== PRUEBA 5: Comparar puntajes =====
        System.out.println("--- PRUEBA 5: Comparar Puntajes ---");
        Puntaje puntaje1 = new Puntaje();
        puntaje1.calcularPuntos(ResultadoDisparo.HUNDIMIENTO); // 50 puntos

        Puntaje puntaje2 = new Puntaje();
        puntaje2.calcularPuntos(ResultadoDisparo.IMPACTO); // 10 puntos

        int comparacion = service.compararPuntajes(puntaje1, puntaje2);
        System.out.println("Puntaje1: " + puntaje1.getPuntosTotales() + " puntos");
        System.out.println("Puntaje2: " + puntaje2.getPuntosTotales() + " puntos");
        System.out.println("Comparación: " + comparacion + " (esperado: positivo)");
        verificar(comparacion > 0, "Comparar puntajes - mayor");

        comparacion = service.compararPuntajes(puntaje2, puntaje1);
        System.out.println("Comparación inversa: " + comparacion + " (esperado: negativo)");
        verificar(comparacion < 0, "Comparar puntajes - menor");
        System.out.println("✅ PRUEBA 5 PASADA\n");

        // ===== PRUEBA 6: Obtener jugador con mayor puntaje =====
        System.out.println("--- PRUEBA 6: Obtener Jugador con Mayor Puntaje ---");
        List<Jugador> jugadores = new ArrayList<>();

        Jugador j1 = new Jugador("Alice", ColorJugador.AZUL, EstadoJugador.JUGANDO);
        j1.getPuntaje().calcularPuntos(ResultadoDisparo.IMPACTO);
        j1.getPuntaje().calcularPuntos(ResultadoDisparo.IMPACTO);
        // Alice: 20 puntos

        Jugador j2 = new Jugador("Bob", ColorJugador.ROJO, EstadoJugador.JUGANDO);
        j2.getPuntaje().calcularPuntos(ResultadoDisparo.HUNDIMIENTO);
        j2.getPuntaje().calcularPuntos(ResultadoDisparo.IMPACTO);
        // Bob: 60 puntos

        Jugador j3 = new Jugador("Charlie", ColorJugador.AZUL, EstadoJugador.JUGANDO);
        j3.getPuntaje().calcularPuntos(ResultadoDisparo.IMPACTO);
        // Charlie: 10 puntos

        jugadores.add(j1);
        jugadores.add(j2);
        jugadores.add(j3);

        Jugador ganador = service.obtenerJugadorConMayorPuntaje(jugadores);
        System.out.println("Jugadores:");
        jugadores.forEach(j -> System.out.println("  " + j.getNombre() + ": " + j.getPuntaje().getPuntosTotales() + " puntos"));
        System.out.println("Ganador: " + ganador.getNombre() + " (esperado: Bob)");
        verificar(ganador.getNombre().equals("Bob"), "Mayor puntaje - ganador");
        verificar(ganador.getPuntaje().getPuntosTotales() == 60, "Mayor puntaje - puntos");
        System.out.println("✅ PRUEBA 6 PASADA\n");

        // ===== PRUEBA 7: Generar reporte comparativo =====
        System.out.println("--- PRUEBA 7: Generar Reporte Comparativo ---");
        String reporte = service.generarReporteComparativo(jugadores);
        System.out.println(reporte);
        verificar(reporte.contains("Bob"), "Reporte - contiene Bob");
        verificar(reporte.contains("Alice"), "Reporte - contiene Alice");
        verificar(reporte.contains("Charlie"), "Reporte - contiene Charlie");
        verificar(reporte.contains("60"), "Reporte - contiene puntaje de Bob");
        System.out.println("✅ PRUEBA 7 PASADA\n");

        // ===== PRUEBA 8: Calcular precisión promedio =====
        System.out.println("--- PRUEBA 8: Calcular Precisión Promedio ---");
        double precisionPromedio = service.calcularPrecisionPromedio(jugadores);
        System.out.println("Precisión promedio: " + String.format("%.2f%%", precisionPromedio));
        System.out.println("Precisiones individuales:");
        jugadores.forEach(j -> System.out.println("  " + j.getNombre() + ": " + String.format("%.2f%%", j.getPuntaje().getPrecision())));

        // Verificar que el promedio es razonable (debería estar entre 0 y 100)
        verificar(precisionPromedio >= 0 && precisionPromedio <= 100, "Precisión promedio - rango válido");
        System.out.println("✅ PRUEBA 8 PASADA\n");

        // ===== PRUEBA 9: Reiniciar puntaje =====
        System.out.println("--- PRUEBA 9: Reiniciar Puntaje ---");
        Puntaje puntajeReiniciar = new Puntaje();
        puntajeReiniciar.calcularPuntos(ResultadoDisparo.HUNDIMIENTO);
        puntajeReiniciar.calcularPuntos(ResultadoDisparo.IMPACTO);
        System.out.println("Puntos antes de reiniciar: " + puntajeReiniciar.getPuntosTotales());

        service.reiniciarPuntaje(puntajeReiniciar);
        System.out.println("Puntos después de reiniciar: " + puntajeReiniciar.getPuntosTotales() + " (esperado: 0)");
        verificar(puntajeReiniciar.getPuntosTotales() == 0, "Reiniciar - puntos");
        verificar(!puntajeReiniciar.tieneDatos(), "Reiniciar - sin datos");
        System.out.println("✅ PRUEBA 9 PASADA\n");

        // ===== PRUEBA 10: Validar puntaje =====
        System.out.println("--- PRUEBA 10: Validar Puntaje ---");
        Jugador jugadorConDatos = new Jugador("Test", ColorJugador.AZUL, EstadoJugador.JUGANDO);
        jugadorConDatos.getPuntaje().calcularPuntos(ResultadoDisparo.IMPACTO);

        Jugador jugadorSinDatos = new Jugador("Test2", ColorJugador.ROJO, EstadoJugador.JUGANDO);

        boolean tieneValido1 = service.tienePuntajeValido(jugadorConDatos);
        boolean tieneValido2 = service.tienePuntajeValido(jugadorSinDatos);
        boolean tieneValido3 = service.tienePuntajeValido(null);

        System.out.println("Jugador con datos: " + tieneValido1 + " (esperado: true)");
        System.out.println("Jugador sin datos: " + tieneValido2 + " (esperado: false)");
        System.out.println("Jugador null: " + tieneValido3 + " (esperado: false)");

        verificar(tieneValido1, "Validar - con datos");
        verificar(!tieneValido2, "Validar - sin datos");
        verificar(!tieneValido3, "Validar - null");
        System.out.println("✅ PRUEBA 10 PASADA\n");

        // ===== PRUEBA 11: Validaciones de argumentos null =====
        System.out.println("--- PRUEBA 11: Validaciones de Argumentos Null ---");
        boolean exceptionLanzada = false;
        try {
            service.registrarDisparo(null, ResultadoDisparo.IMPACTO);
        } catch (IllegalArgumentException e) {
            exceptionLanzada = true;
            System.out.println("✓ Exception capturada correctamente: " + e.getMessage());
        }
        verificar(exceptionLanzada, "Validación null - exception");

        exceptionLanzada = false;
        try {
            service.compararPuntajes(null, new Puntaje());
        } catch (IllegalArgumentException e) {
            exceptionLanzada = true;
            System.out.println("✓ Exception capturada correctamente: " + e.getMessage());
        }
        verificar(exceptionLanzada, "Validación null comparar - exception");
        System.out.println("✅ PRUEBA 11 PASADA\n");

        // ===== RESUMEN FINAL =====
        System.out.println("==============================================");
        System.out.println("  ✅ TODAS LAS PRUEBAS DEL SERVICIO PASARON");
        System.out.println("  PUNTAJE SERVICE - FUNCIONANDO CORRECTAMENTE");
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
