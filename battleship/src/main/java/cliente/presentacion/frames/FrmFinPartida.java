package cliente.presentacion.frames;

import cliente.controlador.ControlVista;
import compartido.comunicacion.dto.JugadorDTO;
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
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Pantalla de fin de partida que muestra el resultado (Victoria/Derrota).
 * Solo muestra un boton para volver al menu principal.
 *
 * @author Equipo
 */
public class FrmFinPartida extends JFrame {

    // Colores del tema
    private static final Color COLOR_VICTORIA_1 = new Color(34, 100, 34);
    private static final Color COLOR_VICTORIA_2 = new Color(74, 140, 74);
    private static final Color COLOR_DERROTA_1 = new Color(100, 34, 34);
    private static final Color COLOR_DERROTA_2 = new Color(140, 74, 74);
    private static final Color COLOR_BOTON = new Color(45, 45, 45);
    private static final Color COLOR_BOTON_HOVER = new Color(65, 65, 65);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_DORADO = new Color(255, 215, 0);

    private JugadorDTO ganador;
    private boolean gane;
    private boolean porAbandono;
    private ControlVista controlVista;

    public FrmFinPartida(JugadorDTO ganador, boolean gane) {
        this(ganador, gane, false);
    }

    public FrmFinPartida(JugadorDTO ganador, boolean gane, boolean porAbandono) {
        this.ganador = ganador;
        this.gane = gane;
        this.porAbandono = porAbandono;
        this.controlVista = ControlVista.getInstancia();
        initComponents();
    }

    private void initComponents() {
        setTitle("Battleship - Fin de Partida");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 520); // Aumentado para que quepa el botón
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal con gradiente segun resultado
        JPanel pnlPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                Color color1 = gane ? COLOR_VICTORIA_1 : COLOR_DERROTA_1;
                Color color2 = gane ? COLOR_VICTORIA_2 : COLOR_DERROTA_2;

                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        pnlPrincipal.setLayout(new BoxLayout(pnlPrincipal, BoxLayout.Y_AXIS));
        pnlPrincipal.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Espaciado superior reducido
        pnlPrincipal.add(Box.createVerticalStrut(10));

        // Icono/Trofeo
        JLabel lblIcono = new JLabel(gane ? "🏆" : "⚓");
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        lblIcono.setAlignmentX(CENTER_ALIGNMENT);
        pnlPrincipal.add(lblIcono);

        pnlPrincipal.add(Box.createVerticalStrut(20));

        // Resultado principal
        String textoResultado = gane ? "¡VICTORIA!" : "DERROTA";
        JLabel lblResultado = new JLabel(textoResultado, SwingConstants.CENTER);
        lblResultado.setFont(new Font("Segoe UI", Font.BOLD, 56));
        lblResultado.setForeground(COLOR_TEXTO);
        lblResultado.setAlignmentX(CENTER_ALIGNMENT);
        pnlPrincipal.add(lblResultado);

        pnlPrincipal.add(Box.createVerticalStrut(15));

        // Mensaje secundario
        String mensaje;
        if (gane) {
            if (porAbandono) {
                mensaje = "El oponente abandonó la partida";
            } else {
                mensaje = "Has hundido todas las naves enemigas";
            }
        } else {
            mensaje = "Tu flota ha sido destruida";
        }
        JLabel lblMensaje = new JLabel(mensaje, SwingConstants.CENTER);
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblMensaje.setForeground(COLOR_TEXTO);
        lblMensaje.setAlignmentX(CENTER_ALIGNMENT);
        pnlPrincipal.add(lblMensaje);

        pnlPrincipal.add(Box.createVerticalStrut(30));

        // Panel de estadisticas
        JPanel pnlStats = crearPanelEstadisticas();
        pnlStats.setAlignmentX(CENTER_ALIGNMENT);
        pnlPrincipal.add(pnlStats);

        pnlPrincipal.add(Box.createVerticalStrut(40));

        // Boton Menu Principal
        JButton btnMenu = crearBoton("Menú Principal");
        btnMenu.addActionListener(e -> volverAlMenu());
        btnMenu.setAlignmentX(CENTER_ALIGNMENT);
        pnlPrincipal.add(btnMenu);

        // Espaciado inferior fijo
        pnlPrincipal.add(Box.createVerticalStrut(20));

        setContentPane(pnlPrincipal);
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 2),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)
        ));
        panel.setMaximumSize(new Dimension(350, 120));

        // Titulo
        JLabel lblTitulo = new JLabel("ESTADÍSTICAS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblTitulo);

        panel.add(Box.createVerticalStrut(10));

        // Nombre del ganador
        String nombreGanador = (ganador != null) ? ganador.getNombre() : "Desconocido";
        JLabel lblGanador = new JLabel("Ganador: " + nombreGanador, SwingConstants.CENTER);
        lblGanador.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblGanador.setForeground(COLOR_DORADO);
        lblGanador.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblGanador);

        panel.add(Box.createVerticalStrut(5));

        // Mensaje de partida
        JLabel lblInfo = new JLabel("La batalla ha terminado", SwingConstants.CENTER);
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblInfo.setForeground(new Color(200, 200, 200));
        lblInfo.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblInfo);

        return panel;
    }

    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(COLOR_TEXTO);
        btn.setBackground(COLOR_BOTON);
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setMaximumSize(new Dimension(200, 45));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_BOTON_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_BOTON);
            }
        });

        return btn;
    }

    private void volverAlMenu() {
        // Reiniciar el estado para permitir nueva partida
        controlVista.reiniciarEstado();

        // Abrir menu principal
        FrmMenuPrincipal menu = new FrmMenuPrincipal();
        menu.setVisible(true);
        dispose();
    }
}
