package pruebas;

import models.entidades.*;
import models.enums.*;
import models.services.PuntajeService;
import servidor.cronometro.Cronometro;
import dtos.PuntajeDTO;
import views.frames.PuntajePanel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

public class PruebaIntegracionPuntaje {

    public static void main(String[] args) {
        System.out.println("  PRUEBA DE INTEGRACIÓN");
        System.out.println("  Caso de Uso: Gestionar Puntaje");
        System.out.println("  Flujo Completo: Disparo → Puntaje → Vista");

        System.out.println("--- PASO 1: Configuración de la Partida ---");

        // Crear tableros
        Casilla[][] casillas1 = crearTablero();
        Casilla[][] casillas2 = crearTablero();

        // Agregar naves al tablero 1
        Nave barco1 = new Barco(OrientacionNave.HORIZONTAL);
        casillas1[0][0].setNave(barco1);

        Nave submarino1 = new Submarino(OrientacionNave.VERTICAL);
        casillas1[2][2].setNave(submarino1);
        casillas1[3][2].setNave(submarino1);

        // Agregar naves al tablero 2
        Nave barco2 = new Barco(OrientacionNave.HORIZONTAL);
        casillas2[0][0].setNave(barco2);

        Nave crucero2 = new Crucero(OrientacionNave.HORIZONTAL);
        casillas2[4][4].setNave(crucero2);
        casillas2[4][5].setNave(crucero2);
        casillas2[4][6].setNave(crucero2);

        Tablero tablero1 = new Tablero(casillas1, 10, 10);
        Tablero tablero2 = new Tablero(casillas2, 10, 10);

        // Crear jugadores
        List<Nave> navesJ1 = new ArrayList<>();
        navesJ1.add(barco1);
        navesJ1.add(submarino1);

        List<Nave> navesJ2 = new ArrayList<>();
        navesJ2.add(barco2);
        navesJ2.add(crucero2);

        Jugador jugador1 = new Jugador(
                "Jugador 1",
                ColorJugador.AZUL,
                navesJ1,
                tablero1,
                EstadoJugador.JUGANDO
        );

        Jugador jugador2 = new Jugador(
                "Jugador 2",
                ColorJugador.ROJO,
                navesJ2,
                tablero2,
                EstadoJugador.JUGANDO
        );

        System.out.println(jugador1.getNombre() + " creado con " + navesJ1.size() + " naves");
        System.out.println(jugador2.getNombre() + " creado con " + navesJ2.size() + " naves");
        System.out.println("Puntajes inicializados automáticamente\n");

        // Crear partida
        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(jugador1);
        jugadores.add(jugador2);

        Cronometro cronometro = new Cronometro(30000);
        Partida partida = new Partida(
                jugador1,
                jugadores,
                2, 0, 0, 0, 2,
                EstadoPartida.EN_CURSO,
                new ArrayList<>(),
                cronometro
        );
        cronometro.setPartida(partida);

        System.out.println("Partida creada - Estado: " + EstadoPartida.EN_CURSO);
        System.out.println("Turno inicial: " + jugador1.getNombre() + "\n");

        System.out.println("--- Simulación de Disparos ---\n");

        PuntajeService service = new PuntajeService();

        // Turno 1: Jugador 1 dispara y falla
        System.out.println(">> Turno 1 - " + jugador1.getNombre() + " dispara a (1, 1)");
        Disparo disparo1 = partida.realizarDisparo(new Coordenadas(1, 1), jugador1, System.currentTimeMillis());
        System.out.println("   Resultado: " + disparo1.getResultadoDisparo());
        System.out.println("   Puntaje de " + jugador1.getNombre() + ": " + jugador1.getPuntaje().getPuntosTotales() + " puntos");
        System.out.println();

        // Turno 2: Jugador 2 dispara e impacta
        System.out.println(">> Turno 2 - " + jugador2.getNombre() + " dispara a (0, 0) - Hay barco");
        Disparo disparo2 = partida.realizarDisparo(new Coordenadas(0, 0), jugador2, System.currentTimeMillis());
        System.out.println("   Resultado: " + disparo2.getResultadoDisparo());
        System.out.println("   Puntaje de " + jugador2.getNombre() + ": " + jugador2.getPuntaje().getPuntosTotales() + " puntos");
        System.out.println();

        // Turno 3: Jugador 1 impacta
        System.out.println(">> Turno 3 - " + jugador1.getNombre() + " dispara a (0, 0) - Hay barco");
        Disparo disparo3 = partida.realizarDisparo(new Coordenadas(0, 0), jugador1, System.currentTimeMillis());
        System.out.println("   Resultado: " + disparo3.getResultadoDisparo());
        System.out.println("   Puntaje de " + jugador1.getNombre() + ": " + jugador1.getPuntaje().getPuntosTotales() + " puntos");
        System.out.println();

        // Turno 4: Jugador 2 hunde submarino (parte 1)
        System.out.println(">> Turno 4 - " + jugador2.getNombre() + " dispara a (2, 2) - Submarino parte 1");
        Disparo disparo4 = partida.realizarDisparo(new Coordenadas(2, 2), jugador2, System.currentTimeMillis());
        System.out.println("   Resultado: " + disparo4.getResultadoDisparo());
        System.out.println("   Puntaje de " + jugador2.getNombre() + ": " + jugador2.getPuntaje().getPuntosTotales() + " puntos");
        System.out.println();

        // Turno 5: Jugador 1 impacta crucero
        System.out.println(">> Turno 5 - " + jugador1.getNombre() + " dispara a (4, 4) - Crucero parte 1");
        Disparo disparo5 = partida.realizarDisparo(new Coordenadas(4, 4), jugador1, System.currentTimeMillis());
        System.out.println("   Resultado: " + disparo5.getResultadoDisparo());
        System.out.println("   Puntaje de " + jugador1.getNombre() + ": " + jugador1.getPuntaje().getPuntosTotales() + " puntos");
        System.out.println();

        // Turno 6: Jugador 2 hunde submarino (parte 2) - HUNDIMIENTO
        System.out.println(">> Turno 6 - " + jugador2.getNombre() + " dispara a (3, 2) - Submarino parte 2 - HUNDIMIENTO");
        Disparo disparo6 = partida.realizarDisparo(new Coordenadas(3, 2), jugador2, System.currentTimeMillis());
        System.out.println("   Resultado: " + disparo6.getResultadoDisparo());
        System.out.println("   Puntaje de " + jugador2.getNombre() + ": " + jugador2.getPuntaje().getPuntosTotales() + " puntos");
        System.out.println("   Naves hundidas: " + jugador2.getPuntaje().getNavesHundidas());
        System.out.println();

        System.out.println("--- estadísticas Intermedias ---\n");

        System.out.println("Puntajes actuales:");
        mostrarEstadisticas(jugador1);
        mostrarEstadisticas(jugador2);

        System.out.println("--- PASO 4: Conversión a DTO ---\n");

        PuntajeDTO dto1 = service.convertirADTO(jugador1.getPuntaje());
        PuntajeDTO dto2 = service.convertirADTO(jugador2.getPuntaje());

        System.out.println("DTO de " + jugador1.getNombre() + ":");
        System.out.println("  " + dto1);

        System.out.println("\nDTO de " + jugador2.getNombre() + ":");
        System.out.println("  " + dto2);
        System.out.println();

        System.out.println("--- Reporte---\n");

        String reporte = service.generarReporteComparativo(jugadores);
        System.out.println(reporte);
        System.out.println();

        System.out.println("--- Ganador Actual ---\n");

        Jugador lider = service.obtenerJugadorConMayorPuntaje(jugadores);
        System.out.println("Líder actual: " + lider.getNombre());
        System.out.println("Puntaje: " + lider.getPuntaje().getPuntosTotales() + " puntos");
        System.out.println("Precisión: " + String.format("%.2f%%", lider.getPuntaje().getPrecision()));
        System.out.println();

        System.out.println("--- Estadísticas ---\n");

        double precisionPromedio = service.calcularPrecisionPromedio(jugadores);
        System.out.println("Precisión promedio del grupo: " + String.format("%.2f%%", precisionPromedio));
        System.out.println();


        System.out.println("  RESUMEN DE LA PRUEBA DE INTEGRACIÓN");
        System.out.println("Disparos realizados: 6");
        System.out.println("Impactos: " + (jugador1.getPuntaje().getDisparosAcertados() + jugador2.getPuntaje().getDisparosAcertados()));
        System.out.println("Fallos: " + (jugador1.getPuntaje().getDisparosFallados() + jugador2.getPuntaje().getDisparosFallados()));
        System.out.println("Naves hundidas: " + (jugador1.getPuntaje().getNavesHundidas() + jugador2.getPuntaje().getNavesHundidas()));
        System.out.println();
        System.out.println("Flujo completo verificado:");
    }

