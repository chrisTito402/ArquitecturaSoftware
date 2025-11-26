package models.builder;

import models.entidades.Disparo;
import models.entidades.Jugador;
import models.entidades.Partida;
import models.enums.EstadoPartida;
import models.observador.ISuscriptor;
import java.util.ArrayList;
import java.util.List;

public class PartidaBuilder implements IPartidaBuilder {

    private Jugador turno;
    private List<Jugador> jugadores;
    private int cantBarcos;
    private int cantSubmarinos;
    private int cantCruceros;
    private int cantPortaAviones;
    private int totalNaves;
    private EstadoPartida estado;
    private Disparo disparo;
    private List<ISuscriptor> suscriptores;

    public PartidaBuilder() {
        this.jugadores = new ArrayList<>();
        this.suscriptores = new ArrayList<>();
        this.estado = EstadoPartida.POR_EMPEZAR;
    }

    @Override
    public void setTurno(Jugador turno) {
        this.turno = turno;
    }

    @Override
    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores != null ? jugadores : new ArrayList<>();
    }

    @Override
    public void setCantBarcos(int cantBarcos) {
        if (cantBarcos < 0) {
            throw new IllegalArgumentException("La cantidad de barcos no puede ser negativa");
        }
        this.cantBarcos = cantBarcos;
    }

    @Override
    public void setCantSubmarinos(int cantSubmarinos) {
        if (cantSubmarinos < 0) {
            throw new IllegalArgumentException("La cantidad de submarinos no puede ser negativa");
        }
        this.cantSubmarinos = cantSubmarinos;
    }

    @Override
    public void setCantCruceros(int cantCruceros) {
        if (cantCruceros < 0) {
            throw new IllegalArgumentException("La cantidad de cruceros no puede ser negativa");
        }
        this.cantCruceros = cantCruceros;
    }

    @Override
    public void setCantPortaAviones(int cantPortaAviones) {
        if (cantPortaAviones < 0) {
            throw new IllegalArgumentException("La cantidad de portaaviones no puede ser negativa");
        }
        this.cantPortaAviones = cantPortaAviones;
    }

    @Override
    public void setTotalNaves(int totalNaves) {
        if (totalNaves < 0) {
            throw new IllegalArgumentException("El total de naves no puede ser negativo");
        }
        this.totalNaves = totalNaves;
    }

    @Override
    public void setEstado(EstadoPartida estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser null");
        }
        this.estado = estado;
    }

    @Override
    public void setDisparo(Disparo disparo) {
        this.disparo = disparo;
    }

    @Override
    public void setSuscriptores(List<ISuscriptor> suscriptores) {
        this.suscriptores = suscriptores != null ? suscriptores : new ArrayList<>();
    }

    private void validar() {
        if (jugadores == null) {
            throw new IllegalStateException("La lista de jugadores no puede ser null");
        }
        if (suscriptores == null) {
            throw new IllegalStateException("La lista de suscriptores no puede ser null");
        }
        if (estado == null) {
            throw new IllegalStateException("El estado no puede ser null");
        }
    }

    @Override
    public Partida getResult() {
        validar();
        return new Partida(turno, jugadores, cantBarcos, cantSubmarinos, cantCruceros, cantPortaAviones, totalNaves, estado, suscriptores);
    }

    public PartidaBuilder reset() {
        this.turno = null;
        this.jugadores = new ArrayList<>();
        this.cantBarcos = 0;
        this.cantSubmarinos = 0;
        this.cantCruceros = 0;
        this.cantPortaAviones = 0;
        this.totalNaves = 0;
        this.estado = EstadoPartida.POR_EMPEZAR;
        this.disparo = null;
        this.suscriptores = new ArrayList<>();
        return this;
    }
}
