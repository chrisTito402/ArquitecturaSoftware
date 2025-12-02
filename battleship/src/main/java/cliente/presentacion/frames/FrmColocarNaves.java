package cliente.presentacion.frames;

import cliente.controlador.ControlVista;
import cliente.presentacion.componentes.CasillaPanel;
import cliente.presentacion.componentes.TimerPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import compartido.enums.EstadoNave;
import compartido.enums.OrientacionNave;
import compartido.observador.ISuscriptor;
import compartido.comunicacion.dto.CoordenadasDTO;
import compartido.comunicacion.dto.JugadorDTO;
import compartido.comunicacion.dto.NaveDTO;
import compartido.comunicacion.dto.TipoNaveDTO;
import compartido.comunicacion.dto.TurnoDTO;
import compartido.enums.ColorJugador;

/**
 * Pantalla para colocar las naves antes de iniciar la partida.
 * Permite ARRASTRAR Y SOLTAR naves del "astillero" al tablero.
 * También permite rotar las naves con clic derecho o botón.
 * Implementa ISuscriptor para recibir eventos del modelo.
 *
 * @author Equipo
 */
public class FrmColocarNaves extends JFrame implements ISuscriptor {

    // Constantes del tablero
    private static final int FILAS = 10;
    private static final int COLUMNAS = 10;
    private static final int CELL_SIZE = 40;

    // Componentes de UI
    private JLayeredPane layeredPane;
    private JPanel pnlContenido;
    private JPanel pnlTablero;
    private JPanel pnlTableroGrid;
    private JPanel pnlShipyard;
    private JPanel pnlControles;
    private JButton[][] casillas;
    private JLabel lblInstrucciones;
    private JLabel lblNaveSeleccionada;
    private JButton btnRotar;
    private JButton btnListo;
    private JButton btnRegresar;

    // Estado del juego
    private ControlVista controlVista;
    private NaveDTO naveSeleccionada;
    private OrientacionNave orientacionActual;
    private List<CoordenadasDTO> coordenadasPreview;

    // Control de naves colocadas
    private Map<TipoNaveDTO, Integer> navesDisponibles;
    private Map<TipoNaveDTO, Integer> navesColocadas;
    private List<NaveDTO> navesEnTablero;
    private boolean[][] casillaOcupada;

    // Paneles de naves arrastrables en el shipyard
    private Map<TipoNaveDTO, JPanel> panelesNaveShipyard;
    private Map<TipoNaveDTO, JButton> botonesShipyard;

    // Estado de espera
    private boolean esperandoOtroJugador;
    private JLabel lblEstadoEspera;
    private Map<String, Consumer<Object>> manejadoresNotificacion;

    // Color del jugador
    private ColorJugador colorJugador;
    private Color colorNaveJugador;

    // Bandera para evitar iniciar partida multiples veces
    private boolean partidaIniciada = false;

    // === VARIABLES PARA DRAG & DROP ===
    private JPanel naveDragVisual;      // Panel visual que sigue al mouse durante el arrastre
    private boolean arrastrando = false;
    private Point dragOffset;           // Offset del click dentro de la nave
    private TipoNaveDTO tipoDragActual;
    private int tamanioDragActual;

    public FrmColocarNaves() {
        this.controlVista = ControlVista.getInstancia();
        this.esperandoOtroJugador = false;
        initNavesDisponibles();
        initManejadoresNotificacion();
        initColorJugador();
        initComponents();

        // Suscribirse a eventos del modelo
        controlVista.suscribirAModelo();
        controlVista.suscribirLobby(this);
    }

    /**
     * Inicializa el color del jugador basado en su seleccion.
     */
    private void initColorJugador() {
        JugadorDTO jugador = controlVista.getControl().getJugador();
        if (jugador != null && jugador.getColor() != null) {
            this.colorJugador = jugador.getColor();
            switch (colorJugador) {
                case ROJO -> colorNaveJugador = new Color(220, 20, 60);    // Crimson
                case AZUL -> colorNaveJugador = new Color(30, 144, 255);   // DodgerBlue
                default -> colorNaveJugador = Color.GRAY;
            }
        } else {
            this.colorJugador = ColorJugador.AZUL;
            this.colorNaveJugador = new Color(30, 144, 255);
        }
        System.out.println("[FrmColocarNaves] Color del jugador: " + colorJugador);
    }

