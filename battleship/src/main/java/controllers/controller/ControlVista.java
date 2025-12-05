package controllers.controller;

import views.DTOs.CoordenadasDTO;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import models.enums.ColorJugador;
import models.enums.EstadoPartida;
import models.enums.OrientacionNave;
import models.enums.ResultadoConfirmarNaves;
import models.enums.ResultadoEmpezarPartida;
import views.DTOs.AddNaveDTO;
import views.DTOs.DisparoDTO;
import views.DTOs.JugadorDTO;
import views.DTOs.TipoNaveDTO;
import views.frames.AddNavePanel;
import views.frames.AddNaves;
import views.frames.CasillaButton;
import views.frames.CasillaPanel;
import views.frames.FrmLobby;
import views.frames.FrmPartidaEnCurso;
import views.frames.FrmRegistrarJugador;
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
    private JFrame frameActual;
    private JLabel lblTurno;
    
    private AddNavePanel addNavePanel;

    private ControlVista() {
        manejadoresNoti = new HashMap<>();
        manejadoresNoti.put("RESULTADO_DISPARO", this::manejarDisparo);
        manejadoresNoti.put("ABANDONO_PARTIDA", this::manejarAbandono);
        manejadoresNoti.put("JUGADOR_UNIDO", this::manejarJugadorUnido);  // El servidor envía con esta clave
        manejadoresNoti.put("RESULTADO_UNIRSE_PARTIDA", this::manejarResultadoUnirsePartida);
        manejadoresNoti.put("EMPEZAR_PARTIDA", this::manejarEmpezarPartida);
        manejadoresNoti.put("NO_EMPEZAR_PARTIDA", this::manejarNoEmpezarPartida);
        manejadoresNoti.put("ABANDONAR_LOBBY", this::manejarAbandonarLobby);
        manejadoresNoti.put("RESULTADO_ADD_NAVE", this::manejarResultadoAddNave);
        manejadoresNoti.put("RESULTADO_CONFIRMAR_NAVES", this::manejarResultadoConfirmarNaves);
        manejadoresNoti.put("CAMBIAR_TURNO", this::manejarCambiarTurno);
        
        suscriptoresLobby = new ArrayList<>();
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
        for (ISuscriptor s : suscriptoresLobby) {
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
        if (datos == null) {
            System.out.println("Los datos estan vacios.");
            return;
        } else {
            manejadoresNoti.get(contexto).accept(datos);
        }
    }

    private void manejarCambiarTurno(Object datos) {
        JugadorDTO j = (JugadorDTO) datos;
        timer.initTimer();
        
        lblTurno.setText("Turno del Jugador: " + j.getNombre());
        lblTurno.revalidate();
        lblTurno.repaint();
        
        System.out.println("CAMBIAR_TURNO");
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

    private void manejarResultadoAddNave(Object datos) {
        AddNaveDTO resultado = (AddNaveDTO) datos;
        
        addNavePanel.pintarNaveAñadida(resultado.getCoordenadases());
    }
    
    private void manejarResultadoConfirmarNaves(Object datos) {
        ResultadoConfirmarNaves resultado = (ResultadoConfirmarNaves) datos;
        
        System.out.println(resultado);
        if (resultado == ResultadoConfirmarNaves.EMPEZAR_PARTIDA) {
            initTableroPropio();
            initTableroEnemigo();
            mostrarFrmPartidaEnCurso();
            return;
        }
        
        JOptionPane.showMessageDialog(null, resultado.toString());
    }
    
    public void confirmarNaves() {
        control.setConfirmarNaves();
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

    // ADD NAVE METODOS
    public void addNave(TipoNaveDTO tipo, OrientacionNave orientacion, List<Coordenadas> coordenadas) {
        control.addNave(tipo, orientacion, coordenadas);
    }

    public AddNavePanel getAddNavePanel() {
        return addNavePanel;
    }

    public void setAddNavePanel(AddNavePanel addNavePanel) {
        this.addNavePanel = addNavePanel;
    }
    
    public void addJugador(Jugador j) {
        control.addJugador(j);
    }

    public JLabel getLblTurno() {
        return lblTurno;
    }

    public void mostrarFrmPartidaEnCurso() {
        if (frameActual != null) {
            frameActual.dispose();
        }
        frameActual = new FrmPartidaEnCurso();
        frameActual.setVisible(true);
    }
    
    public void mostrarFrmAddNaves() {
        if (frameActual != null) {
            frameActual.dispose();
        }
        this.lblTurno = new JLabel();
        addNavePanel = new AddNavePanel();
        frameActual = new AddNaves(addNavePanel);
        frameActual.setVisible(true);
    }

    public void mostrarFrmLobby(String nombre, Color color) {
        if (frameActual != null) {
            frameActual.dispose();
        }
        frameActual = new FrmLobby(nombre, color);
        frameActual.setVisible(true);
    }
    
    public void mostrarFrmRegistrarJugador() {
        if (frameActual != null) {
            frameActual.dispose();
        }
        
        frameActual = new FrmRegistrarJugador();
        frameActual.setVisible(true);
    }
    
    // Caso de Uso: Unirse Partida
    public void unirsePartida(String nombre, ColorJugador color) {
        control.unirsePartida(nombre, color);
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

    private void manejarJugadorUnido(Object datos) {
        JugadorDTO j = (JugadorDTO) datos;
        
        if (frameActual instanceof FrmLobby) {
            FrmLobby frame = (FrmLobby) frameActual;
            frame.agregarJugador(j.getNombre(), Color.yellow);
        }
    }
    
    private void manejarResultadoUnirsePartida(Object datos) {
        JugadorDTO dto = (JugadorDTO) datos;

        // Notificar a los suscriptores del lobby para actualizar la UI
        Color color;
        if (dto.getColor() == ColorJugador.AZUL) {
            color = Color.BLUE;
        } else {
            color = Color.RED;
        }
        mostrarFrmLobby(dto.getNombre(), color);
    }

    private void manejarEmpezarPartida(Object datos) {
        mostrarFrmAddNaves();
    }
    
    private void manejarNoEmpezarPartida(Object datos) {
        ResultadoEmpezarPartida resultado = (ResultadoEmpezarPartida) datos;
        JOptionPane.showMessageDialog(null, resultado.name());
    }

    private void manejarAbandonarLobby(Object datos) {
        JugadorDTO dto = (JugadorDTO) datos;
        JOptionPane.showMessageDialog(null, "El jugador " + dto.getNombre() + " abandono el lobby.");
    }
}
