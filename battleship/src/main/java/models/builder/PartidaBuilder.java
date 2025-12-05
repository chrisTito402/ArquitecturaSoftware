package models.builder;

import models.entidades.Disparo;
import models.entidades.Jugador;
import models.entidades.Partida;
import models.enums.EstadoPartida;
import models.observador.ISuscriptor;
import java.util.ArrayList;
import java.util.List;
import models.control.ControlModelo;
import models.control.IModeloCliente;
import views.DTOs.JugadorDTO;
import views.DTOs.TableroDTO;
import views.DTOs.NaveDTO;

/**
 *
 * @author daniel
 */
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

    @Override
    public void setTurno(Jugador turno) {
        this.turno = turno;
    }

    @Override
    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    @Override
    public void setCantBarcos(int cantBarcos) {
        this.cantBarcos = cantBarcos;
    }

    @Override
    public void setCantSubmarinos(int cantSubmarinos) {
        this.cantSubmarinos = cantSubmarinos;
    }

    @Override
    public void setCantCruceros(int cantCruceros) {
        this.cantCruceros = cantCruceros;
    }

    @Override
    public void setCantPortaAviones(int cantPortaAviones) {
        this.cantPortaAviones = cantPortaAviones;
    }

    @Override
    public void setTotalNaves(int totalNaves) {
        this.totalNaves = totalNaves;
    }

    @Override
    public void setEstado(EstadoPartida estado) {
        this.estado = estado;
    }

    @Override
    public void setDisparo(Disparo disparo) {
        this.disparo = disparo;
    }

    @Override
    public void setSuscriptores(List<ISuscriptor> suscriptores) {
        this.suscriptores = suscriptores;
    }

    @Override
    public IModeloCliente getResult() {
        // Crear TableroDTO con límites 10x10
        TableroDTO tableroDTO = new TableroDTO(10, 10);

        // Crear lista de naves vacía (se llenarán en FrmColocarNaves)
        List<NaveDTO> naves = new ArrayList<>();

        return null;
    }

}
