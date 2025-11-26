package views.frames;

import dtos.enums.EstadoNaveDTO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MarcadorNavesPanel extends JPanel {

    private Map<String, int[]> contadoresNaves;
    private Map<String, JLabel[]> labelsNaves;

    private static final Color COLOR_SIN_DANIOS = new Color(76, 175, 80);
    private static final Color COLOR_AVERIADO = new Color(255, 193, 7);
    private static final Color COLOR_HUNDIDO = new Color(244, 67, 54);

    private static final String[] TIPOS_NAVES = {"Portaaviones", "Cruceros", "Submarinos", "Barcos"};
    private static final int[] CANTIDADES_INICIALES = {2, 2, 4, 3};

    public MarcadorNavesPanel() {
        contadoresNaves = new HashMap<>();
        labelsNaves = new HashMap<>();

        setBackground(UIConstants.COLOR_FONDO_SECUNDARIO);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_ACENTO_AZUL, 2),
                new EmptyBorder(10, 15, 10, 15)
        ));

        inicializarContadores();
        crearComponentes();
    }

    private void inicializarContadores() {
        for (int i = 0; i < TIPOS_NAVES.length; i++) {
            contadoresNaves.put(TIPOS_NAVES[i], new int[]{CANTIDADES_INICIALES[i], 0, 0});
        }
    }

    private void crearComponentes() {
        JLabel lblTitulo = new JLabel("⚓ ESTADO DE FLOTA");
        lblTitulo.setFont(UIConstants.FONT_TITULO_PEQUENO);
        lblTitulo.setForeground(UIConstants.COLOR_ACENTO_DORADO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lblTitulo);
        add(Box.createVerticalStrut(10));

        JPanel panelLeyenda = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelLeyenda.setBackground(UIConstants.COLOR_FONDO_SECUNDARIO);
        panelLeyenda.add(crearIndicadorLeyenda("●", COLOR_SIN_DANIOS, "OK"));
        panelLeyenda.add(crearIndicadorLeyenda("●", COLOR_AVERIADO, "Avg"));
        panelLeyenda.add(crearIndicadorLeyenda("●", COLOR_HUNDIDO, "Hnd"));
        add(panelLeyenda);
        add(Box.createVerticalStrut(10));

        for (int i = 0; i < TIPOS_NAVES.length; i++) {
            add(crearFilaNave(TIPOS_NAVES[i], CANTIDADES_INICIALES[i]));
            if (i < TIPOS_NAVES.length - 1) {
                add(Box.createVerticalStrut(8));
            }
        }
    }

    private JPanel crearIndicadorLeyenda(String simbolo, Color color, String texto) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        panel.setBackground(UIConstants.COLOR_FONDO_SECUNDARIO);

        JLabel lblSimbolo = new JLabel(simbolo);
        lblSimbolo.setForeground(color);
        lblSimbolo.setFont(new Font("Segoe UI", Font.BOLD, 10));

        JLabel lblTexto = new JLabel(texto);
        lblTexto.setForeground(UIConstants.COLOR_TEXTO_SECUNDARIO);
        lblTexto.setFont(new Font("Segoe UI", Font.PLAIN, 9));

        panel.add(lblSimbolo);
        panel.add(lblTexto);
        return panel;
    }

    private JPanel crearFilaNave(String tipoNave, int cantidad) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBackground(UIConstants.COLOR_FONDO_SECUNDARIO);
        panel.setMaximumSize(new Dimension(200, 25));

        JLabel lblNombre = new JLabel(tipoNave);
        lblNombre.setForeground(UIConstants.COLOR_TEXTO_PRINCIPAL);
        lblNombre.setFont(UIConstants.FONT_TEXTO_PEQUENO);
        panel.add(lblNombre, BorderLayout.WEST);

        JPanel panelContadores = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelContadores.setBackground(UIConstants.COLOR_FONDO_SECUNDARIO);

        JLabel lblSinDanios = crearLabelContador(String.valueOf(cantidad), COLOR_SIN_DANIOS);
        JLabel lblAveriados = crearLabelContador("0", COLOR_AVERIADO);
        JLabel lblHundidos = crearLabelContador("0", COLOR_HUNDIDO);

        labelsNaves.put(tipoNave, new JLabel[]{lblSinDanios, lblAveriados, lblHundidos});

        panelContadores.add(lblSinDanios);
        panelContadores.add(lblAveriados);
        panelContadores.add(lblHundidos);

        panel.add(panelContadores, BorderLayout.EAST);

        return panel;
    }

    private JLabel crearLabelContador(String texto, Color color) {
        JLabel label = new JLabel(texto);
        label.setForeground(color);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(20, 20));
        return label;
    }

    public void actualizarNave(String tipoNave, EstadoNaveDTO nuevoEstado) {
        if (!contadoresNaves.containsKey(tipoNave)) return;

        int[] contadores = contadoresNaves.get(tipoNave);
        JLabel[] labels = labelsNaves.get(tipoNave);

        if (nuevoEstado == EstadoNaveDTO.AVERIADO) {
            if (contadores[0] > 0) {
                contadores[0]--;
                contadores[1]++;
            }
        } else if (nuevoEstado == EstadoNaveDTO.HUNDIDO) {
            if (contadores[1] > 0) {
                contadores[1]--;
            } else if (contadores[0] > 0) {
                contadores[0]--;
            }
            contadores[2]++;
        }

        labels[0].setText(String.valueOf(contadores[0]));
        labels[1].setText(String.valueOf(contadores[1]));
        labels[2].setText(String.valueOf(contadores[2]));

        animarCambio(labels);
    }

    private void animarCambio(JLabel[] labels) {
        Timer timer = new Timer(100, null);
        final int[] count = {0};

        timer.addActionListener(e -> {
            count[0]++;
            if (count[0] % 2 == 0) {
                for (JLabel lbl : labels) {
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                }
            } else {
                for (JLabel lbl : labels) {
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                }
            }
            if (count[0] >= 4) {
                timer.stop();
                for (JLabel lbl : labels) {
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                }
            }
        });
        timer.start();
    }

    public void reiniciar() {
        inicializarContadores();
        for (int i = 0; i < TIPOS_NAVES.length; i++) {
            JLabel[] labels = labelsNaves.get(TIPOS_NAVES[i]);
            labels[0].setText(String.valueOf(CANTIDADES_INICIALES[i]));
            labels[1].setText("0");
            labels[2].setText("0");
        }
    }

    public int getTotalHundidos() {
        int total = 0;
        for (int[] contadores : contadoresNaves.values()) {
            total += contadores[2];
        }
        return total;
    }

    public boolean todasHundidas() {
        for (int i = 0; i < TIPOS_NAVES.length; i++) {
            int[] contadores = contadoresNaves.get(TIPOS_NAVES[i]);
            if (contadores[2] < CANTIDADES_INICIALES[i]) {
                return false;
            }
        }
        return true;
    }
}
