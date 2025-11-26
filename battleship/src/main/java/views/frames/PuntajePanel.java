package views.frames;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import dtos.PuntajeDTO;

/**
 * Panel mejorado para mostrar el puntaje del jugador en tiempo real.
 * Diseño moderno con colores, iconos y animaciones sutiles.
 *
 * Caso de Uso: Gestionar Puntaje - Vista mejorada
 * @author I-Fred
 */
public class PuntajePanel extends JPanel {

    // Colores del tema (Naval/Marítimo)
    private static final Color COLOR_FONDO = new Color(26, 35, 53);           // Azul oscuro marino
    private static final Color COLOR_HEADER = new Color(33, 47, 73);          // Azul medio
    private static final Color COLOR_TEXTO = new Color(236, 240, 245);        // Blanco suave
    private static final Color COLOR_ACENTO_DORADO = new Color(255, 193, 7);  // Dorado para puntos
    private static final Color COLOR_ACIERTO = new Color(76, 175, 80);        // Verde
    private static final Color COLOR_FALLO = new Color(244, 67, 54);          // Rojo
    private static final Color COLOR_HUNDIDO = new Color(255, 152, 0);        // Naranja
    private static final Color COLOR_PRECISION = new Color(33, 150, 243);     // Azul claro

    private JLabel lblTitulo;
    private JLabel lblPuntosTotales;
    private JLabel lblValorPuntos;
    private JLabel lblAciertos;
    private JLabel lblFallos;
    private JLabel lblHundidos;
    private JLabel lblPrecision;
    private JProgressBar barPrecision;

    public PuntajePanel() {
        initComponents();
    }

    private void initComponents() {
        // Configuración del panel principal
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setPreferredSize(new Dimension(220, 280));
        this.setMinimumSize(new Dimension(220, 280));
        this.setMaximumSize(new Dimension(220, 280));
        this.setBackground(COLOR_FONDO);
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACENTO_DORADO, 2),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        // Header con título
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(COLOR_HEADER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblTitulo = new JLabel("⭐ PUNTAJE ⭐", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_ACENTO_DORADO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(lblTitulo);

        this.add(headerPanel);
        this.add(Box.createRigidArea(new Dimension(0, 10)));

        // Puntos totales (destacado)
        lblPuntosTotales = crearLabelIcono("TOTAL:", COLOR_TEXTO);
        this.add(lblPuntosTotales);

        lblValorPuntos = new JLabel("0 pts", SwingConstants.CENTER);
        lblValorPuntos.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValorPuntos.setForeground(COLOR_ACENTO_DORADO);
        lblValorPuntos.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(lblValorPuntos);

        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(crearSeparador());
        this.add(Box.createRigidArea(new Dimension(0, 8)));

        // Estadísticas
        lblAciertos = crearLabelIcono("✓ Aciertos: 0", COLOR_ACIERTO);
        this.add(lblAciertos);
        this.add(Box.createRigidArea(new Dimension(0, 5)));

        lblFallos = crearLabelIcono("✗ Fallos: 0", COLOR_FALLO);
        this.add(lblFallos);
        this.add(Box.createRigidArea(new Dimension(0, 5)));

        lblHundidos = crearLabelIcono("💥 Hundidos: 0", COLOR_HUNDIDO);
        this.add(lblHundidos);

        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(crearSeparador());
        this.add(Box.createRigidArea(new Dimension(0, 8)));

        // Barra de precisión
        lblPrecision = crearLabelIcono("🎯 Precisión:", COLOR_PRECISION);
        this.add(lblPrecision);
        this.add(Box.createRigidArea(new Dimension(0, 5)));

        barPrecision = new JProgressBar(0, 100);
        barPrecision.setValue(0);
        barPrecision.setStringPainted(true);
        barPrecision.setString("0.00%");
        barPrecision.setPreferredSize(new Dimension(190, 25));
        barPrecision.setMaximumSize(new Dimension(190, 25));
        barPrecision.setForeground(COLOR_PRECISION);
        barPrecision.setBackground(COLOR_HEADER);
        barPrecision.setBorder(BorderFactory.createLineBorder(COLOR_TEXTO, 1));
        barPrecision.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(barPrecision);
    }

    /**
     * Crea un JLabel con estilo consistente.
     */
    private JLabel crearLabelIcono(String texto, Color color) {
        JLabel label = new JLabel(texto, SwingConstants.LEFT);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setMaximumSize(new Dimension(200, 25));
        return label;
    }

    /**
     * Crea un separador visual.
     */
    private JSeparator crearSeparador() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(COLOR_TEXTO);
        sep.setMaximumSize(new Dimension(200, 1));
        return sep;
    }
    
    /**
     * Actualiza el panel con los datos del puntaje.
     * Incluye animación sutil para cambios de valor.
     *
     * @param puntaje DTO con los datos actualizados del puntaje
     */
    public void actualizarPuntaje(PuntajeDTO puntaje) {
        if (puntaje == null) {
            return;
        }

        // Efecto de "flash" sutil al actualizar (animación)
        animarActualizacion();

        // Actualizar puntos totales con formato
        lblValorPuntos.setText(puntaje.getPuntosTotales() + " pts");

        // Actualizar estadísticas con iconos
        lblAciertos.setText(String.format("✓ Aciertos: %d", puntaje.getDisparosAcertados()));
        lblFallos.setText(String.format("✗ Fallos: %d", puntaje.getDisparosFallados()));
        lblHundidos.setText(String.format("💥 Hundidos: %d", puntaje.getNavesHundidas()));

        // Actualizar barra de precisión
        int precision = (int) Math.round(puntaje.getPrecision());
        barPrecision.setValue(precision);
        barPrecision.setString(String.format("%.2f%%", puntaje.getPrecision()));

        // Cambiar color de la barra según la precisión
        if (precision >= 70) {
            barPrecision.setForeground(COLOR_ACIERTO);      // Verde si >70%
        } else if (precision >= 40) {
            barPrecision.setForeground(COLOR_HUNDIDO);      // Naranja si 40-70%
        } else {
            barPrecision.setForeground(COLOR_FALLO);        // Rojo si <40%
        }

        this.revalidate();
        this.repaint();
    }

    /**
     * Reinicia todos los valores del puntaje a cero.
     */
    public void resetPuntaje() {
        lblValorPuntos.setText("0 pts");
        lblAciertos.setText("✓ Aciertos: 0");
        lblFallos.setText("✗ Fallos: 0");
        lblHundidos.setText("💥 Hundidos: 0");
        barPrecision.setValue(0);
        barPrecision.setString("0.00%");
        barPrecision.setForeground(COLOR_PRECISION);

        this.revalidate();
        this.repaint();
    }

    /**
     * Animación sutil de "flash" al actualizar el puntaje.
     * Crea un efecto visual que indica que el valor cambió.
     */
    private void animarActualizacion() {
        Color colorOriginal = lblValorPuntos.getForeground();
        lblValorPuntos.setForeground(Color.WHITE);

        Timer timer = new Timer(150, e -> {
            lblValorPuntos.setForeground(colorOriginal);
        });
        timer.setRepeats(false);
        timer.start();
    }
}