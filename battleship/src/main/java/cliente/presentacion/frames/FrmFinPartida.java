package cliente.presentacion.frames;

import cliente.controlador.ControlVista;
import compartido.comunicacion.dto.JugadorDTO;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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

/**
 * Pantalla de fin de partida que muestra el resultado (Victoria/Derrota).
 * Muestra estadisticas y opciones para volver al menu o jugar de nuevo.
 *
 * @author Equipo
 */
public class FrmFinPartida extends JFrame {

    private JugadorDTO ganador;
    private boolean gane;
    private ControlVista controlVista;

    // Componentes UI
    private JPanel pnlPrincipal;
    private JLabel lblResultado;
    private JLabel lblMensaje;
    private JLabel lblGanador;
    private JButton btnVolverMenu;
    private JButton btnNuevaPartida;

    public FrmFinPartida(JugadorDTO ganador, boolean gane) {
        this.ganador = ganador;
        this.gane = gane;
        this.controlVista = ControlVista.getInstancia();
        initComponents();
    }

    private void initComponents() {
        setTitle("Battleship - Fin de Partida");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal con gradiente
        pnlPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int w = getWidth();
                int h = getHeight();

                Color color1, color2;
                if (gane) {
                    color1 = new Color(34, 139, 34);   // Verde oscuro
                    color2 = new Color(144, 238, 144); // Verde claro
                } else {
                    color1 = new Color(139, 0, 0);     // Rojo oscuro
                    color2 = new Color(255, 99, 71);   // Rojo claro
                }

                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        pnlPrincipal.setLayout(new BoxLayout(pnlPrincipal, BoxLayout.Y_AXIS));
        pnlPrincipal.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Icono/Emoji grande
        JLabel lblIcono = new JLabel(gane ? "🏆" : "💀");
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        lblIcono.setAlignmentX(CENTER_ALIGNMENT);

        // Resultado principal
        lblResultado = new JLabel(gane ? "¡VICTORIA!" : "DERROTA");
        lblResultado.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblResultado.setForeground(Color.WHITE);
        lblResultado.setAlignmentX(CENTER_ALIGNMENT);

        // Mensaje secundario
        String mensaje;
        if (gane) {
            mensaje = "¡Felicitaciones! Has hundido toda la flota enemiga.";
        } else {
            mensaje = "Tu flota ha sido destruida. ¡Mejor suerte la proxima vez!";
        }
        lblMensaje = new JLabel(mensaje);
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblMensaje.setForeground(Color.WHITE);
        lblMensaje.setAlignmentX(CENTER_ALIGNMENT);

        // Nombre del ganador
        String textoGanador = "Ganador: " + (ganador != null ? ganador.getNombre() : "Desconocido");
        lblGanador = new JLabel(textoGanador);
        lblGanador.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblGanador.setForeground(new Color(255, 215, 0)); // Dorado
        lblGanador.setAlignmentX(CENTER_ALIGNMENT);

        // Panel de estadisticas
        JPanel pnlStats = crearPanelEstadisticas();

        // Panel de botones
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pnlBotones.setOpaque(false);

        btnVolverMenu = new JButton("Volver al Menu");
        btnVolverMenu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVolverMenu.setPreferredSize(new Dimension(150, 40));
        btnVolverMenu.setBackground(new Color(70, 130, 180));
        btnVolverMenu.setForeground(Color.WHITE);
        btnVolverMenu.setFocusPainted(false);
        btnVolverMenu.addActionListener(e -> volverAlMenu());

        btnNuevaPartida = new JButton("Nueva Partida");
        btnNuevaPartida.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNuevaPartida.setPreferredSize(new Dimension(150, 40));
        btnNuevaPartida.setBackground(new Color(50, 205, 50));
        btnNuevaPartida.setForeground(Color.WHITE);
        btnNuevaPartida.setFocusPainted(false);
        btnNuevaPartida.addActionListener(e -> nuevaPartida());

        pnlBotones.add(btnVolverMenu);
        pnlBotones.add(btnNuevaPartida);

        // Agregar componentes al panel principal
        pnlPrincipal.add(Box.createVerticalGlue());
        pnlPrincipal.add(lblIcono);
        pnlPrincipal.add(Box.createVerticalStrut(10));
        pnlPrincipal.add(lblResultado);
        pnlPrincipal.add(Box.createVerticalStrut(10));
        pnlPrincipal.add(lblMensaje);
        pnlPrincipal.add(Box.createVerticalStrut(15));
        pnlPrincipal.add(lblGanador);
        pnlPrincipal.add(Box.createVerticalStrut(20));
        pnlPrincipal.add(pnlStats);
        pnlPrincipal.add(Box.createVerticalStrut(30));
        pnlPrincipal.add(pnlBotones);
        pnlPrincipal.add(Box.createVerticalGlue());

        add(pnlPrincipal);
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        panel.setMaximumSize(new Dimension(300, 100));
        panel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel("Resumen de Partida");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);

        // Aqui se podrian agregar mas estadisticas si se tienen disponibles
        JLabel lblInfo = new JLabel("La batalla ha terminado");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setAlignmentX(CENTER_ALIGNMENT);

        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblInfo);

        return panel;
    }

    private void volverAlMenu() {
        // Cerrar esta ventana y la de partida si existe
        cerrarVentanaPartida();

        // Abrir menu principal
        FrmMenuPrincipal menu = new FrmMenuPrincipal();
        menu.setVisible(true);
        dispose();
    }

    private void nuevaPartida() {
        // Cerrar esta ventana y la de partida si existe
        cerrarVentanaPartida();

        // Ir a seleccionar tipo de partida
        FrmSeleccionPartida seleccion = new FrmSeleccionPartida();
        seleccion.setVisible(true);
        dispose();
    }

    private void cerrarVentanaPartida() {
        // Buscar y cerrar FrmPartidaEnCurso si esta abierta
        java.awt.Window[] windows = java.awt.Window.getWindows();
        for (java.awt.Window window : windows) {
            if (window instanceof FrmPartidaEnCurso) {
                window.dispose();
            }
        }
    }
}
