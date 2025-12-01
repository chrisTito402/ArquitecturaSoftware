package views.frames;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 *
 * @author daniel
 */
public class TimerPanel extends JLabel {

    private Timer timer;
    private int[] tiempo;
    private int total;

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
                    tiempo[0]--;
                    if (tiempo[0] < 0) {
                        timer.stop();
                        tiempo[0] = 30;
                        timer.start();
                    }
                }
        );

        this.setVisible(true);
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
