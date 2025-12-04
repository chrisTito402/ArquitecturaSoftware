package cliente.presentacion.frames;

import cliente.controlador.ControlVista;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import compartido.enums.ColorJugador;
import compartido.enums.EstadoJugador;
import compartido.observador.ISuscriptor;
import compartido.comunicacion.dto.JugadorDTO;
import compartido.comunicacion.dto.RespuestaUnirseDTO;

/**
 * Pantalla para que el Jugador 2 se una a una partida existente con codigo.
 *
 * @author Equipo
 */
public class FrmUnirsePartida extends JFrame implements ISuscriptor {

    // Datos temporales para usar despues de la validacion
    private String codigoPendiente;
    private JugadorDTO jugadorPendiente;
    private boolean navegando = false; // Evita navegacion duplicada
    private boolean errorMostrado = false; // Evita mostrar error multiples veces

    // Colores del tema
    private static final Color COLOR_FONDO = new Color(74, 89, 98);
    private static final Color COLOR_FONDO_CLARO = new Color(94, 109, 118);
    private static final Color COLOR_PANEL = new Color(240, 240, 240);
    private static final Color COLOR_BOTON = new Color(45, 45, 45);
    private static final Color COLOR_BOTON_HOVER = new Color(65, 65, 65);
    private static final Color COLOR_BOTON_RETROCEDER = new Color(139, 0, 0);
    private static final Color COLOR_BOTON_RETROCEDER_HOVER = new Color(178, 34, 34);
    private static final Color COLOR_TEXTO = Color.WHITE;

    private JTextField txtNombre;
    private JTextField txtCodigo;
    private JButton btnRojo;
    private JButton btnAzul;
    private JButton btnUnirse;
    private JButton btnRetroceder;

    private ColorJugador colorSeleccionado;
    private ControlVista controlVista;

    public FrmUnirsePartida() {
        this.controlVista = ControlVista.getInstancia();
        initComponents();
        // Suscribirse UNA sola vez para recibir respuestas del servidor
        controlVista.getControl().suscribirAPartida(this);
    }

    private void initComponents() {
        setTitle("Battleship - Unirse a Partida");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setResizable(false);
        setLocationRelativeTo(null);

        // Panel principal con gradiente
        JPanel pnlPrincipal = new JPanel() {
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
        pnlPrincipal.setLayout(new BoxLayout(pnlPrincipal, BoxLayout.Y_AXIS));

        // Espaciado superior
        pnlPrincipal.add(Box.createVerticalStrut(30));

        // Titulo
        JLabel lblTitulo = new JLabel("BattleShip", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 48));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);
        pnlPrincipal.add(lblTitulo);

        pnlPrincipal.add(Box.createVerticalStrut(10));

