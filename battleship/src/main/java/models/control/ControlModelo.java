package models.control;

import java.time.Instant;
import java.util.List;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.enums.ResultadoDisparo;
import models.observador.ISuscriptor;
import dtos.AddNaveDTO;
import dtos.DisparoDTO;
import dtos.JugadorDTO;
import dtos.NaveDTO;
import dtos.TableroDTO;

/**
 *
 * @author daniel
 */
public class ControlModelo implements IModeloCliente {

    private JugadorDTO jugador;
    private TableroDTO tablero;
    private List<NaveDTO> naves;
    private boolean turno;
    private List<ISuscriptor> suscriptores;

    public ControlModelo(JugadorDTO jugador, TableroDTO tablero, List<NaveDTO> naves, boolean turno, List<ISuscriptor> suscriptores) {
        this.jugador = jugador;
        this.tablero = tablero;
        this.naves = naves;
        this.turno = turno;
        this.suscriptores = suscriptores;
    }

    @Override
    public DisparoDTO realizarDisparo(Coordenadas coordenadas) {
        if (!turno) {
            System.out.println("No es el turno del Jugador");
            return null;
        }
        if (coordenadas.getX() > tablero.getLimiteX() || coordenadas.getX() < 0) {
            System.out.println("Las Coordenadas en X estan fuera del limite");
            return null;
        }
        if (coordenadas.getY() > tablero.getLimiteY() || coordenadas.getY() < 0) {
            System.out.println("Las coordenas en Y estan fuera del limite");
            return null;
        }

        DisparoDTO disparo = new DisparoDTO(jugador, coordenadas, null, null, Instant.now().toEpochMilli());
        return disparo;
    }
    
    @Override
    public void manejarResultadoDisparo(DisparoDTO disparo) {
        if (disparo.getResultadoDisparo() == ResultadoDisparo.DISPARO_FUERA_TIEMPO) {
            System.out.println(disparo.getResultadoDisparo());
            return;
        }
        if (disparo.getResultadoDisparo() == ResultadoDisparo.TURNO_INCORRECTO) {
            System.out.println(disparo.getResultadoDisparo());
            return;
        }
        
        notificarAllSuscriptores("RESULTADO_DISPARO", disparo);
    }

    @Override
    public AddNaveDTO addNave(NaveDTO nave, List<Coordenadas> coordenadas) {
        
        return null;
    }

    @Override
    public void addJugador(Jugador j) {
        if (j == null) {
            System.err.println("Error: No se puede agregar un jugador nulo");
            return;
        }

        // Convertir Jugador a DTO
        this.jugador = new JugadorDTO(j.getNombre(), j.getColor(), j.getEstado());
        System.out.println("Jugador agregado al modelo cliente: " + j.getNombre());
    }

    @Override
    public void crearTableros() {
        // En el modelo del cliente, los tableros se crean desde el servidor
        // Esta operación se notifica a través del patrón Observer
        System.out.println("Solicitud de crear tableros procesada");
        notificarAllSuscriptores("TABLEROS_CREADOS", null);
    }

    @Override
    public void suscribirAPartida(ISuscriptor suscriptor) {
        suscriptores.add(suscriptor);
    }

    @Override
    public void notificarAllSuscriptores(String contexto, Object datos) {
        suscriptores.forEach(s -> s.notificar(contexto, datos));
    }

    @Override
    public void unirsePartida(Jugador jugador) {
        if (jugador == null) {
            System.err.println("Error: No se puede unir un jugador nulo");
            return;
        }

        this.jugador = new JugadorDTO(jugador.getNombre(), jugador.getColor(), jugador.getEstado());
        System.out.println("Jugador " + jugador.getNombre() + " se unió a la partida");
        notificarAllSuscriptores("JUGADOR_UNIDO_LOCAL", jugador);
    }

    @Override
    public void empezarPartida() {
        this.turno = true; // El primer jugador comienza con turno
        System.out.println("Partida iniciada en el cliente");
        notificarAllSuscriptores("PARTIDA_INICIADA", null);
    }

    @Override
    public void abandonarLobby(Jugador jugador) {
        JugadorDTO dto = new JugadorDTO(jugador.getNombre(), jugador.getColor(), jugador.getEstado());
        notificarAllSuscriptores("ABANDONAR_PARTIDA", dto);
    }
    
    @Override
    public List<Jugador> getJugadores() {
        // En el modelo del cliente, no se mantiene lista completa de jugadores
        // Solo se tiene el jugador actual
        System.out.println("Advertencia: getJugadores() en modelo cliente retorna lista vacía");
        return new java.util.ArrayList<>();
    }

    @Override
    public JugadorDTO getJugador() {
        return jugador;
    }
}
