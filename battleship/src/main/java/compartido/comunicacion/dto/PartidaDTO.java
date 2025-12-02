package compartido.comunicacion.dto;

import compartido.enums.EstadoPartida;

/**
 * DTO para transferir informacion de la partida entre capas.
 * Capa compartida - no pertenece a Vista ni Modelo.
 *
 * @author daniel
 */
public class PartidaDTO {

    private int cantBarcos;
    private int cantSubmarinos;
    private int cantCruceros;
    private int cantPortaAviones;
    private int totalNaves;
    private EstadoPartida estado;

    public PartidaDTO() {
    }

    public PartidaDTO(int cantBarcos, int cantSubmarinos, int cantCruceros, int cantPortaAviones, int totalNaves, EstadoPartida estado) {
        this.cantBarcos = cantBarcos;
        this.cantSubmarinos = cantSubmarinos;
        this.cantCruceros = cantCruceros;
        this.cantPortaAviones = cantPortaAviones;
        this.totalNaves = totalNaves;
        this.estado = estado;
    }

    public int getCantBarcos() {
        return cantBarcos;
    }

    public void setCantBarcos(int cantBarcos) {
        this.cantBarcos = cantBarcos;
    }

    public int getCantSubmarinos() {
        return cantSubmarinos;
    }

    public void setCantSubmarinos(int cantSubmarinos) {
        this.cantSubmarinos = cantSubmarinos;
    }

    public int getCantCruceros() {
        return cantCruceros;
    }

    public void setCantCruceros(int cantCruceros) {
        this.cantCruceros = cantCruceros;
    }

    public int getCantPortaAviones() {
        return cantPortaAviones;
    }

    public void setCantPortaAviones(int cantPortaAviones) {
        this.cantPortaAviones = cantPortaAviones;
    }

    public int getTotalNaves() {
        return totalNaves;
    }

    public void setTotalNaves(int totalNaves) {
        this.totalNaves = totalNaves;
    }

    public EstadoPartida getEstado() {
        return estado;
    }

    public void setEstado(EstadoPartida estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "PartidaDTO{" + "cantBarcos=" + cantBarcos + ", cantSubmarinos=" + cantSubmarinos + ", cantCruceros=" + cantCruceros + ", cantPortaAviones=" + cantPortaAviones + ", totalNaves=" + totalNaves + ", estado=" + estado + '}';
    }
}