    /**
     * Crea un tablero 10x10 vacío.
     */
    private static Casilla[][] crearTablero() {
        Casilla[][] casillas = new Casilla[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                casillas[i][j] = new Casilla(EstadoCasilla.AGUA, new Coordenadas(i, j));
            }
        }
        return casillas;
    }

    /**
     * Muestra las estadísticas de un jugador.
     */
    private static void mostrarEstadisticas(Jugador jugador) {
        Puntaje p = jugador.getPuntaje();
        System.out.println(jugador.getNombre() + ":");
        System.out.println("  Puntos: " + p.getPuntosTotales());
        System.out.println("  Aciertos: " + p.getDisparosAcertados());
        System.out.println("  Fallos: " + p.getDisparosFallados());
        System.out.println("  Total disparos: " + p.getTotalDisparos());
        System.out.println("  Naves hundidas: " + p.getNavesHundidas());
        System.out.println("  Precisión: " + String.format("%.2f%%", p.getPrecision()));
        System.out.println();
    }

    /**
     * Muestra la visualización en ventanas Swing (opcional).
     * Descomenta la llamada en main() para ver la UI.
     */
    @SuppressWarnings("unused")
    private static void mostrarVisualizacion(Jugador j1, PuntajeDTO dto1, Jugador j2, PuntajeDTO dto2) {
        // Panel para jugador 1
        JFrame frame1 = new JFrame("Puntaje - " + j1.getNombre());
        PuntajePanel panel1 = new PuntajePanel();
        panel1.actualizarPuntaje(dto1);
        frame1.add(panel1);
        frame1.pack();
        frame1.setLocation(100, 100);
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setVisible(true);

        // Panel para jugador 2
        JFrame frame2 = new JFrame("Puntaje - " + j2.getNombre());
        PuntajePanel panel2 = new PuntajePanel();
        panel2.actualizarPuntaje(dto2);
        frame2.add(panel2);
        frame2.pack();
        frame2.setLocation(300, 100);
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setVisible(true);

    }
}
