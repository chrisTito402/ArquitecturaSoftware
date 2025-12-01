package views.frames;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import models.entidades.Coordenadas;

/**
 *
 * @author daniel
 */
public class AddNavePanel extends JPanel {
    // Configuración
    private final int FILAS = 10;
    private final int COLS = 10;
    private final int SIZE = 50; // Tamaño de cada celda (50x50 px)
    
    private boolean estaArrastrando = false;
    
    // Lista para guardar nuestras celdas de la cuadrícula
    private ArrayList<Celda> grid = new ArrayList<>();
    
    // El cuadrado que movemos (El "Jugador")
    private Rectangle playerRect;
    
    private int offsetX, offsetY;

    public AddNavePanel() {
        this.setPreferredSize(new Dimension(COLS * SIZE, FILAS * SIZE));
        this.setBackground(Color.WHITE);

        // 1. Inicializar la cuadrícula
        // Creamos 100 objetos "Celda" y los guardamos
        for (int y = 0; y < FILAS; y++) {
            for (int x = 0; x < COLS; x++) {
                grid.add(new Celda(x * SIZE, y * SIZE, SIZE, new Coordenadas(y, x)));
            }
        }

        // 2. Inicializar el jugador (empezamos fuera de pantalla o en 0,0)
        playerRect = new Rectangle(0, 0, 30, 30); // El jugador es más pequeño (30x30)

        
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (playerRect.contains(e.getPoint())) {
                    estaArrastrando = true;
                    offsetX = e.getX() - playerRect.x;
                    offsetY = e.getY() - playerRect.y;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                estaArrastrando = false;
                
                playerRect.x = 0;
                playerRect.y = 0;
                
                grid.forEach(c -> {
                    if (c.isOverlapped) {
                        System.out.println(c.coordenadas);
                    }
                });
                
                limpiarCeldas();
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (estaArrastrando) {
                    int nuevoX = e.getX() - offsetX;
                    int nuevoY = e.getY() - offsetY;
                    playerRect.setLocation(nuevoX, nuevoY);
                    
                    updatePlayerPosition(e.getX(), e.getY());
                }
            }
        };
        
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
        
        // 3. Añadir el detector de movimiento del mouse
//        this.addMouseMotionListener(new MouseMotionAdapter() {
//            @Override
//            public void mouseMoved(MouseEvent e) {
//                updatePlayerPosition(e.getX(), e.getY());
//            }
//        });
    }

    // Actualiza posición y verifica colisiones
    private void updatePlayerPosition(int mouseX, int mouseY) {
        // Centrar el cuadrado en el mouse
        playerRect.x = mouseX - (playerRect.width / 2);
        playerRect.y = mouseY - (playerRect.height / 2);

        // --- AQUÍ ESTÁ LA MAGIA ---
        // Revisamos CADA celda de la cuadrícula
        for (Celda celda : grid) {
            // Usamos el método 'intersects' nativo de Java
            if (playerRect.intersects(celda.rect)) {
                celda.isOverlapped = true;
            } else {
                celda.isOverlapped = false;
            }
        }
        
        // Redibujar la pantalla
        repaint();
    }

    public void limpiarCeldas() {
        grid.forEach(c -> c.isOverlapped = false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Dibujar la cuadrícula
        for (Celda celda : grid) {
            if (celda.isOverlapped) {
                g.setColor(Color.RED); // ¡Colisión detectada!
                g.fillRect(celda.rect.x, celda.rect.y, SIZE, SIZE);
            } else {
                g.setColor(Color.LIGHT_GRAY); // Estado normal
                g.drawRect(celda.rect.x, celda.rect.y, SIZE, SIZE);
            }
        }

        // Dibujar el jugador
        g.setColor(Color.BLUE);
        g.fillRect(playerRect.x, playerRect.y, playerRect.width, playerRect.height);
    }

    // Clase auxiliar para guardar datos de cada celda
    private class Celda {
        Rectangle rect;
        boolean isOverlapped = false;
        boolean isOcupada = false;
        Coordenadas coordenadas;

        public Celda(int x, int y, int size, Coordenadas coordenadas) {
            // Guardamos la posición lógica
            this.rect = new Rectangle(x, y, size, size);
            this.coordenadas = coordenadas;
        }
    }
}
