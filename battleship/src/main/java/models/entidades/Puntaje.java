/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.entidades;

import models.enums.ResultadoDisparo;

/**
 * @author Fred
 */
public class Puntaje {

    public static final int PUNTOS_AGUA = 0; //Impacto en agua
    public static final int PUNTOS_IMPACTO = 10; //Impacto a Barco
    public static final int PUNTOS_HUNDIMIENTO = 50; //Barco hundido
    public static final int PUNTOS_VICTORIA = 100; //Victoria
    private int puntosTotales;
    private int disparosAcertados;
    private int disparosFallados;
    private int navesHundidas;

    public Puntaje() {
        this.puntosTotales = 0;
        this.disparosAcertados = 0;
        this.disparosFallados = 0;
        this.navesHundidas = 0;
    }

    public int calcularPuntos(ResultadoDisparo resultado) {
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
            default -> {
                puntosObtenidos = 0;
            }
        }

        puntosTotales += puntosObtenidos;
        return puntosObtenidos;
    }

    public void sumarVictoria() {
        puntosTotales += PUNTOS_VICTORIA;
    }

    // Getters
    public int getPuntosTotales() {
        return puntosTotales;
    }

    public int getDisparosAcertados() {
        return disparosAcertados;
    }

    public int getDisparosFallados() {
        return disparosFallados;
    }

    public int getNavesHundidas() {
        return navesHundidas;
    }

    public double getPrecision() {
        int totalDisparos = disparosAcertados + disparosFallados;
        if (totalDisparos == 0) {
            return 0.0;
        }
        return (disparosAcertados * 100.0) / totalDisparos;
    }

    @Override
    public String toString() {
        return "Puntaje{"
                + "puntosTotales=" + puntosTotales
                + ", disparosAcertados=" + disparosAcertados
                + ", disparosFallados=" + disparosFallados
                + ", navesHundidas=" + navesHundidas
                + ", precision=" + String.format("%.2f", getPrecision()) + "%"
                + '}';
    }
}