    private void initManejadoresNotificacion() {
        manejadoresNotificacion = new HashMap<>();
        manejadoresNotificacion.put("TABLEROS_LISTOS", this::manejarTablerosListos);
        manejadoresNotificacion.put("TURNO_INICIAL", this::manejarTurnoInicial);
        manejadoresNotificacion.put("CONFIRMAR_TABLERO", this::manejarConfirmacionOtroJugador);
    }

    @Override
    public void notificar(String contexto, Object datos) {
        Consumer<Object> manejador = manejadoresNotificacion.get(contexto);
        if (manejador != null) {
            SwingUtilities.invokeLater(() -> manejador.accept(datos));
        }
    }

    private void manejarTablerosListos(Object datos) {
        if (partidaIniciada) {
            System.out.println("[FrmColocarNaves] Partida ya iniciada - ignorando evento duplicado");
            return;
        }
        partidaIniciada = true;
        System.out.println("[FrmColocarNaves] Tableros listos - Iniciando partida");
        iniciarPartida();
    }

    private void manejarTurnoInicial(Object datos) {
        if (datos instanceof TurnoDTO turno) {
            System.out.println("[FrmColocarNaves] Turno inicial: " + turno.getNombreJugadorEnTurno());
        }
    }

    private void manejarConfirmacionOtroJugador(Object datos) {
        if (datos instanceof JugadorDTO jugadorDTO) {
            String miNombre = controlVista.getControl().getJugador().getNombre();
            if (!jugadorDTO.getNombre().equals(miNombre)) {
                System.out.println("[FrmColocarNaves] El otro jugador confirmo su tablero");
                if (esperandoOtroJugador && lblEstadoEspera != null) {
                    lblEstadoEspera.setText("El otro jugador esta listo. Esperando sincronizacion...");
                }
            }
        }
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
        panelesNaveShipyard = new HashMap<>();
        botonesShipyard = new HashMap<>();
    }

