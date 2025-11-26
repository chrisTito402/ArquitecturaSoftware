package views.frames;

import dtos.CoordenadasDTO;
import javax.swing.*;
import java.awt.*;

public class CasillaTableroColocacion extends JPanel {

    private CoordenadasDTO coordenadas;
    private boolean ocupada;
    private boolean resaltada;
    private boolean valida;
    private NaveArrastrable naveAsociada;

    private static final Color COLOR_VACIA = new Color(70, 130, 180);
    private static final Color COLOR_OCUPADA = new Color(76, 175, 80);
    private static final Color COLOR_RESALTADA_VALIDA = new Color(100, 200, 100, 150);
    private static final Color COLOR_RESALTADA_INVALIDA = new Color(244, 67, 54, 150);
    private static final Color COLOR_BORDE = new Color(25, 25, 112);

    public CasillaTableroColocacion(CoordenadasDTO coordenadas) {
        this.coordenadas = coordenadas;
        this.ocupada = false;
        this.resaltada = false;
        this.valida = true;

        setPreferredSize(new Dimension(35, 35));
        setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        setOpaque(true);
        actualizarColor();
    }

    private void actualizarColor() {
        if (resaltada) {
            setBackground(valida ? COLOR_RESALTADA_VALIDA : COLOR_RESALTADA_INVALIDA);
        } else if (ocupada) {
            setBackground(COLOR_OCUPADA);
        } else {
            setBackground(COLOR_VACIA);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (ocupada && naveAsociada != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(naveAsociada.getColorNave());
            g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 6, 6);

            g2d.setColor(naveAsociada.getColorNave().darker());
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 6, 6);

            g2d.dispose();
        }
    }

    public CoordenadasDTO getCoordenadas() {
        return coordenadas;
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public void setOcupada(boolean ocupada, NaveArrastrable nave) {
        this.ocupada = ocupada;
        this.naveAsociada = nave;
        actualizarColor();
        repaint();
    }

    public void setResaltada(boolean resaltada, boolean valida) {
        this.resaltada = resaltada;
        this.valida = valida;
        actualizarColor();
    }

    public void limpiar() {
        this.ocupada = false;
        this.naveAsociada = null;
        this.resaltada = false;
        actualizarColor();
        repaint();
    }

    public NaveArrastrable getNaveAsociada() {
        return naveAsociada;
    }
}
