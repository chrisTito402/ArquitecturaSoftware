package views.frames;

import controllers.controller.ControlVista;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import models.entidades.Coordenadas;
import views.DTOs.TipoNaveDTO;

/**
 *
 * @author daniel
 */
public class AddNavePanel extends JPanel {
    // Configuración
    private final int FILAS = 10;
    private final int COLS = 10;
    private final int SIZE = 50; // Tamaño de cada celda
    private final int SIDEBAR_WIDTH = 150; // Espacio extra a la derecha para las naves

    private boolean estaArrastrando = false;

    // Lista para guardar nuestras celdas de la cuadrícula
    private ArrayList<Celda> grid = new ArrayList<>();

    // LISTA de cuadrados disponibles para arrastrar (Barcos en el puerto)
    private ArrayList<Rectangle> navesDisponibles = new ArrayList<>();

    // La nave que estamos arrastrando actualmente (puede ser null si no arrastramos nada)
    private Rectangle naveSeleccionada = null;

    private int offsetX, offsetY;

    public AddNavePanel() {
        // Aumentamos el ancho para incluir la barra lateral
        this.setPreferredSize(new Dimension((COLS * SIZE) + SIDEBAR_WIDTH, FILAS * SIZE));
        this.setBackground(Color.WHITE);

        // 1. Inicializar la cuadrícula
        for (int y = 0; y < FILAS; y++) {
            for (int x = 0; x < COLS; x++) {
                grid.add(new Celda(x * SIZE, y * SIZE, SIZE, new Coordenadas(y, x)));
            }
        }

        // 2. Crear las naves iniciales a un lado (en la zona del SIDEBAR)
        // Por ejemplo, creamos 3 cuadrados
        int startX = (COLS * SIZE) + 50; // Posición X fuera de la cuadrícula
        navesDisponibles.add(new Rectangle(startX, 50, 40, 40));
        navesDisponibles.add(new Rectangle(startX, 50, 40, 40));
        navesDisponibles.add(new Rectangle(startX, 50, 40, 40));

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Revisar si dimos click en alguna de las naves disponibles
                for (Rectangle nave : navesDisponibles) {
                    if (nave.contains(e.getPoint())) {
                        naveSeleccionada = nave; // "Agarramos" esta nave
                        estaArrastrando = true;
                        offsetX = e.getX() - nave.x;
                        offsetY = e.getY() - nave.y;
                        break; // Ya encontramos una, dejamos de buscar
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (estaArrastrando && naveSeleccionada != null) {
                    
                    // 1. "Pintar" permanentemente las celdas afectadas
                    List<Coordenadas> coordenadas = new ArrayList<>();
                    for (Celda c : grid) {
                        if (c.isOverlapped) {
                            coordenadas.add(c.coordenadas);
//                            c.isPintada = true; // SE QUEDA PINTADO PARA SIEMPRE
                            System.out.println("Pintada celda: " + c.coordenadas);
                        }
                    }
                    
                    ControlVista.getInstancia().addNave(TipoNaveDTO.BARCO, coordenadas);
                    
                }

                // Resetear variables
                estaArrastrando = false;
                limpiarOverlaps(); // Limpiar cualquier rastro rojo
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (estaArrastrando && naveSeleccionada != null) {
                    int nuevoX = e.getX() - offsetX;
                    int nuevoY = e.getY() - offsetY;
                    naveSeleccionada.setLocation(nuevoX, nuevoY);

                    checkCollisions(); // Revisar vista previa
                }
            }
        };

        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
    }

    // Actualiza qué celdas están siendo tocadas (Vista previa ROJA)
    private void checkCollisions() {
        if (naveSeleccionada == null) return;

        for (Celda celda : grid) {
            if (naveSeleccionada.intersects(celda.rect)) {
                celda.isOverlapped = true;
            } else {
                celda.isOverlapped = false;
            }
        }
        repaint();
    }

    public void limpiarOverlaps() {
        grid.forEach(c -> c.isOverlapped = false);
    }
    
    public void pintarNaveAñadida(List<Coordenadas> coordenadas) {
        coordenadas.forEach(cord -> {
            Celda celda = grid.stream()
                    .filter(c -> c.coordenadas.getX() == cord.getX() &&
                            c.coordenadas.getY() == cord.getY())
                    .findFirst()
                    .orElse(null);
            
            if (celda != null) {
                celda.isPintada = true; // SE QUEDA PINTADO PARA SIEMPRE
                System.out.println("Pintada celda: " + celda.coordenadas);
                
                navesDisponibles.remove(naveSeleccionada);
                naveSeleccionada = null;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. Dibujar línea divisoria del menú lateral
        g.setColor(Color.BLACK);
        g.drawLine(COLS * SIZE, 0, COLS * SIZE, getHeight());

        // 2. Dibujar la cuadrícula
        for (Celda celda : grid) {
            if (celda.isPintada) {
                // ESTADO PERMANENTE (YA SOLTADO)
                g.setColor(Color.DARK_GRAY);
                g.fillRect(celda.rect.x, celda.rect.y, SIZE, SIZE);
                g.setColor(Color.BLACK); // Borde
                g.drawRect(celda.rect.x, celda.rect.y, SIZE, SIZE);
            } 
            else if (celda.isOverlapped) {
                // ESTADO TEMPORAL (VISTA PREVIA AL ARRASTRAR)
                g.setColor(new Color(255, 100, 100)); // Rojo suave
                g.fillRect(celda.rect.x, celda.rect.y, SIZE, SIZE);
            } 
            else {
                // ESTADO NORMAL (VACÍO)
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(celda.rect.x, celda.rect.y, SIZE, SIZE);
            }
        }

        // 3. Dibujar las naves disponibles (las que están a un lado o siendo arrastradas)
        g.setColor(Color.BLUE);
        // Usamos un iterador o un for simple para evitar errores de concurrencia si borramos muy rápido
        for (Rectangle nave : navesDisponibles) {
            g.fillRect(nave.x, nave.y, nave.width, nave.height);
            g.setColor(Color.BLACK);
            g.drawRect(nave.x, nave.y, nave.width, nave.height);
            g.setColor(Color.BLUE); // Restaurar color para el siguiente fill
        }
    }

    // Clase auxiliar
    private class Celda {
        Rectangle rect;
        boolean isOverlapped = false; // Temporal (mouse encima)
        boolean isPintada = false;    // Permanente (ya se soltó una nave aquí)
        Coordenadas coordenadas;

        public Celda(int x, int y, int size, Coordenadas coordenadas) {
            this.rect = new Rectangle(x, y, size, size);
            this.coordenadas = coordenadas;
        }
    }
}
