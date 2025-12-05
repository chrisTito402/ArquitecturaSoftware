package views.frames;

import controllers.controller.ControlVista;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
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
import models.entidades.Coordenadas;
import models.enums.EstadoNave;
import models.enums.OrientacionNave;
import views.DTOs.NaveDTO;
import views.DTOs.TipoNaveDTO;
import views.DTOs.CoordenadasDTO;

/**
 * Pantalla para colocar las naves antes de iniciar la partida.
 * Permite seleccionar naves del "astillero" y colocarlas en el tablero.
 *
 * @author Equipo
 */
public class FrmColocarNaves extends JFrame {

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
    private JButton btnRotar;
    private JButton btnListo;
    private JButton btnRegresar;

    // Estado del juego
    private ControlVista controlVista;
    private NaveDTO naveSeleccionada;
    private OrientacionNave orientacionActual;
    private List<Coordenadas> coordenadasPreview;

    // Control de naves colocadas
    private Map<TipoNaveDTO, Integer> navesDisponibles;
    private Map<TipoNaveDTO, Integer> navesColocadas;
    private List<NaveDTO> navesEnTablero;
    private boolean[][] casillaOcupada;

    // Botones del shipyard
    private Map<TipoNaveDTO, JButton> botonesShipyard;

    public FrmColocarNaves() {
        this.controlVista = ControlVista.getInstancia();
        initNavesDisponibles();
        initComponents();
        configurarEventos();
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
        botonesShipyard = new HashMap<>();
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

        lblInstrucciones = new JLabel("Selecciona una nave del astillero y haz clic en el tablero para colocarla");
        lblInstrucciones.setAlignmentX(CENTER_ALIGNMENT);

        lblNaveSeleccionada = new JLabel("Nave seleccionada: Ninguna");
        lblNaveSeleccionada.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        lblNaveSeleccionada.setAlignmentX(CENTER_ALIGNMENT);

        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblInstrucciones);
        panel.add(Box.createVerticalStrut(5));
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

                // Eventos del mouse para preview
                casillas[i][j].addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        mostrarPreview(fila, col);
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        limpiarPreview();
                    }
                });

                // Click para colocar nave
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
        panel.setPreferredSize(new Dimension(200, 400));

        // Título del astillero
        JLabel lblAstillero = new JLabel("ASTILLERO");
        lblAstillero.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        lblAstillero.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblAstillero);
        panel.add(Box.createVerticalStrut(15));

        // Botones de naves
        agregarBotonNave(panel, TipoNaveDTO.PORTAAVIONES, "Portaaviones", 4, new Color(128, 0, 128));
        agregarBotonNave(panel, TipoNaveDTO.CRUCERO, "Crucero", 3, new Color(0, 100, 0));
        agregarBotonNave(panel, TipoNaveDTO.SUBMARINO, "Submarino", 2, new Color(0, 0, 139));
        agregarBotonNave(panel, TipoNaveDTO.BARCO, "Barco", 1, new Color(139, 69, 19));

        panel.add(Box.createVerticalStrut(20));

        // Botón rotar
        btnRotar = new JButton("Rotar (Horizontal)");
        btnRotar.setAlignmentX(CENTER_ALIGNMENT);
        btnRotar.setMaximumSize(new Dimension(180, 35));
        btnRotar.addActionListener(e -> rotarNave());
        panel.add(btnRotar);

        panel.add(Box.createVerticalStrut(10));

        // Leyenda
        JPanel leyenda = crearLeyenda();
        panel.add(leyenda);

        return panel;
    }

    private void agregarBotonNave(JPanel panel, TipoNaveDTO tipo, String nombre, int tamanio, Color color) {
        JPanel pnlNave = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlNave.setMaximumSize(new Dimension(200, 50));

        // Representación visual de la nave
        JPanel pnlVisual = new JPanel(new GridLayout(1, tamanio));
        for (int i = 0; i < tamanio; i++) {
            JPanel celda = new JPanel();
            celda.setPreferredSize(new Dimension(20, 20));
            celda.setBackground(color);
            celda.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            pnlVisual.add(celda);
        }

        // Botón con nombre y cantidad
        JButton btn = new JButton(nombre + " (" + navesDisponibles.get(tipo) + ")");
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(150, 30));
        btn.addActionListener(e -> seleccionarNave(tipo, tamanio, color));
        botonesShipyard.put(tipo, btn);

        pnlNave.add(pnlVisual);
        panel.add(pnlNave);
        panel.add(btn);
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
        lblNaveSeleccionada.setText("Nave seleccionada: " + getNombreNave(tipo) +
                " (Tamaño: " + tamanio + ", Disponibles: " + disponibles + ")");
        lblInstrucciones.setText("Haz clic en el tablero para colocar la nave");
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
            btnRotar.setText("Rotar (Vertical)");
        } else {
            orientacionActual = OrientacionNave.HORIZONTAL;
            btnRotar.setText("Rotar (Horizontal)");
        }

        if (naveSeleccionada != null) {
            naveSeleccionada.setOrientacion(orientacionActual);
        }
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
        //controlVista.addNave(naveSeleccionada, coordenadas);

        // Limpiar selección
        naveSeleccionada = null;
        lblNaveSeleccionada.setText("Nave seleccionada: Ninguna");
        lblInstrucciones.setText("Selecciona otra nave del astillero");

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
        JButton btn = botonesShipyard.get(tipo);
        btn.setText(getNombreNave(tipo) + " (" + disponibles + ")");
        if (disponibles <= 0) {
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
            // Crear e inicializar el TimerPanel (30 segundos por turno)
            TimerPanel timerPanel = new TimerPanel(1000, 30);
            controlVista.setTimer(timerPanel);

            // Inicializar tableros en ControlVista
            controlVista.initTableroPropio();
            controlVista.initTableroEnemigo();

            // Marcar las casillas propias donde hay naves
            marcarNavesEnTableroPropio();

            // Notificar al servidor que está listo
            controlVista.empezarPartida();

            // Abrir pantalla de partida
            controlVista.mostrarFrmPartidaEnCurso();
            dispose();
        }
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
            //FrmLobby lobby = new FrmLobby();
            //lobby.setVisible(true);
            dispose();
        }
    }
}
