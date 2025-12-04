package cliente.presentacion.frames;

import cliente.controlador.ControlVista;
import cliente.presentacion.componentes.IndicadorTurnoPanel;
import cliente.presentacion.componentes.MarcadorNavesPanel;
import cliente.presentacion.componentes.TimerPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Pantalla principal del juego donde se desarrolla la partida.
 * Muestra ambos tableros, el timer, el indicador de turno y el marcador de naves.
 *
 * @author Equipo
 */
public class FrmPartidaEnCurso extends JFrame {

    // Colores del tema
    private static final Color COLOR_FONDO = new Color(74, 89, 98);
    private static final Color COLOR_FONDO_CLARO = new Color(94, 109, 118);
    private static final Color COLOR_PANEL = new Color(240, 240, 240);
    private static final Color COLOR_BOTON_ABANDONAR = new Color(139, 0, 0);
    private static final Color COLOR_BOTON_ABANDONAR_HOVER = new Color(178, 34, 34);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_TABLERO_PROPIO = new Color(0, 102, 0);
    private static final Color COLOR_TABLERO_ENEMIGO = new Color(153, 0, 0);

    // Componentes
    private JPanel jPanel1; // Tablero propio
    private JPanel jPanel2; // Tablero enemigo
    private JPanel pnlTimer;
    private JButton btnAbandonar;
    private IndicadorTurnoPanel indicadorTurno;
    private MarcadorNavesPanel marcadorNaves;

    private ControlVista controlVista;

    public FrmPartidaEnCurso() {
        this.controlVista = ControlVista.getInstancia();
        initComponents();
        configurarPartida();
    }

    private void initComponents() {
        setTitle("Battleship - Partida en Curso");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 650);
        setMinimumSize(new Dimension(1100, 600));
        setResizable(true);

