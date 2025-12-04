package cliente.presentacion.frames;

import cliente.controlador.ControlVista;
import cliente.presentacion.componentes.CasillaPanel;
import cliente.presentacion.componentes.TimerPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
 * Permite ARRASTRAR Y SOLTAR naves del astillero al tablero.
 * Click derecho para rotar las naves.
 *
 * @author Equipo
 */
public class FrmColocarNaves extends JFrame implements ISuscriptor {

    // Colores del tema
    private static final Color COLOR_FONDO = new Color(74, 89, 98);
    private static final Color COLOR_FONDO_CLARO = new Color(94, 109, 118);
    private static final Color COLOR_PANEL = new Color(240, 240, 240);
    private static final Color COLOR_BOTON = new Color(45, 45, 45);
    private static final Color COLOR_BOTON_HOVER = new Color(65, 65, 65);
    private static final Color COLOR_BOTON_DESHABILITADO = new Color(128, 128, 128);
    private static final Color COLOR_BOTON_ABANDONAR = new Color(139, 0, 0);
    private static final Color COLOR_BOTON_ABANDONAR_HOVER = new Color(178, 34, 34);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_AGUA = new Color(173, 216, 230);
    private static final Color COLOR_PREVIEW_VALIDO = new Color(144, 238, 144);
    private static final Color COLOR_PREVIEW_INVALIDO = new Color(255, 182, 193);

    // Constantes del tablero
    private static final int FILAS = 10;
    private static final int COLUMNAS = 10;
    private static final int CELL_SIZE = 35;
    private static final int LABEL_WIDTH = 25;

    // Componentes de UI
    private JLayeredPane layeredPane;
    private JPanel pnlContenido;
    private JPanel pnlTableroGrid;
    private JButton[][] casillas;
    private JLabel lblEstado;
    private JButton btnComenzarListo;
    private JButton btnLimpiar;
    private JButton btnAbandonar;

    // Estado del juego
    private ControlVista controlVista;
    private NaveDTO naveSeleccionada;
    private OrientacionNave orientacionActual;
    private List<CoordenadasDTO> coordenadasPreview;
    private boolean esHost;

    // Control de naves
    private Map<TipoNaveDTO, Integer> navesDisponibles;
    private Map<TipoNaveDTO, Integer> navesColocadas;
    private List<NaveDTO> navesEnTablero;
    private boolean[][] casillaOcupada;
    private Map<TipoNaveDTO, JLabel> lblContadores;

    // Estado de espera
    private boolean esperandoOtroJugador = false;
    private boolean jugador2Listo = false;
    private Map<String, Consumer<Object>> manejadoresNotificacion;

    // Color del jugador
    private ColorJugador colorJugador;
    private Color colorNaveJugador;

    // Bandera para evitar iniciar partida multiples veces
    private boolean partidaIniciada = false;

    // Variables para Drag & Drop
    private JPanel naveDragVisual;
    private boolean arrastrando = false;
    private Point dragOffset;
    private TipoNaveDTO tipoDragActual;
    private int tamanioDragActual;

    public FrmColocarNaves() {
        this.controlVista = ControlVista.getInstancia();
        this.esHost = controlVista.isEsHost();
        initNavesDisponibles();
        initManejadoresNotificacion();
        initColorJugador();
        initComponents();

        controlVista.suscribirAModelo();
        controlVista.suscribirLobby(this);
    }

    private void initColorJugador() {
        JugadorDTO jugador = controlVista.getControl().getJugador();
        if (jugador != null && jugador.getColor() != null) {
            this.colorJugador = jugador.getColor();
            switch (colorJugador) {
                case ROJO -> colorNaveJugador = new Color(220, 20, 60);
                case AZUL -> colorNaveJugador = new Color(30, 144, 255);
                default -> colorNaveJugador = Color.GRAY;
            }
        } else {
            this.colorJugador = ColorJugador.AZUL;
            this.colorNaveJugador = new Color(30, 144, 255);
        }
    }

    private void initManejadoresNotificacion() {
        manejadoresNotificacion = new HashMap<>();
        manejadoresNotificacion.put("TABLEROS_LISTOS", this::manejarTablerosListos);
        manejadoresNotificacion.put("TURNO_INICIAL", this::manejarTurnoInicial);
        manejadoresNotificacion.put("CONFIRMAR_TABLERO", this::manejarConfirmacionOtroJugador);
        manejadoresNotificacion.put("JUGADOR_LISTO", d -> { jugador2Listo = true; actualizarBotonComenzar(); });
        manejadoresNotificacion.put("ABANDONO_PARTIDA", this::manejarAbandonoOponente);
    }

