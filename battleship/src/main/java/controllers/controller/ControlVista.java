package controllers.controller;

import shared.dto.CoordenadasDTO;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.enums.ResultadoDisparo;
import models.observador.ISuscriptor;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import models.enums.EstadoPartida;
import shared.dto.DisparoDTO;
import shared.dto.JugadorDTO;
import shared.dto.NaveDTO;
import views.frames.CasillaButton;
import views.frames.CasillaPanel;
import views.frames.FrmPartidaEnCurso;
import views.frames.PuntajePanel;
import views.frames.TimerPanel;

/**
 *
 * @author daniel
 */
public class ControlVista implements ISuscriptor {

    private static ControlVista controlVista;

    private IControlador control;
    private List<CasillaPanel> casillasPropias;
    private List<CasillaButton> casillasEnemigas;
    private TimerPanel timer;
    private PuntajePanel puntajePanel;
    private Map<String, Consumer<Object>> manejadoresNoti;
    private List<ISuscriptor> suscriptoresLobby;
    private String codigoPartida;
    private boolean esHost;

    private ControlVista() {
        manejadoresNoti = new HashMap<>();
        manejadoresNoti.put("RESULTADO_DISPARO", this::manejarDisparo);
        manejadoresNoti.put("ABANDONO_PARTIDA", this::manejarAbandono);
        manejadoresNoti.put("UNIRSE_PARTIDA", this::manejarUnirsePartida);
        manejadoresNoti.put("JUGADOR_UNIDO", this::manejarUnirsePartida);
        manejadoresNoti.put("EMPEZAR_PARTIDA", this::manejarEmpezarPartida);
        manejadoresNoti.put("ABANDONAR_LOBBY", this::manejarAbandonarLobby);
        // Nuevos manejadores para crear/unirse partida con codigo
        manejadoresNoti.put("RESULTADO_CREAR_PARTIDA", this::manejarResultadoCrearPartida);
        manejadoresNoti.put("RESULTADO_VALIDAR_CODIGO", this::manejarResultadoValidarCodigo);
        manejadoresNoti.put("RESULTADO_UNIRSE_PARTIDA", this::manejarResultadoUnirsePartida);
        manejadoresNoti.put("OPONENTE_LISTO", this::manejarOponenteListo);
        suscriptoresLobby = new ArrayList<>();
    }

    private void manejarOponenteListo(Object datos) {
        System.out.println("ControlVista: Oponente esta listo, notificando a lobby...");
        notificarLobby("OPONENTE_LISTO", datos);
    }

    public void suscribirLobby(ISuscriptor suscriptor) {
        if (suscriptor != null && !suscriptoresLobby.contains(suscriptor)) {
            suscriptoresLobby.add(suscriptor);
        }
    }

    public void desuscribirLobby(ISuscriptor suscriptor) {
        suscriptoresLobby.remove(suscriptor);
    }

    private void notificarLobby(String contexto, Object datos) {
        // Crear copia para evitar ConcurrentModificationException
        // si un suscriptor se desuscribe durante la notificacion
        List<ISuscriptor> copia = new ArrayList<>(suscriptoresLobby);
        for (ISuscriptor s : copia) {
            s.notificar(contexto, datos);
        }
    }

    public String getCodigoPartida() {
        return codigoPartida;
    }

    public void setCodigoPartida(String codigoPartida) {
        this.codigoPartida = codigoPartida;
    }

    public boolean isEsHost() {
        return esHost;
    }

    public void setEsHost(boolean esHost) {
        this.esHost = esHost;
    }

    public static ControlVista getInstancia() {
        if (controlVista == null) {
            controlVista = new ControlVista();
        }
        return controlVista;
    }

    public IControlador getControl() {
        return this.control;
    }

    public List<CasillaPanel> getCasillasPropias() {
        return casillasPropias;
    }

    public List<CasillaButton> getCasillasEnemigas() {
        return casillasEnemigas;
    }

    public void setControl(IControlador control) {
        this.control = control;
    }

    public TimerPanel getTimer() {
        return timer;
    }

    public void setTimer(TimerPanel timer) {
        this.timer = timer;
    }

    public PuntajePanel getPuntajePanel() {
        return puntajePanel;
    }

    public void setPuntajePanel(javax.swing.JPanel panel) {  //Para que acepte JPanel Generico
        if (panel instanceof PuntajePanel) {
            this.puntajePanel = (PuntajePanel) panel;
            System.out.println("PuntajePanel registrado correctamente");
        } else {
            System.err.println("ERROR: El panel no es de tipo PuntajePanel");
        }
    }

    public void realizarDisparo(Coordenadas c) {
        control.realizarDisparo(c);
    }

    private Component getCasillaPropia(Coordenadas c) {
        Component cP = casillasPropias.stream().filter(e -> e.getCoordenadas().getX() == c.getX()
                && e.getCoordenadas().getY() == c.getY())
                .findFirst()
                .orElse(null);

        return cP;
    }

