package views.frames;

import dtos.CoordenadasDTO;
import dtos.enums.ResultadoDisparoDTO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CasillaButton extends JButton {

    private static final Color COLOR_NO_DISPARADO = new Color(70, 130, 180);
    private static final Color COLOR_AGUA = new Color(100, 149, 237);
    private static final Color COLOR_IMPACTO = new Color(255, 140, 0);
    private static final Color COLOR_HUNDIDO = new Color(220, 20, 60);
    private static final Color COLOR_HOVER = new Color(30, 144, 255);
    private static final Color COLOR_BORDE = new Color(25, 25, 112);

    private CoordenadasDTO coordenadas;
    private EstadoCasilla estado;

    public enum EstadoCasilla {
        NO_DISPARADO,
        AGUA,
        IMPACTO,
        HUNDIDO
    }

    public CasillaButton(CoordenadasDTO coordenadas) {
        this.coordenadas = coordenadas;
        this.estado = EstadoCasilla.NO_DISPARADO;
        configurarEstilo();
        agregarEfectosHover();
    }

    private void configurarEstilo() {
        this.setPreferredSize(new Dimension(40, 40));
        this.setFont(new Font("Segoe UI", Font.BOLD, 16));
        this.setFocusPainted(false);
        this.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actualizarApariencia();
    }

    private void agregarEfectosHover() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (estado == EstadoCasilla.NO_DISPARADO) {
                    setBackground(COLOR_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                actualizarApariencia();
            }
        });
    }

    private void actualizarApariencia() {
        switch (estado) {
            case NO_DISPARADO:
                setBackground(COLOR_NO_DISPARADO);
                setText("~");
                setForeground(new Color(224, 255, 255));
                setEnabled(true);
                break;

            case AGUA:
                setBackground(COLOR_AGUA);
                setText("O");
                setForeground(Color.WHITE);
                setEnabled(false);
                break;

            case IMPACTO:
                setBackground(COLOR_IMPACTO);
                setText("X");
                setForeground(Color.WHITE);
                setEnabled(false);
                break;

            case HUNDIDO:
                setBackground(COLOR_HUNDIDO);
                setText("!");
                setEnabled(false);
                break;
        }
    }

    public void marcarResultado(ResultadoDisparoDTO resultado) {
        if (resultado == null) {
            return;
        }

        switch (resultado) {
            case AGUA:
                this.estado = EstadoCasilla.AGUA;
                break;

            case IMPACTO:
                this.estado = EstadoCasilla.IMPACTO;
                animarImpacto();
                break;

            case HUNDIMIENTO:
                this.estado = EstadoCasilla.HUNDIDO;
                animarHundimiento();
                break;

            default:
                break;
        }

        actualizarApariencia();
    }

    private void animarImpacto() {
        Timer timer = new Timer(100, null);
        final int[] contador = {0};

        timer.addActionListener(e -> {
            if (contador[0] % 2 == 0) {
                setBackground(Color.YELLOW);
            } else {
                setBackground(COLOR_IMPACTO);
            }
            contador[0]++;

            if (contador[0] >= 4) {
                timer.stop();
                actualizarApariencia();
            }
        });
        timer.start();
    }

    private void animarHundimiento() {
        Timer timer = new Timer(100, null);
        final int[] contador = {0};

        timer.addActionListener(e -> {
            if (contador[0] % 2 == 0) {
                setBackground(Color.RED);
            } else {
                setBackground(COLOR_HUNDIDO);
            }
            contador[0]++;

            if (contador[0] >= 6) {
                timer.stop();
                actualizarApariencia();
            }
        });
        timer.start();
    }

    public CoordenadasDTO getCoordenadas() {
        return coordenadas;
    }

    public EstadoCasilla getEstado() {
        return estado;
    }
}