    private void manejarAbandonoOponente(Object datos) {
        if (datos instanceof JugadorDTO jugadorAbandono) {
            // Verificar que no soy yo quien abandonó
            JugadorDTO miJugador = controlVista.getControl().getJugador();
            if (miJugador != null && jugadorAbandono.getNombre().equals(miJugador.getNombre())) {
                return; // Soy yo quien abandonó, ignorar
            }

            // El oponente abandonó, yo gano - mostrar pantalla de victoria
            controlVista.desuscribirLobby(this);

            // Mostrar pantalla de fin de partida con victoria por abandono
            FrmFinPartida frmFin = new FrmFinPartida(miJugador, true, true); // ganador, por abandono
            frmFin.setVisible(true);
            dispose();
        }
    }

    @Override
    public void notificar(String contexto, Object datos) {
        Consumer<Object> manejador = manejadoresNotificacion.get(contexto);
        if (manejador != null) {
            SwingUtilities.invokeLater(() -> manejador.accept(datos));
        }
    }

    private void manejarTablerosListos(Object datos) {
        if (partidaIniciada) return;
        partidaIniciada = true;
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
                jugador2Listo = true;
                actualizarBotonComenzar();
                lblEstado.setText("El otro jugador esta listo.");
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
        lblContadores = new HashMap<>();
    }

