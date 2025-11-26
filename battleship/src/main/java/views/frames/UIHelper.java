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
        btn.setBackground(UIConstants.COLOR_ACENTO_AZUL);
        btn.setForeground(UIConstants.COLOR_TEXTO_PRINCIPAL);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UIConstants.COLOR_ACENTO_AZUL.darker(), 2, true),
            BorderFactory.createEmptyBorder(12, 30, 12, 30)
        ));

        agregarEfectoHover(btn, UIConstants.COLOR_ACENTO_AZUL);
        return btn;
    }

    public static JButton crearBotonExito(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(UIConstants.FONT_BOTON);
        btn.setBackground(UIConstants.COLOR_ACIERTO);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UIConstants.COLOR_ACIERTO.darker(), 2, true),
            BorderFactory.createEmptyBorder(12, 30, 12, 30)
        ));

        agregarEfectoHover(btn, UIConstants.COLOR_ACIERTO);
        return btn;
    }

    public static JButton crearBotonPeligro(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(UIConstants.FONT_BOTON);
        btn.setBackground(UIConstants.COLOR_FALLO);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UIConstants.COLOR_FALLO.darker(), 2, true),
            BorderFactory.createEmptyBorder(12, 30, 12, 30)
        ));

        agregarEfectoHover(btn, UIConstants.COLOR_FALLO);
        return btn;
    }

    public static JButton crearBotonSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(UIConstants.FONT_BOTON);
        btn.setBackground(UIConstants.COLOR_FONDO_SECUNDARIO);
        btn.setForeground(UIConstants.COLOR_TEXTO_PRINCIPAL);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UIConstants.COLOR_FONDO_SECUNDARIO.brighter(), 2, true),
            BorderFactory.createEmptyBorder(12, 30, 12, 30)
        ));

        agregarEfectoHover(btn, UIConstants.COLOR_FONDO_SECUNDARIO);
        return btn;
    }

    public static void personalizarBoton(JButton btn, Color colorFondo) {
        btn.setFont(UIConstants.FONT_BOTON);
        btn.setBackground(colorFondo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(colorFondo.darker(), 2, true),
            BorderFactory.createEmptyBorder(12, 30, 12, 30)
        ));

        agregarEfectoHover(btn, colorFondo);
    }

    private static void agregarEfectoHover(JButton btn, Color colorOriginal) {
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(colorOriginal.brighter());
                btn.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(colorOriginal.brighter(), 2, true),
                    BorderFactory.createEmptyBorder(12, 30, 12, 30)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(colorOriginal);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(colorOriginal.darker(), 2, true),
                    BorderFactory.createEmptyBorder(12, 30, 12, 30)
                ));
            }
        });
    }

    public static JLabel crearTitulo(String texto) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(UIConstants.FONT_TITULO_GRANDE);
        label.setForeground(UIConstants.COLOR_ACENTO_DORADO);
        return label;
    }

    public static JLabel crearSubtitulo(String texto) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(UIConstants.FONT_TITULO_MEDIO);
        label.setForeground(UIConstants.COLOR_TEXTO_SECUNDARIO);
        return label;
    }

    public static JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(UIConstants.FONT_TEXTO_NORMAL);
        label.setForeground(UIConstants.COLOR_TEXTO_PRINCIPAL);
        return label;
    }

    public static JTextField crearTextField() {
        JTextField textField = new JTextField();
        textField.setFont(UIConstants.FONT_TEXTO_NORMAL);
        textField.setBackground(UIConstants.COLOR_FONDO_SECUNDARIO);
        textField.setForeground(UIConstants.COLOR_TEXTO_PRINCIPAL);
        textField.setCaretColor(UIConstants.COLOR_TEXTO_PRINCIPAL);
        textField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UIConstants.COLOR_ACENTO_AZUL, 2, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return textField;
    }

    public static JPanel crearPanelConBorde(String titulo) {
        JPanel panel = new JPanel();
        panel.setBackground(UIConstants.COLOR_FONDO_SECUNDARIO);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.COLOR_ACENTO_AZUL, 2),
            titulo,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            UIConstants.FONT_TITULO_PEQUENO,
            UIConstants.COLOR_TEXTO_PRINCIPAL
        ));
        return panel;
    }

    public static JPanel crearPanelFondo() {
        JPanel panel = new JPanel();
        panel.setBackground(UIConstants.COLOR_FONDO_PRINCIPAL);
        return panel;
    }

    public static JSeparator crearSeparador() {
        JSeparator separator = new JSeparator();
        separator.setForeground(UIConstants.COLOR_FONDO_SECUNDARIO);
        separator.setBackground(UIConstants.COLOR_FONDO_SECUNDARIO);
        return separator;
    }

    public static void aplicarTemaOscuro(JFrame frame) {
        frame.getContentPane().setBackground(UIConstants.COLOR_FONDO_PRINCIPAL);
    }

    public static void aplicarTemaOscuro(JPanel panel) {
        panel.setBackground(UIConstants.COLOR_FONDO_PRINCIPAL);
    }

    public static JCheckBox crearCheckBox(String texto, Color colorFondo) {
        JCheckBox checkBox = new JCheckBox(texto);
        checkBox.setFont(UIConstants.FONT_TEXTO_NORMAL);
        checkBox.setBackground(UIConstants.COLOR_FONDO_PRINCIPAL);
        checkBox.setForeground(UIConstants.COLOR_TEXTO_PRINCIPAL);
        checkBox.setFocusPainted(false);
        checkBox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(colorFondo, 3, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JPanel panelColor = new JPanel();
        panelColor.setBackground(colorFondo);
        panelColor.setPreferredSize(new Dimension(30, 30));
        panelColor.setBorder(new LineBorder(colorFondo.darker(), 2, true));

        return checkBox;
    }

    public static JPanel crearPanelIndicadorEstado(boolean conectado) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(20, 20));
        panel.setBorder(new LineBorder(Color.BLACK, 2, true));

        if (conectado) {
            panel.setBackground(UIConstants.COLOR_ACIERTO);
        } else {
            panel.setBackground(UIConstants.COLOR_FALLO);
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
            "Éxito",
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
            "Confirmación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return resultado == JOptionPane.YES_OPTION;
    }
}
