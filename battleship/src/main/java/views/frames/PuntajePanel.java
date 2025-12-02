/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views.frames;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import views.DTOs.PuntajeDTO;

/**
 * @author I-Fred
 */
//TEMPORAL
public class PuntajePanel extends JPanel {

    private JLabel lblTitulo;
    private JLabel lblPuntosTotales;
    private JLabel lblAciertos;
    private JLabel lblFallos;
    private JLabel lblHundidos;
    private JLabel lblPrecision;

    public PuntajePanel() {
        initComponents();
    }

    private void initComponents() {
        this.setLayout(new GridLayout(6, 1, 5, 5));
        this.setPreferredSize(new Dimension(150, 180));
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        this.setBackground(new Color(240, 240, 240));

        Font fontTitulo = new Font("Segoe UI", Font.BOLD, 14);
        Font fontDatos = new Font("Segoe UI", Font.PLAIN, 12);

        lblTitulo = new JLabel("PUNTAJE", SwingConstants.CENTER);
        lblTitulo.setFont(fontTitulo);
        lblTitulo.setForeground(new Color(0, 102, 153));

        lblPuntosTotales = new JLabel("Total: 0", SwingConstants.LEFT);
        lblPuntosTotales.setFont(new Font("Segoe UI", Font.BOLD, 16));

        lblAciertos = new JLabel("Aciertos: 0", SwingConstants.LEFT);
        lblAciertos.setFont(fontDatos);

        lblFallos = new JLabel("Fallos: 0", SwingConstants.LEFT);
        lblFallos.setFont(fontDatos);

        lblHundidos = new JLabel("Hundidos: 0", SwingConstants.LEFT);
        lblHundidos.setFont(fontDatos);

        lblPrecision = new JLabel("Precisión: 0.00%", SwingConstants.LEFT);
        lblPrecision.setFont(fontDatos);

        this.add(lblTitulo);
        this.add(lblPuntosTotales);
        this.add(lblAciertos);
        this.add(lblFallos);
        this.add(lblHundidos);
        this.add(lblPrecision);
    }

    public void actualizarPuntaje(PuntajeDTO puntaje) {
        if (puntaje == null) {
            return;
        }

        lblPuntosTotales.setText("Total: " + puntaje.getPuntosTotales());
        lblAciertos.setText("Aciertos: " + puntaje.getDisparosAcertados());
        lblFallos.setText("Fallos: " + puntaje.getDisparosFallados());
        lblHundidos.setText("Hundidos: " + puntaje.getNavesHundidas());
        lblPrecision.setText(String.format("Precisión: %.2f%%", puntaje.getPrecision()));

        this.revalidate();
        this.repaint();
    }

    public void resetPuntaje() {
        lblPuntosTotales.setText("Total: 0");
        lblAciertos.setText("Aciertos: 0");
        lblFallos.setText("Fallos: 0");
        lblHundidos.setText("Hundidos: 0");
        lblPrecision.setText("Precisión: 0.00%");
    }
}
