package views.frames;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class TimerPanel extends JPanel {

    private static final Color COLOR_TIEMPO_NORMAL = new Color(60, 60, 60);
    private static final Color COLOR_TIEMPO_ADVERTENCIA = new Color(100, 100, 100);
    private static final Color COLOR_TIEMPO_URGENTE = Color.BLACK;
    private static final Color COLOR_FONDO = Color.WHITE;
    private static final Color COLOR_TEXTO = Color.BLACK;

    private Timer timer;
    private int[] tiempo;
    private int total;
    private JLabel lblTiempo;
    private JProgressBar barTiempo;

    public TimerPanel(int delay, int total) {
        this.total = total;
        this.tiempo = new int[1];
        tiempo[0] = total;

        inicializarComponentes();
        configurarTimer(delay);
    }

    private void inicializarComponentes() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setPreferredSize(new Dimension(180, 70));
        this.setMinimumSize(new Dimension(180, 70));
        this.setMaximumSize(new Dimension(180, 70));
        this.setBackground(COLOR_FONDO);
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblTitulo = new JLabel("[T] TIEMPO DE TURNO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(lblTitulo);

        this.add(Box.createRigidArea(new Dimension(0, 5)));

        lblTiempo = new JLabel("00:" + String.format("%02d", total), SwingConstants.CENTER);
        lblTiempo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTiempo.setForeground(COLOR_TIEMPO_NORMAL);
        lblTiempo.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(lblTiempo);

        this.add(Box.createRigidArea(new Dimension(0, 5)));

        barTiempo = new JProgressBar(0, total);
        barTiempo.setValue(total);
        barTiempo.setPreferredSize(new Dimension(160, 8));
        barTiempo.setMaximumSize(new Dimension(160, 8));
        barTiempo.setForeground(COLOR_TIEMPO_NORMAL);
        barTiempo.setBackground(new Color(220, 220, 220));
        barTiempo.setBorderPainted(false);
        barTiempo.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(barTiempo);

        this.setVisible(true);
    }

    private void configurarTimer(int delay) {
        this.timer = new Timer(delay, e -> {
            lblTiempo.setText("00:" + String.format("%02d", tiempo[0]));

            barTiempo.setValue(tiempo[0]);

            actualizarColores();

            tiempo[0]--;

            if (tiempo[0] < 0) {
                timer.stop();
                tiempo[0] = total;
                timer.start();
            }
        });
    }

    private void actualizarColores() {
        Color colorActual;

        if (tiempo[0] > 15) {
            colorActual = COLOR_TIEMPO_NORMAL;
        } else if (tiempo[0] > 10) {
            colorActual = COLOR_TIEMPO_ADVERTENCIA;
        } else {
            colorActual = COLOR_TIEMPO_URGENTE;

            if (tiempo[0] <= 5 && tiempo[0] % 2 == 0) {
                lblTiempo.setForeground(Color.GRAY);
                return;
            }
        }

        lblTiempo.setForeground(colorActual);
        barTiempo.setForeground(colorActual);
    }

    public void initTimer() {
        if (!timer.isRunning()) {
            timer.start();
        } else {
            tiempo[0] = total;
            barTiempo.setValue(total);
            actualizarColores();
        }
    }

    public void initTimer(int milis) {
        initTimer();
    }

    public void stopTimer() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }

    public int getTiempoRestante() {
        return tiempo[0];
    }
}
