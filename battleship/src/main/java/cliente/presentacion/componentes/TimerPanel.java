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
    private volatile Runnable onTiempoAgotado;
    private volatile boolean detenido = false; // Bandera para saber si se detuvo manualmente
    private volatile boolean partidaTerminada = false; // Bandera adicional para fin de partida

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
                    // Si está detenido o la partida terminó, no hacer nada
                    if (detenido || partidaTerminada) {
                        timer.stop();
                        return;
                    }

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

                        // Capturar callback localmente para evitar race condition
                        Runnable callback = onTiempoAgotado;

                        // Notificar que el tiempo se agotó (solo si no está detenido/terminado)
                        if (callback != null && !detenido && !partidaTerminada) {
                            callback.run();
                        }

                        // NO reiniciar automáticamente - se reinicia cuando se llama initTimer()
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
        detenido = false; // Permitir que el timer funcione
        tiempo[0] = total;
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    public void initTimer() {
        detenido = false; // Permitir que el timer funcione
        tiempo[0] = total;
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    public void stopTimer() {
        detenido = true; // Marcar como detenido permanentemente
        partidaTerminada = true; // Marcar partida como terminada
        onTiempoAgotado = null; // Limpiar callback para que no se ejecute más
        if (timer.isRunning()) {
            timer.stop();
        }
        this.setText("--:--");
        this.setForeground(Color.GRAY);
    }

    /**
     * Reinicia completamente el timer para una nueva partida.
     * Limpia todas las banderas de detenido/terminado.
     */
    public void resetForNewGame() {
        detenido = false;
        partidaTerminada = false;
        tiempo[0] = total;
        this.setText("00:" + String.format("%02d", total));
        this.setForeground(Color.BLACK);
    }
}