        // Subtitulo
        JLabel lblSubtitulo = new JLabel("MultiPlayer - Unirse a Partida", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblSubtitulo.setForeground(new Color(200, 200, 200));
        lblSubtitulo.setAlignmentX(CENTER_ALIGNMENT);
        pnlPrincipal.add(lblSubtitulo);

        pnlPrincipal.add(Box.createVerticalStrut(30));

        // Panel del formulario
        JPanel pnlFormulario = crearPanelFormulario();
        pnlFormulario.setAlignmentX(CENTER_ALIGNMENT);
        pnlPrincipal.add(pnlFormulario);

        pnlPrincipal.add(Box.createVerticalGlue());

        setContentPane(pnlPrincipal);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));
        panel.setMaximumSize(new Dimension(450, 320));
        panel.setPreferredSize(new Dimension(450, 320));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Tu Nombre
        JLabel lblNombre = new JLabel("Tu Nombre:");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblNombre, gbc);

        txtNombre = new JTextField(15);
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombre.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtNombre, gbc);

        // Tu Color
        JLabel lblColor = new JLabel("Tu Color:");
        lblColor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(lblColor, gbc);

        JPanel pnlColores = new JPanel();
        pnlColores.setBackground(COLOR_PANEL);

        btnRojo = new JButton();
        btnRojo.setBackground(Color.RED);
        btnRojo.setPreferredSize(new Dimension(40, 30));
        btnRojo.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        btnRojo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRojo.addActionListener(e -> seleccionarColor(ColorJugador.ROJO));
        pnlColores.add(btnRojo);

        btnAzul = new JButton();
        btnAzul.setBackground(Color.BLUE);
        btnAzul.setPreferredSize(new Dimension(40, 30));
        btnAzul.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        btnAzul.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAzul.addActionListener(e -> seleccionarColor(ColorJugador.AZUL));
        pnlColores.add(btnAzul);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(pnlColores, gbc);

        // Codigo de partida
        JLabel lblCodigo = new JLabel("Codigo:");
        lblCodigo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(lblCodigo, gbc);

        JPanel pnlCodigo = new JPanel();
        pnlCodigo.setBackground(COLOR_PANEL);

        txtCodigo = new JTextField(8);
        txtCodigo.setFont(new Font("Consolas", Font.BOLD, 16));
        txtCodigo.setHorizontalAlignment(JTextField.CENTER);
        txtCodigo.setPreferredSize(new Dimension(100, 30));
        pnlCodigo.add(txtCodigo);

        // Texto de ayuda
        JLabel lblAyuda = new JLabel("(del Host)");
        lblAyuda.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAyuda.setForeground(Color.GRAY);
        pnlCodigo.add(lblAyuda);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(pnlCodigo, gbc);

        // Espaciador
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        panel.add(Box.createVerticalStrut(15), gbc);

        // Botones
        JPanel pnlBotones = new JPanel();
        pnlBotones.setBackground(COLOR_PANEL);

        btnUnirse = crearBotonFormulario("Unirse", COLOR_BOTON, COLOR_BOTON_HOVER);
        btnUnirse.setPreferredSize(new Dimension(130, 35));
        btnUnirse.addActionListener(e -> unirse());
        pnlBotones.add(btnUnirse);

        pnlBotones.add(Box.createHorizontalStrut(20));

        btnRetroceder = crearBotonFormulario("Retroceder", COLOR_BOTON_RETROCEDER, COLOR_BOTON_RETROCEDER_HOVER);
        btnRetroceder.setPreferredSize(new Dimension(130, 35));
        btnRetroceder.addActionListener(e -> retroceder());
        pnlBotones.add(btnRetroceder);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(pnlBotones, gbc);

        return panel;
    }

    private JButton crearBotonFormulario(String texto, Color colorNormal, Color colorHover) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(COLOR_TEXTO);
        btn.setBackground(colorNormal);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(colorHover);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(colorNormal);
            }
        });

        return btn;
    }

    private void seleccionarColor(ColorJugador color) {
        this.colorSeleccionado = color;
        if (color == ColorJugador.ROJO) {
            btnRojo.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
            btnAzul.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        } else {
            btnAzul.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
            btnRojo.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        }
    }

    private void unirse() {
        String codigo = txtCodigo.getText().trim().toUpperCase();
        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa tu nombre.",
                    "Nombre requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (colorSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona un color.",
                    "Color requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa el codigo de partida.",
                    "Codigo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (codigo.length() != 5) {
            JOptionPane.showMessageDialog(this, "El codigo debe tener 5 caracteres.",
                    "Codigo invalido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Limpiar estado anterior antes de unirse a nueva partida
        controlVista.reiniciarEstado();

        // Resetear banderas para nuevo intento
        this.errorMostrado = false;
        this.navegando = false;

        // Guardar datos pendientes para usar despues de la validacion
        this.codigoPendiente = codigo;
        this.jugadorPendiente = new JugadorDTO(nombre, colorSeleccionado, EstadoJugador.JUGANDO);

        // Deshabilitar boton mientras se valida
        btnUnirse.setEnabled(false);
        btnUnirse.setText("Validando...");

        // Enviar solicitud con codigo al servidor (NO navegar todavia)
        controlVista.unirsePartidaConCodigo(jugadorPendiente, codigo);
    }

    /**
     * Recibe notificaciones del modelo (respuesta del servidor).
     */
    @Override
    public void notificar(String contexto, Object datos) {
        if ("ERROR_UNIRSE".equals(contexto)) {
            // El servidor rechazo la solicitud - mostrar solo una vez
            if (errorMostrado || !isVisible()) {
                return;
            }
            errorMostrado = true;

            RespuestaUnirseDTO respuesta = (RespuestaUnirseDTO) datos;

            javax.swing.SwingUtilities.invokeLater(() -> {
                btnUnirse.setEnabled(true);
                btnUnirse.setText("Unirse");

                JOptionPane.showMessageDialog(this,
                        respuesta.getMensaje(),
                        "Error al unirse",
                        JOptionPane.ERROR_MESSAGE);
            });

        } else if ("UNIRSE_PARTIDA".equals(contexto)) {
            // Union exitosa - navegar al lobby
            JugadorDTO jugadorUnido = (JugadorDTO) datos;

            // Verificar que soy yo quien se unio (no el otro jugador)
            // Verificacion null defensiva y evitar navegacion duplicada
            if (!navegando &&
                jugadorPendiente != null &&
                jugadorUnido != null &&
                jugadorUnido.getNombre() != null &&
                jugadorUnido.getNombre().equals(jugadorPendiente.getNombre())) {

                navegando = true; // Marcar que ya estamos navegando

                javax.swing.SwingUtilities.invokeLater(() -> {
                    // Guardar datos en ControlVista
                    controlVista.setCodigoPartida(codigoPendiente);
                    controlVista.setEsHost(false);

                    // Registrar jugador en el modelo local ahora que fue validado
                    controlVista.unirsePartida(jugadorPendiente);

                    // Crear el lobby y navegar
                    FrmLobby lobby = new FrmLobby(codigoPendiente, false);
                    lobby.setVisible(true);
                    dispose();
                });
            }
        }
    }

    private void retroceder() {
        // Desuscribirse antes de cerrar para evitar notificaciones huerfanas
        controlVista.getControl().desuscribirDePartida(this);

        FrmMultiPlayer frm = new FrmMultiPlayer();
        frm.setVisible(true);
        dispose();
    }
}
