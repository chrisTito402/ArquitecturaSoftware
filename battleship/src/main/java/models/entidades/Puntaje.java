/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.entidades;

import models.enums.ResultadoDisparo;

/**
 * Clase que representa el puntaje de un jugador en el juego Battleship.
 * Gestiona los puntos obtenidos, estadísticas de disparos y precisión.
 *
 * Caso de Uso: Gestionar Puntaje
 *
 * @author Fred
 */
public class Puntaje {

    // Constantes de puntuación
    public static final int PUNTOS_AGUA = 0;          // Impacto en agua (disparo fallado)
    public static final int PUNTOS_IMPACTO = 10;      // Impacto a nave (disparo acertado)
    public static final int PUNTOS_HUNDIMIENTO = 50;  // Nave hundida completamente
    public static final int PUNTOS_VICTORIA = 100;    // Victoria en la partida

    // Atributos del puntaje
    private int puntosTotales;
    private int disparosAcertados;
    private int disparosFallados;
    private int navesHundidas;

    /**
     * Constructor por defecto.
     * Inicializa todos los contadores en cero.
     */
    public Puntaje() {
        this.puntosTotales = 0;
        this.disparosAcertados = 0;
        this.disparosFallados = 0;
        this.navesHundidas = 0;
    }
    
    /**
     * Calcula y suma los puntos obtenidos según el resultado del disparo.
     * Actualiza las estadísticas correspondientes (aciertos, fallos, hundidos).
     *
     * Caso de Uso: Gestionar Puntaje - Método principal
     *
     * @param resultado El resultado del disparo (AGUA, IMPACTO, HUNDIMIENTO, etc.)
     * @return Los puntos obtenidos en este disparo
     */
    public int calcularPuntos(ResultadoDisparo resultado) {
        // Validar que el resultado no sea null
        if (resultado == null) {
            System.err.println("Error: ResultadoDisparo es null");
            return 0;
        }

        int puntosObtenidos = 0;

        switch (resultado) {
            case AGUA -> {
                puntosObtenidos = PUNTOS_AGUA;
                disparosFallados++;
            }
            case IMPACTO -> {
                puntosObtenidos = PUNTOS_IMPACTO;
                disparosAcertados++;
            }
            case HUNDIMIENTO -> {
                puntosObtenidos = PUNTOS_HUNDIMIENTO;
                disparosAcertados++;
                navesHundidas++;
            }
            // Casos especiales que no suman puntos ni estadísticas
            case YA_DISPARADO, TURNO_INCORRECTO, DISPARO_FUERA_TIEMPO -> {
                puntosObtenidos = 0;
                // No se cuentan como aciertos ni fallos
            }
            default -> {
                puntosObtenidos = 0;
            }
        }

        puntosTotales += puntosObtenidos;
        return puntosObtenidos;
    }

    /**
     * Suma los puntos por victoria al puntaje total.
     *
     * Caso de Uso: Gestionar Puntaje - Bonus de victoria
     */
    public void sumarVictoria() {
        puntosTotales += PUNTOS_VICTORIA;
    }

    /**
     * Reinicia todos los contadores del puntaje a cero.
     * Útil para comenzar una nueva partida.
     *
     * Caso de Uso: Gestionar Puntaje - Reiniciar puntaje
     */
    public void resetear() {
        this.puntosTotales = 0;
        this.disparosAcertados = 0;
        this.disparosFallados = 0;
        this.navesHundidas = 0;
    }

    /**
     * Obtiene el total de disparos realizados (aciertos + fallos).
     *
     * @return Total de disparos realizados
     */
    public int getTotalDisparos() {
        return disparosAcertados + disparosFallados;
    }

    /**
     * Verifica si el jugador tiene datos de puntaje registrados.
     *
     * @return true si hay al menos un disparo registrado, false en caso contrario
     */
    public boolean tieneDatos() {
        return getTotalDisparos() > 0;
    }

    /**
     * Obtiene un resumen textual del puntaje con todas las estadísticas.
     *
     * @return String con el resumen del puntaje
     */
    public String obtenerResumen() {
        return String.format(
                "=== RESUMEN DE PUNTAJE ===%n" +
                "Puntos Totales: %d%n" +
                "Disparos Acertados: %d%n" +
                "Disparos Fallados: %d%n" +
                "Total de Disparos: %d%n" +
                "Naves Hundidas: %d%n" +
                "Precisión: %.2f%%%n" +
                "========================",
                puntosTotales,
                disparosAcertados,
                disparosFallados,
                getTotalDisparos(),
                navesHundidas,
                getPrecision()
        );
    }

    // ==================== GETTERS ====================

    /**
     * Obtiene el puntaje total acumulado del jugador.
     * Incluye puntos por impactos, hundimientos y victoria.
     *
     * @return Puntos totales
     */
    public int getPuntosTotales() {
        return puntosTotales;
    }

    /**
     * Obtiene la cantidad de disparos acertados (impactos + hundimientos).
     *
     * @return Número de disparos acertados
     */
    public int getDisparosAcertados() {
        return disparosAcertados;
    }

    /**
     * Obtiene la cantidad de disparos fallados (agua).
     *
     * @return Número de disparos fallados
     */
    public int getDisparosFallados() {
        return disparosFallados;
    }

    /**
     * Obtiene la cantidad de naves hundidas completamente.
     *
     * @return Número de naves hundidas
     */
    public int getNavesHundidas() {
        return navesHundidas;
    }

    /**
     * Calcula y retorna la precisión de disparo del jugador.
     * La precisión es el porcentaje de disparos acertados sobre el total.
     *
     * Caso de Uso: Gestionar Puntaje - Calcular precisión
     *
     * @return Precisión en porcentaje (0.0 a 100.0)
     */
    public double getPrecision() {
        int totalDisparos = getTotalDisparos();
        if (totalDisparos == 0) {
            return 0.0;
        }
        return (disparosAcertados * 100.0) / totalDisparos;
    }

    /**
     * Representación en String del puntaje.
     *
     * @return String con la información del puntaje
     */
    @Override
    public String toString() {
        return "Puntaje{" +
                "puntosTotales=" + puntosTotales +
                ", disparosAcertados=" + disparosAcertados +
                ", disparosFallados=" + disparosFallados +
                ", navesHundidas=" + navesHundidas +
                ", precision=" + String.format("%.2f", getPrecision()) + "%" +
                '}';
    }
}