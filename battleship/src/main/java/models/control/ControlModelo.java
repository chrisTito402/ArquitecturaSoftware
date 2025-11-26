package models.control;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import models.entidades.Jugador;
import models.entidades.Tablero;
import models.observador.GestorSuscriptores;
import models.observador.ISuscriptor;

public class ControlModelo implements IModeloCliente {

    private Jugador jugadorLocal;
    private Tablero tablero;
    private boolean esMiTurno;
    private GestorSuscriptores gestorSuscriptores;
    private List<Jugador> jugadores;

    public ControlModelo(List<ISuscriptor> suscriptores) {
        this.gestorSuscriptores = new GestorSuscriptores(suscriptores);
        this.jugadores = new CopyOnWriteArrayList<>();
        this.esMiTurno = false;
    }

    @Override
    public Jugador getTurno() {
        return esMiTurno ? jugadorLocal : null;
    }

    public boolean esMiTurno() {
        return esMiTurno;
    }

    public void setEsMiTurno(boolean esMiTurno) {
        this.esMiTurno = esMiTurno;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public void setTablero(Tablero tablero) {
        this.tablero = tablero;
    }

    public Jugador getJugadorLocal() {
        return jugadorLocal;
    }

    @Override
    public void addJugador(Jugador j) {
        if (j == null) {
            return;
        }

        this.jugadorLocal = j;
        if (!jugadores.contains(j)) {
            jugadores.add(j);
        }
    }

    @Override
    public void crearTableros() {
        notificarAllSuscriptores("TABLEROS_CREADOS", null);
    }

    @Override
    public void suscribirAPartida(ISuscriptor suscriptor) {
        gestorSuscriptores.suscribir(suscriptor);
    }

    @Override
    public void notificarAllSuscriptores(String contexto, Object datos) {
        gestorSuscriptores.notificarTodos(contexto, datos);
    }

    @Override
    public void unirsePartida(Jugador jugador) {
        if (jugador == null) {
            return;
        }

        this.jugadorLocal = jugador;
        if (!jugadores.contains(jugador)) {
            jugadores.add(jugador);
        }
        notificarAllSuscriptores("JUGADOR_UNIDO_LOCAL", jugador);
    }

    @Override
    public void empezarPartida() {
        this.esMiTurno = true;
        notificarAllSuscriptores("PARTIDA_INICIADA", null);
    }

    @Override
    public void abandonarLobby(Jugador jugador) {
        jugadores.removeIf(j -> j.equals(jugador));
        notificarAllSuscriptores("ABANDONAR_PARTIDA", jugador);
    }

    @Override
    public List<Jugador> getJugadores() {
        return new ArrayList<>(jugadores);
    }
}