    private Component getCasillaEnemiga(Coordenadas c) {
        Component cB = casillasEnemigas.stream().filter(e -> e.getCoordenadas().getX() == c.getX()
                && e.getCoordenadas().getY() == c.getY())
                .findFirst()
                .orElse(null);

        return cB;
    }

    @Override
    public void notificar(String contexto, Object datos) {
        Consumer<Object> manejador = manejadoresNoti.get(contexto);
        if (manejador != null) {
            manejador.accept(datos);
        } else {
            System.out.println("ControlVista: No hay manejador para contexto: " + contexto);
        }
    }

    private void manejarDisparo(Object datos) {
        if (!(datos instanceof DisparoDTO)) {
            System.out.println("Los datos no son un objeto DisparoDTO");
            return;
        }

        // Reiniciar Temporizador
        timer.initTimer();

        DisparoDTO d = (DisparoDTO) datos;
        Coordenadas c = d.getCoordenadas();

        JugadorDTO jugador = control.getJugador();

        Component componente;
        if (d.getJugador().getNombre().equals(jugador.getNombre())) {
            componente = getCasillaEnemiga(d.getCoordenadas());
            if (puntajePanel != null && d.getPuntaje() != null) {
                puntajePanel.actualizarPuntaje(d.getPuntaje());
                System.out.println("Puntaje actualizado en UI: " + d.getPuntaje().getPuntosTotales());
            }
        } else {
            componente = getCasillaPropia(d.getCoordenadas());
        }

        if (d.getResultadoDisparo() == ResultadoDisparo.IMPACTO) {
            componente.setBackground(Color.YELLOW);
        }
        if (d.getResultadoDisparo() == ResultadoDisparo.HUNDIMIENTO) {
            componente.setBackground(Color.RED);
        }
        if (d.getResultadoDisparo() == ResultadoDisparo.AGUA) {
            componente.setBackground(Color.BLUE);
        }

        System.out.println(c.getX() + " " + c.getY());
        System.out.println(d.getResultadoDisparo().toString());

        if (d.getEstadoPartida() == EstadoPartida.FINALIZADA) {
            casillasEnemigas.forEach(e -> e.setEnabled(false));
            timer.stopTimer();
            System.out.println("EL JUGADOR " + d.getJugador().getNombre() + " GANO LA PARTIDA!!");

            if (d.getPuntaje() != null) {
                String mensaje = String.format(
                        "¡Partida terminada!\n\n"
                        + "Ganador: %s\n"
                        + "Puntaje final: %d puntos\n"
                        + "Aciertos: %d\n"
                        + "Fallos: %d\n"
                        + "Naves hundidas: %d\n"
                        + "Precisión: %.2f%%",
                        d.getJugador().getNombre(),
                        d.getPuntaje().getPuntosTotales(),
                        d.getPuntaje().getDisparosAcertados(),
                        d.getPuntaje().getDisparosFallados(),
                        d.getPuntaje().getNavesHundidas(),
                        d.getPuntaje().getPrecision()
                );

                JOptionPane.showMessageDialog(null, mensaje, "Fin de Partida", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void manejarAbandono(Object datos) {
        JugadorDTO dto = (JugadorDTO) datos;
        JugadorDTO yo = control.getJugador();

        // Si YO soy el que abandonó → no mostrar nada
        if (dto.getNombre().equals(yo.getNombre())) {
            return;
        }

        // Mostrar aviso SOLO al rival
        JOptionPane.showMessageDialog(
                null,
                "El jugador " + dto.getNombre() + " abandonó la partida."
        );

        casillasEnemigas.forEach(c -> c.setEnabled(false));
        timer.stopTimer();

        System.out.println("Partida finalizada por abandono del rival.");
    }

    public void initTableroPropio() {
        casillasPropias = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                CoordenadasDTO coordenadas = new CoordenadasDTO(i, j);
                CasillaPanel cP = new CasillaPanel(coordenadas);
                cP.setBackground(new Color(242, 242, 242)); // O cualquier color que necesites
                cP.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                casillasPropias.add(cP);
            }
        }
    }

    public void initTableroEnemigo() {
        casillasEnemigas = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Coordenadas coordenadas = new Coordenadas(i, j);
                CasillaButton cB = new CasillaButton(coordenadas);
                cB.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                cB.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        realizarDisparo(coordenadas);
                        //cB.setEnabled(false);
                    }
                });
                casillasEnemigas.add(cB);
            }
        }
    }

    public void crearPartida(Jugador j) {
        control.crearPartida(j);
    }

    public void addNave(NaveDTO nave, List<Coordenadas> coordenadas) {
        control.addNave(nave, coordenadas);
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

    public void mostrarFrmPartidaEnCurso() {
        new FrmPartidaEnCurso().setVisible(true);
        timer.initTimer();
    }

    // Caso de Uso: Unirse Partida
    public void unirsePartida(JugadorDTO jugador) {
        control.unirsePartida(jugador);
    }

    public void empezarPartida() {
        control.empezarPartida();
    }

    public void abandonarLobby(JugadorDTO jugador) {
        control.abandonarLobby(jugador);
    }

    public List<JugadorDTO> getJugadores() {
        return control.getJugadores();
    }

    public void abandonarPartida() {

        // 1. Obtener DTO del jugador actual
        JugadorDTO dto = control.getJugador();

        // 2. Convertir DTO → ENTIDAD
        Jugador jugador = new Jugador(dto.getNombre(), dto.getColor(), dto.getEstado());

        control.abandonarPartida(jugador);
    }

    private void manejarUnirsePartida(Object datos) {
        JugadorDTO dto = (JugadorDTO) datos;
        System.out.println("=== ControlVista: manejarUnirsePartida ===");
        System.out.println("Jugador recibido: " + dto.getNombre());
        System.out.println("Suscriptores del lobby: " + suscriptoresLobby.size());

        // Notificar a los suscriptores del lobby para actualizar la UI
        notificarLobby("JUGADOR_UNIDO", dto);
    }

    private void manejarEmpezarPartida(Object datos) {
        System.out.println("ControlVista: Partida empezando, notificando a lobby...");
        // Notificar a los suscriptores del lobby (FrmColocarNaves) para que transicionen
        notificarLobby("EMPEZAR_PARTIDA", datos);
    }

    private void manejarAbandonarLobby(Object datos) {
        JugadorDTO dto = (JugadorDTO) datos;
        JOptionPane.showMessageDialog(null, "El jugador " + dto.getNombre() + " abandono el lobby.");
    }

    // ===== NUEVOS METODOS PARA CREAR/UNIRSE PARTIDA CON CODIGO =====

    /**
     * Crea una nueva partida en el servidor con el codigo especificado.
     * @param jugador datos del jugador que crea la partida
     * @param codigo codigo de la partida
     */
    public void crearPartidaConCodigo(JugadorDTO jugador, String codigo) {
        this.codigoPartida = codigo;
        this.esHost = true;
        control.crearPartida(jugador, codigo);
    }

    /**
     * Valida si un codigo de partida es valido antes de intentar unirse.
     * @param codigo codigo a validar
     */
    public void validarCodigoPartida(String codigo) {
        control.validarCodigoPartida(codigo);
    }

    /**
     * Se une a una partida existente usando el codigo.
     * @param jugador datos del jugador
     * @param codigo codigo de la partida
     */
    public void unirsePartidaConCodigo(JugadorDTO jugador, String codigo) {
        this.codigoPartida = codigo;
        this.esHost = false;
        control.unirsePartida(jugador, codigo);
    }

    private void manejarResultadoCrearPartida(Object datos) {
        shared.dto.CrearPartidaDTO resultado = (shared.dto.CrearPartidaDTO) datos;

        if (resultado.isExito()) {
            System.out.println("Partida creada: " + resultado.getCodigoPartida());
            this.codigoPartida = resultado.getCodigoPartida();
            notificarLobby("PARTIDA_CREADA", resultado);
        } else {
            JOptionPane.showMessageDialog(null,
                resultado.getMensaje(),
                "Error al crear partida",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void manejarResultadoValidarCodigo(Object datos) {
        shared.dto.UnirsePartidaDTO resultado = (shared.dto.UnirsePartidaDTO) datos;
        notificarLobby("RESULTADO_VALIDAR_CODIGO", resultado);
    }

    private void manejarResultadoUnirsePartida(Object datos) {
        shared.dto.UnirsePartidaDTO resultado = (shared.dto.UnirsePartidaDTO) datos;

        if (resultado.isExito()) {
            System.out.println("Te uniste a la partida: " + resultado.getCodigoPartida());
            notificarLobby("UNIDO_A_PARTIDA", resultado);
        } else {
            JOptionPane.showMessageDialog(null,
                resultado.getMensaje(),
                "Error al unirse",
                JOptionPane.ERROR_MESSAGE);
            notificarLobby("ERROR_UNIRSE_PARTIDA", resultado);
        }
    }

    /**
     * Notifica al guest que debe ir a la pantalla de colocar naves.
     * Llamado por el host cuando presiona "Empezar Partida" en el lobby.
     */
    public void notificarIrAColocarNaves() {
        control.notificarIrAColocarNaves();
    }

    /**
     * Maneja la notificacion de ir a colocar naves (recibida del servidor).
     */
    public void manejarIrAColocarNaves() {
        notificarLobby("IR_A_COLOCAR_NAVES", null);
    }

    /**
     * Notifica que este jugador (guest) esta listo con sus naves.
     */
    public void notificarJugadorListo() {
        control.notificarJugadorListo();
    }
}
