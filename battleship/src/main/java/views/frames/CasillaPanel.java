package views.frames;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import models.entidades.Coordenadas;

/**
 *
 * @author daniel
 */
public class CasillaPanel extends JPanel {

    private Coordenadas coordenadas;
    private volatile boolean dibujarX;

    public CasillaPanel(Coordenadas coordenadas) {
        this.coordenadas = coordenadas;
        this.dibujarX = false;
    }

    public Coordenadas getCoordenadas() {
        return coordenadas;
    }

    public boolean isDibujarX() {
        return dibujarX;
    }

    public void setDibujarX(boolean dibujarX) {
        this.dibujarX = dibujarX;
    }
    
    public void marcarDisparo() {
        dibujarX = true;
        revalidate();
        repaint();
        this.getParent().revalidate();
        this.getParent().repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (dibujarX) {
            System.out.println("Entro a dibujar");
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int ancho = getWidth();
            int alto = getHeight();

            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(5));

            g2d.drawLine(0, 0, ancho, alto);

            g2d.drawLine(ancho, 0, 0, alto);
            System.out.println("dibujo");
        }
    }

}
