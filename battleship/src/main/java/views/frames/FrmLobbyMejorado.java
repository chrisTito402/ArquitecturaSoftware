package views.frames;

import controllers.controller.ControlVista;
import dtos.JugadorDTO;
import models.entidades.Jugador;
import models.observador.ISuscriptor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class FrmLobbyMejorado extends JFrame implements ISuscriptor {

    private Jugador jugadorLocal;
    private JPanel panelJugador1;
    private JPanel panelJugador2;
    private JLabel lblNombreJ1;
    private JLabel lblNombreJ2;
    private JPanel indicadorJ1;
    private JPanel indicadorJ2;
    private JLabel lblEstadoJ1;
    private JLabel lblEstadoJ2;
    private JButton btnIniciar;
    private JButton btnRegresar;
    private JLabel lblEsperando;
    private Timer timerAnimacion;

    public FrmLobbyMejorado(Jugador jugador) {
        this.jugadorLocal = jugador;
        setTitle("Battleship - Sala de Espera");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        inicializarComponentes();
        configurarVentana();
        iniciarAnimacionEspera();
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 20));
        panelPrincipal.setBackground(UIConstants.COLOR_FONDO_PRINCIPAL);
        panelPrincipal.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel lblTitulo = UIHelper.crearTitulo("⚓ SALA DE ESPERA");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 40, 0));
        panelCentral.setBackground(UIConstants.COLOR_FONDO_PRINCIPAL);

        panelJugador1 = crearPanelJugador("JUGADOR 1", true);
        panelJugador2 = crearPanelJugador("JUGADOR 2", false);

        panelCentral.add(panelJugador1);
        panelCentral.add(panelJugador2);

        JPanel contenedorCentral = new JPanel(new BorderLayout(0, 15));
        contenedorCentral.setBackground(UIConstants.COLOR_FONDO_PRINCIPAL);
        contenedorCentral.add(panelCentral, BorderLayout.CENTER);

        lblEsperando = new JLabel("Esperando oponente...", SwingConstants.CENTER);
        lblEsperando.setFont(UIConstants.FONT_TITULO_MEDIO);
        lblEsperando.setForeground(UIConstants.COLOR_ACENTO_DORADO);
        contenedorCentral.add(lblEsperando, BorderLayout.SOUTH);

        panelPrincipal.add(contenedorCentral, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        panelBotones.setBackground(UIConstants.COLOR_FONDO_PRINCIPAL);

        btnRegresar = UIHelper.crearBotonSecundario("← Regresar");
        btnRegresar.addActionListener(e -> regresar());

        btnIniciar = UIHelper.crearBotonExito("▶ Iniciar Partida");
        btnIniciar.setEnabled(false);
        btnIniciar.addActionListener(e -> iniciarPartida());

        panelBotones.add(btnRegresar);
        panelBotones.add(btnIniciar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
        actualizarJugador1(jugadorLocal.getNombre());
    }

    private JPanel crearPanelJugador(String titulo, boolean esJugador1) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.COLOR_FONDO_SECUNDARIO);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_ACENTO_AZUL, 2),
                new EmptyBorder(20, 25, 20, 25)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(UIConstants.FONT_TITULO_PEQUENO);
        lblTitulo.setForeground(UIConstants.COLOR_TEXTO_SECUNDARIO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(15));

        JPanel indicador = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillOval(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        indicador.setPreferredSize(new Dimension(80, 80));
        indicador.setMaximumSize(new Dimension(80, 80));
        indicador.setBackground(UIConstants.COLOR_FALLO);
        indicador.setOpaque(false);
        indicador.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(indicador);
        panel.add(Box.createVerticalStrut(15));

        JLabel lblNombre = new JLabel("Esperando...");
        lblNombre.setFont(UIConstants.FONT_TITULO_MEDIO);
        lblNombre.setForeground(UIConstants.COLOR_TEXTO_PRINCIPAL);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblNombre);
        panel.add(Box.createVerticalStrut(10));

        JLabel lblEstado = new JLabel("Desconectado");
        lblEstado.setFont(UIConstants.FONT_TEXTO_PEQUENO);
        lblEstado.setForeground(UIConstants.COLOR_FALLO);
        lblEstado.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblEstado);

        if (esJugador1) {
            indicadorJ1 = indicador;
            lblNombreJ1 = lblNombre;
            lblEstadoJ1 = lblEstado;
        } else {
            indicadorJ2 = indicador;
            lblNombreJ2 = lblNombre;
            lblEstadoJ2 = lblEstado;
        }

        return panel;
    }

    private void configurarVentana() {
        pack();
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);
    }

    public void actualizarJugador1(String nombre) {
        lblNombreJ1.setText(nombre);
        indicadorJ1.setBackground(UIConstants.COLOR_ACIERTO);
        lblEstadoJ1.setText("Conectado ✓");
        lblEstadoJ1.setForeground(UIConstants.COLOR_ACIERTO);
        indicadorJ1.repaint();
    }

    public void actualizarJugador2(String nombre) {
        lblNombreJ2.setText(nombre);
        indicadorJ2.setBackground(UIConstants.COLOR_ACIERTO);
        lblEstadoJ2.setText("Conectado ✓");
        lblEstadoJ2.setForeground(UIConstants.COLOR_ACIERTO);
        indicadorJ2.repaint();

        lblEsperando.setText("¡Oponente encontrado!");
        lblEsperando.setForeground(UIConstants.COLOR_ACIERTO);
        btnIniciar.setEnabled(true);

        if (timerAnimacion != null) {
            timerAnimacion.stop();
        }
    }

    public void actualizarLobby(List<JugadorDTO> jugadores) {
        if (jugadores == null || jugadores.isEmpty()) {
            return;
        }

        if (jugadores.size() >= 1) {
            actualizarJugador1(jugadores.get(0).getNombre());
        }

        if (jugadores.size() >= 2) {
            actualizarJugador2(jugadores.get(1).getNombre());
        }
    }

    private void iniciarAnimacionEspera() {
        final String[] puntos = {".", "..", "..."};
        final int[] index = {0};

        timerAnimacion = new Timer(500, e -> {
            lblEsperando.setText("Esperando oponente" + puntos[index[0]]);
            index[0] = (index[0] + 1) % puntos.length;
        });
        timerAnimacion.start();
    }

    private void iniciarPartida() {
        if (timerAnimacion != null) {
            timerAnimacion.stop();
        }

        FrmColocarNaves colocarNaves = new FrmColocarNaves(jugadorLocal);
        colocarNaves.setVisible(true);
        this.dispose();
    }

    private void regresar() {
        if (timerAnimacion != null) {
            timerAnimacion.stop();
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que deseas salir de la sala?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            ControlVista.getInstancia().abandonarLobby(jugadorLocal);
            FrmMenuPrincipal menu = new FrmMenuPrincipal();
            menu.setVisible(true);
            this.dispose();
        }
    }

    @Override
    public void notificar(String contexto, Object datos) {
        if ("JUGADOR_UNIDO".equals(contexto) && datos instanceof JugadorDTO) {
            JugadorDTO jugadorDTO = (JugadorDTO) datos;
            if (!jugadorDTO.getNombre().equals(jugadorLocal.getNombre())) {
                SwingUtilities.invokeLater(() -> actualizarJugador2(jugadorDTO.getNombre()));
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            models.builder.JugadorBuilder builder = new models.builder.JugadorBuilder();
            builder.setNombre("TestPlayer");
            builder.setColor(models.enums.ColorJugador.ROJO);
            builder.setEstado(models.enums.EstadoJugador.JUGANDO);
            Jugador testJugador = builder.getResult();

            FrmLobbyMejorado frame = new FrmLobbyMejorado(testJugador);
            frame.setVisible(true);

            new Timer(3000, e -> {
                frame.actualizarJugador2("Oponente");
                ((Timer) e.getSource()).stop();
            }).start();
        });
    }
}
