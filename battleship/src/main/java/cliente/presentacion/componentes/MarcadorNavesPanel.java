package cliente.presentacion.componentes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * El marcador que te dice como van las naves del enemigo.
 * Verde = sin tocar, Amarillo = le pegaste pero no se hundio, Rojo = hundida.
 * Lo hicimos asi porque en el PDF del profe decia que asi tenia que ser.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public class MarcadorNavesPanel extends JPanel {

    // Colores segun el PDF
    private static final Color COLOR_SIN_DANIO = new Color(34, 139, 34);    // Verde
    private static final Color COLOR_AVERIADA = new Color(255, 165, 0);      // Amarillo/Naranja
    private static final Color COLOR_HUNDIDA = new Color(220, 20, 60);       // Rojo

    // Total de naves por tipo
    private static final int TOTAL_PORTAAVIONES = 2;
    private static final int TOTAL_CRUCEROS = 2;
    private static final int TOTAL_SUBMARINOS = 4;
    private static final int TOTAL_BARCOS = 3;

    // Contadores por estado para cada tipo
    private int portaavionesAveriados = 0;
    private int portaavionesHundidos = 0;

    private int crucerosAveriados = 0;
    private int crucerosHundidos = 0;

    private int submarinosAveriados = 0;
    private int submarinosHundidos = 0;

    private int barcosAveriados = 0;
    private int barcosHundidos = 0;

    // Labels para mostrar estados
    private JLabel lblPortaavionesEstado;
    private JLabel lblCrucerosEstado;
    private JLabel lblSubmarinosEstado;
    private JLabel lblBarcosEstado;

    public MarcadorNavesPanel() {
        initComponents();
    }

    private void initComponents() {
        this.setLayout(new GridBagLayout());
        this.setPreferredSize(new Dimension(220, 200));
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        this.setBackground(new Color(240, 248, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fontTitulo = new Font("Segoe UI", Font.BOLD, 14);
        Font fontNave = new Font("Segoe UI", Font.BOLD, 11);
        Font fontEstado = new Font("Segoe UI", Font.PLAIN, 10);

        // Titulo
        JLabel lblTitulo = new JLabel("FLOTA ENEMIGA", SwingConstants.CENTER);
        lblTitulo.setFont(fontTitulo);
        lblTitulo.setForeground(new Color(0, 51, 102));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        this.add(lblTitulo, gbc);

        // Leyenda de colores
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JPanel leyenda = crearLeyenda();
        this.add(leyenda, gbc);

        // Separador
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        this.add(new JLabel(" "), gbc);

        // --- Portaaviones ---
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        JLabel lblPortaaviones = new JLabel("Portaaviones:");
        lblPortaaviones.setFont(fontNave);
        this.add(lblPortaaviones, gbc);

        gbc.gridx = 1;
        lblPortaavionesEstado = new JLabel();
        lblPortaavionesEstado.setFont(fontEstado);
        this.add(lblPortaavionesEstado, gbc);

        // --- Cruceros ---
        gbc.gridy = 4;
        gbc.gridx = 0;
        JLabel lblCruceros = new JLabel("Cruceros:");
        lblCruceros.setFont(fontNave);
        this.add(lblCruceros, gbc);

        gbc.gridx = 1;
        lblCrucerosEstado = new JLabel();
        lblCrucerosEstado.setFont(fontEstado);
        this.add(lblCrucerosEstado, gbc);

        // --- Submarinos ---
        gbc.gridy = 5;
        gbc.gridx = 0;
        JLabel lblSubmarinos = new JLabel("Submarinos:");
        lblSubmarinos.setFont(fontNave);
        this.add(lblSubmarinos, gbc);

        gbc.gridx = 1;
        lblSubmarinosEstado = new JLabel();
        lblSubmarinosEstado.setFont(fontEstado);
        this.add(lblSubmarinosEstado, gbc);

        // --- Barcos ---
        gbc.gridy = 6;
        gbc.gridx = 0;
        JLabel lblBarcos = new JLabel("Barcos:");
        lblBarcos.setFont(fontNave);
        this.add(lblBarcos, gbc);

        gbc.gridx = 1;
        lblBarcosEstado = new JLabel();
        lblBarcosEstado.setFont(fontEstado);
        this.add(lblBarcosEstado, gbc);

        // Actualizar la visualizacion inicial
        actualizarVisualizacion();
    }

    /**
     * Crea el panel de leyenda con los colores.
     */
    private JPanel crearLeyenda() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1, 3, 1, 3);

        Font fontLeyenda = new Font("Segoe UI", Font.PLAIN, 9);

        // Sin dano - Verde
        gbc.gridx = 0;
        gbc.gridy = 0;
        JPanel boxVerde = new JPanel();
        boxVerde.setBackground(COLOR_SIN_DANIO);
        boxVerde.setPreferredSize(new Dimension(12, 12));
        boxVerde.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.add(boxVerde, gbc);

        gbc.gridx = 1;
        JLabel lblVerde = new JLabel("Sin dano");
        lblVerde.setFont(fontLeyenda);
        lblVerde.setForeground(COLOR_SIN_DANIO);
        panel.add(lblVerde, gbc);

        // Averiada - Amarillo
        gbc.gridx = 2;
        JPanel boxAmarillo = new JPanel();
        boxAmarillo.setBackground(COLOR_AVERIADA);
        boxAmarillo.setPreferredSize(new Dimension(12, 12));
        boxAmarillo.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.add(boxAmarillo, gbc);

        gbc.gridx = 3;
        JLabel lblAmarillo = new JLabel("Averiada");
        lblAmarillo.setFont(fontLeyenda);
        lblAmarillo.setForeground(COLOR_AVERIADA.darker());
        panel.add(lblAmarillo, gbc);

        // Hundida - Rojo
        gbc.gridx = 4;
        JPanel boxRojo = new JPanel();
        boxRojo.setBackground(COLOR_HUNDIDA);
        boxRojo.setPreferredSize(new Dimension(12, 12));
        boxRojo.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.add(boxRojo, gbc);

        gbc.gridx = 5;
        JLabel lblRojo = new JLabel("Hundida");
        lblRojo.setFont(fontLeyenda);
        lblRojo.setForeground(COLOR_HUNDIDA);
        panel.add(lblRojo, gbc);

        return panel;
    }

    /**
     * Actualiza la visualizacion de todos los estados.
     */
    private void actualizarVisualizacion() {
        // Portaaviones
        int sinDanioPA = TOTAL_PORTAAVIONES - portaavionesAveriados - portaavionesHundidos;
        lblPortaavionesEstado.setText(formatearEstado(sinDanioPA, portaavionesAveriados, portaavionesHundidos));

        // Cruceros
        int sinDanioC = TOTAL_CRUCEROS - crucerosAveriados - crucerosHundidos;
        lblCrucerosEstado.setText(formatearEstado(sinDanioC, crucerosAveriados, crucerosHundidos));

        // Submarinos
        int sinDanioS = TOTAL_SUBMARINOS - submarinosAveriados - submarinosHundidos;
        lblSubmarinosEstado.setText(formatearEstado(sinDanioS, submarinosAveriados, submarinosHundidos));

        // Barcos
        int sinDanioB = TOTAL_BARCOS - barcosAveriados - barcosHundidos;
        lblBarcosEstado.setText(formatearEstado(sinDanioB, barcosAveriados, barcosHundidos));

        this.revalidate();
        this.repaint();
    }

    /**
     * Formatea el texto de estado con colores HTML.
     */
    private String formatearEstado(int sinDanio, int averiadas, int hundidas) {
        StringBuilder sb = new StringBuilder("<html>");

        // Sin dano (verde)
        sb.append("<font color='#228B22'>").append(sinDanio).append("</font>");
        sb.append(" / ");

        // Averiadas (amarillo/naranja)
        sb.append("<font color='#FFA500'>").append(averiadas).append("</font>");
        sb.append(" / ");

        // Hundidas (rojo)
        sb.append("<font color='#DC143C'>").append(hundidas).append("</font>");

        sb.append("</html>");
        return sb.toString();
    }

    /**
     * Registra cuando una nave es averiada (impacto sin hundimiento).
     * @param tipoNave Tipo de nave impactada (PORTAAVIONES, CRUCERO, SUBMARINO, BARCO)
     */
    public void naveAveriada(String tipoNave) {
        if (tipoNave == null) return;

        switch (tipoNave.toUpperCase()) {
            case "PORTAAVIONES" -> {
                if (portaavionesAveriados + portaavionesHundidos < TOTAL_PORTAAVIONES) {
                    portaavionesAveriados++;
                }
            }
            case "CRUCERO" -> {
                if (crucerosAveriados + crucerosHundidos < TOTAL_CRUCEROS) {
                    crucerosAveriados++;
                }
            }
            case "SUBMARINO" -> {
                if (submarinosAveriados + submarinosHundidos < TOTAL_SUBMARINOS) {
                    submarinosAveriados++;
                }
            }
            case "BARCO" -> {
                if (barcosAveriados + barcosHundidos < TOTAL_BARCOS) {
                    barcosAveriados++;
                }
            }
        }
        actualizarVisualizacion();
    }

    /**
     * Registra cuando una nave es hundida.
     * Si la nave estaba averiada, la pasa a hundida.
     * @param tipoNave Tipo de nave hundida (PORTAAVIONES, CRUCERO, SUBMARINO, BARCO)
     */
    public void naveHundida(String tipoNave) {
        if (tipoNave == null) return;

        switch (tipoNave.toUpperCase()) {
            case "PORTAAVIONES" -> {
                // Si habia una averiada, la pasamos a hundida
                if (portaavionesAveriados > 0) {
                    portaavionesAveriados--;
                }
                portaavionesHundidos++;
            }
            case "CRUCERO" -> {
                if (crucerosAveriados > 0) {
                    crucerosAveriados--;
                }
                crucerosHundidos++;
            }
            case "SUBMARINO" -> {
                if (submarinosAveriados > 0) {
                    submarinosAveriados--;
                }
                submarinosHundidos++;
            }
            case "BARCO" -> {
                if (barcosAveriados > 0) {
                    barcosAveriados--;
                }
                barcosHundidos++;
            }
        }
        actualizarVisualizacion();
    }

    /**
     * Reinicia el marcador a su estado inicial.
     */
    public void resetMarcador() {
        portaavionesAveriados = 0;
        portaavionesHundidos = 0;
        crucerosAveriados = 0;
        crucerosHundidos = 0;
        submarinosAveriados = 0;
        submarinosHundidos = 0;
        barcosAveriados = 0;
        barcosHundidos = 0;

        actualizarVisualizacion();
    }

    /**
     * Obtiene el total de naves hundidas.
     */
    public int getTotalHundidos() {
        return portaavionesHundidos + crucerosHundidos + submarinosHundidos + barcosHundidos;
    }

    /**
     * Obtiene el total de naves averiadas.
     */
    public int getTotalAveriadas() {
        return portaavionesAveriados + crucerosAveriados + submarinosAveriados + barcosAveriados;
    }

    /**
     * Verifica si todas las naves enemigas han sido hundidas.
     */
    public boolean todasHundidas() {
        int totalNaves = TOTAL_PORTAAVIONES + TOTAL_CRUCEROS + TOTAL_SUBMARINOS + TOTAL_BARCOS;
        return getTotalHundidos() >= totalNaves;
    }
}
