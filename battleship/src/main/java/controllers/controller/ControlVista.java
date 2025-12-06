package controllers.controller;

import models.entidades.Coordenadas;
import models.observador.ISuscriptor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import models.enums.ColorJugador;
import models.enums.EstadoPartida;
import models.enums.OrientacionNave;
import models.enums.ResultadoAddJugador;
import models.enums.ResultadoConfirmarNaves;
import models.enums.ResultadoEmpezarPartida;
import views.DTOs.AddNaveDTO;
import views.DTOs.DisparoDTO;
import views.DTOs.JugadorDTO;
import views.DTOs.ResultadoConfirmarNavesDTO;
import views.DTOs.TipoNaveDTO;
import views.frames.AddNavePanel;
import views.frames.AddNaves;
import views.frames.CasillaButton;
import views.frames.CasillaPanel;
import views.frames.FrmLobby;
import views.frames.FrmPartidaEnCurso;
import views.frames.FrmRegistrarJugador;
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
    private Map<String, Consumer<Object>> manejadoresNoti;
    private JFrame frameActual;
    private JLabel lblTurno;
    
    private AddNavePanel addNavePanel;

    private ControlVista() {
        manejadoresNoti = new HashMap<>();
        manejadoresNoti.put("RESULTADO_DISPARO_PROPIO", this::manejarDisparoPropio);
        manejadoresNoti.put("RESULTADO_DISPARO_ENEMIGO", this::manejarDisparoEnemigo);
        manejadoresNoti.put("HUNDIMIENTO_NAVE", this::manejarHundimientoNave);
        manejadoresNoti.put("ABANDONO_PARTIDA", this::manejarAbandono);
        manejadoresNoti.put("JUGADOR_UNIDO", this::manejarJugadorUnido);  // El servidor envía con esta clave
        manejadoresNoti.put("RESULTADO_UNIRSE_PARTIDA", this::manejarResultadoUnirsePartida);
        manejadoresNoti.put("ERROR_UNIRSE_PARTIDA", this::manejarErrorUniserPartida);
        manejadoresNoti.put("EMPEZAR_PARTIDA", this::manejarEmpezarPartida);
        manejadoresNoti.put("NO_EMPEZAR_PARTIDA", this::manejarNoEmpezarPartida);
        manejadoresNoti.put("ABANDONAR_LOBBY", this::manejarAbandonarLobby);
        manejadoresNoti.put("RESULTADO_ADD_NAVE", this::manejarResultadoAddNave);
        manejadoresNoti.put("RESULTADO_CONFIRMAR_NAVES", this::manejarResultadoConfirmarNaves);
        manejadoresNoti.put("ERROR_CONFIRMAR_NAVES", this::manejarErrorConfirmarNaves);
        manejadoresNoti.put("CAMBIAR_TURNO", this::manejarCambiarTurno);
        manejadoresNoti.put("JUGADOR_ENEMIGO_OBTENIDO", this::manejarJugadorEnemigoObtenido);
        manejadoresNoti.put("MOSTRAR_MARCADOR", this::manejarMostrarMarcador);
        manejadoresNoti.put("ERROR_ADD_NAVE", this::manejarErrorAddNave);
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
    
    private void manejarDisparoEnemigo(Object datos) {
        if (!(datos instanceof DisparoDTO)) {
            System.out.println("Los datos no son un objeto DisparoDTO");
            return;
        }

        // Reiniciar Temporizador
        timer.initTimer();
        
        DisparoDTO d = (DisparoDTO) datos;
        
        
        Component componente = getCasillaPropia(d.getCoordenadas());
        CasillaPanel casillaPanel = (CasillaPanel) componente;
        casillaPanel.marcarDisparo();
        
        if (d.getEstadoPartida() == EstadoPartida.FINALIZADA) {
            casillasEnemigas.forEach(e -> e.setEnabled(false));
            timer.stopTimer();
            System.out.println("EL JUGADOR " + d.getJugador().getNombre() + " GANO LA PARTIDA!!");
            JOptionPane.showMessageDialog(frameActual, "EL JUGADOR " + d.getJugador().getNombre() + " GANO LA PARTIDA!!");
        }
    }
    
    private void manejarDisparoPropio(Object datos) {
        if (!(datos instanceof DisparoDTO)) {
            System.out.println("Los datos no son un objeto DisparoDTO");
            return;
        }

        // Reiniciar Temporizador
        timer.initTimer();

        DisparoDTO d = (DisparoDTO) datos;
        Coordenadas c = d.getCoordenadas();

        Component componente = getCasillaEnemiga(d.getCoordenadas());

        if (null != d.getResultadoDisparo()) {
            switch (d.getResultadoDisparo()) {
                case IMPACTO ->
                    componente.setBackground(Color.YELLOW);
                case HUNDIMIENTO ->
                    componente.setBackground(Color.RED);
                case AGUA ->
                    componente.setBackground(Color.BLUE);
                default -> {
                }
            }
        }

        System.out.println(c.getX() + " " + c.getY());
        System.out.println(d.getResultadoDisparo().toString());

        if (d.getEstadoPartida() == EstadoPartida.FINALIZADA) {
            casillasEnemigas.forEach(e -> e.setEnabled(false));
            timer.stopTimer();
            System.out.println("EL JUGADOR " + d.getJugador().getNombre() + " GANO LA PARTIDA!!");
            JOptionPane.showMessageDialog(frameActual, "EL JUGADOR " + d.getJugador().getNombre() + " GANO LA PARTIDA!!");
        }
    }
    
    private void manejarHundimientoNave(Object datos) {
//        List<Coordenadas> coordenadas = (List<Coordenadas>) datos;
//        
//        coordenadas.forEach(c -> {
//            CasillaButton casilla = casillasEnemigas.stream()
//                    .filter(e -> e.getCoordenadas().equals(c))
//                    .findFirst()
//                    .orElse(null);
//            
//            if (casilla != null) {
//                casilla.setBackground(Color.RED);
//            }
//        });
    }

    private void manejarResultadoAddNave(Object datos) {
        AddNaveDTO resultado = (AddNaveDTO) datos;
        
        addNavePanel.pintarNaveAñadida(resultado.getCoordenadases());
    }
    
    private void manejarErrorAddNave(Object datos) {
        AddNaveDTO dto = (AddNaveDTO) datos;
        JOptionPane.showMessageDialog(frameActual, dto.getResultado().name());
    }
    
    private void manejarErrorConfirmarNaves(Object datos) {
        ResultadoConfirmarNaves resultado = (ResultadoConfirmarNaves) datos;
        JOptionPane.showMessageDialog(null, resultado.name());
    }
    
    private void manejarResultadoConfirmarNaves(Object datos) {
        ResultadoConfirmarNavesDTO resultado = (ResultadoConfirmarNavesDTO) datos;

        System.out.println(resultado);
        initTableroPropio();
        initTableroEnemigo();
        
        Color color;
        if (resultado.getColor() == ColorJugador.AZUL) {
            color = Color.BLUE;
        } else {
            color = Color.RED;
        }
        
        resultado.getCoordenadas().forEach(e -> {
            CasillaPanel pnl = casillasPropias.stream()
                    .filter(c -> c.getCoordenadas().equals(e))
                    .findFirst()
                    .orElse(null);
            if (pnl != null) {
                pnl.setBackground(color);
            }
        });

        mostrarFrmPartidaEnCurso();
    }
    
    public void confirmarNaves() {
        control.setConfirmarNaves();
    }
    
    private void manejarAbandono(Object datos) {
        JugadorDTO dto = (JugadorDTO) datos;

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
                Coordenadas coordenadas = new Coordenadas(i, j);
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
        obtenerJugadorEnemigo();
        frameActual.setVisible(true);
    }
    
    public void mostrarFrmRegistrarJugador() {
        if (frameActual != null) {
            frameActual.dispose();
        }
        
        frameActual = new FrmRegistrarJugador();
        frameActual.setVisible(true);
    }
    
    public void mostrarMarcador() {
        control.obtenerMarcador();
    }
    
    // Caso de Uso: Unirse Partida
    public void unirsePartida(String nombre, ColorJugador color) {
        control.unirsePartida(nombre, color);
    }

    public void empezarPartida() {
        control.empezarPartida();
    }

    public void abandonarLobby() {
//        control.abandonarLobby(jugador);
    }
    
    public void abandonarPartida() {
        control.abandonarPartida();
    }

    private void manejarJugadorUnido(Object datos) {
        JugadorDTO j = (JugadorDTO) datos;
        
        if (!(frameActual instanceof FrmLobby)) {
            return;
        }
        
        Color color;
        if (j.getColor() == ColorJugador.AZUL) {
            color = Color.RED;
        } else {
            color = Color.BLUE;
        }
        FrmLobby frame = (FrmLobby) frameActual;
        frame.agregarJugador(j.getNombre(), color);
    }
    
    private void manejarErrorUniserPartida(Object datos) {
        ResultadoAddJugador resultado = (ResultadoAddJugador) datos;
        JOptionPane.showMessageDialog(null, resultado.name());
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
    
    private void manejarJugadorEnemigoObtenido(Object datos) {
        JugadorDTO j = (JugadorDTO) datos;
        if (frameActual instanceof FrmLobby) {
            FrmLobby frame = (FrmLobby) frameActual;
            
            Color color;
            if (j.getColor() == ColorJugador.AZUL) {
                color = Color.BLUE;
            } else {
                color = Color.RED;
            }
            
            frame.agregarJugador(j.getNombre(), color);
        }
    }
    
    public void obtenerJugadorEnemigo() {
        if (frameActual instanceof FrmLobby) {
            control.obtenerJugadorEnemigo();
        }
    }
    
    private void manejarMostrarMarcador(Object datos) {
        String marcador = (String) datos;
        JTextArea txtArea = new JTextArea(marcador);
        txtArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtArea.setOpaque(false);
        txtArea.setEditable(false);
        
        JOptionPane.showMessageDialog(frameActual, txtArea);
    }
}
