package views.frames;

import controllers.controller.ControlVista;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import models.enums.ColorJugador;
import models.enums.EstadoJugador;
import views.DTOs.JugadorDTO;

/**
 * Pantalla para crear una partida y generar codigo.
 *
 * @author Equipo
 */
public class FrmCrearPartida extends JFrame {

    private JTextField txtNombre;
    private JButton btnRojo;
    private JButton btnAzul;
    private JLabel lblCodigo;
    private JButton btnCopiar;
    private JButton btnContinuar;
    private JButton btnRegresar;

    private String codigoPartida;
    private ColorJugador colorSeleccionado;
    private ControlVista controlVista;

    public FrmCrearPartida() {
        this.controlVista = ControlVista.getInstancia();
        this.codigoPartida = generarCodigo();
        initComponents();
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

    private void copiarCodigo() {
        StringSelection selection = new StringSelection(codigoPartida);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        JOptionPane.showMessageDialog(this, "Codigo copiado al portapapeles!");
    }

    private void continuar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa tu nombre.");
            return;
        }
        if (colorSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona un color.");
            return;
        }

        // Guardar el codigo de la partida en ControlVista
        controlVista.setCodigoPartida(codigoPartida);
        controlVista.setEsHost(true);

        // IMPORTANTE: Crear el lobby PRIMERO para que se suscriba
        // ANTES de enviar el mensaje de unirse
        //FrmLobby lobby = new FrmLobby();
        //lobby.setCodigoPartida(codigoPartida);

        // Ahora sí enviar el mensaje de unirse (lobby ya está suscrito)
        JugadorDTO jugador = new JugadorDTO(nombre, colorSeleccionado, EstadoJugador.JUGANDO);
        //controlVista.unirsePartida(jugador);

        // Mostrar el lobby
        //lobby.setVisible(true);
        dispose();
    }

    private void regresar() {
        FrmSeleccionPartida frm = new FrmSeleccionPartida();
        frm.setVisible(true);
        dispose();
    }
}
