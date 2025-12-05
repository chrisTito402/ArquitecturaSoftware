package cliente.presentacion.componentes;

import compartido.comunicacion.dto.CoordenadasDTO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Panel para las casillas de TU tablero (no el del enemigo).
 * Aqui es donde pones tus naves y donde ves cuando te disparan.
 * Puede mostrar si una casilla esta normal, si fue impactada
 * o si ya se hundio la nave. Usamos DTO para respetar el MVC.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
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