    private void initComponents() {
        setTitle("Battleship - Colocar Naves (Arrastra las naves al tablero)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Usar JLayeredPane para permitir que la nave arrastrada aparezca sobre todo
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(750, 650));
        setContentPane(layeredPane);

        // Panel de contenido principal
        pnlContenido = new JPanel(new BorderLayout(10, 10));
        pnlContenido.setBounds(0, 0, 750, 650);
        pnlContenido.setOpaque(true);
        layeredPane.add(pnlContenido, JLayeredPane.DEFAULT_LAYER);

        // Panel superior con título e instrucciones
        JPanel pnlSuperior = crearPanelSuperior();
        pnlContenido.add(pnlSuperior, BorderLayout.NORTH);

        // Panel central con tablero
        pnlTablero = crearPanelTablero();
        pnlContenido.add(pnlTablero, BorderLayout.CENTER);

        // Panel derecho con shipyard y controles
        JPanel pnlDerecho = crearPanelDerecho();
        pnlContenido.add(pnlDerecho, BorderLayout.EAST);

        // Panel inferior con botones
        pnlControles = crearPanelControles();
        pnlContenido.add(pnlControles, BorderLayout.SOUTH);

        // Configurar listener global para el arrastre
        configurarDragGlobal();

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Configura el listener global para mover la nave mientras se arrastra.
     */
    private void configurarDragGlobal() {
        // Listener para mover la nave visual mientras se arrastra
        layeredPane.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (arrastrando && naveDragVisual != null) {
                    // Mover el panel visual de la nave
                    int x = e.getX() - dragOffset.x;
                    int y = e.getY() - dragOffset.y;
                    naveDragVisual.setLocation(x, y);

                    // Calcular sobre qué casilla está el cursor
                    Point puntoEnTablero = SwingUtilities.convertPoint(layeredPane, e.getPoint(), pnlTableroGrid);
                    int col = puntoEnTablero.x / CELL_SIZE;
                    int fila = puntoEnTablero.y / CELL_SIZE;

                    // Mostrar preview si está sobre el tablero
                    if (fila >= 0 && fila < FILAS && col >= 0 && col < COLUMNAS) {
                        mostrarPreview(fila, col);
                    } else {
                        limpiarPreview();
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                // Actualizar preview cuando hay nave seleccionada (modo click)
                if (naveSeleccionada != null && !arrastrando) {
                    Point puntoEnTablero = SwingUtilities.convertPoint(layeredPane, e.getPoint(), pnlTableroGrid);
                    int col = puntoEnTablero.x / CELL_SIZE;
                    int fila = puntoEnTablero.y / CELL_SIZE;

                    if (fila >= 0 && fila < FILAS && col >= 0 && col < COLUMNAS) {
                        mostrarPreview(fila, col);
                    }
                }
            }
        });

        // Listener para soltar la nave
        layeredPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (arrastrando && naveDragVisual != null) {
                    // Calcular donde se soltó
                    Point puntoEnTablero = SwingUtilities.convertPoint(layeredPane, e.getPoint(), pnlTableroGrid);
                    int col = puntoEnTablero.x / CELL_SIZE;
                    int fila = puntoEnTablero.y / CELL_SIZE;

                    // Intentar colocar la nave
                    if (fila >= 0 && fila < FILAS && col >= 0 && col < COLUMNAS) {
                        colocarNaveDrag(fila, col);
                    }

                    // Limpiar el arrastre
                    finalizarArrastre();
                }
            }
        });
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("BATTLESHIP - Coloca tus Naves");
        lblTitulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);

        lblInstrucciones = new JLabel("ARRASTRA una nave del astillero al tablero (o haz clic para seleccionar)");
        lblInstrucciones.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        lblInstrucciones.setAlignmentX(CENTER_ALIGNMENT);

        lblNaveSeleccionada = new JLabel("Clic derecho o botón 'Rotar' para cambiar orientación");
        lblNaveSeleccionada.setFont(new java.awt.Font("Segoe UI", java.awt.Font.ITALIC, 12));
        lblNaveSeleccionada.setAlignmentX(CENTER_ALIGNMENT);

        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblInstrucciones);
        panel.add(Box.createVerticalStrut(3));
        panel.add(lblNaveSeleccionada);

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

        // Panel del tablero (grid)
        pnlTableroGrid = new JPanel(new GridLayout(FILAS, COLUMNAS));
        pnlTableroGrid.setPreferredSize(new Dimension(COLUMNAS * CELL_SIZE, FILAS * CELL_SIZE));
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

                // Eventos del mouse para preview (modo click)
                casillas[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent evt) {
                        if (!arrastrando && naveSeleccionada != null) {
                            mostrarPreview(fila, col);
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent evt) {
                        if (!arrastrando) {
                            limpiarPreview();
                        }
                    }

                    @Override
                    public void mouseClicked(MouseEvent evt) {
                        // Clic izquierdo para colocar (modo click tradicional)
                        if (SwingUtilities.isLeftMouseButton(evt) && naveSeleccionada != null) {
                            colocarNave(fila, col);
                        }
                        // Clic derecho para rotar
                        if (SwingUtilities.isRightMouseButton(evt)) {
                            rotarNave();
                            if (naveSeleccionada != null) {
                                mostrarPreview(fila, col);
                            }
                        }
                    }
                });

                pnlTableroGrid.add(casillas[i][j]);
            }
        }

        pnlPrincipal.add(pnlTableroGrid, BorderLayout.CENTER);
        contenedor.add(pnlPrincipal, BorderLayout.CENTER);

        return contenedor;
    }

    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
        panel.setPreferredSize(new Dimension(220, 500));

        // Título del astillero
        JLabel lblAstillero = new JLabel("ASTILLERO");
        lblAstillero.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        lblAstillero.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblAstillero);

        JLabel lblDragDrop = new JLabel("(Arrastra las naves al tablero)");
        lblDragDrop.setFont(new java.awt.Font("Segoe UI", java.awt.Font.ITALIC, 10));
        lblDragDrop.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblDragDrop);

        // Indicador del color del jugador
        JLabel lblMiColor = new JLabel("Tu color: " + (colorJugador != null ? colorJugador.name() : "AZUL"));
        lblMiColor.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        lblMiColor.setForeground(colorNaveJugador);
        lblMiColor.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblMiColor);
        panel.add(Box.createVerticalStrut(15));

        // Naves arrastrables
        agregarNaveArrastrable(panel, TipoNaveDTO.PORTAAVIONES, "Portaaviones", 4);
        agregarNaveArrastrable(panel, TipoNaveDTO.CRUCERO, "Crucero", 3);
        agregarNaveArrastrable(panel, TipoNaveDTO.SUBMARINO, "Submarino", 2);
        agregarNaveArrastrable(panel, TipoNaveDTO.BARCO, "Barco", 1);

        panel.add(Box.createVerticalStrut(15));

        // Botón rotar
        btnRotar = new JButton("Rotar: HORIZONTAL");
        btnRotar.setAlignmentX(CENTER_ALIGNMENT);
        btnRotar.setMaximumSize(new Dimension(180, 35));
        btnRotar.setBackground(new Color(100, 149, 237));
        btnRotar.setForeground(Color.WHITE);
        btnRotar.addActionListener(e -> rotarNave());
        panel.add(btnRotar);

        panel.add(Box.createVerticalStrut(10));

        // Leyenda
        JPanel leyenda = crearLeyenda();
        panel.add(leyenda);

        return panel;
    }

    /**
     * Agrega una nave arrastrable al astillero.
     */
    private void agregarNaveArrastrable(JPanel panel, TipoNaveDTO tipo, String nombre, int tamanio) {
        Color colorNave = getColorNave(tipo);
        int disponibles = navesDisponibles.get(tipo);

        // Contenedor de la nave
        JPanel contenedorNave = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        contenedorNave.setMaximumSize(new Dimension(210, 60));
        contenedorNave.setBorder(BorderFactory.createTitledBorder(nombre + " (" + disponibles + ")"));

        // Panel visual de la nave (arrastrable)
        JPanel pnlNaveVisual = new JPanel(new GridLayout(1, tamanio, 1, 1));
        pnlNaveVisual.setPreferredSize(new Dimension(tamanio * 25, 25));
        pnlNaveVisual.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        for (int i = 0; i < tamanio; i++) {
            JPanel celda = new JPanel();
            celda.setBackground(colorNave);
            celda.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            pnlNaveVisual.add(celda);
        }

        // Guardar referencia al panel de la nave
        panelesNaveShipyard.put(tipo, contenedorNave);

        // === EVENTOS DRAG & DROP ===
        pnlNaveVisual.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int disponiblesActual = navesDisponibles.get(tipo) - navesColocadas.get(tipo);
                    if (disponiblesActual > 0) {
                        iniciarArrastre(tipo, tamanio, colorNave, e);
                    } else {
                        JOptionPane.showMessageDialog(FrmColocarNaves.this,
                                "Ya colocaste todas las naves de este tipo.",
                                "Sin naves disponibles",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
                // Clic derecho para rotar
                if (SwingUtilities.isRightMouseButton(e)) {
                    rotarNave();
                }
            }
        });

        // También permitir selección con clic (modo tradicional)
        JButton btnSeleccionar = new JButton("Seleccionar");
        btnSeleccionar.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 10));
        btnSeleccionar.addActionListener(e -> seleccionarNave(tipo, tamanio, colorNave));
        botonesShipyard.put(tipo, btnSeleccionar);

        contenedorNave.add(pnlNaveVisual);
        contenedorNave.add(btnSeleccionar);

        panel.add(contenedorNave);
        panel.add(Box.createVerticalStrut(5));
    }

    /**
     * Inicia el arrastre de una nave.
     */
    private void iniciarArrastre(TipoNaveDTO tipo, int tamanio, Color color, MouseEvent e) {
        arrastrando = true;
        tipoDragActual = tipo;
        tamanioDragActual = tamanio;

        // Crear selección temporal
        naveSeleccionada = new NaveDTO(EstadoNave.SIN_DAÑOS, orientacionActual, tipo, tamanio);

        // Crear panel visual para el arrastre
        naveDragVisual = crearPanelNaveVisual(tamanio, color, orientacionActual);

        // Calcular offset
        dragOffset = new Point(e.getX(), e.getY());

        // Posicionar donde está el mouse
        Point puntoEnLayered = SwingUtilities.convertPoint((JComponent) e.getSource(), e.getPoint(), layeredPane);
        naveDragVisual.setLocation(puntoEnLayered.x - dragOffset.x, puntoEnLayered.y - dragOffset.y);

        // Agregar a la capa superior
        layeredPane.add(naveDragVisual, JLayeredPane.DRAG_LAYER);
        layeredPane.repaint();

        // Actualizar instrucciones
        lblInstrucciones.setText("Arrastrando " + getNombreNave(tipo) + " - Suelta sobre el tablero");
    }

    /**
     * Crea un panel visual de la nave para arrastrar.
     */
    private JPanel crearPanelNaveVisual(int tamanio, Color color, OrientacionNave orientacion) {
        int filas = orientacion == OrientacionNave.VERTICAL ? tamanio : 1;
        int cols = orientacion == OrientacionNave.HORIZONTAL ? tamanio : 1;

        JPanel panel = new JPanel(new GridLayout(filas, cols, 1, 1));
        panel.setOpaque(false);

        int ancho = cols * CELL_SIZE;
        int alto = filas * CELL_SIZE;
        panel.setSize(ancho, alto);

        for (int i = 0; i < tamanio; i++) {
            JPanel celda = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 180));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(Color.BLACK);
                    g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                }
            };
            celda.setOpaque(false);
            panel.add(celda);
        }

        return panel;
    }

    /**
     * Finaliza el arrastre y limpia.
     */
    private void finalizarArrastre() {
        if (naveDragVisual != null) {
            layeredPane.remove(naveDragVisual);
            layeredPane.repaint();
            naveDragVisual = null;
        }
        arrastrando = false;
        tipoDragActual = null;
        tamanioDragActual = 0;
        naveSeleccionada = null;
        limpiarPreview();

        lblInstrucciones.setText("ARRASTRA una nave del astillero al tablero (o haz clic para seleccionar)");
    }

    /**
     * Coloca la nave después de arrastrarla.
     */
    private void colocarNaveDrag(int fila, int col) {
        if (tipoDragActual == null) return;

        // Recrear el NaveDTO con la orientación actual
        naveSeleccionada = new NaveDTO(EstadoNave.SIN_DAÑOS, orientacionActual, tipoDragActual, tamanioDragActual);
        colocarNave(fila, col);
    }

    private JPanel crearLeyenda() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Leyenda"));

        agregarItemLeyenda(panel, new Color(173, 216, 230), "Agua");
        agregarItemLeyenda(panel, new Color(144, 238, 144), "Posición válida");
        agregarItemLeyenda(panel, new Color(255, 182, 193), "Posición inválida");

        return panel;
    }

    private void agregarItemLeyenda(JPanel panel, Color color, String texto) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(15, 15));
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

        btnListo = new JButton("LISTO");
        btnListo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        btnListo.setBackground(new Color(0, 153, 0));
        btnListo.setForeground(Color.WHITE);
        btnListo.setEnabled(false);
        btnListo.addActionListener(e -> confirmarTablero());

        panel.add(btnRegresar);
        panel.add(btnListo);

        return panel;
    }

    /**
     * Selecciona una nave (modo click tradicional).
     */
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
        lblInstrucciones.setText("Nave seleccionada: " + getNombreNave(tipo) +
                " (Tamaño: " + tamanio + ") - Haz clic en el tablero para colocarla");
    }

    private String getNombreNave(TipoNaveDTO tipo) {
        return switch (tipo) {
            case PORTAAVIONES -> "Portaaviones";
            case CRUCERO -> "Crucero";
            case SUBMARINO -> "Submarino";
            case BARCO -> "Barco";
            default -> "Desconocido";
        };
    }

    private void rotarNave() {
        if (orientacionActual == OrientacionNave.HORIZONTAL) {
            orientacionActual = OrientacionNave.VERTICAL;
            btnRotar.setText("Rotar: VERTICAL");
        } else {
            orientacionActual = OrientacionNave.HORIZONTAL;
            btnRotar.setText("Rotar: HORIZONTAL");
        }

        if (naveSeleccionada != null) {
            naveSeleccionada.setOrientacion(orientacionActual);
        }

        // Si está arrastrando, actualizar el visual
        if (arrastrando && naveDragVisual != null && tipoDragActual != null) {
            Point ubicacion = naveDragVisual.getLocation();
            layeredPane.remove(naveDragVisual);
            naveDragVisual = crearPanelNaveVisual(tamanioDragActual, getColorNave(tipoDragActual), orientacionActual);
            naveDragVisual.setLocation(ubicacion);
            layeredPane.add(naveDragVisual, JLayeredPane.DRAG_LAYER);
            layeredPane.repaint();
        }
    }

    private void mostrarPreview(int fila, int col) {
        if (naveSeleccionada == null) return;

        limpiarPreview();
        coordenadasPreview = calcularCoordenadas(fila, col, naveSeleccionada.getTamanio());
        boolean valido = validarColocacion(coordenadasPreview);

        Color colorPreview = valido ? new Color(144, 238, 144) : new Color(255, 182, 193);

        for (CoordenadasDTO coord : coordenadasPreview) {
            if (coord.getX() >= 0 && coord.getX() < FILAS &&
                    coord.getY() >= 0 && coord.getY() < COLUMNAS) {
                if (!casillaOcupada[coord.getX()][coord.getY()]) {
                    casillas[coord.getX()][coord.getY()].setBackground(colorPreview);
                }
            }
        }
    }

    private void limpiarPreview() {
        for (CoordenadasDTO coord : coordenadasPreview) {
            if (coord.getX() >= 0 && coord.getX() < FILAS &&
                    coord.getY() >= 0 && coord.getY() < COLUMNAS) {
                if (!casillaOcupada[coord.getX()][coord.getY()]) {
                    casillas[coord.getX()][coord.getY()].setBackground(new Color(173, 216, 230));
                }
            }
        }
        coordenadasPreview.clear();
    }

    private List<CoordenadasDTO> calcularCoordenadas(int fila, int col, int tamanio) {
        List<CoordenadasDTO> coords = new ArrayList<>();
        for (int i = 0; i < tamanio; i++) {
            if (orientacionActual == OrientacionNave.HORIZONTAL) {
                coords.add(new CoordenadasDTO(fila, col + i));
            } else {
                coords.add(new CoordenadasDTO(fila + i, col));
            }
        }
        return coords;
    }

    private boolean validarColocacion(List<CoordenadasDTO> coordenadas) {
        for (CoordenadasDTO coord : coordenadas) {
            if (coord.getX() < 0 || coord.getX() >= FILAS ||
                    coord.getY() < 0 || coord.getY() >= COLUMNAS) {
                return false;
            }
            if (casillaOcupada[coord.getX()][coord.getY()]) {
                return false;
            }
            if (hayNaveAdyacente(coord.getX(), coord.getY(), coordenadas)) {
                return false;
            }
        }
        return true;
    }

    private boolean hayNaveAdyacente(int fila, int col, List<CoordenadasDTO> excluir) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                int newFila = fila + i;
                int newCol = col + j;
                if (newFila >= 0 && newFila < FILAS && newCol >= 0 && newCol < COLUMNAS) {
                    if (casillaOcupada[newFila][newCol]) {
                        boolean esParteDeNave = false;
                        for (CoordenadasDTO c : excluir) {
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
                    "Primero selecciona o arrastra una nave del astillero.",
                    "Sin nave seleccionada",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<CoordenadasDTO> coordenadas = calcularCoordenadas(fila, col, naveSeleccionada.getTamanio());

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
        for (CoordenadasDTO coord : coordenadas) {
            casillaOcupada[coord.getX()][coord.getY()] = true;
            casillas[coord.getX()][coord.getY()].setBackground(colorNave);
        }

        // Registrar nave colocada
        navesEnTablero.add(naveSeleccionada);
        TipoNaveDTO tipoColocado = naveSeleccionada.getTipo();
        navesColocadas.put(tipoColocado, navesColocadas.get(tipoColocado) + 1);

        // Actualizar panel del shipyard
        actualizarPanelShipyard(tipoColocado);

        // Enviar al servidor
        controlVista.addNave(naveSeleccionada, coordenadas);

        // Limpiar selección
        naveSeleccionada = null;
        lblInstrucciones.setText("¡Nave colocada! Arrastra otra nave al tablero.");

        // Verificar si se colocaron todas
        verificarTodasColocadas();
    }

    private Color getColorNave(TipoNaveDTO tipo) {
        if (colorNaveJugador != null) {
            float[] hsb = Color.RGBtoHSB(
                    colorNaveJugador.getRed(),
                    colorNaveJugador.getGreen(),
                    colorNaveJugador.getBlue(),
                    null
            );

            float ajusteBrillo = switch (tipo) {
                case PORTAAVIONES -> 0.9f;
                case CRUCERO -> 1.0f;
                case SUBMARINO -> 1.1f;
                case BARCO -> 1.2f;
                default -> 1.0f;
            };

            float nuevoBrillo = Math.min(1.0f, hsb[2] * ajusteBrillo);
            return Color.getHSBColor(hsb[0], hsb[1], nuevoBrillo);
        }

        return switch (tipo) {
            case PORTAAVIONES -> new Color(128, 0, 128);
            case CRUCERO -> new Color(0, 100, 0);
            case SUBMARINO -> new Color(0, 0, 139);
            case BARCO -> new Color(139, 69, 19);
            default -> Color.GRAY;
        };
    }

    private void actualizarPanelShipyard(TipoNaveDTO tipo) {
        int disponibles = navesDisponibles.get(tipo) - navesColocadas.get(tipo);

        // Actualizar el título del panel
        JPanel panelNave = panelesNaveShipyard.get(tipo);
        if (panelNave != null) {
            panelNave.setBorder(BorderFactory.createTitledBorder(getNombreNave(tipo) + " (" + disponibles + ")"));

            // Deshabilitar si no quedan
            if (disponibles <= 0) {
                for (java.awt.Component comp : panelNave.getComponents()) {
                    comp.setEnabled(false);
                }
                panelNave.setEnabled(false);
            }
        }

        // Actualizar botón
        JButton btn = botonesShipyard.get(tipo);
        if (btn != null && disponibles <= 0) {
            btn.setEnabled(false);
        }
    }

    private void verificarTodasColocadas() {
        int totalNecesarias = navesDisponibles.values().stream().mapToInt(Integer::intValue).sum();
        int totalColocadas = navesColocadas.values().stream().mapToInt(Integer::intValue).sum();

        if (totalColocadas >= totalNecesarias) {
            btnListo.setEnabled(true);
            lblInstrucciones.setText("¡Todas las naves colocadas! Presiona LISTO para continuar.");
        }
    }

    private void confirmarTablero() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de tu configuración?\nNo podrás modificarla después.",
                "Confirmar tablero",
                JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            TimerPanel timerPanel = new TimerPanel(1000, 30);
            controlVista.setTimer(timerPanel);

            controlVista.initTableroPropio();
            controlVista.initTableroEnemigo();

            marcarNavesEnTableroPropio();

            btnListo.setEnabled(false);
            btnRegresar.setEnabled(false);
            btnRotar.setEnabled(false);
            panelesNaveShipyard.values().forEach(p -> p.setEnabled(false));
            botonesShipyard.values().forEach(b -> b.setEnabled(false));

            esperandoOtroJugador = true;
            mostrarEstadoEspera();

            controlVista.getControl().confirmarTablero();
        }
    }

    private void mostrarEstadoEspera() {
        lblInstrucciones.setText("Tablero confirmado. Esperando al otro jugador...");

        JPanel pnlEspera = new JPanel();
        pnlEspera.setLayout(new BoxLayout(pnlEspera, BoxLayout.Y_AXIS));
        pnlEspera.setBackground(new Color(255, 255, 200));
        pnlEspera.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.ORANGE, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        lblEstadoEspera = new JLabel("Esperando a que el otro jugador confirme su tablero...");
        lblEstadoEspera.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        lblEstadoEspera.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblIcono = new JLabel("⏳");
        lblIcono.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 48));
        lblIcono.setAlignmentX(CENTER_ALIGNMENT);

        pnlEspera.add(lblIcono);
        pnlEspera.add(Box.createVerticalStrut(10));
        pnlEspera.add(lblEstadoEspera);

        pnlControles.removeAll();
        pnlControles.add(pnlEspera);
        pnlControles.revalidate();
        pnlControles.repaint();
    }

    private void iniciarPartida() {
        SwingUtilities.invokeLater(() -> {
            controlVista.desuscribirLobby(this);
            controlVista.mostrarFrmPartidaEnCurso();
            dispose();
        });
    }

    private void marcarNavesEnTableroPropio() {
        List<CasillaPanel> casillasPropias = controlVista.getCasillasPropias();
        if (casillasPropias == null) return;

        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (casillaOcupada[i][j]) {
                    final int fila = i;
                    final int col = j;
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
