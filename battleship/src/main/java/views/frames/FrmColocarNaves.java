package views.frames;

import controllers.controller.ControlVista;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import models.entidades.Coordenadas;
import models.enums.EstadoNave;
import models.enums.OrientacionNave;
import models.observador.ISuscriptor;
import shared.dto.NaveDTO;
import shared.dto.TipoNaveDTO;
import shared.dto.CoordenadasDTO;

/**
 * Pantalla para colocar las naves antes de iniciar la partida.
 * Permite seleccionar naves del "astillero" y colocarlas en el tablero.
 * Soporta drag and drop y rotacion con click derecho.
 *
 * @author Equipo
 */
public class FrmColocarNaves extends JFrame implements DropTargetListener, PropertyChangeListener, ISuscriptor {

    // Constantes del tablero
    private static final int FILAS = 10;
    private static final int COLUMNAS = 10;
    private static final int CELL_SIZE = 40;

    // Componentes de UI
    private JPanel pnlTablero;
    private JPanel pnlShipyard;
    private JPanel pnlControles;
    private JButton[][] casillas;
    private JLabel lblInstrucciones;
    private JLabel lblNaveSeleccionada;
    private JLabel lblOrientacion;
    private JButton btnRotar;
    private JButton btnListo;
    private JButton btnRegresar;

    // Estado del juego
    private ControlVista controlVista;
    private NaveDTO naveSeleccionada;
    private NaveArrastrable naveArrastrableSeleccionada;
    private OrientacionNave orientacionActual;
    private List<Coordenadas> coordenadasPreview;

    // Control de naves colocadas
    private Map<TipoNaveDTO, Integer> navesDisponibles;
    private Map<TipoNaveDTO, Integer> navesColocadas;
    private List<NaveDTO> navesEnTablero;
    private boolean[][] casillaOcupada;

    // Componentes arrastrables del shipyard
    private Map<TipoNaveDTO, NaveArrastrable> navesArrastrables;

    // Control de host/guest y estado de listos
    private boolean esHost;
    private boolean misNavesColocadas;
    private boolean oponenteListo;

    public FrmColocarNaves() {
        this.controlVista = ControlVista.getInstancia();
        this.esHost = controlVista.isEsHost();
        this.misNavesColocadas = false;
        this.oponenteListo = false;
        initNavesDisponibles();
        initComponents();
        configurarEventos();
        suscribirANotificaciones();
    }

    private void initNavesDisponibles() {
        navesDisponibles = new HashMap<>();
        navesDisponibles.put(TipoNaveDTO.PORTAAVIONES, 2);
        navesDisponibles.put(TipoNaveDTO.CRUCERO, 2);
        navesDisponibles.put(TipoNaveDTO.SUBMARINO, 4);
        navesDisponibles.put(TipoNaveDTO.BARCO, 3);

        navesColocadas = new HashMap<>();
        navesColocadas.put(TipoNaveDTO.PORTAAVIONES, 0);
        navesColocadas.put(TipoNaveDTO.CRUCERO, 0);
        navesColocadas.put(TipoNaveDTO.SUBMARINO, 0);
        navesColocadas.put(TipoNaveDTO.BARCO, 0);

        navesEnTablero = new ArrayList<>();
        casillaOcupada = new boolean[FILAS][COLUMNAS];
        coordenadasPreview = new ArrayList<>();
        orientacionActual = OrientacionNave.HORIZONTAL;
        navesArrastrables = new HashMap<>();
    }

