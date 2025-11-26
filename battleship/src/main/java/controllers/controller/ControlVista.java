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

    private ControlVista() {
        this.manejadorNotificaciones = new ManejadorNotificacionesVista();
    }

    private static class SingletonHolder {
        private static final ControlVista INSTANCIA = new ControlVista();
    }

    public static ControlVista getInstancia() {
        return SingletonHolder.INSTANCIA;
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
        control.realizarDisparo(c);
    }

    public void realizarDisparoDTO(CoordenadasDTO coordenadasDTO) {
        Coordenadas coordenadas = CoordenadasMapper.toEntity(coordenadasDTO);
        control.realizarDisparo(coordenadas);
    }

    @Override
    public void notificar(String contexto, Object datos) {
        manejadorNotificaciones.manejar(contexto, datos);
    }

    public void crearPartida(Jugador j) {
        control.crearPartida(j);
    }

    public void addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas) {
        control.addNave(jugador, nave, coordenadas);
    }

    public void addJugador(Jugador j) {
        control.addJugador(j);
    }

    public void crearTableros() {
        control.crearTableros();
    }

    public void suscribirAModelo() {
        control.suscribirAPartida(this);
    }

    public void unirsePartida(Jugador jugador) {
        control.unirsePartida(jugador);
    }

    public void empezarPartida() {
        control.empezarPartida();
    }

    public void abandonarLobby(Jugador jugador) {
        control.abandonarLobby(jugador);
    }

    public List<Jugador> getJugadores() {
        return control.getJugadores();
    }
}
