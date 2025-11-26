package views.frames;

import java.awt.*;

/**
 * Constantes y utilidades para el tema visual del juego Battleship.
 * Centraliza colores, fuentes y dimensiones para mantener consistencia.
 *
 * Tema: Naval/Marítimo con tonos azules oscuros y acentos dorados.
 *
 * @author daniel
 */
public class UIConstants {

    // ========== COLORES DEL TEMA ==========

    // Fondos
    public static final Color COLOR_FONDO_PRINCIPAL = new Color(26, 35, 53);      // Azul oscuro marino
    public static final Color COLOR_FONDO_SECUNDARIO = new Color(33, 47, 73);    // Azul medio
    public static final Color COLOR_FONDO_HEADER = new Color(33, 47, 73);        // Header panels

    // Texto
    public static final Color COLOR_TEXTO_PRINCIPAL = new Color(236, 240, 245);  // Blanco suave
    public static final Color COLOR_TEXTO_SECUNDARIO = new Color(189, 197, 209); // Gris claro

    // Acentos
    public static final Color COLOR_ACENTO_DORADO = new Color(255, 193, 7);      // Dorado (puntos)
    public static final Color COLOR_ACENTO_AZUL = new Color(33, 150, 243);       // Azul brillante

    // Estados de juego
    public static final Color COLOR_ACIERTO = new Color(76, 175, 80);            // Verde (hit)
    public static final Color COLOR_FALLO = new Color(244, 67, 54);              // Rojo (miss)
    public static final Color COLOR_HUNDIDO = new Color(255, 152, 0);            // Naranja (sunk)
    public static final Color COLOR_AGUA = new Color(70, 130, 180);              // Azul océano

    // Temporizador
    public static final Color COLOR_TIEMPO_NORMAL = new Color(76, 175, 80);      // Verde (>15s)
    public static final Color COLOR_TIEMPO_ADVERTENCIA = new Color(255, 193, 7); // Amarillo (10-15s)
    public static final Color COLOR_TIEMPO_URGENTE = new Color(244, 67, 54);     // Rojo (<10s)

    // Casillas del tablero
    public static final Color COLOR_CASILLA_NO_DISPARADO = new Color(70, 130, 180);  // Azul océano
    public static final Color COLOR_CASILLA_AGUA = new Color(100, 149, 237);         // Azul claro
    public static final Color COLOR_CASILLA_IMPACTO = new Color(255, 140, 0);        // Naranja
    public static final Color COLOR_CASILLA_HUNDIDO = new Color(220, 20, 60);        // Rojo crimson
    public static final Color COLOR_CASILLA_HOVER = new Color(30, 144, 255);         // Azul brillante
    public static final Color COLOR_CASILLA_BORDE = new Color(25, 25, 112);          // Azul marino oscuro

    // ========== FUENTES ==========

    public static final Font FONT_TITULO_GRANDE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_TITULO_MEDIO = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_TITULO_PEQUENO = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_TEXTO_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_TEXTO_PEQUENO = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BOTON = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_TIMER = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_PUNTAJE = new Font("Segoe UI", Font.BOLD, 28);

    // ========== DIMENSIONES ==========

    public static final Dimension DIM_CASILLA = new Dimension(40, 40);
    public static final Dimension DIM_PANEL_PUNTAJE = new Dimension(220, 280);
    public static final Dimension DIM_PANEL_TIMER = new Dimension(180, 70);
    public static final Dimension DIM_BOTON_STANDAR = new Dimension(120, 40);

    // ========== BORDES ==========

    public static final int BORDE_GROSOR_NORMAL = 1;
    public static final int BORDE_GROSOR_DESTACADO = 2;
    public static final int PADDING_PEQUENO = 5;
    public static final int PADDING_MEDIO = 8;
    public static final int PADDING_GRANDE = 12;

    // ========== ICONOS (EMOJIS) ==========

    public static final String ICONO_ESTRELLA = "⭐";
    public static final String ICONO_TIMER = "⏱";
    public static final String ICONO_ACIERTO = "✓";
    public static final String ICONO_FALLO = "✗";
    public static final String ICONO_EXPLOSION = "💥";
    public static final String ICONO_PRECISION = "🎯";
    public static final String ICONO_AGUA = "~";
    public static final String ICONO_CIRCULO = "○";
    public static final String ICONO_X = "✖";

    // ========== TIEMPOS DE ANIMACIÓN (ms) ==========

    public static final int ANIM_DURACION_CORTA = 100;
    public static final int ANIM_DURACION_MEDIA = 150;
    public static final int ANIM_DURACION_LARGA = 200;
    public static final int ANIM_REPETICIONES_IMPACTO = 4;
    public static final int ANIM_REPETICIONES_HUNDIMIENTO = 6;

    // ========== UMBRALES DE PRECISIÓN ==========

    public static final int PRECISION_ALTA = 70;      // >= 70% es precisión alta (verde)
    public static final int PRECISION_MEDIA = 40;     // 40-70% es precisión media (naranja)
                                                      // < 40% es precisión baja (rojo)

    // ========== UMBRALES DE TIEMPO ==========

    public static final int TIEMPO_NORMAL = 15;       // > 15s tiempo normal (verde)
    public static final int TIEMPO_ADVERTENCIA = 10;  // 10-15s advertencia (amarillo)
                                                      // < 10s urgente (rojo)
    public static final int TIEMPO_PARPADEO = 5;      // <= 5s empieza parpadeo

    /**
     * Constructor privado para prevenir instanciación.
     */
    private UIConstants() {
        throw new AssertionError("No se puede instanciar esta clase de constantes");
    }

    /**
     * Obtiene el color de la barra de precisión según el valor.
     *
     * @param precision Valor de precisión (0-100)
     * @return Color correspondiente al nivel de precisión
     */
    public static Color getColorPrecision(double precision) {
        if (precision >= PRECISION_ALTA) {
            return COLOR_ACIERTO;
        } else if (precision >= PRECISION_MEDIA) {
            return COLOR_HUNDIDO;
        } else {
            return COLOR_FALLO;
        }
    }

    /**
     * Obtiene el color del temporizador según el tiempo restante.
     *
     * @param tiempoRestante Tiempo restante en segundos
     * @return Color correspondiente al nivel de urgencia
     */
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