        // Manejar cierre con X para notificar abandono
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                abandonarPartida();
            }
        });

        // Panel principal con gradiente
        JPanel pnlPrincipal = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, COLOR_FONDO, 0, getHeight(), COLOR_FONDO_CLARO);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        pnlPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Panel superior (titulo + timer)
        JPanel pnlSuperior = crearPanelSuperior();
        pnlPrincipal.add(pnlSuperior, BorderLayout.NORTH);

        // Panel central (tableros)
        JPanel pnlTableros = crearPanelTableros();
        pnlPrincipal.add(pnlTableros, BorderLayout.CENTER);

        // Panel derecho (turno + marcador)
        JPanel pnlDerecho = crearPanelDerecho();
        pnlPrincipal.add(pnlDerecho, BorderLayout.EAST);

        // Panel inferior (boton abandonar)
        JPanel pnlInferior = crearPanelInferior();
        pnlPrincipal.add(pnlInferior, BorderLayout.SOUTH);

        setContentPane(pnlPrincipal);
        setLocationRelativeTo(null);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        panel.setOpaque(false);

        // Titulo
        JLabel lblTitulo = new JLabel("BattleShip", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitulo.setForeground(COLOR_TEXTO);
        panel.add(lblTitulo);

        // Timer
        pnlTimer = new JPanel(new GridLayout(1, 1));
        pnlTimer.setPreferredSize(new Dimension(120, 45));
        pnlTimer.setOpaque(false);
        panel.add(pnlTimer);

        return panel;
    }

    private JPanel crearPanelTableros() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 25, 10, 25);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Tablero propio
        JPanel panelTableroPropio = crearContenedorTablero("TU FLOTA", COLOR_TABLERO_PROPIO, true);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(panelTableroPropio, gbc);

        // Tablero enemigo
        JPanel panelTableroEnemigo = crearContenedorTablero("TABLERO ENEMIGO (Click para disparar)", COLOR_TABLERO_ENEMIGO, false);
        gbc.gridx = 1;
        panel.add(panelTableroEnemigo, gbc);

        return panel;
    }

    private JPanel crearContenedorTablero(String titulo, Color colorTitulo, boolean esPropio) {
        JPanel contenedor = new JPanel(new BorderLayout(0, 8));
        contenedor.setOpaque(false);

        // Titulo
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(colorTitulo);
        contenedor.add(lblTitulo, BorderLayout.NORTH);

        // Tablero con coordenadas
        JPanel panelConCoordenadas = new JPanel(new BorderLayout());
        panelConCoordenadas.setOpaque(false);

        // Etiquetas de columnas (A-J)
        JPanel pnlColumnas = new JPanel(new GridLayout(1, 11));
        pnlColumnas.setOpaque(false);
        pnlColumnas.add(new JLabel("")); // Esquina
        for (int i = 0; i < 10; i++) {
            JLabel lbl = new JLabel(String.valueOf((char) ('A' + i)), SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(COLOR_TEXTO);
            pnlColumnas.add(lbl);
        }
        panelConCoordenadas.add(pnlColumnas, BorderLayout.NORTH);

        // Etiquetas de filas (1-10)
        JPanel pnlFilas = new JPanel(new GridLayout(10, 1));
        pnlFilas.setOpaque(false);
        for (int i = 0; i < 10; i++) {
            JLabel lbl = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(COLOR_TEXTO);
            lbl.setPreferredSize(new Dimension(20, 35));
            pnlFilas.add(lbl);
        }
        panelConCoordenadas.add(pnlFilas, BorderLayout.WEST);

        // Grid del tablero
        JPanel tablero = new JPanel(new GridLayout(10, 10));
        tablero.setBackground(new Color(204, 204, 204));
        tablero.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorTitulo, 2),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        tablero.setPreferredSize(new Dimension(350, 350));

        if (esPropio) {
            jPanel1 = tablero;
        } else {
            jPanel2 = tablero;
        }

        panelConCoordenadas.add(tablero, BorderLayout.CENTER);
        contenedor.add(panelConCoordenadas, BorderLayout.CENTER);

        return contenedor;
    }

    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        panel.setPreferredSize(new Dimension(230, 400));

        // Indicador de turno
        indicadorTurno = new IndicadorTurnoPanel();
        indicadorTurno.setMaximumSize(new Dimension(200, 80));
        indicadorTurno.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(indicadorTurno);

        panel.add(Box.createVerticalStrut(25));

        // Marcador de naves
        marcadorNaves = new MarcadorNavesPanel();
        marcadorNaves.setMaximumSize(new Dimension(200, 200));
        marcadorNaves.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(marcadorNaves);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        panel.setOpaque(false);

        btnAbandonar = new JButton("Abandonar Partida");
        btnAbandonar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAbandonar.setForeground(COLOR_TEXTO);
        btnAbandonar.setBackground(COLOR_BOTON_ABANDONAR);
        btnAbandonar.setPreferredSize(new Dimension(160, 38));
        btnAbandonar.setFocusPainted(false);
        btnAbandonar.setBorderPainted(false);
        btnAbandonar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnAbandonar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnAbandonar.setBackground(COLOR_BOTON_ABANDONAR_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnAbandonar.setBackground(COLOR_BOTON_ABANDONAR);
            }
        });

        btnAbandonar.addActionListener(e -> abandonarPartida());
        panel.add(btnAbandonar);

        return panel;
    }

    private void configurarPartida() {
        // Agregar casillas a los tableros
        controlVista.getCasillasEnemigas().forEach(c -> jPanel2.add(c));
        controlVista.getCasillasPropias().forEach(c -> jPanel1.add(c));

        // Configurar timer
        TimerPanel timerPanel = controlVista.getTimer();
        pnlTimer.add(timerPanel);
        pnlTimer.revalidate();
        pnlTimer.repaint();

        // Configurar callback cuando el tiempo se agota
        timerPanel.setOnTiempoAgotado(() -> {
            // No hacer nada si la partida ya terminó
            if (controlVista.isPartidaFinalizada()) {
                return;
            }
            if (controlVista.esMiTurno()) {
                JOptionPane.showMessageDialog(this,
                        "¡Se acabó el tiempo! Pierdes tu turno.",
                        "Tiempo agotado",
                        JOptionPane.WARNING_MESSAGE);
                controlVista.tiempoAgotado();
            }
        });

        // Registrar componentes en ControlVista
        controlVista.setIndicadorTurnoPanel(indicadorTurno);
        controlVista.setMarcadorNavesPanel(marcadorNaves);

        // Verificar turno inicial
        boolean esMiTurno = controlVista.esMiTurno();
        controlVista.habilitarCasillasEnemigas(esMiTurno);
        indicadorTurno.setEsMiTurno(esMiTurno);

        System.out.println("[FrmPartidaEnCurso] Iniciado - Es mi turno: " + esMiTurno);
    }

    private void abandonarPartida() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que quieres abandonar la partida?\nEl oponente será declarado ganador.",
                "Confirmar abandono",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (opcion == JOptionPane.YES_OPTION) {
            // Detener el timer
            TimerPanel timer = controlVista.getTimer();
            if (timer != null) {
                timer.stopTimer();
            }

            // Notificar al servidor
            controlVista.abandonarPartida();

            // Volver al menu principal
            FrmMenuPrincipal menu = new FrmMenuPrincipal();
            menu.setVisible(true);
            dispose();
        }
    }
}
