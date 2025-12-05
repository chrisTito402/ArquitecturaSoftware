package cliente.negocio.builder;

import compartido.entidades.Disparo;
import compartido.entidades.Jugador;
import compartido.enums.EstadoPartida;
import compartido.observador.ISuscriptor;
import java.util.ArrayList;
import java.util.List;
import cliente.negocio.ControlModelo;
import cliente.negocio.IModeloCliente;
import compartido.comunicacion.dto.JugadorDTO;
import compartido.comunicacion.dto.TableroDTO;
import compartido.comunicacion.dto.NaveDTO;

/**
 * Este es el Builder que arma el ControlModelo (el modelo de la partida).
 * Vas configurando las cosas una por una con los setters y al final
 * llamas getResult() y te da el objeto ya armado.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
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

    /**
     * Retorna el modelo construido.
     */
    @Override
    public IModeloCliente getResult() {
        // Crear TableroDTO con límites 10x10
        TableroDTO tableroDTO = new TableroDTO(10, 10);

        // Crear lista de naves vacía (se llenarán en FrmColocarNaves)
        List<NaveDTO> naves = new ArrayList<>();

        // Crear ControlModelo con los valores configurados
        ControlModelo modelo = new ControlModelo(
                null,  // jugadorDTO se asignará cuando se una
                tableroDTO,
                naves,
                false, // turno inicial
                suscriptores != null ? suscriptores : new ArrayList<>()
        );

        return modelo;
    }

}
