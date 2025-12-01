package views.frames;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import models.enums.OrientacionNave;
import shared.dto.TipoNaveDTO;

/**
 * Componente visual de nave que puede ser arrastrado al tablero.
 * Soporta rotacion con click derecho y drag and drop.
 *
 * @author Equipo
 */
public class NaveArrastrable extends JPanel implements DragGestureListener, DragSourceListener, Transferable {

    public static final DataFlavor NAVE_FLAVOR = new DataFlavor(NaveArrastrable.class, "NaveArrastrable");

    private TipoNaveDTO tipo;
    private int tamanio;
    private Color colorNave;
    private OrientacionNave orientacion;
    private boolean disponible;
    private int cantidadDisponible;
    private DragSource dragSource;

    // Listener para notificar cambios de orientacion
    private OrientacionChangeListener orientacionListener;

    public NaveArrastrable(TipoNaveDTO tipo, int tamanio, Color color, int cantidadDisponible) {
        this.tipo = tipo;
        this.tamanio = tamanio;
        this.colorNave = color;
        this.orientacion = OrientacionNave.HORIZONTAL;
        this.cantidadDisponible = cantidadDisponible;
        this.disponible = cantidadDisponible > 0;

        initComponente();
        initDragAndDrop();
        initMouseListeners();
    }

    private void initComponente() {
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText(getNombreTipo() + " - Click derecho para rotar");
        actualizarLayout();
    }

    private void actualizarLayout() {
        removeAll();

        int filas = orientacion == OrientacionNave.VERTICAL ? tamanio : 1;
        int cols = orientacion == OrientacionNave.HORIZONTAL ? tamanio : 1;

        setLayout(new GridLayout(filas, cols, 1, 1));

        int cellSize = 30;
        setPreferredSize(new Dimension(cols * cellSize, filas * cellSize));

        for (int i = 0; i < tamanio; i++) {
            JPanel celda = new JPanel();
            celda.setBackground(disponible ? colorNave : Color.GRAY);
            celda.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            add(celda);
        }

        revalidate();
        repaint();
    }

    private void initDragAndDrop() {
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);
    }

    private void initMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Click derecho para rotar
                if (e.getButton() == MouseEvent.BUTTON3 && disponible) {
                    rotar();
                }
                // Click izquierdo para seleccionar (notifica al padre)
                if (e.getButton() == MouseEvent.BUTTON1 && disponible) {
                    firePropertyChange("naveSeleccionada", null, NaveArrastrable.this);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (disponible) {
                    setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(null);
            }
        });
    }

    public void rotar() {
        if (!disponible) return;

        orientacion = (orientacion == OrientacionNave.HORIZONTAL) ?
                OrientacionNave.VERTICAL : OrientacionNave.HORIZONTAL;
        actualizarLayout();

        if (orientacionListener != null) {
            orientacionListener.onOrientacionChanged(orientacion);
        }
    }

    public void decrementarDisponible() {
        cantidadDisponible--;
        if (cantidadDisponible <= 0) {
            disponible = false;
            actualizarLayout();
            setCursor(Cursor.getDefaultCursor());
        }
    }

    public String getNombreTipo() {
        switch (tipo) {
            case PORTAAVIONES: return "Portaaviones";
            case CRUCERO: return "Crucero";
            case SUBMARINO: return "Submarino";
            case BARCO: return "Barco";
            default: return "Nave";
        }
    }

    // Getters
    public TipoNaveDTO getTipo() { return tipo; }
    public int getTamanio() { return tamanio; }
    public Color getColorNave() { return colorNave; }
    public OrientacionNave getOrientacion() { return orientacion; }
    public boolean isDisponible() { return disponible; }
    public int getCantidadDisponible() { return cantidadDisponible; }

    public void setOrientacionListener(OrientacionChangeListener listener) {
        this.orientacionListener = listener;
    }

    /**
     * Establece la orientacion sin disparar el listener.
     * Usado para sincronizar desde el tablero sin causar ciclos.
     * @param nuevaOrientacion la orientacion a establecer
     */
    public void setOrientacion(OrientacionNave nuevaOrientacion) {
        if (!disponible || nuevaOrientacion == this.orientacion) return;
        this.orientacion = nuevaOrientacion;
        actualizarLayout();
    }

    // === DragGestureListener ===
    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        if (!disponible) return;
        dragSource.startDrag(dge, DragSource.DefaultCopyDrop, this, this);
    }

    // === DragSourceListener ===
    @Override
    public void dragEnter(DragSourceDragEvent dsde) {
        dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
    }

    @Override
    public void dragOver(DragSourceDragEvent dsde) {}

    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {}

    @Override
    public void dragExit(DragSourceEvent dse) {
        dse.getDragSourceContext().setCursor(DragSource.DefaultCopyNoDrop);
    }

    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {}

    // === Transferable ===
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{NAVE_FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(NAVE_FLAVOR);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(NAVE_FLAVOR)) {
            return this;
        }
        throw new UnsupportedFlavorException(flavor);
    }

    // Interface para notificar cambios de orientacion
    public interface OrientacionChangeListener {
        void onOrientacionChanged(OrientacionNave nuevaOrientacion);
    }
}
