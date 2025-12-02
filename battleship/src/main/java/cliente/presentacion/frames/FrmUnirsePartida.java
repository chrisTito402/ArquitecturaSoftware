package cliente.presentacion.frames;

import cliente.controlador.ControlVista;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
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
 * Pantalla para unirse a una partida existente con codigo.
 *
 * @author Equipo
 */
public class FrmUnirsePartida extends JFrame {

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JButton btnRojo;
    private JButton btnAzul;
    private JButton btnUnirse;
    private JButton btnRegresar;

    private ColorJugador colorSeleccionado;
    private ControlVista controlVista;

    public FrmUnirsePartida() {
        this.controlVista = ControlVista.getInstancia();
        initComponents();
    }

    private void initComponents() {
        setTitle("Battleship - Unirse a Partida");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(500, 450);

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

        JPanel panelColores = new JPanel();
        panelColores.setBackground(Color.WHITE);

        btnRojo = new JButton("ROJO");
        btnRojo.setBackground(Color.RED);
        btnRojo.setForeground(Color.WHITE);
        btnRojo.setPreferredSize(new Dimension(80, 35));
        btnRojo.addActionListener(e -> seleccionarColor(ColorJugador.ROJO));
        panelColores.add(btnRojo);

        btnAzul = new JButton("AZUL");
        btnAzul.setBackground(Color.BLUE);
        btnAzul.setForeground(Color.WHITE);
        btnAzul.setPreferredSize(new Dimension(80, 35));
        btnAzul.addActionListener(e -> seleccionarColor(ColorJugador.AZUL));
        panelColores.add(btnAzul);

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

    private void seleccionarColor(ColorJugador color) {
        this.colorSeleccionado = color;
        if (color == ColorJugador.ROJO) {
            btnRojo.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
            btnAzul.setBorder(BorderFactory.createEmptyBorder());
        } else {
            btnAzul.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
            btnRojo.setBorder(BorderFactory.createEmptyBorder());
        }
    }

    private void unirse() {
        String codigo = txtCodigo.getText().trim().toUpperCase();
        String nombre = txtNombre.getText().trim();

        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa el codigo de partida.");
            return;
        }
        if (codigo.length() != 5) {
            JOptionPane.showMessageDialog(this, "El codigo debe tener 5 caracteres.");
            return;
        }
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa tu nombre.");
            return;
        }
        if (colorSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona un color.");
            return;
        }

        // Guardar codigo y marcar que no es host
        controlVista.setCodigoPartida(codigo);
        controlVista.setEsHost(false);

        // IMPORTANTE: Crear el lobby PRIMERO para que se suscriba
        // ANTES de enviar el mensaje de unirse
        FrmLobby lobby = new FrmLobby();
        lobby.setCodigoPartida(codigo);

        // Ahora sí enviar el mensaje de unirse (lobby ya está suscrito)
        JugadorDTO jugador = new JugadorDTO(nombre, colorSeleccionado, EstadoJugador.JUGANDO);
        controlVista.unirsePartida(jugador);

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
