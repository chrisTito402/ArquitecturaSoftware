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
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.Random;
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
import compartido.comunicacion.dto.JugadorDTO;

/**
 * Pantalla para crear una partida MultiPlayer.
 * El Host ingresa su nombre, selecciona color y obtiene un codigo para compartir.
 *
 * @author Equipo
 */
public class FrmCrearPartida extends JFrame {

    // Colores del tema
    private static final Color COLOR_FONDO = new Color(74, 89, 98);
    private static final Color COLOR_FONDO_CLARO = new Color(94, 109, 118);
    private static final Color COLOR_PANEL = new Color(240, 240, 240);
    private static final Color COLOR_BOTON = new Color(45, 45, 45);
    private static final Color COLOR_BOTON_HOVER = new Color(65, 65, 65);
    private static final Color COLOR_BOTON_RETROCEDER = new Color(139, 0, 0);
    private static final Color COLOR_BOTON_RETROCEDER_HOVER = new Color(178, 34, 34);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_TEXTO_OSCURO = Color.BLACK;

    private JTextField txtNombre;
    private JButton btnRojo;
    private JButton btnAzul;
    private JTextField txtCodigo;
    private JButton btnCopiar;
    private JButton btnCrearPartida;
    private JButton btnRetroceder;

    private String codigoPartida;
    private ColorJugador colorSeleccionado;
    private ControlVista controlVista;

    public FrmCrearPartida() {
        this.controlVista = ControlVista.getInstancia();
        this.codigoPartida = generarCodigo();
        initComponents();
    }

    private String generarCodigo() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder codigo = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            codigo.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return codigo.toString();
    }

    private void initComponents() {
        setTitle("Battleship - Crear Partida");
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
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);
        pnlPrincipal.add(lblTitulo);

        pnlPrincipal.add(Box.createVerticalStrut(10));

        // Subtitulo
        JLabel lblSubtitulo = new JLabel("MultiPlayer - Crear Partida", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblSubtitulo.setForeground(new Color(200, 200, 200));
        lblSubtitulo.setAlignmentX(CENTER_ALIGNMENT);
        pnlPrincipal.add(lblSubtitulo);

        pnlPrincipal.add(Box.createVerticalStrut(30));

        // Panel del formulario (fondo claro)
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

        // Codigo
        JLabel lblCodigo = new JLabel("Codigo:");
        lblCodigo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(lblCodigo, gbc);

        txtCodigo = new JTextField(codigoPartida);
        txtCodigo.setFont(new Font("Consolas", Font.BOLD, 16));
        txtCodigo.setEditable(false);
        txtCodigo.setPreferredSize(new Dimension(100, 30));
        txtCodigo.setHorizontalAlignment(JTextField.CENTER);
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(txtCodigo, gbc);

        btnCopiar = crearBotonFormulario("Copiar Codigo", COLOR_BOTON, COLOR_BOTON_HOVER);
        btnCopiar.setPreferredSize(new Dimension(120, 30));
        btnCopiar.addActionListener(e -> copiarCodigo());
        gbc.gridx = 2;
        panel.add(btnCopiar, gbc);

        // Espaciador
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        panel.add(Box.createVerticalStrut(15), gbc);

        // Botones
        JPanel pnlBotones = new JPanel();
        pnlBotones.setBackground(COLOR_PANEL);

        btnCrearPartida = crearBotonFormulario("Crear Partida", COLOR_BOTON, COLOR_BOTON_HOVER);
        btnCrearPartida.setPreferredSize(new Dimension(130, 35));
        btnCrearPartida.addActionListener(e -> crearPartida());
        pnlBotones.add(btnCrearPartida);

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

    private void copiarCodigo() {
        StringSelection selection = new StringSelection(codigoPartida);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        JOptionPane.showMessageDialog(this, "Codigo copiado al portapapeles:\n" + codigoPartida,
                "Codigo Copiado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void crearPartida() {
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

        // Limpiar estado anterior antes de crear nueva partida
        controlVista.reiniciarEstado();

        // Guardar datos en ControlVista
        controlVista.setCodigoPartida(codigoPartida);
        controlVista.setEsHost(true);

        // Crear el jugador y registrar la partida ANTES de crear el lobby
        // Asi cuando el lobby cargue los jugadores, el host ya estara registrado
        JugadorDTO jugador = new JugadorDTO(nombre, colorSeleccionado, EstadoJugador.JUGANDO);
        controlVista.crearPartidaConCodigo(jugador, codigoPartida);

        // Ahora crear el lobby (cargarJugadores() encontrara al host)
        FrmLobby lobby = new FrmLobby(codigoPartida, true);

        // Mostrar el lobby
        lobby.setVisible(true);
        dispose();
    }

    private void retroceder() {
        FrmMultiPlayer frm = new FrmMultiPlayer();
        frm.setVisible(true);
        dispose();
    }
}
