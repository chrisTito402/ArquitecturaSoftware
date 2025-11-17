package views.frames;

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

    public TimerPanel(int seg) {
        this.setPreferredSize(new Dimension(44, 25));
        this.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        this.tiempo = new int[1];
        tiempo[0] = seg;
        this.timer = new Timer(
                seg,
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
    
    public void initTimer(int seg) {
        if (!timer.isRunning()) {
            timer.start();
        } else {
            timer.stop();
            timer.start();
        }
    }
    
    public void initTimer() {
        if (!timer.isRunning()) {
            timer.start();
        } else {
            timer.stop();
            timer.start();
        }
    }
    
    public void stopTimer() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }
}
