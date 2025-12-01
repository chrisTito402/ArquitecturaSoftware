package views.frames;

import controllers.controller.ControlVista;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import models.config.ConfiguracionJugador;
import models.enums.ColorJugador;
import models.enums.EstadoJugador;
import servidor.validacion.ValidadorJugador;
import shared.dto.JugadorDTO;

/**
 * Pantalla para unirse a una partida existente con codigo.
 *
 * @author Equipo
 */
public class FrmUnirsePartida extends JFrame {

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JButton btnUnirse;
    private JButton btnRegresar;

    private ColorJugador colorSeleccionado;
    private ControlVista controlVista;
    private ConfiguracionJugador configuracion;
    private Map<ColorJugador, JButton> botonesColores;

    public FrmUnirsePartida() {
        this.controlVista = ControlVista.getInstancia();
        this.configuracion = ConfiguracionJugador.getInstancia();
        this.botonesColores = new HashMap<>();
        initComponents();
        cargarPreferenciasGuardadas();
    }

    private void initComponents() {
        setTitle("Battleship - Unirse a Partida");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(500, 500);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titulo
        JLabel lblTitulo = new JLabel("UNIRSE A PARTIDA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        // Codigo
        JLabel lblCodigo = new JLabel("Codigo de partida:");
        lblCodigo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(lblCodigo, gbc);

        txtCodigo = new JTextField(10);
        txtCodigo.setFont(new Font("Consolas", Font.BOLD, 24));
        txtCodigo.setHorizontalAlignment(JTextField.CENTER);
        gbc.gridx = 1;
        panel.add(txtCodigo, gbc);

        // Nombre
        JLabel lblNombre = new JLabel("Tu nombre:");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblNombre, gbc);

        txtNombre = new JTextField(15);
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 1;
        panel.add(txtNombre, gbc);

        // Color
        JLabel lblColor = new JLabel("Tu color:");
        lblColor.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(lblColor, gbc);

        JPanel panelColores = crearPanelColores();
        gbc.gridx = 1;
        panel.add(panelColores, gbc);

        // Instruccion
        JLabel lblInstruccion = new JLabel("<html><center>Ingresa el codigo que te dio<br>el creador de la partida</center></html>", SwingConstants.CENTER);
        lblInstruccion.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(lblInstruccion, gbc);

        // Botones
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(Color.WHITE);

        btnRegresar = new JButton("Regresar");
        btnRegresar.addActionListener(e -> regresar());
        panelBotones.add(btnRegresar);

        btnUnirse = new JButton("Unirse");
        btnUnirse.setBackground(new Color(0, 102, 204));
        btnUnirse.setForeground(Color.WHITE);
        btnUnirse.setPreferredSize(new Dimension(100, 35));
        btnUnirse.addActionListener(e -> unirse());
        panelBotones.add(btnUnirse);

        gbc.gridy = 5;
        panel.add(panelBotones, gbc);

        add(panel);
        setLocationRelativeTo(null);
    }

    private JPanel crearPanelColores() {
        // Panel horizontal para los 2 colores (Rojo y Azul)
        JPanel panelColores = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelColores.setBackground(Color.WHITE);

        // Crear un boton para cada color disponible
        for (ColorJugador color : ColorJugador.values()) {
            JButton btnColor = new JButton(color.getNombreEspanol());
            btnColor.setBackground(color.getColorAWT());
            btnColor.setForeground(Color.WHITE);
            btnColor.setPreferredSize(new Dimension(80, 35));
            btnColor.setToolTipText(color.getNombreEspanol());
            btnColor.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            btnColor.setFocusPainted(false);

            btnColor.addActionListener(e -> seleccionarColor(color));
            botonesColores.put(color, btnColor);
            panelColores.add(btnColor);
        }

        return panelColores;
    }

    private void seleccionarColor(ColorJugador color) {
        this.colorSeleccionado = color;

        // Quitar seleccion de todos los botones
        for (Map.Entry<ColorJugador, JButton> entry : botonesColores.entrySet()) {
            if (entry.getKey() == color) {
                entry.getValue().setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
            } else {
                entry.getValue().setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            }
        }
    }

    private void cargarPreferenciasGuardadas() {
        // NO cargar el nombre automaticamente para evitar conflictos entre clientes
        // El nombre debe ingresarse manualmente cada vez

        // Solo cargar el color preferido
        ColorJugador colorGuardado = configuracion.getColorGuardado();
        if (colorGuardado != null) {
            seleccionarColor(colorGuardado);
        }
    }

    private void unirse() {
        String codigo = txtCodigo.getText().trim().toUpperCase();
        String nombre = txtNombre.getText().trim();

        // Validaciones de UI para codigo
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa el codigo de partida.");
            return;
        }
        if (codigo.length() != 5) {
            JOptionPane.showMessageDialog(this, "El codigo debe tener 5 caracteres.");
            return;
        }
        if (!codigo.matches("^[A-Z0-9]+$")) {
            JOptionPane.showMessageDialog(this, "El codigo solo puede contener letras y numeros.");
            return;
        }

        // Usar el validador centralizado para nombre
        ValidadorJugador.ResultadoValidacion resultadoNombre = ValidadorJugador.validarNombre(nombre);
        if (!resultadoNombre.isValido()) {
            JOptionPane.showMessageDialog(this, resultadoNombre.getMensaje(),
                "Error de validacion", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ValidadorJugador.ResultadoValidacion resultadoColor = ValidadorJugador.validarColor(colorSeleccionado);
        if (!resultadoColor.isValido()) {
            JOptionPane.showMessageDialog(this, resultadoColor.getMensaje(),
                "Error de validacion", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Guardar preferencias para la proxima vez
        configuracion.guardarPreferencias(nombre, colorSeleccionado);

        // Crear el jugador
        JugadorDTO jugador = new JugadorDTO(nombre, colorSeleccionado, EstadoJugador.JUGANDO);

        // Unirse a la partida (el Controlador agrega el jugador al modelo)
        controlVista.unirsePartidaConCodigo(jugador, codigo);

        // Crear el lobby DESPUES de agregar el jugador al modelo
        FrmLobby lobby = new FrmLobby();
        lobby.setCodigoPartida(codigo);

        // Mostrar el lobby
        lobby.setVisible(true);
        dispose();
    }

    private void regresar() {
        FrmSeleccionPartida frm = new FrmSeleccionPartida();
        frm.setVisible(true);
        dispose();
    }
}
