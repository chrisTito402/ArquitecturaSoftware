package cliente.presentacion.componentes;

import cliente.controlador.ControlVista;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * Panel que muestra el temporizador del turno.
 * Cuando el tiempo llega a 0, notifica al sistema para cambiar de turno.
 *
 * @author daniel
 */
public class TimerPanel extends JLabel {

    private Timer timer;
    private int[] tiempo;
    private int total;
    private Runnable onTiempoAgotado;

    public TimerPanel(int delay, int total) {
        this.setPreferredSize(new Dimension(44, 25));
        this.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        this.setBackground(Color.GREEN);
        this.setText("00:30");

        this.total = total;
        this.tiempo = new int[1];
        tiempo[0] = total;
        this.timer = new Timer(
                delay,
                e -> {
                    this.setText("00:" + String.format("%02d", tiempo[0]));

                    // Cambiar color según tiempo restante
                    if (tiempo[0] <= 5) {
                        this.setForeground(Color.RED);
                    } else if (tiempo[0] <= 10) {
                        this.setForeground(Color.ORANGE);
                    } else {
                        this.setForeground(Color.BLACK);
                    }

                    tiempo[0]--;
                    if (tiempo[0] < 0) {
                        timer.stop();
                        tiempo[0] = total;

                        // Notificar que el tiempo se agotó
                        if (onTiempoAgotado != null) {
                            onTiempoAgotado.run();
                        }

                        timer.start();
                    }
                }
        );

        this.setVisible(true);
    }

    /**
     * Establece el callback que se ejecuta cuando el tiempo se agota.
     */
    public void setOnTiempoAgotado(Runnable callback) {
        this.onTiempoAgotado = callback;
    }

    public void initTimer(int milis) {
        if (!timer.isRunning()) {
            timer.start();
        } else {
            tiempo[0] = total;
        }
    }

    public void initTimer() {
        if (!timer.isRunning()) {
            timer.start();
        } else {
            tiempo[0] = total;
        }
    }

    public void stopTimer() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }
}
