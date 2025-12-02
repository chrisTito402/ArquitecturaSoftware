package cliente.presentacion.frames;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Pantalla para seleccionar si crear o unirse a una partida.
 *
 * @author Equipo
 */
public class FrmSeleccionPartida extends JFrame {

    private JButton btnCrearPartida;
    private JButton btnUnirsePartida;
    private JButton btnRegresar;

    public FrmSeleccionPartida() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Battleship - Seleccionar Partida");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(500, 400);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titulo
        JLabel lblTitulo = new JLabel("BATTLESHIP", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 48));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        // Subtitulo
        JLabel lblSubtitulo = new JLabel("Selecciona una opcion", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridy = 1;
        panel.add(lblSubtitulo, gbc);

        // Boton Crear Partida
        btnCrearPartida = new JButton("Crear Partida");
        btnCrearPartida.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnCrearPartida.setPreferredSize(new Dimension(250, 50));
        btnCrearPartida.setBackground(new Color(0, 153, 0));
        btnCrearPartida.setForeground(Color.WHITE);
        btnCrearPartida.addActionListener(e -> crearPartida());
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(btnCrearPartida, gbc);

        // Boton Unirse a Partida
        btnUnirsePartida = new JButton("Unirse a Partida");
        btnUnirsePartida.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnUnirsePartida.setPreferredSize(new Dimension(250, 50));
        btnUnirsePartida.setBackground(new Color(0, 102, 204));
        btnUnirsePartida.setForeground(Color.WHITE);
        btnUnirsePartida.addActionListener(e -> unirsePartida());
        gbc.gridy = 3;
        panel.add(btnUnirsePartida, gbc);

        // Boton Regresar
        btnRegresar = new JButton("Regresar");
        btnRegresar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnRegresar.addActionListener(e -> regresar());
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(btnRegresar, gbc);

        add(panel);
        setLocationRelativeTo(null);
    }

    private void crearPartida() {
        FrmCrearPartida frm = new FrmCrearPartida();
        frm.setVisible(true);
        dispose();
    }

    private void unirsePartida() {
        FrmUnirsePartida frm = new FrmUnirsePartida();
        frm.setVisible(true);
        dispose();
    }

    private void regresar() {
        FrmMenuPrincipal menu = new FrmMenuPrincipal();
        menu.setVisible(true);
        dispose();
    }
}
