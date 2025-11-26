package views.frames;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIHelper {

    public static JButton crearBotonPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(UIConstants.FONT_BOTON);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));

        agregarEfectoHover(btn);
        return btn;
    }

    public static JButton crearBotonExito(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(UIConstants.FONT_BOTON);
        btn.setBackground(Color.BLACK);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));

        agregarEfectoHoverInvertido(btn);
        return btn;
    }

    public static JButton crearBotonPeligro(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(UIConstants.FONT_BOTON);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));

        agregarEfectoHover(btn);
        return btn;
    }

    public static JButton crearBotonSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(UIConstants.FONT_BOTON);
        btn.setBackground(new Color(240, 240, 240));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));

        agregarEfectoHover(btn);
        return btn;
    }

    public static void personalizarBoton(JButton btn, Color colorFondo) {
        btn.setFont(UIConstants.FONT_BOTON);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));

        agregarEfectoHover(btn);
    }

    private static void agregarEfectoHover(JButton btn) {
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(230, 230, 230));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });
    }

    private static void agregarEfectoHoverInvertido(JButton btn) {
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(60, 60, 60));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.BLACK);
            }
        });
    }

    public static JLabel crearTitulo(String texto) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(UIConstants.FONT_TITULO_GRANDE);
        label.setForeground(Color.BLACK);
        return label;
    }

    public static JLabel crearSubtitulo(String texto) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(UIConstants.FONT_TITULO_MEDIO);
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    public static JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(UIConstants.FONT_TEXTO_NORMAL);
        label.setForeground(Color.BLACK);
        return label;
    }

    public static JTextField crearTextField() {
        JTextField textField = new JTextField();
        textField.setFont(UIConstants.FONT_TEXTO_NORMAL);
        textField.setBackground(Color.WHITE);
        textField.setForeground(Color.BLACK);
        textField.setCaretColor(Color.BLACK);
        textField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return textField;
    }

    public static JPanel crearPanelConBorde(String titulo) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            titulo,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            UIConstants.FONT_TITULO_PEQUENO,
            Color.BLACK
        ));
        return panel;
    }

    public static JPanel crearPanelFondo() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        return panel;
    }

    public static JSeparator crearSeparador() {
        JSeparator separator = new JSeparator();
        separator.setForeground(Color.GRAY);
        separator.setBackground(Color.GRAY);
        return separator;
    }

    public static void aplicarTemaOscuro(JFrame frame) {
        frame.getContentPane().setBackground(Color.WHITE);
    }

    public static void aplicarTemaOscuro(JPanel panel) {
        panel.setBackground(Color.WHITE);
    }

    public static JCheckBox crearCheckBox(String texto, Color colorFondo) {
        JCheckBox checkBox = new JCheckBox(texto);
        checkBox.setFont(UIConstants.FONT_TEXTO_NORMAL);
        checkBox.setBackground(Color.WHITE);
        checkBox.setForeground(Color.BLACK);
        checkBox.setFocusPainted(false);
        checkBox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return checkBox;
    }

    public static JPanel crearPanelIndicadorEstado(boolean conectado) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(20, 20));
        panel.setBorder(new LineBorder(Color.BLACK, 2));

        if (conectado) {
            panel.setBackground(Color.BLACK);
        } else {
            panel.setBackground(Color.WHITE);
        }

        return panel;
    }

    public static void centrarVentana(Window window) {
        window.setLocationRelativeTo(null);
    }

    public static void mostrarMensajeExito(Component parent, String mensaje) {
        JOptionPane.showMessageDialog(
            parent,
            mensaje,
            "Exito",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void mostrarMensajeError(Component parent, String mensaje) {
        JOptionPane.showMessageDialog(
            parent,
            mensaje,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public static boolean mostrarConfirmacion(Component parent, String mensaje) {
        int resultado = JOptionPane.showConfirmDialog(
            parent,
            mensaje,
            "Confirmacion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return resultado == JOptionPane.YES_OPTION;
    }
}
