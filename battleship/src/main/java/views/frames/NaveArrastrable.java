package views.frames;

import models.enums.OrientacionNave;
import models.enums.TipoNave;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NaveArrastrable extends JPanel {

    private TipoNave tipo;
    private int tamanio;
    private OrientacionNave orientacion;
    private Color colorNave;
    private boolean colocada;
    private Point posicionOriginal;

    public NaveArrastrable(TipoNave tipo, Color colorNave) {
        this.tipo = tipo;
        this.tamanio = obtenerTamanio(tipo);
        this.orientacion = OrientacionNave.HORIZONTAL;
        this.colorNave = colorNave;
        this.colocada = false;

        configurarComponente();
        configurarInteraccion();
    }

    private int obtenerTamanio(TipoNave tipo) {
        return switch (tipo) {
            case PORTAAVIONES -> 4;
            case CRUCERO -> 3;
            case SUBMARINO -> 2;
            case BARCO -> 1;
        };
    }

    private void configurarComponente() {
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        actualizarDimensiones();
    }

    private void actualizarDimensiones() {
        int ancho, alto;
        int tamCasilla = 35;

        if (orientacion == OrientacionNave.HORIZONTAL) {
            ancho = tamanio * tamCasilla;
            alto = tamCasilla;
        } else {
            ancho = tamCasilla;
            alto = tamanio * tamCasilla;
        }

        setPreferredSize(new Dimension(ancho, alto));
        setSize(ancho, alto);
        revalidate();
        repaint();
    }

    private void configurarInteraccion() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !colocada) {
                    rotarNave();
                }
            }
        });
    }

    public void rotarNave() {
        if (orientacion == OrientacionNave.HORIZONTAL) {
            orientacion = OrientacionNave.VERTICAL;
        } else {
            orientacion = OrientacionNave.HORIZONTAL;
        }
        actualizarDimensiones();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int tamCasilla = 35;

        for (int i = 0; i < tamanio; i++) {
            int x, y;
            if (orientacion == OrientacionNave.HORIZONTAL) {
                x = i * tamCasilla;
                y = 0;
            } else {
                x = 0;
                y = i * tamCasilla;
            }

            g2d.setColor(colorNave);
            g2d.fillRoundRect(x + 2, y + 2, tamCasilla - 4, tamCasilla - 4, 8, 8);

            g2d.setColor(colorNave.darker());
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x + 2, y + 2, tamCasilla - 4, tamCasilla - 4, 8, 8);

            g2d.setColor(new Color(255, 255, 255, 80));
            g2d.fillRoundRect(x + 4, y + 4, tamCasilla - 12, (tamCasilla - 4) / 3, 4, 4);
        }

        g2d.dispose();
    }

    public TipoNave getTipo() {
        return tipo;
    }

    public int getTamanio() {
        return tamanio;
    }

    public OrientacionNave getOrientacion() {
        return orientacion;
    }

    public void setOrientacion(OrientacionNave orientacion) {
        this.orientacion = orientacion;
        actualizarDimensiones();
    }

    public boolean isColocada() {
        return colocada;
    }

    public void setColocada(boolean colocada) {
        this.colocada = colocada;
        if (colocada) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } else {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }

    public Point getPosicionOriginal() {
        return posicionOriginal;
    }

    public void setPosicionOriginal(Point posicionOriginal) {
        this.posicionOriginal = posicionOriginal;
    }

    public Color getColorNave() {
        return colorNave;
    }

    public String getNombreTipo() {
        return switch (tipo) {
            case PORTAAVIONES -> "Portaaviones";
            case CRUCERO -> "Crucero";
            case SUBMARINO -> "Submarino";
            case BARCO -> "Barco";
        };
    }
}
