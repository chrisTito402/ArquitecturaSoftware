package views.frames;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Panel mejorado para mostrar el temporizador de turno.
 * Incluye indicador visual de tiempo y cambios de color según urgencia.
 *
 * Colores:
 * - Verde: Tiempo suficiente (>15s)
 * - Amarillo: Advertencia (10-15s)
 * - Rojo: Urgente (<10s)
 *
 * @author daniel
 */
public class TimerPanel extends JPanel {

    // Colores para estados del tiempo
    private static final Color COLOR_TIEMPO_NORMAL = new Color(76, 175, 80);      // Verde
    private static final Color COLOR_TIEMPO_ADVERTENCIA = new Color(255, 193, 7); // Amarillo
    private static final Color COLOR_TIEMPO_URGENTE = new Color(244, 67, 54);     // Rojo
    private static final Color COLOR_FONDO = new Color(33, 47, 73);               // Azul oscuro
    private static final Color COLOR_TEXTO = new Color(236, 240, 245);            // Blanco suave

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

    /**
     * Inicializa los componentes visuales del panel.
     */
    private void inicializarComponentes() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setPreferredSize(new Dimension(180, 70));
        this.setMinimumSize(new Dimension(180, 70));
        this.setMaximumSize(new Dimension(180, 70));
        this.setBackground(COLOR_FONDO);
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_TEXTO, 2),
                new EmptyBorder(8, 12, 8, 12)
        ));

        // Etiqueta de título
        JLabel lblTitulo = new JLabel("⏱ TIEMPO DE TURNO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(lblTitulo);

        this.add(Box.createRigidArea(new Dimension(0, 5)));

        // Etiqueta del tiempo
        lblTiempo = new JLabel("00:" + String.format("%02d", total), SwingConstants.CENTER);
        lblTiempo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTiempo.setForeground(COLOR_TIEMPO_NORMAL);
        lblTiempo.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(lblTiempo);

        this.add(Box.createRigidArea(new Dimension(0, 5)));

        // Barra de progreso visual
        barTiempo = new JProgressBar(0, total);
        barTiempo.setValue(total);
        barTiempo.setPreferredSize(new Dimension(160, 8));
        barTiempo.setMaximumSize(new Dimension(160, 8));
        barTiempo.setForeground(COLOR_TIEMPO_NORMAL);
        barTiempo.setBackground(new Color(50, 50, 50));
        barTiempo.setBorderPainted(false);
        barTiempo.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(barTiempo);

        this.setVisible(true);
    }

    /**
     * Configura el Timer con la lógica de actualización.
     */
    private void configurarTimer(int delay) {
        this.timer = new Timer(delay, e -> {
            // Actualizar texto del tiempo
            lblTiempo.setText("00:" + String.format("%02d", tiempo[0]));

            // Actualizar barra de progreso
            barTiempo.setValue(tiempo[0]);

            // Cambiar colores según el tiempo restante
            actualizarColores();

            // Decrementar tiempo
            tiempo[0]--;

            // Reiniciar cuando llegue a cero
            if (tiempo[0] < 0) {
                timer.stop();
                tiempo[0] = total;
                timer.start();
            }
        });
    }

    /**
     * Actualiza los colores del timer según el tiempo restante.
     */
    private void actualizarColores() {
        Color colorActual;

        if (tiempo[0] > 15) {
            // Tiempo normal (verde)
            colorActual = COLOR_TIEMPO_NORMAL;
        } else if (tiempo[0] > 10) {
            // Advertencia (amarillo)
            colorActual = COLOR_TIEMPO_ADVERTENCIA;
        } else {
            // Urgente (rojo)
            colorActual = COLOR_TIEMPO_URGENTE;

            // Parpadeo en los últimos 5 segundos
            if (tiempo[0] <= 5 && tiempo[0] % 2 == 0) {
                lblTiempo.setForeground(Color.WHITE);
                return;
            }
        }

        lblTiempo.setForeground(colorActual);
        barTiempo.setForeground(colorActual);
    }

    /**
     * Inicia o reinicia el temporizador.
     */
    public void initTimer() {
        if (!timer.isRunning()) {
            timer.start();
        } else {
            tiempo[0] = total;
            barTiempo.setValue(total);
            actualizarColores();
        }
    }

    /**
     * Inicia o reinicia el temporizador (sobrecarga para compatibilidad).
     */
    public void initTimer(int milis) {
        initTimer();
    }

    /**
     * Detiene el temporizador.
     */
    public void stopTimer() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }

    /**
     * Obtiene el tiempo restante en segundos.
     */
    public int getTiempoRestante() {
        return tiempo[0];
    }
}
