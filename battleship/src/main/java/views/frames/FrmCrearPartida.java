package views.frames;

import controllers.controller.ControlVista;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
 * Pantalla para crear una partida y generar codigo.
 *
 * @author Equipo
 */
public class FrmCrearPartida extends JFrame {

    private JTextField txtNombre;
    private JLabel lblCodigo;
    private JButton btnCopiar;
    private JButton btnContinuar;
    private JButton btnRegresar;

    private String codigoPartida;
    private ColorJugador colorSeleccionado;
    private ControlVista controlVista;
    private ConfiguracionJugador configuracion;
    private Map<ColorJugador, JButton> botonesColores;

    public FrmCrearPartida() {
        this.controlVista = ControlVista.getInstancia();
        this.configuracion = ConfiguracionJugador.getInstancia();
        this.codigoPartida = generarCodigo();
        this.botonesColores = new HashMap<>();
        initComponents();
        cargarPreferenciasGuardadas();
    }

    private String generarCodigo() {
        // Generar codigo de 5 caracteres alfanumericos
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
        setResizable(false);
        setSize(500, 500);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titulo
        JLabel lblTitulo = new JLabel("CREAR PARTIDA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        // Nombre
        JLabel lblNombre = new JLabel("Tu nombre:");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(lblNombre, gbc);

        txtNombre = new JTextField(15);
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 1;
        panel.add(txtNombre, gbc);

        // Color
        JLabel lblColor = new JLabel("Tu color:");
        lblColor.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblColor, gbc);

        JPanel panelColores = crearPanelColores();
        gbc.gridx = 1;
        panel.add(panelColores, gbc);

        // Codigo de partida
        JLabel lblCodigoTitulo = new JLabel("Codigo de partida:", SwingConstants.CENTER);
        lblCodigoTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(lblCodigoTitulo, gbc);

        lblCodigo = new JLabel(codigoPartida, SwingConstants.CENTER);
        lblCodigo.setFont(new Font("Consolas", Font.BOLD, 36));
        lblCodigo.setForeground(new Color(0, 102, 204));
        lblCodigo.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        lblCodigo.setPreferredSize(new Dimension(200, 60));
        gbc.gridy = 4;
        panel.add(lblCodigo, gbc);

        // Boton copiar
        btnCopiar = new JButton("Copiar codigo");
        btnCopiar.addActionListener(e -> copiarCodigo());
        gbc.gridy = 5;
        panel.add(btnCopiar, gbc);

        // Instruccion
        JLabel lblInstruccion = new JLabel("<html><center>Comparte este codigo con tu oponente<br>para que se una a la partida</center></html>", SwingConstants.CENTER);
        lblInstruccion.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        gbc.gridy = 6;
        panel.add(lblInstruccion, gbc);

        // Botones
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(Color.WHITE);

        btnRegresar = new JButton("Regresar");
        btnRegresar.addActionListener(e -> regresar());
        panelBotones.add(btnRegresar);

        btnContinuar = new JButton("Continuar");
        btnContinuar.setBackground(new Color(0, 153, 0));
        btnContinuar.setForeground(Color.WHITE);
        btnContinuar.addActionListener(e -> continuar());
        panelBotones.add(btnContinuar);

        gbc.gridy = 7;
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

    private void copiarCodigo() {
        StringSelection selection = new StringSelection(codigoPartida);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        JOptionPane.showMessageDialog(this, "Codigo copiado al portapapeles!");
    }

    private void continuar() {
        String nombre = txtNombre.getText().trim();

        // Usar el validador centralizado
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

        // Crear la partida en el servidor (el Controlador agrega el jugador al modelo)
        controlVista.crearPartidaConCodigo(jugador, codigoPartida);

        // Crear el lobby DESPUES de agregar el jugador al modelo
        FrmLobby lobby = new FrmLobby();
        lobby.setCodigoPartida(codigoPartida);

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
