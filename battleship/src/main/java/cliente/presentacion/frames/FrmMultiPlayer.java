package cliente.presentacion.frames;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Pantalla MultiPlayer para seleccionar entre Crear Partida o Unirse a Partida.
 *
 * @author Equipo
 */
public class FrmMultiPlayer extends JFrame {

    // Colores del tema (consistentes con las otras pantallas)
    private static final Color COLOR_FONDO = new Color(74, 89, 98);
    private static final Color COLOR_FONDO_CLARO = new Color(94, 109, 118);
    private static final Color COLOR_BOTON = new Color(45, 45, 45);
    private static final Color COLOR_BOTON_HOVER = new Color(65, 65, 65);
    private static final Color COLOR_BOTON_RETROCEDER = new Color(139, 0, 0);
    private static final Color COLOR_BOTON_RETROCEDER_HOVER = new Color(178, 34, 34);
    private static final Color COLOR_TEXTO = Color.WHITE;

    private JButton btnCrearPartida;
    private JButton btnUnirsePartida;
    private JButton btnRetroceder;

    public FrmMultiPlayer() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Battleship - MultiPlayer");
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
        pnlPrincipal.add(Box.createVerticalGlue());

        // Titulo
        JLabel lblTitulo = new JLabel("BattleShip", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 64));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);
        pnlPrincipal.add(lblTitulo);

        pnlPrincipal.add(Box.createVerticalStrut(15));

        // Subtitulo
        JLabel lblSubtitulo = new JLabel("MultiPlayer", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        lblSubtitulo.setForeground(new Color(200, 200, 200));
        lblSubtitulo.setAlignmentX(CENTER_ALIGNMENT);
        pnlPrincipal.add(lblSubtitulo);

        pnlPrincipal.add(Box.createVerticalStrut(50));

        // Boton Crear Partida
        btnCrearPartida = crearBoton("Crear Partida", COLOR_BOTON, COLOR_BOTON_HOVER);
        btnCrearPartida.addActionListener(e -> crearPartida());
        pnlPrincipal.add(btnCrearPartida);

        pnlPrincipal.add(Box.createVerticalStrut(15));

        // Boton Unirse a Partida
        btnUnirsePartida = crearBoton("Unirse a Partida", COLOR_BOTON, COLOR_BOTON_HOVER);
        btnUnirsePartida.addActionListener(e -> unirsePartida());
        pnlPrincipal.add(btnUnirsePartida);

        pnlPrincipal.add(Box.createVerticalStrut(25));

        // Boton Retroceder
        btnRetroceder = crearBoton("Retroceder", COLOR_BOTON_RETROCEDER, COLOR_BOTON_RETROCEDER_HOVER);
        btnRetroceder.addActionListener(e -> retroceder());
        pnlPrincipal.add(btnRetroceder);

        // Espaciado inferior
        pnlPrincipal.add(Box.createVerticalGlue());

        setContentPane(pnlPrincipal);
    }

    /**
     * Crea un boton estilizado con efecto hover.
     */
    private JButton crearBoton(String texto, Color colorNormal, Color colorHover) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        btn.setForeground(COLOR_TEXTO);
        btn.setBackground(colorNormal);
        btn.setAlignmentX(CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 45));
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover
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

    private void retroceder() {
        FrmSeleccionModo frm = new FrmSeleccionModo();
        frm.setVisible(true);
        dispose();
    }
}
