package controllers.controller;

import dtos.CoordenadasDTO;
import dtos.mappers.CoordenadasMapper;
import java.util.List;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.observador.ISuscriptor;
import views.IVistaPartida;

public class ControlVista implements ISuscriptor {

    private IControlador control;
    private IVistaPartida vista;
    private final ManejadorNotificacionesVista manejadorNotificaciones;

    private static volatile ControlVista instanciaGlobal;
    private static final Object LOCK = new Object();

    public ControlVista() {
        this.manejadorNotificaciones = new ManejadorNotificacionesVista();
    }

    public ControlVista(IControlador control) {
        this.control = control;
        this.manejadorNotificaciones = new ManejadorNotificacionesVista();
        this.manejadorNotificaciones.setControlador(control);
    }

    public ControlVista(IControlador control, IVistaPartida vista) {
        this.control = control;
        this.vista = vista;
        this.manejadorNotificaciones = new ManejadorNotificacionesVista();
        this.manejadorNotificaciones.setControlador(control);
        this.manejadorNotificaciones.setVista(vista);
    }

    public static void setInstanciaGlobal(ControlVista instancia) {
        synchronized (LOCK) {
            instanciaGlobal = instancia;
        }
    }

    public static ControlVista getInstancia() {
        ControlVista resultado = instanciaGlobal;
        if (resultado == null) {
            synchronized (LOCK) {
                resultado = instanciaGlobal;
                if (resultado == null) {
                    instanciaGlobal = resultado = new ControlVista();
                }
            }
        }
        return resultado;
    }

    public static void resetInstancia() {
        synchronized (LOCK) {
            instanciaGlobal = null;
        }
    }

    public IControlador getControl() {
        return this.control;
    }

    public void setControl(IControlador control) {
        this.control = control;
        manejadorNotificaciones.setControlador(control);
    }

    public void setVista(IVistaPartida vista) {
        this.vista = vista;
        manejadorNotificaciones.setVista(vista);
    }

    public IVistaPartida getVista() {
        return this.vista;
    }

    public void realizarDisparo(Coordenadas c) {
        if (control != null) {
            control.realizarDisparo(c);
        }
    }

    public void realizarDisparoDTO(CoordenadasDTO coordenadasDTO) {
        Coordenadas coordenadas = CoordenadasMapper.toEntity(coordenadasDTO);
        realizarDisparo(coordenadas);
    }

    @Override
    public void notificar(String contexto, Object datos) {
        manejadorNotificaciones.manejar(contexto, datos);
    }

    public void crearPartida(Jugador j) {
        if (control != null) {
            control.crearPartida(j);
        }
    }

    public void addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas) {
        if (control != null) {
            control.addNave(jugador, nave, coordenadas);
        }
    }

    public void addJugador(Jugador j) {
        if (control != null) {
            control.addJugador(j);
        }
    }

    public void crearTableros() {
        if (control != null) {
            control.crearTableros();
        }
    }

    public void suscribirAModelo() {
        if (control != null) {
            control.suscribirAPartida(this);
        }
    }

    public void unirsePartida(Jugador jugador) {
        if (control != null) {
            control.unirsePartida(jugador);
        }
    }

    public void empezarPartida() {
        if (control != null) {
            control.empezarPartida();
        }
    }

    public void abandonarLobby(Jugador jugador) {
        if (control != null) {
            control.abandonarLobby(jugador);
        }
    }

    public List<Jugador> getJugadores() {
        if (control != null) {
            return control.getJugadores();
        }
        return List.of();
    }

    public boolean esMiTurno() {
        if (control != null) {
            return control.esMiTurno();
        }
        return false;
    }
}
