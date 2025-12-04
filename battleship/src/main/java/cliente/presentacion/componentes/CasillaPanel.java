package cliente.presentacion.componentes;

import compartido.comunicacion.dto.CoordenadasDTO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Panel que representa una casilla del tablero propio.
 * Usa CoordenadasDTO para cumplir con MVC (Vista no importa del Modelo).
 * Soporta visualizacion de impactos y hundimientos con indicadores claros.
 *
 * @author daniel
 */
public class CasillaPanel extends JPanel {

    private CoordenadasDTO coordenadas;
    private EstadoCasilla estado = EstadoCasilla.NORMAL;
    private Color colorOriginal;

    public enum EstadoCasilla {
        NORMAL,      // Sin impacto
        IMPACTO,     // Recibio impacto pero no hundida
        HUNDIDA      // Parte de nave hundida
    }

    public CasillaPanel(CoordenadasDTO coordenadas) {
        this.coordenadas = coordenadas;
    }

    public CoordenadasDTO getCoordenadas() {
        return coordenadas;
    }

    /**
     * Establece el estado de la casilla para mostrar indicadores visuales.
     */
    public void setEstado(EstadoCasilla estado) {
        this.estado = estado;
        repaint();
    }

    public EstadoCasilla getEstado() {
        return estado;
    }

    /**
     * Guarda el color original de la nave para referencia.
     */
    public void setColorOriginal(Color color) {
        this.colorOriginal = color;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (estado == EstadoCasilla.NORMAL) {
            return; // No dibujar nada extra
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int margin = 4;

        if (estado == EstadoCasilla.IMPACTO) {
            // Dibujar una X amarilla/naranja sobre la casilla (impacto sin hundir)
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(255, 140, 0)); // Naranja oscuro
            g2.drawLine(margin, margin, w - margin, h - margin);
            g2.drawLine(w - margin, margin, margin, h - margin);

            // Borde de alerta
            g2.setStroke(new BasicStroke(2));
            g2.setColor(new Color(255, 165, 0)); // Naranja
            g2.drawRect(1, 1, w - 3, h - 3);

        } else if (estado == EstadoCasilla.HUNDIDA) {
            // Dibujar una X roja gruesa y oscurecer el fondo
            // Oscurecer el fondo ligeramente
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillRect(0, 0, w, h);

            // X roja gruesa
            g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(139, 0, 0)); // Rojo oscuro
            g2.drawLine(margin, margin, w - margin, h - margin);
            g2.drawLine(w - margin, margin, margin, h - margin);

            // Borde rojo
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.RED);
            g2.drawRect(1, 1, w - 3, h - 3);
        }
    }
}
