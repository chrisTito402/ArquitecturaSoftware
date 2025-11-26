package views.frames;

import models.entidades.Coordenadas;
import models.enums.ResultadoDisparo;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Botón mejorado para representar casillas del tablero enemigo.
 * Incluye estados visuales claros y efectos hover.
 *
 * Estados:
 * - NO_DISPARADO: Azul (agua)
 * - AGUA: Gris (disparo fallado)
 * - IMPACTO: Naranja (nave tocada)
 * - HUNDIDO: Rojo (nave hundida)
 *
 * @author daniel
 */
public class CasillaButton extends JButton {

    // Colores para estados
    private static final Color COLOR_NO_DISPARADO = new Color(70, 130, 180);     // Azul océano
    private static final Color COLOR_AGUA = new Color(100, 149, 237);            // Azul claro (miss)
    private static final Color COLOR_IMPACTO = new Color(255, 140, 0);           // Naranja (hit)
    private static final Color COLOR_HUNDIDO = new Color(220, 20, 60);           // Rojo (sunk)
    private static final Color COLOR_HOVER = new Color(30, 144, 255);            // Azul brillante
    private static final Color COLOR_BORDE = new Color(25, 25, 112);             // Azul marino oscuro

    private Coordenadas coordenadas;
    private EstadoCasilla estado;

    /**
     * Estados posibles de una casilla del tablero enemigo.
     */
    public enum EstadoCasilla {
        NO_DISPARADO,
        AGUA,
        IMPACTO,
        HUNDIDO
    }

    public CasillaButton(Coordenadas coordenadas) {
        this.coordenadas = coordenadas;
        this.estado = EstadoCasilla.NO_DISPARADO;
        configurarEstilo();
        agregarEfectosHover();
    }

    /**
     * Configura el estilo inicial del botón.
     */
    private void configurarEstilo() {
        this.setPreferredSize(new Dimension(40, 40));
        this.setFont(new Font("Segoe UI", Font.BOLD, 16));
        this.setFocusPainted(false);
        this.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actualizarApariencia();
    }

    /**
     * Agrega efectos visuales al pasar el mouse (solo si no se ha disparado).
     */
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

    /**
     * Actualiza la apariencia del botón según su estado.
     */
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
                setText("○");  // Círculo vacío (miss)
                setForeground(Color.WHITE);
                setEnabled(false);
                break;

            case IMPACTO:
                setBackground(COLOR_IMPACTO);
                setText("✖");  // X (hit)
                setForeground(Color.WHITE);
                setEnabled(false);
                break;

            case HUNDIDO:
                setBackground(COLOR_HUNDIDO);
                setText("💥"); // Explosión (sunk)
                setEnabled(false);
                break;
        }
    }

    /**
     * Marca la casilla con el resultado del disparo y anima el cambio.
     *
     * @param resultado El resultado del disparo
     */
    public void marcarResultado(ResultadoDisparo resultado) {
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

    /**
     * Animación de impacto (parpadeo naranja).
     */
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

    /**
     * Animación de hundimiento (parpadeo rojo intenso).
     */
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

    public Coordenadas getCoordenadas() {
        return coordenadas;
    }

    public EstadoCasilla getEstado() {
        return estado;
    }
}
