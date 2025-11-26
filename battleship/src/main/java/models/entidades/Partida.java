package models.entidades;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import models.enums.EstadoPartida;
import models.observador.GestorSuscriptores;
import models.observador.ISuscriptor;

public class Partida {

    private Jugador turno;
    private List<Jugador> jugadores;
    private int cantBarcos;
    private int cantSubmarinos;
    private int cantCruceros;
    private int cantPortaAviones;
    private int totalNaves;
    private EstadoPartida estado;
    private GestorSuscriptores gestorSuscriptores;

    public Partida(Jugador turno, List<Jugador> jugadores, int cantBarcos, int cantSubmarinos,
                   int cantCruceros, int cantPortaAviones, int totalNaves, EstadoPartida estado,
                   List<ISuscriptor> suscriptores) {
        this.turno = turno;
        this.jugadores = new CopyOnWriteArrayList<>(jugadores);
        this.cantBarcos = cantBarcos;
        this.cantSubmarinos = cantSubmarinos;
        this.cantCruceros = cantCruceros;
        this.cantPortaAviones = cantPortaAviones;
        this.totalNaves = totalNaves;
        this.estado = estado;
        this.gestorSuscriptores = new GestorSuscriptores(suscriptores);
    }

    public boolean cambiarTurno() {
        Jugador siguienteTurno = jugadores.stream()
                .filter(e -> !e.equals(turno))
                .findFirst()
                .orElse(null);

        if (siguienteTurno == null) {
            return false;
        }

        this.turno = siguienteTurno;
        return true;
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = new CopyOnWriteArrayList<>(jugadores);
    }

    public void addJugador(Jugador j) {
        jugadores.add(j);
    }

    public void removeJugador(Jugador j) {
        jugadores.removeIf(jugador -> jugador.equals(j));
    }

    public Jugador getTurno() {
        return turno;
    }

    public void setTurno(Jugador turno) {
        this.turno = turno;
    }

    public EstadoPartida getEstado() {
        return estado;
    }

    public void setEstado(EstadoPartida estado) {
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

    public void suscribirAPartida(ISuscriptor suscriptor) {
        gestorSuscriptores.suscribir(suscriptor);
    }

    public void desuscribir(ISuscriptor suscriptor) {
        gestorSuscriptores.desuscribir(suscriptor);
    }

    public void notificarAllSuscriptores(String contexto, Object datos) {
        gestorSuscriptores.notificarTodos(contexto, datos);
    }
}
