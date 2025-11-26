package views.frames;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import dtos.PuntajeDTO;

public class PuntajePanel extends JPanel {

    private static final Color COLOR_FONDO = Color.WHITE;
    private static final Color COLOR_HEADER = new Color(240, 240, 240);
    private static final Color COLOR_TEXTO = Color.BLACK;
    private static final Color COLOR_ACENTO = Color.BLACK;
    private static final Color COLOR_ACIERTO = new Color(60, 60, 60);
    private static final Color COLOR_FALLO = new Color(120, 120, 120);
    private static final Color COLOR_HUNDIDO = new Color(80, 80, 80);
    private static final Color COLOR_PRECISION = new Color(100, 100, 100);

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
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setPreferredSize(new Dimension(220, 280));
        this.setMinimumSize(new Dimension(220, 280));
        this.setMaximumSize(new Dimension(220, 280));
        this.setBackground(COLOR_FONDO);
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(COLOR_HEADER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblTitulo = new JLabel("* PUNTAJE *", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_ACENTO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(lblTitulo);

        this.add(headerPanel);
        this.add(Box.createRigidArea(new Dimension(0, 10)));

        lblPuntosTotales = crearLabelIcono("TOTAL:", COLOR_TEXTO);
        this.add(lblPuntosTotales);

        lblValorPuntos = new JLabel("0 pts", SwingConstants.CENTER);
        lblValorPuntos.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValorPuntos.setForeground(COLOR_ACENTO);
        lblValorPuntos.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(lblValorPuntos);

        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(crearSeparador());
        this.add(Box.createRigidArea(new Dimension(0, 8)));

        lblAciertos = crearLabelIcono("[X] Aciertos: 0", COLOR_ACIERTO);
        this.add(lblAciertos);
        this.add(Box.createRigidArea(new Dimension(0, 5)));

        lblFallos = crearLabelIcono("[O] Fallos: 0", COLOR_FALLO);
        this.add(lblFallos);
        this.add(Box.createRigidArea(new Dimension(0, 5)));

        lblHundidos = crearLabelIcono("[!] Hundidos: 0", COLOR_HUNDIDO);
        this.add(lblHundidos);

        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(crearSeparador());
        this.add(Box.createRigidArea(new Dimension(0, 8)));

        lblPrecision = crearLabelIcono("[#] Precision:", COLOR_PRECISION);
        this.add(lblPrecision);
        this.add(Box.createRigidArea(new Dimension(0, 5)));

        barPrecision = new JProgressBar(0, 100);
        barPrecision.setValue(0);
        barPrecision.setStringPainted(true);
        barPrecision.setString("0.00%");
        barPrecision.setPreferredSize(new Dimension(190, 25));
        barPrecision.setMaximumSize(new Dimension(190, 25));
        barPrecision.setForeground(COLOR_PRECISION);
        barPrecision.setBackground(new Color(220, 220, 220));
        barPrecision.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        barPrecision.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(barPrecision);
    }

    private JLabel crearLabelIcono(String texto, Color color) {
        JLabel label = new JLabel(texto, SwingConstants.LEFT);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setMaximumSize(new Dimension(200, 25));
        return label;
    }

    private JSeparator crearSeparador() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(Color.BLACK);
        sep.setMaximumSize(new Dimension(200, 1));
        return sep;
    }

    public void actualizarPuntaje(PuntajeDTO puntaje) {
        if (puntaje == null) {
            return;
        }

        animarActualizacion();

        lblValorPuntos.setText(puntaje.getPuntosTotales() + " pts");

        lblAciertos.setText(String.format("[X] Aciertos: %d", puntaje.getDisparosAcertados()));
        lblFallos.setText(String.format("[O] Fallos: %d", puntaje.getDisparosFallados()));
        lblHundidos.setText(String.format("[!] Hundidos: %d", puntaje.getNavesHundidas()));

        int precision = (int) Math.round(puntaje.getPrecision());
        barPrecision.setValue(precision);
        barPrecision.setString(String.format("%.2f%%", puntaje.getPrecision()));

        if (precision >= 70) {
            barPrecision.setForeground(COLOR_ACIERTO);
        } else if (precision >= 40) {
            barPrecision.setForeground(COLOR_HUNDIDO);
        } else {
            barPrecision.setForeground(COLOR_FALLO);
        }

        this.revalidate();
        this.repaint();
    }

    public void resetPuntaje() {
        lblValorPuntos.setText("0 pts");
        lblAciertos.setText("[X] Aciertos: 0");
        lblFallos.setText("[O] Fallos: 0");
        lblHundidos.setText("[!] Hundidos: 0");
        barPrecision.setValue(0);
        barPrecision.setString("0.00%");
        barPrecision.setForeground(COLOR_PRECISION);

        this.revalidate();
        this.repaint();
    }

    private void animarActualizacion() {
        Color colorOriginal = lblValorPuntos.getForeground();
        lblValorPuntos.setForeground(Color.GRAY);

        Timer timer = new Timer(150, e -> {
            lblValorPuntos.setForeground(colorOriginal);
        });
        timer.setRepeats(false);
        timer.start();
    }
}
