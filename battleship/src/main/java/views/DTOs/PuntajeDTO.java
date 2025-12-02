/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views.DTOs;

/**
 * @author Fred
 */
public class PuntajeDTO {

    private int puntosTotales;
    private int disparosAcertados;
    private int disparosFallados;
    private int navesHundidas;
    private double precision;

    public PuntajeDTO() {
    }

    public PuntajeDTO(int puntosTotales, int disparosAcertados, int disparosFallados, int navesHundidas, double precision) {
        this.puntosTotales = puntosTotales;
        this.disparosAcertados = disparosAcertados;
        this.disparosFallados = disparosFallados;
        this.navesHundidas = navesHundidas;
        this.precision = precision;
    }

    // Getters y Setters
    public int getPuntosTotales() {
        return puntosTotales;
    }

    public void setPuntosTotales(int puntosTotales) {
        this.puntosTotales = puntosTotales;
    }

    public int getDisparosAcertados() {
        return disparosAcertados;
    }

    public void setDisparosAcertados(int disparosAcertados) {
        this.disparosAcertados = disparosAcertados;
    }

    public int getDisparosFallados() {
        return disparosFallados;
    }

    public void setDisparosFallados(int disparosFallados) {
        this.disparosFallados = disparosFallados;
    }

    public int getNavesHundidas() {
        return navesHundidas;
    }

    public void setNavesHundidas(int navesHundidas) {
        this.navesHundidas = navesHundidas;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    @Override
    public String toString() {
        return "PuntajeDTO{"
                + "puntosTotales=" + puntosTotales
                + ", disparosAcertados=" + disparosAcertados
                + ", disparosFallados=" + disparosFallados
                + ", navesHundidas=" + navesHundidas
                + ", precision=" + precision
                + '}';
    }
}