    private void initComponents() {
        setTitle("Battleship - Colocar Naves");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        // Manejar cierre con X para notificar abandono
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                abandonar();
            }
        });

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(950, 620));
        setContentPane(layeredPane);

        // Panel de contenido con gradiente
        pnlContenido = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, COLOR_FONDO, 0, getHeight(), COLOR_FONDO_CLARO);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        pnlContenido.setBounds(0, 0, 950, 620);
        pnlContenido.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        layeredPane.add(pnlContenido, JLayeredPane.DEFAULT_LAYER);

        // Panel superior
        JPanel pnlSuperior = crearPanelSuperior();
        pnlContenido.add(pnlSuperior, BorderLayout.NORTH);

        // Panel central (tablero + astillero)
        JPanel pnlCentral = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        pnlCentral.setOpaque(false);

        JPanel pnlTablero = crearPanelTablero();
        pnlCentral.add(pnlTablero);

        JPanel pnlAstillero = crearPanelAstillero();
        pnlCentral.add(pnlAstillero);

        pnlContenido.add(pnlCentral, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel pnlInferior = crearPanelBotones();
        pnlContenido.add(pnlInferior, BorderLayout.SOUTH);

        configurarDragGlobal();

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lblTitulo = new JLabel("Coloca tus Naves", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblTitulo);

        panel.add(Box.createVerticalStrut(5));

        JLabel lblInstruccion = new JLabel("Arrastra las naves al tablero | Click derecho para rotar", SwingConstants.CENTER);
        lblInstruccion.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblInstruccion.setForeground(new Color(200, 200, 200));
        lblInstruccion.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblInstruccion);

        panel.add(Box.createVerticalStrut(5));

        lblEstado = new JLabel("Coloca todas tus naves para continuar", SwingConstants.CENTER);
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(new Color(255, 255, 150));
        lblEstado.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblEstado);

        return panel;
    }

    private JPanel crearPanelTablero() {
        JPanel contenedor = new JPanel(new BorderLayout(5, 5));
        contenedor.setOpaque(false);

        // Titulo del tablero
        JLabel lblTablero = new JLabel("Tu Tablero", SwingConstants.CENTER);
        lblTablero.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTablero.setForeground(COLOR_TEXTO);
        contenedor.add(lblTablero, BorderLayout.NORTH);

        // Panel con etiquetas de columnas (usando FlowLayout para mejor alineación)
        JPanel pnlColumnas = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlColumnas.setOpaque(false);

        // Espaciador para alinear con los números de fila
        JLabel espaciador = new JLabel("");
        espaciador.setPreferredSize(new Dimension(LABEL_WIDTH, 20));
        pnlColumnas.add(espaciador);

        for (int i = 0; i < COLUMNAS; i++) {
            JLabel lbl = new JLabel(String.valueOf((char) ('A' + i)), SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(COLOR_TEXTO);
            lbl.setPreferredSize(new Dimension(CELL_SIZE, 20));
            pnlColumnas.add(lbl);
        }

        JPanel pnlPrincipal = new JPanel(new BorderLayout(0, 0));
        pnlPrincipal.setOpaque(false);
        pnlPrincipal.add(pnlColumnas, BorderLayout.NORTH);

        // Panel de filas numeradas
        JPanel pnlFilas = new JPanel(new GridLayout(FILAS, 1, 0, 1));
        pnlFilas.setOpaque(false);
        pnlFilas.setPreferredSize(new Dimension(LABEL_WIDTH, FILAS * CELL_SIZE));
        for (int i = 0; i < FILAS; i++) {
            JLabel lbl = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(COLOR_TEXTO);
            pnlFilas.add(lbl);
        }
        pnlPrincipal.add(pnlFilas, BorderLayout.WEST);

        // Grid del tablero
        pnlTableroGrid = new JPanel(new GridLayout(FILAS, COLUMNAS, 1, 1));
        pnlTableroGrid.setBackground(Color.DARK_GRAY);
        pnlTableroGrid.setPreferredSize(new Dimension(COLUMNAS * CELL_SIZE, FILAS * CELL_SIZE));
        casillas = new JButton[FILAS][COLUMNAS];

        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                final int fila = i;
                final int col = j;
                casillas[i][j] = new JButton();
                casillas[i][j].setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                casillas[i][j].setBackground(COLOR_AGUA);
                casillas[i][j].setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
                casillas[i][j].setFocusPainted(false);

                casillas[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent evt) {
                        if (arrastrando && naveSeleccionada != null) {
                            mostrarPreview(fila, col);
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent evt) {
                        // No limpiar preview aquí, se maneja en el drag global
                    }

                    @Override
                    public void mouseClicked(MouseEvent evt) {
                        // Click derecho para rotar (solo si se está arrastrando)
                        if (SwingUtilities.isRightMouseButton(evt) && arrastrando) {
                            rotarNave();
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

    private JPanel crearPanelAstillero() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setPreferredSize(new Dimension(200, 400));

        JLabel lblTitulo = new JLabel("NAVES DISPONIBLES");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblTitulo);

        panel.add(Box.createVerticalStrut(5));

        JLabel lblColor = new JLabel("Tu color: " + (colorJugador != null ? colorJugador.name() : "AZUL"));
        lblColor.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblColor.setForeground(colorNaveJugador);
        lblColor.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblColor);

        panel.add(Box.createVerticalStrut(15));

        agregarNaveAlAstillero(panel, TipoNaveDTO.PORTAAVIONES, "Porta aviones", 4);
        agregarNaveAlAstillero(panel, TipoNaveDTO.CRUCERO, "Crucero", 3);
        agregarNaveAlAstillero(panel, TipoNaveDTO.SUBMARINO, "Submarino", 2);
        agregarNaveAlAstillero(panel, TipoNaveDTO.BARCO, "Barco", 1);

        panel.add(Box.createVerticalStrut(15));

        // Leyenda
        JPanel leyenda = new JPanel();
        leyenda.setLayout(new BoxLayout(leyenda, BoxLayout.Y_AXIS));
        leyenda.setBackground(COLOR_PANEL);
        leyenda.setBorder(BorderFactory.createTitledBorder("Leyenda"));

        agregarItemLeyenda(leyenda, COLOR_AGUA, "Agua");
        agregarItemLeyenda(leyenda, COLOR_PREVIEW_VALIDO, "Posicion valida");
        agregarItemLeyenda(leyenda, COLOR_PREVIEW_INVALIDO, "Posicion invalida");

        panel.add(leyenda);

        return panel;
    }

    private void agregarNaveAlAstillero(JPanel panel, TipoNaveDTO tipo, String nombre, int tamanio) {
        int disponibles = navesDisponibles.get(tipo);

        JPanel contenedor = new JPanel(new BorderLayout(5, 3));
        contenedor.setBackground(COLOR_PANEL);
        contenedor.setMaximumSize(new Dimension(180, 50));
        contenedor.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Nombre y contador
        JLabel lblNombre = new JLabel(nombre);
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblContador = new JLabel("x" + disponibles);
        lblContador.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblContador.setForeground(new Color(0, 100, 0));
        lblContadores.put(tipo, lblContador);

        JPanel pnlInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlInfo.setBackground(COLOR_PANEL);
        pnlInfo.add(lblNombre);
        pnlInfo.add(lblContador);
        contenedor.add(pnlInfo, BorderLayout.NORTH);

        // Visual de la nave (arrastrable)
        JPanel pnlNave = new JPanel(new GridLayout(1, tamanio, 1, 0));
        pnlNave.setPreferredSize(new Dimension(tamanio * 22, 20));
        pnlNave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        for (int i = 0; i < tamanio; i++) {
            JPanel celda = new JPanel();
            celda.setBackground(colorNaveJugador);
            celda.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            pnlNave.add(celda);
        }

        pnlNave.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int restantes = navesDisponibles.get(tipo) - navesColocadas.get(tipo);
                    if (restantes > 0) {
                        iniciarArrastre(tipo, tamanio, e, pnlNave);
                    } else {
                        lblEstado.setText("Ya colocaste todas las " + nombre + ".");
                        lblEstado.setForeground(new Color(255, 150, 150));
                    }
                }
                if (SwingUtilities.isRightMouseButton(e)) {
                    rotarNave();
                    lblEstado.setText("Orientacion: " + (orientacionActual == OrientacionNave.HORIZONTAL ? "Horizontal" : "Vertical"));
                }
            }
        });

        pnlNave.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (arrastrando && naveDragVisual != null) {
                    Point puntoEnLayered = SwingUtilities.convertPoint(pnlNave, e.getPoint(), layeredPane);
                    naveDragVisual.setLocation(puntoEnLayered.x - dragOffset.x, puntoEnLayered.y - dragOffset.y);

                    int[] pos = calcularPosicionTablero(puntoEnLayered);
                    if (pos != null) {
                        mostrarPreview(pos[0], pos[1]);
                    } else {
                        limpiarPreview();
                    }
                    layeredPane.repaint();
                }
            }
        });

        contenedor.add(pnlNave, BorderLayout.CENTER);
        panel.add(contenedor);
        panel.add(Box.createVerticalStrut(5));
    }

    private void agregarItemLeyenda(JPanel panel, Color color, String texto) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        item.setBackground(COLOR_PANEL);

        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(15, 15));
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        item.add(colorBox);

        item.add(new JLabel(texto));
        panel.add(item);
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setOpaque(false);

        // Boton Limpiar
        btnLimpiar = crearBoton("Limpiar", COLOR_BOTON, COLOR_BOTON_HOVER);
        btnLimpiar.addActionListener(e -> limpiarTablero());
        panel.add(btnLimpiar);

        // Boton Comenzar/Listo
        String textoBoton = esHost ? "Comenzar" : "Listo";
        btnComenzarListo = crearBoton(textoBoton, COLOR_BOTON_DESHABILITADO, COLOR_BOTON_DESHABILITADO);
        btnComenzarListo.setEnabled(false);
        btnComenzarListo.addActionListener(e -> accionComenzarListo());
        panel.add(btnComenzarListo);

        // Boton Abandonar
        btnAbandonar = crearBoton("Abandonar", COLOR_BOTON_ABANDONAR, COLOR_BOTON_ABANDONAR_HOVER);
        btnAbandonar.addActionListener(e -> abandonar());
        panel.add(btnAbandonar);

        return panel;
    }

    private JButton crearBoton(String texto, Color colorNormal, Color colorHover) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(COLOR_TEXTO);
        btn.setBackground(colorNormal);
        btn.setPreferredSize(new Dimension(120, 38));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                if (btn.isEnabled()) btn.setBackground(colorHover);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                if (btn.isEnabled()) btn.setBackground(colorNormal);
            }
        });

        return btn;
    }

    private void configurarDragGlobal() {
        layeredPane.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (arrastrando && naveDragVisual != null) {
                    int x = e.getX() - dragOffset.x;
                    int y = e.getY() - dragOffset.y;
                    naveDragVisual.setLocation(x, y);

                    int[] pos = calcularPosicionTablero(e.getPoint());
                    if (pos != null) {
                        mostrarPreview(pos[0], pos[1]);
                    } else {
                        limpiarPreview();
                    }
                }
            }
        });

        layeredPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // Solo responder al botón izquierdo, ignorar click derecho
                if (SwingUtilities.isLeftMouseButton(e) && arrastrando && naveDragVisual != null) {
                    int[] pos = calcularPosicionTablero(e.getPoint());
                    if (pos != null) {
                        colocarNaveDrag(pos[0], pos[1]);
                    }
                    finalizarArrastre();
                }
            }
        });
    }

    private int[] calcularPosicionTablero(Point puntoEnLayered) {
        Point puntoEnTablero = SwingUtilities.convertPoint(layeredPane, puntoEnLayered, pnlTableroGrid);

        // Calcular fila y columna considerando el tamaño real de celda con gaps
        int gridWidth = pnlTableroGrid.getWidth();
        int gridHeight = pnlTableroGrid.getHeight();

        if (puntoEnTablero.x < 0 || puntoEnTablero.y < 0 ||
            puntoEnTablero.x >= gridWidth || puntoEnTablero.y >= gridHeight) {
            return null;
        }

        int col = (puntoEnTablero.x * COLUMNAS) / gridWidth;
        int fila = (puntoEnTablero.y * FILAS) / gridHeight;

        // Asegurar que esté dentro de límites
        fila = Math.max(0, Math.min(FILAS - 1, fila));
        col = Math.max(0, Math.min(COLUMNAS - 1, col));

        return new int[]{fila, col};
    }

    private void iniciarArrastre(TipoNaveDTO tipo, int tamanio, MouseEvent e, JPanel panelOrigen) {
        arrastrando = true;
        tipoDragActual = tipo;
        tamanioDragActual = tamanio;
        naveSeleccionada = new NaveDTO(EstadoNave.SIN_DAÑOS, orientacionActual, tipo, tamanio);

        naveDragVisual = crearPanelNaveVisual(tamanio, orientacionActual);
        dragOffset = new Point(naveDragVisual.getWidth() / 2, naveDragVisual.getHeight() / 2);

        Point puntoEnLayered = SwingUtilities.convertPoint(panelOrigen, e.getPoint(), layeredPane);
        naveDragVisual.setLocation(puntoEnLayered.x - dragOffset.x, puntoEnLayered.y - dragOffset.y);

        layeredPane.add(naveDragVisual, JLayeredPane.DRAG_LAYER);
        layeredPane.repaint();

        lblEstado.setText("Arrastrando " + getNombreNave(tipo) + " - Suelta sobre el tablero");
        lblEstado.setForeground(new Color(255, 255, 150));

        // Añadir listener para cuando se suelta el mouse en el panel origen
        MouseAdapter releaseListener = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent evt) {
                // Solo responder al botón izquierdo, ignorar click derecho
                if (SwingUtilities.isLeftMouseButton(evt) && arrastrando) {
                    Point puntoEnLayered = SwingUtilities.convertPoint(panelOrigen, evt.getPoint(), layeredPane);
                    int[] pos = calcularPosicionTablero(puntoEnLayered);
                    if (pos != null) {
                        colocarNaveDrag(pos[0], pos[1]);
                    }
                    finalizarArrastre();
                    panelOrigen.removeMouseListener(this);
                }
            }
        };
        panelOrigen.addMouseListener(releaseListener);
    }

    private JPanel crearPanelNaveVisual(int tamanio, OrientacionNave orientacion) {
        int filas = orientacion == OrientacionNave.VERTICAL ? tamanio : 1;
        int cols = orientacion == OrientacionNave.HORIZONTAL ? tamanio : 1;

        JPanel panel = new JPanel(new GridLayout(filas, cols, 1, 1));
        panel.setOpaque(false);
        panel.setSize(cols * CELL_SIZE, filas * CELL_SIZE);

        for (int i = 0; i < tamanio; i++) {
            JPanel celda = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(new Color(colorNaveJugador.getRed(), colorNaveJugador.getGreen(), colorNaveJugador.getBlue(), 180));
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

    private void finalizarArrastre() {
        if (naveDragVisual != null) {
            layeredPane.remove(naveDragVisual);
            naveDragVisual = null;
        }
        arrastrando = false;
        tipoDragActual = null;
        tamanioDragActual = 0;
        naveSeleccionada = null;
        limpiarPreview();
        actualizarEstado();
        lblEstado.setForeground(new Color(255, 255, 150));
        layeredPane.repaint();
    }

    private void colocarNaveDrag(int fila, int col) {
        if (tipoDragActual == null) return;
        naveSeleccionada = new NaveDTO(EstadoNave.SIN_DAÑOS, orientacionActual, tipoDragActual, tamanioDragActual);
        colocarNave(fila, col);
    }

    private void rotarNave() {
        if (orientacionActual == OrientacionNave.HORIZONTAL) {
            orientacionActual = OrientacionNave.VERTICAL;
        } else {
            orientacionActual = OrientacionNave.HORIZONTAL;
        }

        if (naveSeleccionada != null) {
            naveSeleccionada.setOrientacion(orientacionActual);
        }

        if (arrastrando && naveDragVisual != null && tipoDragActual != null) {
            Point ubicacion = naveDragVisual.getLocation();
            layeredPane.remove(naveDragVisual);
            naveDragVisual = crearPanelNaveVisual(tamanioDragActual, orientacionActual);
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

        Color colorPreview = valido ? COLOR_PREVIEW_VALIDO : COLOR_PREVIEW_INVALIDO;

        for (CoordenadasDTO coord : coordenadasPreview) {
            if (coord.getX() >= 0 && coord.getX() < FILAS && coord.getY() >= 0 && coord.getY() < COLUMNAS) {
                if (!casillaOcupada[coord.getX()][coord.getY()]) {
                    casillas[coord.getX()][coord.getY()].setBackground(colorPreview);
                }
            }
        }
    }

    private void limpiarPreview() {
        for (CoordenadasDTO coord : coordenadasPreview) {
            if (coord.getX() >= 0 && coord.getX() < FILAS && coord.getY() >= 0 && coord.getY() < COLUMNAS) {
                if (!casillaOcupada[coord.getX()][coord.getY()]) {
                    casillas[coord.getX()][coord.getY()].setBackground(COLOR_AGUA);
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
            if (coord.getX() < 0 || coord.getX() >= FILAS || coord.getY() < 0 || coord.getY() >= COLUMNAS) {
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
                        boolean esParteDeNave = excluir.stream()
                                .anyMatch(c -> c.getX() == newFila && c.getY() == newCol);
                        if (!esParteDeNave) return true;
                    }
                }
            }
        }
        return false;
    }

    private void colocarNave(int fila, int col) {
        if (naveSeleccionada == null) return;

        List<CoordenadasDTO> coordenadas = calcularCoordenadas(fila, col, naveSeleccionada.getTamanio());

        if (!validarColocacion(coordenadas)) {
            // No mostrar popup molesto, solo feedback visual
            lblEstado.setText("Posicion invalida - Intenta en otro lugar");
            lblEstado.setForeground(new Color(255, 150, 150));
            return;
        }

        // Colocar la nave visualmente
        for (CoordenadasDTO coord : coordenadas) {
            casillaOcupada[coord.getX()][coord.getY()] = true;
            casillas[coord.getX()][coord.getY()].setBackground(colorNaveJugador);
        }

        navesEnTablero.add(naveSeleccionada);
        TipoNaveDTO tipoColocado = naveSeleccionada.getTipo();
        navesColocadas.put(tipoColocado, navesColocadas.get(tipoColocado) + 1);

        // Feedback de éxito
        lblEstado.setText(getNombreNave(tipoColocado) + " colocado!");
        lblEstado.setForeground(new Color(150, 255, 150));

        actualizarContador(tipoColocado);
        controlVista.addNave(naveSeleccionada, coordenadas);

        naveSeleccionada = null;
        verificarTodasColocadas();

        // Actualizar estado después de un momento
        javax.swing.Timer t = new javax.swing.Timer(1000, e -> actualizarEstado());
        t.setRepeats(false);
        t.start();
    }

    private void actualizarContador(TipoNaveDTO tipo) {
        int restantes = navesDisponibles.get(tipo) - navesColocadas.get(tipo);
        JLabel lbl = lblContadores.get(tipo);
        if (lbl != null) {
            lbl.setText("x" + restantes);
            if (restantes <= 0) {
                lbl.setForeground(Color.RED);
            }
        }
    }

    private void actualizarEstado() {
        int totalNecesarias = navesDisponibles.values().stream().mapToInt(Integer::intValue).sum();
        int totalColocadas = navesColocadas.values().stream().mapToInt(Integer::intValue).sum();

        if (totalColocadas >= totalNecesarias) {
            lblEstado.setText("Todas las naves colocadas. Presiona " + (esHost ? "Comenzar" : "Listo"));
        } else {
            lblEstado.setText("Naves colocadas: " + totalColocadas + "/" + totalNecesarias);
        }
    }

    private void verificarTodasColocadas() {
        int totalNecesarias = navesDisponibles.values().stream().mapToInt(Integer::intValue).sum();
        int totalColocadas = navesColocadas.values().stream().mapToInt(Integer::intValue).sum();

        if (totalColocadas >= totalNecesarias) {
            if (!esHost) {
                // Jugador 2 puede dar Listo inmediatamente
                habilitarBotonComenzar(true);
            } else {
                // Host necesita esperar a que jugador 2 esté listo
                actualizarBotonComenzar();
            }
        }
    }

    private void actualizarBotonComenzar() {
        int totalNecesarias = navesDisponibles.values().stream().mapToInt(Integer::intValue).sum();
        int totalColocadas = navesColocadas.values().stream().mapToInt(Integer::intValue).sum();
        boolean todasColocadas = totalColocadas >= totalNecesarias;

        if (esHost) {
            // Host: habilitar solo si colocó todas y jugador 2 está listo
            habilitarBotonComenzar(todasColocadas && jugador2Listo);
        } else {
            // Jugador 2: habilitar si colocó todas
            habilitarBotonComenzar(todasColocadas);
        }
    }

    private void habilitarBotonComenzar(boolean habilitar) {
        btnComenzarListo.setEnabled(habilitar);
        if (habilitar) {
            btnComenzarListo.setBackground(COLOR_BOTON);
        } else {
            btnComenzarListo.setBackground(COLOR_BOTON_DESHABILITADO);
        }
    }

    private void limpiarTablero() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Quieres limpiar el tablero y empezar de nuevo?",
                "Limpiar tablero", JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            // Limpiar casillas
            for (int i = 0; i < FILAS; i++) {
                for (int j = 0; j < COLUMNAS; j++) {
                    casillaOcupada[i][j] = false;
                    casillas[i][j].setBackground(COLOR_AGUA);
                }
            }

            // Reiniciar contadores
            navesColocadas.replaceAll((k, v) -> 0);
            navesEnTablero.clear();

            // Actualizar labels
            for (TipoNaveDTO tipo : navesDisponibles.keySet()) {
                JLabel lbl = lblContadores.get(tipo);
                if (lbl != null) {
                    lbl.setText("x" + navesDisponibles.get(tipo));
                    lbl.setForeground(new Color(0, 100, 0));
                }
            }

            habilitarBotonComenzar(false);
            actualizarEstado();

            // Notificar al servidor para limpiar naves
            controlVista.limpiarNaves();
        }
    }

    private void accionComenzarListo() {
        TimerPanel timerPanel = new TimerPanel(1000, 30);
        controlVista.setTimer(timerPanel);
        controlVista.initTableroPropio();
        controlVista.initTableroEnemigo();
        marcarNavesEnTableroPropio();

        if (esHost) {
            if (jugador2Listo) {
                controlVista.getControl().confirmarTablero();
                esperandoOtroJugador = true;
                btnComenzarListo.setEnabled(false);
                btnLimpiar.setEnabled(false);
                lblEstado.setText("Esperando sincronizacion...");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Esperando a que el otro jugador esté listo.",
                        "Esperando", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            // Jugador 2 da Listo
            controlVista.getControl().confirmarTablero();
            btnComenzarListo.setVisible(false);
            btnLimpiar.setEnabled(false);
            lblEstado.setText("Listo! Esperando al Host...");
        }
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

    private void iniciarPartida() {
        SwingUtilities.invokeLater(() -> {
            controlVista.desuscribirLobby(this);
            controlVista.mostrarFrmPartidaEnCurso();
            dispose();
        });
    }

    private void abandonar() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Abandonar la partida?\nEl otro jugador ganara automaticamente.",
                "Confirmar abandono", JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            controlVista.abandonarPartida();
            controlVista.desuscribirLobby(this);
            FrmMenuPrincipal menu = new FrmMenuPrincipal();
            menu.setVisible(true);
            dispose();
        }
    }

    private String getNombreNave(TipoNaveDTO tipo) {
        return switch (tipo) {
            case PORTAAVIONES -> "Porta aviones";
            case CRUCERO -> "Crucero";
            case SUBMARINO -> "Submarino";
            case BARCO -> "Barco";
            default -> "Nave";
        };
    }
}
