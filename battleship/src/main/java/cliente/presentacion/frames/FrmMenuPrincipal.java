package cliente.presentacion.frames;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

/**
 * Pantalla principal del juego Batalla Naval.
 * Estilo minimalista con fondo oscuro tipo naval.
 *
 * @author Equipo
 */
public class FrmMenuPrincipal extends JFrame {

    // Colores del tema
    private static final Color COLOR_FONDO = new Color(74, 89, 98);
    private static final Color COLOR_FONDO_CLARO = new Color(94, 109, 118);
    private static final Color COLOR_BOTON = new Color(45, 45, 45);
    private static final Color COLOR_BOTON_HOVER = new Color(65, 65, 65);
    private static final Color COLOR_BOTON_SALIR = new Color(139, 0, 0);
    private static final Color COLOR_BOTON_SALIR_HOVER = new Color(178, 34, 34);
    private static final Color COLOR_TEXTO = Color.WHITE;

    private JButton btnJugar;
    private JButton btnSalir;

    public FrmMenuPrincipal() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Battleship");
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

        pnlPrincipal.add(Box.createVerticalStrut(80));

        // Boton Jugar
        btnJugar = crearBoton("Jugar", COLOR_BOTON, COLOR_BOTON_HOVER);
        btnJugar.addActionListener(e -> jugar());
        pnlPrincipal.add(btnJugar);

        pnlPrincipal.add(Box.createVerticalStrut(20));

        // Boton Salir
        btnSalir = crearBoton("Salir", COLOR_BOTON_SALIR, COLOR_BOTON_SALIR_HOVER);
        btnSalir.addActionListener(e -> salir());
        pnlPrincipal.add(btnSalir);

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

    private void jugar() {
        FrmSeleccionModo seleccion = new FrmSeleccionModo();
        seleccion.setVisible(true);
        dispose();
    }

    private void salir() {
        int opcion = JOptionPane.showConfirmDialog(
            this,
            "¿Estás seguro de que quieres salir?",
            "Confirmar salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (opcion == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

}
