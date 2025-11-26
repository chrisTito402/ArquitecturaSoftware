package controllers.controller;

import dtos.DisparoDTO;
import dtos.JugadorDTO;
import dtos.enums.EstadoPartidaDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import views.IVistaPartida;

public class ManejadorNotificacionesVista {

    private final Map<String, Consumer<Object>> manejadores;
    private IVistaPartida vista;
    private IControlador controlador;

    public ManejadorNotificacionesVista() {
        this.manejadores = new HashMap<>();
        registrarManejadores();
    }

    private void registrarManejadores() {
        manejadores.put("RESULTADO_DISPARO", this::manejarDisparo);
        manejadores.put("ABANDONO_PARTIDA", this::manejarAbandono);
    }

    public void setVista(IVistaPartida vista) {
        this.vista = vista;
    }

    public void setControlador(IControlador controlador) {
        this.controlador = controlador;
    }

    public void manejar(String contexto, Object datos) {
        if (datos == null) {
            return;
        }

        Consumer<Object> manejador = manejadores.get(contexto);
        if (manejador != null) {
            manejador.accept(datos);
        }
    }

    private void manejarDisparo(Object datos) {
        if (!(datos instanceof DisparoDTO) || vista == null || controlador == null) {
            return;
        }

        DisparoDTO d = (DisparoDTO) datos;
        JugadorDTO jugador = controlador.getJugador();

        vista.reiniciarTimer();

        boolean esCasillaPropia = !d.getJugador().getNombre().equals(jugador.getNombre());

        vista.mostrarCasillaImpactada(d.getCoordenadas(), d.getResultadoDisparo(), esCasillaPropia);

        if (!esCasillaPropia && d.getPuntaje() != null) {
            vista.actualizarPuntaje(d.getPuntaje());
        }

        if (d.getEstadoPartida() == EstadoPartidaDTO.FINALIZADA) {
            vista.deshabilitarTableroEnemigo();
            vista.detenerTimer();

            if (d.getPuntaje() != null) {
                vista.mostrarMensajeFinPartida(d.getJugador().getNombre(), d.getPuntaje());
            }
        }
    }

    private void manejarAbandono(Object datos) {
        if (!(datos instanceof JugadorDTO) || vista == null) {
            return;
        }

        JugadorDTO dto = (JugadorDTO) datos;
        vista.mostrarMensajeFinPartida(dto.getNombre() + " abandonó la partida", null);
        vista.deshabilitarTableroEnemigo();
        vista.detenerTimer();
    }

    public boolean tieneManejador(String contexto) {
        return manejadores.containsKey(contexto);
    }
}