    private void initComponents() {
        setTitle("Battleship - Colocar Naves");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        // Panel superior con título e instrucciones
        JPanel pnlSuperior = crearPanelSuperior();
        add(pnlSuperior, BorderLayout.NORTH);

        // Panel central con tablero
        pnlTablero = crearPanelTablero();
        add(pnlTablero, BorderLayout.CENTER);

        // Panel derecho con shipyard y controles
        JPanel pnlDerecho = crearPanelDerecho();
        add(pnlDerecho, BorderLayout.EAST);

        // Panel inferior con botones
        pnlControles = crearPanelControles();
        add(pnlControles, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("BATTLESHIP - Coloca tus Naves");
        lblTitulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);

        lblInstrucciones = new JLabel("<html><center>Arrastra las naves al tablero o haz clic para seleccionar<br>" +
                "<b>Click derecho</b> sobre la nave para rotarla</center></html>");
        lblInstrucciones.setAlignmentX(CENTER_ALIGNMENT);

        lblNaveSeleccionada = new JLabel("Nave seleccionada: Ninguna");
        lblNaveSeleccionada.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        lblNaveSeleccionada.setAlignmentX(CENTER_ALIGNMENT);

        lblOrientacion = new JLabel("Orientacion: HORIZONTAL");
        lblOrientacion.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblOrientacion.setForeground(new Color(0, 102, 204));
        lblOrientacion.setAlignmentX(CENTER_ALIGNMENT);

        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblInstrucciones);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblNaveSeleccionada);
        panel.add(Box.createVerticalStrut(3));
        panel.add(lblOrientacion);

        return panel;
    }

    private JPanel crearPanelTablero() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel para las etiquetas de columnas (A-J)
        JPanel pnlColumnas = new JPanel(new GridLayout(1, COLUMNAS + 1));
        pnlColumnas.add(new JLabel("")); // Esquina vacía
        for (int i = 0; i < COLUMNAS; i++) {
            JLabel lbl = new JLabel(String.valueOf((char) ('A' + i)), SwingConstants.CENTER);
            lbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
            pnlColumnas.add(lbl);
        }
        contenedor.add(pnlColumnas, BorderLayout.NORTH);

        // Panel principal con filas numeradas y tablero
        JPanel pnlPrincipal = new JPanel(new BorderLayout());

        // Panel para las etiquetas de filas (1-10)
        JPanel pnlFilas = new JPanel(new GridLayout(FILAS, 1));
        for (int i = 0; i < FILAS; i++) {
            JLabel lbl = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
            lbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
            lbl.setPreferredSize(new Dimension(25, CELL_SIZE));
            pnlFilas.add(lbl);
        }
        pnlPrincipal.add(pnlFilas, BorderLayout.WEST);

        // Panel del tablero
        JPanel tablero = new JPanel(new GridLayout(FILAS, COLUMNAS));
        tablero.setPreferredSize(new Dimension(COLUMNAS * CELL_SIZE, FILAS * CELL_SIZE));
        casillas = new JButton[FILAS][COLUMNAS];

        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                final int fila = i;
                final int col = j;
                casillas[i][j] = new JButton();
                casillas[i][j].setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                casillas[i][j].setBackground(new Color(173, 216, 230)); // Azul claro (agua)
                casillas[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY));
                casillas[i][j].setFocusPainted(false);

                // Habilitar drop target para cada casilla
                new DropTarget(casillas[i][j], this);

                // Eventos del mouse para preview y click derecho
                casillas[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent evt) {
                        mostrarPreview(fila, col);
                    }

                    @Override
                    public void mouseExited(MouseEvent evt) {
                        limpiarPreview();
                    }

                    @Override
                    public void mouseClicked(MouseEvent evt) {
                        // Click derecho para rotar la nave seleccionada
                        if (SwingUtilities.isRightMouseButton(evt)) {
                            rotarNave();
                            mostrarPreview(fila, col); // Actualizar preview con nueva orientacion
                        }
                    }
                });

                // Click izquierdo para colocar nave
                casillas[i][j].addActionListener((ActionEvent e) -> {
                    colocarNave(fila, col);
                });

                tablero.add(casillas[i][j]);
            }
        }

        pnlPrincipal.add(tablero, BorderLayout.CENTER);
        contenedor.add(pnlPrincipal, BorderLayout.CENTER);

        return contenedor;
    }

    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
        panel.setPreferredSize(new Dimension(220, 500));

        // Titulo del astillero
        JLabel lblAstillero = new JLabel("ASTILLERO");
        lblAstillero.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        lblAstillero.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblAstillero);

        JLabel lblAstilleroInfo = new JLabel("<html><center><small>Arrastra o haz clic<br>Click derecho = Rotar</small></center></html>");
        lblAstilleroInfo.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblAstilleroInfo);
        panel.add(Box.createVerticalStrut(10));

        // Naves arrastrables
        agregarNaveArrastrable(panel, TipoNaveDTO.PORTAAVIONES, 4, new Color(128, 0, 128));
        agregarNaveArrastrable(panel, TipoNaveDTO.CRUCERO, 3, new Color(0, 100, 0));
        agregarNaveArrastrable(panel, TipoNaveDTO.SUBMARINO, 2, new Color(0, 0, 139));
        agregarNaveArrastrable(panel, TipoNaveDTO.BARCO, 1, new Color(139, 69, 19));

        panel.add(Box.createVerticalStrut(15));

        // Boton rotar (alternativa al click derecho)
        btnRotar = new JButton("Rotar (R)");
        btnRotar.setAlignmentX(CENTER_ALIGNMENT);
        btnRotar.setMaximumSize(new Dimension(180, 35));
        btnRotar.setToolTipText("Tambien puedes usar click derecho");
        btnRotar.addActionListener(e -> rotarNave());
        panel.add(btnRotar);

        panel.add(Box.createVerticalStrut(10));

        // Leyenda
        JPanel leyenda = crearLeyenda();
        panel.add(leyenda);

        return panel;
    }

    private void agregarNaveArrastrable(JPanel panel, TipoNaveDTO tipo, int tamanio, Color color) {
        // Panel contenedor con label
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setMaximumSize(new Dimension(200, 80));
        contenedor.setAlignmentX(CENTER_ALIGNMENT);

        // Label con nombre y cantidad
        int disponibles = navesDisponibles.get(tipo);
        JLabel lblNave = new JLabel(getNombreNave(tipo) + " x" + disponibles);
        lblNave.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        lblNave.setAlignmentX(CENTER_ALIGNMENT);

        // Crear la nave arrastrable
        NaveArrastrable nave = new NaveArrastrable(tipo, tamanio, color, disponibles);
        nave.setAlignmentX(CENTER_ALIGNMENT);

        // Registrar listener para cuando se seleccione la nave
        nave.addPropertyChangeListener("naveSeleccionada", this);

        // Registrar listener para cuando cambie la orientacion
        nave.setOrientacionListener(nuevaOrientacion -> {
            orientacionActual = nuevaOrientacion;
            actualizarLabelOrientacion();
        });

        navesArrastrables.put(tipo, nave);

        contenedor.add(lblNave);
        contenedor.add(Box.createVerticalStrut(3));
        contenedor.add(nave);

        panel.add(contenedor);
        panel.add(Box.createVerticalStrut(10));
    }

    private JPanel crearLeyenda() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Leyenda"));

        agregarItemLeyenda(panel, new Color(173, 216, 230), "Agua");
        agregarItemLeyenda(panel, new Color(144, 238, 144), "Preview válido");
        agregarItemLeyenda(panel, new Color(255, 182, 193), "Preview inválido");
        agregarItemLeyenda(panel, Color.GRAY, "Nave colocada");

        return panel;
    }

    private void agregarItemLeyenda(JPanel panel, Color color, String texto) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        item.add(colorBox);
        item.add(new JLabel(texto));
        panel.add(item);
    }

    private JPanel crearPanelControles() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        btnRegresar = new JButton("Regresar");
        btnRegresar.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        btnRegresar.addActionListener(e -> regresar());

        // El texto del boton depende de si es host o guest
        if (esHost) {
            btnListo = new JButton("EMPEZAR BATALLA");
            btnListo.setToolTipText("Se activara cuando ambos jugadores esten listos");
        } else {
            btnListo = new JButton("LISTO");
            btnListo.setToolTipText("Presiona cuando hayas colocado todas tus naves");
        }

        btnListo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        btnListo.setBackground(new Color(0, 153, 0));
        btnListo.setForeground(Color.WHITE);
        btnListo.setEnabled(false);
        btnListo.addActionListener(e -> confirmarTablero());

        panel.add(btnRegresar);
        panel.add(btnListo);

        return panel;
    }

    private void configurarEventos() {
        // Eventos adicionales si son necesarios
    }

    private void seleccionarNave(TipoNaveDTO tipo, int tamanio, Color color) {
        int disponibles = navesDisponibles.get(tipo) - navesColocadas.get(tipo);
        if (disponibles <= 0) {
            JOptionPane.showMessageDialog(this,
                "Ya colocaste todas las naves de este tipo.",
                "Sin naves disponibles",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        naveSeleccionada = new NaveDTO(EstadoNave.SIN_DAÑOS, orientacionActual, tipo, tamanio);
        naveArrastrableSeleccionada = navesArrastrables.get(tipo);
        lblNaveSeleccionada.setText("Nave seleccionada: " + getNombreNave(tipo) +
                " (Tam: " + tamanio + ", Disp: " + disponibles + ")");
        lblInstrucciones.setText("<html><center>Haz clic en el tablero para colocar<br>o arrastra la nave</center></html>");
    }

    // === PropertyChangeListener - Para cuando se selecciona una nave del shipyard ===
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("naveSeleccionada".equals(evt.getPropertyName())) {
            NaveArrastrable nave = (NaveArrastrable) evt.getNewValue();
            if (nave != null && nave.isDisponible()) {
                seleccionarNave(nave.getTipo(), nave.getTamanio(), nave.getColorNave());
                orientacionActual = nave.getOrientacion();
                actualizarLabelOrientacion();
            }
        }
    }

    // === DropTargetListener - Para recibir naves arrastradas ===
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        if (dtde.isDataFlavorSupported(NaveArrastrable.NAVE_FLAVOR)) {
            dtde.acceptDrag(DnDConstants.ACTION_COPY);
        } else {
            dtde.rejectDrag();
        }
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        // Obtener la casilla sobre la que esta el mouse
        java.awt.Point point = dtde.getLocation();
        JButton casilla = (JButton) dtde.getDropTargetContext().getComponent();

        // Buscar las coordenadas de la casilla
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (casillas[i][j] == casilla) {
                    // Mostrar preview si hay nave siendo arrastrada
                    try {
                        Transferable t = dtde.getTransferable();
                        if (t.isDataFlavorSupported(NaveArrastrable.NAVE_FLAVOR)) {
                            NaveArrastrable nave = (NaveArrastrable) t.getTransferData(NaveArrastrable.NAVE_FLAVOR);
                            naveSeleccionada = new NaveDTO(EstadoNave.SIN_DAÑOS, nave.getOrientacion(),
                                    nave.getTipo(), nave.getTamanio());
                            orientacionActual = nave.getOrientacion();
                            mostrarPreview(i, j);
                        }
                    } catch (Exception e) {
                        // Ignorar errores de transferencia durante drag
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {}

    @Override
    public void dragExit(DropTargetEvent dte) {
        limpiarPreview();
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        try {
            Transferable t = dtde.getTransferable();
            if (t.isDataFlavorSupported(NaveArrastrable.NAVE_FLAVOR)) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY);

                NaveArrastrable nave = (NaveArrastrable) t.getTransferData(NaveArrastrable.NAVE_FLAVOR);
                JButton casilla = (JButton) dtde.getDropTargetContext().getComponent();

                // Encontrar coordenadas de la casilla
                for (int i = 0; i < FILAS; i++) {
                    for (int j = 0; j < COLUMNAS; j++) {
                        if (casillas[i][j] == casilla) {
                            // Configurar la nave seleccionada con la orientacion de la nave arrastrada
                            naveSeleccionada = new NaveDTO(EstadoNave.SIN_DAÑOS, nave.getOrientacion(),
                                    nave.getTipo(), nave.getTamanio());
                            naveArrastrableSeleccionada = nave;
                            orientacionActual = nave.getOrientacion();

                            // Intentar colocar la nave
                            colocarNave(i, j);
                            dtde.dropComplete(true);
                            return;
                        }
                    }
                }
            }
            dtde.rejectDrop();
        } catch (Exception e) {
            e.printStackTrace();
            dtde.rejectDrop();
        }
    }

    private String getNombreNave(TipoNaveDTO tipo) {
        switch (tipo) {
            case PORTAAVIONES: return "Portaaviones";
            case CRUCERO: return "Crucero";
            case SUBMARINO: return "Submarino";
            case BARCO: return "Barco";
            default: return "Desconocido";
        }
    }

    private void rotarNave() {
        if (orientacionActual == OrientacionNave.HORIZONTAL) {
            orientacionActual = OrientacionNave.VERTICAL;
        } else {
            orientacionActual = OrientacionNave.HORIZONTAL;
        }

        actualizarLabelOrientacion();

        // Actualizar nave seleccionada (DTO) para el preview y colocacion
        if (naveSeleccionada != null) {
            naveSeleccionada.setOrientacion(orientacionActual);
        }

        // NO sincronizar con la nave en el Astillero
        // La rotacion desde el tablero solo afecta la orientacion de colocacion,
        // no la visualizacion de la nave en el astillero
    }

    private void actualizarLabelOrientacion() {
        String orientacionStr = (orientacionActual == OrientacionNave.HORIZONTAL) ? "HORIZONTAL" : "VERTICAL";
        lblOrientacion.setText("Orientacion: " + orientacionStr);
        btnRotar.setText("Rotar (" + orientacionStr.charAt(0) + ")");
    }

    private void mostrarPreview(int fila, int col) {
        if (naveSeleccionada == null) return;

        limpiarPreview();
        coordenadasPreview = calcularCoordenadas(fila, col, naveSeleccionada.getTamanio());
        boolean valido = validarColocacion(coordenadasPreview);

        Color colorPreview = valido ? new Color(144, 238, 144) : new Color(255, 182, 193);

        for (Coordenadas coord : coordenadasPreview) {
            if (coord.getX() >= 0 && coord.getX() < FILAS &&
                coord.getY() >= 0 && coord.getY() < COLUMNAS) {
                if (!casillaOcupada[coord.getX()][coord.getY()]) {
                    casillas[coord.getX()][coord.getY()].setBackground(colorPreview);
                }
            }
        }
    }

    private void limpiarPreview() {
        for (Coordenadas coord : coordenadasPreview) {
            if (coord.getX() >= 0 && coord.getX() < FILAS &&
                coord.getY() >= 0 && coord.getY() < COLUMNAS) {
                if (!casillaOcupada[coord.getX()][coord.getY()]) {
                    casillas[coord.getX()][coord.getY()].setBackground(new Color(173, 216, 230));
                }
            }
        }
        coordenadasPreview.clear();
    }

    private List<Coordenadas> calcularCoordenadas(int fila, int col, int tamanio) {
        List<Coordenadas> coords = new ArrayList<>();
        for (int i = 0; i < tamanio; i++) {
            if (orientacionActual == OrientacionNave.HORIZONTAL) {
                coords.add(new Coordenadas(fila, col + i));
            } else {
                coords.add(new Coordenadas(fila + i, col));
            }
        }
        return coords;
    }

    private boolean validarColocacion(List<Coordenadas> coordenadas) {
        for (Coordenadas coord : coordenadas) {
            // Verificar límites
            if (coord.getX() < 0 || coord.getX() >= FILAS ||
                coord.getY() < 0 || coord.getY() >= COLUMNAS) {
                return false;
            }

            // Verificar si está ocupada
            if (casillaOcupada[coord.getX()][coord.getY()]) {
                return false;
            }

            // Verificar adyacentes (naves no pueden estar pegadas)
            if (hayNaveAdyacente(coord.getX(), coord.getY(), coordenadas)) {
                return false;
            }
        }
        return true;
    }

    private boolean hayNaveAdyacente(int fila, int col, List<Coordenadas> excluir) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                int newFila = fila + i;
                int newCol = col + j;
                if (newFila >= 0 && newFila < FILAS && newCol >= 0 && newCol < COLUMNAS) {
                    if (casillaOcupada[newFila][newCol]) {
                        // Verificar que no sea parte de las coordenadas que estamos colocando
                        boolean esParteDeNave = false;
                        for (Coordenadas c : excluir) {
                            if (c.getX() == newFila && c.getY() == newCol) {
                                esParteDeNave = true;
                                break;
                            }
                        }
                        if (!esParteDeNave) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void colocarNave(int fila, int col) {
        if (naveSeleccionada == null) {
            JOptionPane.showMessageDialog(this,
                "Primero selecciona una nave del astillero.",
                "Sin nave seleccionada",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Coordenadas> coordenadas = calcularCoordenadas(fila, col, naveSeleccionada.getTamanio());

        if (!validarColocacion(coordenadas)) {
            JOptionPane.showMessageDialog(this,
                "No puedes colocar la nave aquí.\nVerifica que no se salga del tablero, " +
                "no esté sobre otra nave y no esté pegada a otra.",
                "Posición inválida",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Marcar casillas como ocupadas
        Color colorNave = getColorNave(naveSeleccionada.getTipo());
        for (Coordenadas coord : coordenadas) {
            casillaOcupada[coord.getX()][coord.getY()] = true;
            casillas[coord.getX()][coord.getY()].setBackground(colorNave);
        }

        // Registrar nave colocada
        navesEnTablero.add(naveSeleccionada);
        navesColocadas.put(naveSeleccionada.getTipo(),
                navesColocadas.get(naveSeleccionada.getTipo()) + 1);

        // Actualizar botón del shipyard
        actualizarBotonShipyard(naveSeleccionada.getTipo());

        // Enviar al servidor
        controlVista.addNave(naveSeleccionada, coordenadas);

        // Limpiar seleccion
        naveSeleccionada = null;
        naveArrastrableSeleccionada = null;
        lblNaveSeleccionada.setText("Nave seleccionada: Ninguna");
        lblInstrucciones.setText("<html><center>Arrastra las naves al tablero o haz clic para seleccionar<br>" +
                "<b>Click derecho</b> sobre la nave para rotarla</center></html>");

        // Verificar si se colocaron todas
        verificarTodasColocadas();
    }

    private Color getColorNave(TipoNaveDTO tipo) {
        switch (tipo) {
            case PORTAAVIONES: return new Color(128, 0, 128);
            case CRUCERO: return new Color(0, 100, 0);
            case SUBMARINO: return new Color(0, 0, 139);
            case BARCO: return new Color(139, 69, 19);
            default: return Color.GRAY;
        }
    }

    private void actualizarBotonShipyard(TipoNaveDTO tipo) {
        int disponibles = navesDisponibles.get(tipo) - navesColocadas.get(tipo);

        // Actualizar la nave arrastrable
        NaveArrastrable nave = navesArrastrables.get(tipo);
        if (nave != null) {
            nave.decrementarDisponible();
        }

        // Actualizar el label del contenedor padre
        java.awt.Container parent = nave.getParent();
        if (parent != null) {
            for (java.awt.Component comp : parent.getComponents()) {
                if (comp instanceof JLabel) {
                    ((JLabel) comp).setText(getNombreNave(tipo) + " x" + disponibles);
                    break;
                }
            }
        }
    }

    private void verificarTodasColocadas() {
        int totalNecesarias = navesDisponibles.values().stream().mapToInt(Integer::intValue).sum();
        int totalColocadas = navesColocadas.values().stream().mapToInt(Integer::intValue).sum();

        if (totalColocadas >= totalNecesarias) {
            misNavesColocadas = true;
            actualizarEstadoBoton();
        }
    }

    private void actualizarEstadoBoton() {
        if (esHost) {
            // El host necesita que AMBOS esten listos
            boolean puedeEmpezar = misNavesColocadas && oponenteListo;
            btnListo.setEnabled(puedeEmpezar);

            if (misNavesColocadas && !oponenteListo) {
                lblInstrucciones.setText("<html><center>¡Naves colocadas!<br>Esperando a que el oponente este listo...</center></html>");
            } else if (!misNavesColocadas && oponenteListo) {
                lblInstrucciones.setText("<html><center>El oponente esta listo.<br>Termina de colocar tus naves.</center></html>");
            } else if (puedeEmpezar) {
                lblInstrucciones.setText("<html><center>¡Ambos jugadores listos!<br>Presiona EMPEZAR BATALLA.</center></html>");
            }
        } else {
            // El guest solo necesita terminar sus naves
            btnListo.setEnabled(misNavesColocadas);
            if (misNavesColocadas) {
                lblInstrucciones.setText("<html><center>¡Todas las naves colocadas!<br>Presiona LISTO para continuar.</center></html>");
            }
        }
    }

    private void suscribirANotificaciones() {
        // Suscribirse al lobby para recibir notificaciones de oponente listo
        controlVista.suscribirLobby(this);
    }

    // === ISuscriptor - Para recibir notificaciones del servidor ===
    @Override
    public void notificar(String contexto, Object datos) {
        SwingUtilities.invokeLater(() -> {
            if ("OPONENTE_LISTO".equals(contexto)) {
                oponenteListo = true;
                actualizarEstadoBoton();
                System.out.println("Oponente esta listo!");
            } else if ("EMPEZAR_PARTIDA".equals(contexto)) {
                // El host inicio la partida, pasar a la pantalla de juego
                if (!esHost) {
                    iniciarPartida();
                }
            }
        });
    }

    private void confirmarTablero() {
        String mensaje = esHost ?
                "¿Iniciar la partida?\nAmbos jugadores estan listos." :
                "¿Confirmar tu configuracion?\nNo podras modificarla despues.";
        String titulo = esHost ? "Iniciar partida" : "Confirmar tablero";

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, titulo, JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            if (esHost) {
                // El host inicia la partida para ambos
                iniciarPartida();
                // Notificar al guest que la partida empezo
                controlVista.empezarPartida();
            } else {
                // El guest notifica que esta listo y espera
                notificarListo();
            }
        }
    }

    private void notificarListo() {
        // Notificar al servidor/host que este jugador esta listo
        controlVista.notificarJugadorListo(); // Esto notificara al host

        // Deshabilitar el boton y esperar
        btnListo.setEnabled(false);
        btnListo.setText("ESPERANDO...");
        lblInstrucciones.setText("<html><center>Esperando a que el host<br>inicie la batalla...</center></html>");
    }

    private void iniciarPartida() {
        // Crear e inicializar el TimerPanel (30 segundos por turno)
        TimerPanel timerPanel = new TimerPanel(1000, 30);
        controlVista.setTimer(timerPanel);

        // Inicializar tableros en ControlVista
        controlVista.initTableroPropio();
        controlVista.initTableroEnemigo();
        controlVista.suscribirAModelo();

        // Marcar las casillas propias donde hay naves
        marcarNavesEnTableroPropio();

        // Desuscribirse del lobby
        controlVista.desuscribirLobby(this);

        // Abrir pantalla de partida
        controlVista.mostrarFrmPartidaEnCurso();
        dispose();
    }

    private void marcarNavesEnTableroPropio() {
        // Marcar en las casillas propias del ControlVista las naves colocadas
        List<CasillaPanel> casillasPropias = controlVista.getCasillasPropias();
        if (casillasPropias == null) return;

        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (casillaOcupada[i][j]) {
                    final int fila = i;
                    final int col = j;
                    // Buscar la casilla correspondiente y pintarla
                    for (CasillaPanel cp : casillasPropias) {
                        CoordenadasDTO coords = cp.getCoordenadas();
                        if (coords.getX() == fila && coords.getY() == col) {
                            cp.setBackground(casillas[fila][col].getBackground());
                            break;
                        }
                    }
                }
            }
        }
    }

    private void regresar() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que quieres salir?\nPerderás tu configuración.",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            FrmLobby lobby = new FrmLobby();
            lobby.setVisible(true);
            dispose();
        }
    }
}
