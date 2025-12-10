package cliente.presentacion.componentes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * El panelcito que te dice si es tu turno o del otro.
 * Se pone verde cuando es tu turno y rojo/rosa cuando le toca al rival.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public class IndicadorTurnoPanel extends JPanel {

    private JLabel lblTurno;
    private JLabel lblNombreJugador;
    private boolean esMiTurno;

    public IndicadorTurnoPanel() {
        initComponents();
        setEsMiTurno(false); // Por defecto no es mi turno
    }

    private void initComponents() {
        this.setLayout(new GridLayout(2, 1, 5, 5));
        this.setPreferredSize(new Dimension(200, 60));
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        lblTurno = new JLabel("TURNO", SwingConstants.CENTER);
        lblTurno.setFont(new Font("Segoe UI", Font.BOLD, 14));

        lblNombreJugador = new JLabel("Esperando...", SwingConstants.CENTER);
        lblNombreJugador.setFont(new Font("Segoe UI", Font.BOLD, 16));

        this.add(lblTurno);
        this.add(lblNombreJugador);
    }

    /**
     * Establece si es el turno del jugador local.
     * @param esMiTurno true si es mi turno, false si es del oponente
     */
    public void setEsMiTurno(boolean esMiTurno) {
        this.esMiTurno = esMiTurno;

        if (esMiTurno) {
            this.setBackground(new Color(144, 238, 144)); // Verde claro
            lblTurno.setForeground(new Color(0, 100, 0));
            lblNombreJugador.setText("¡TU TURNO!");
            lblNombreJugador.setForeground(new Color(0, 100, 0));
        } else {
            this.setBackground(new Color(255, 182, 193)); // Rosa claro
            lblTurno.setForeground(new Color(139, 0, 0));
            lblNombreJugador.setText("Turno del oponente");
            lblNombreJugador.setForeground(new Color(139, 0, 0));
        }

        this.revalidate();
        this.repaint();
    }

    /**
     * Establece el nombre del jugador en turno.
     * @param nombre Nombre del jugador
     * @param esMiTurno true si es el turno del jugador local
     */
    public void setTurno(String nombre, boolean esMiTurno) {
        this.esMiTurno = esMiTurno;

        if (esMiTurno) {
            this.setBackground(new Color(144, 238, 144)); // Verde claro
            lblTurno.setForeground(new Color(0, 100, 0));
            lblNombreJugador.setText("¡TU TURNO!");
            lblNombreJugador.setForeground(new Color(0, 100, 0));
        } else {
            this.setBackground(new Color(255, 182, 193)); // Rosa claro
            lblTurno.setForeground(new Color(139, 0, 0));
            lblNombreJugador.setText("Turno de: " + nombre);
            lblNombreJugador.setForeground(new Color(139, 0, 0));
        }

        this.revalidate();
        this.repaint();
    }

    /**
     * Indica si actualmente es el turno del jugador local.
     */
    public boolean isEsMiTurno() {
        return esMiTurno;
    }
}
