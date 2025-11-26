package views.frames;

import java.awt.*;

public class UIConstants {

    public static final Color COLOR_FONDO_PRINCIPAL = Color.WHITE;
    public static final Color COLOR_FONDO_SECUNDARIO = new Color(245, 245, 245);
    public static final Color COLOR_FONDO_HEADER = new Color(240, 240, 240);

    public static final Color COLOR_TEXTO_PRINCIPAL = Color.BLACK;
    public static final Color COLOR_TEXTO_SECUNDARIO = new Color(100, 100, 100);

    public static final Color COLOR_ACENTO_DORADO = Color.BLACK;
    public static final Color COLOR_ACENTO_AZUL = new Color(60, 60, 60);

    public static final Color COLOR_ACIERTO = new Color(40, 40, 40);
    public static final Color COLOR_FALLO = new Color(80, 80, 80);
    public static final Color COLOR_HUNDIDO = Color.BLACK;
    public static final Color COLOR_AGUA = new Color(200, 200, 200);

    public static final Color COLOR_TIEMPO_NORMAL = new Color(60, 60, 60);
    public static final Color COLOR_TIEMPO_ADVERTENCIA = new Color(100, 100, 100);
    public static final Color COLOR_TIEMPO_URGENTE = Color.BLACK;

    public static final Color COLOR_CASILLA_NO_DISPARADO = new Color(230, 230, 230);
    public static final Color COLOR_CASILLA_AGUA = new Color(180, 180, 180);
    public static final Color COLOR_CASILLA_IMPACTO = new Color(100, 100, 100);
    public static final Color COLOR_CASILLA_HUNDIDO = Color.BLACK;
    public static final Color COLOR_CASILLA_HOVER = new Color(200, 200, 200);
    public static final Color COLOR_CASILLA_BORDE = Color.BLACK;

    public static final Font FONT_TITULO_GRANDE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_TITULO_MEDIO = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_TITULO_PEQUENO = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_TEXTO_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_TEXTO_PEQUENO = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BOTON = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_TIMER = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_PUNTAJE = new Font("Segoe UI", Font.BOLD, 28);

    public static final Dimension DIM_CASILLA = new Dimension(40, 40);
    public static final Dimension DIM_PANEL_PUNTAJE = new Dimension(220, 280);
    public static final Dimension DIM_PANEL_TIMER = new Dimension(180, 70);
    public static final Dimension DIM_BOTON_STANDAR = new Dimension(120, 40);

    public static final int BORDE_GROSOR_NORMAL = 1;
    public static final int BORDE_GROSOR_DESTACADO = 2;
    public static final int PADDING_PEQUENO = 5;
    public static final int PADDING_MEDIO = 8;
    public static final int PADDING_GRANDE = 12;

    public static final String ICONO_ESTRELLA = "*";
    public static final String ICONO_TIMER = "[T]";
    public static final String ICONO_ACIERTO = "X";
    public static final String ICONO_FALLO = "O";
    public static final String ICONO_EXPLOSION = "!";
    public static final String ICONO_PRECISION = "#";
    public static final String ICONO_AGUA = "~";
    public static final String ICONO_CIRCULO = "o";
    public static final String ICONO_X = "X";

    public static final int ANIM_DURACION_CORTA = 100;
    public static final int ANIM_DURACION_MEDIA = 150;
    public static final int ANIM_DURACION_LARGA = 200;
    public static final int ANIM_REPETICIONES_IMPACTO = 4;
    public static final int ANIM_REPETICIONES_HUNDIMIENTO = 6;

    public static final int PRECISION_ALTA = 70;
    public static final int PRECISION_MEDIA = 40;

    public static final int TIEMPO_NORMAL = 15;
    public static final int TIEMPO_ADVERTENCIA = 10;
    public static final int TIEMPO_PARPADEO = 5;

    private UIConstants() {
        throw new AssertionError("No se puede instanciar esta clase de constantes");
    }

    public static Color getColorPrecision(double precision) {
        if (precision >= PRECISION_ALTA) {
            return COLOR_ACIERTO;
        } else if (precision >= PRECISION_MEDIA) {
            return COLOR_TEXTO_SECUNDARIO;
        } else {
            return COLOR_FALLO;
        }
    }

    public static Color getColorTiempo(int tiempoRestante) {
        if (tiempoRestante > TIEMPO_NORMAL) {
            return COLOR_TIEMPO_NORMAL;
        } else if (tiempoRestante > TIEMPO_ADVERTENCIA) {
            return COLOR_TIEMPO_ADVERTENCIA;
        } else {
            return COLOR_TIEMPO_URGENTE;
        }
    }
}
