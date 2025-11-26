package views.frames;

import controllers.controller.ControlVista;
import dtos.CoordenadasDTO;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.enums.OrientacionNave;
import models.enums.TipoNave;
import models.factories.NaveFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class FrmColocarNaves extends JFrame {

    private static final int TAM_TABLERO = 10;
    private static final int TAM_CASILLA = 35;

    private Jugador jugador;
    private CasillaTableroColocacion[][] casillas;
    private List<NaveArrastrable> navesDisponibles;
    private NaveArrastrable naveSeleccionada;
    private JPanel panelTablero;
    private JPanel panelNaves;
    private JPanel panelInfo;
    private JLabel lblInstrucciones;
    private JLabel lblNavesColocadas;
    private JButton btnConfirmar;
    private JButton btnLimpiar;
    private JButton btnRotar;

    private int navesColocadas = 0;
    private int totalNaves = 11;

    public FrmColocarNaves(Jugador jugador) {
        this.jugador = jugador;
        this.navesDisponibles = new ArrayList<>();

        setTitle("Battleship - Colocar Naves");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        inicializarComponentes();
        crearNaves();
        configurarVentana();
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBackground(UIConstants.COLOR_FONDO_PRINCIPAL);
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = UIHelper.crearTitulo("⚓ COLOCAR NAVES");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        panelCentral.setBackground(UIConstants.COLOR_FONDO_PRINCIPAL);

        crearPanelTablero();
        crearPanelNaves();

        panelCentral.add(panelTablero);
        panelCentral.add(panelNaves);

        panelPrincipal.add(panelCentral, BorderLayout.CENTER);

        crearPanelInferior();
        panelPrincipal.add(panelInfo, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
    }

    private void crearPanelTablero() {
        panelTablero = UIHelper.crearPanelConBorde("Tu Tablero - Jugador: " + jugador.getNombre());
        panelTablero.setLayout(new BorderLayout(5, 5));
        panelTablero.setPreferredSize(new Dimension(400, 420));

        JPanel panelEncabezadoColumnas = new JPanel(new GridLayout(1, TAM_TABLERO + 1));
        panelEncabezadoColumnas.setBackground(UIConstants.COLOR_FONDO_SECUNDARIO);
        panelEncabezadoColumnas.add(new JLabel(""));
        for (int i = 0; i < TAM_TABLERO; i++) {
            JLabel lbl = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
            lbl.setForeground(UIConstants.COLOR_TEXTO_PRINCIPAL);
            lbl.setFont(UIConstants.FONT_TEXTO_PEQUENO);
            panelEncabezadoColumnas.add(lbl);
        }
        panelTablero.add(panelEncabezadoColumnas, BorderLayout.NORTH);

        JPanel panelContenedor = new JPanel(new BorderLayout());
        panelContenedor.setBackground(UIConstants.COLOR_FONDO_SECUNDARIO);

        JPanel panelFilas = new JPanel(new GridLayout(TAM_TABLERO, 1));
        panelFilas.setBackground(UIConstants.COLOR_FONDO_SECUNDARIO);
        for (int i = 0; i < TAM_TABLERO; i++) {
            JLabel lbl = new JLabel(String.valueOf((char) ('A' + i)), SwingConstants.CENTER);
            lbl.setForeground(UIConstants.COLOR_TEXTO_PRINCIPAL);
            lbl.setFont(UIConstants.FONT_TEXTO_PEQUENO);
            lbl.setPreferredSize(new Dimension(20, TAM_CASILLA));
            panelFilas.add(lbl);
        }
        panelContenedor.add(panelFilas, BorderLayout.WEST);

        JPanel gridTablero = new JPanel(new GridLayout(TAM_TABLERO, TAM_TABLERO));
        gridTablero.setBackground(UIConstants.COLOR_FONDO_SECUNDARIO);

        casillas = new CasillaTableroColocacion[TAM_TABLERO][TAM_TABLERO];
        for (int fila = 0; fila < TAM_TABLERO; fila++) {
            for (int col = 0; col < TAM_TABLERO; col++) {
                CasillaTableroColocacion casilla = new CasillaTableroColocacion(new CoordenadasDTO(fila, col));
                final int f = fila;
                final int c = col;

                casilla.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (naveSeleccionada != null && !naveSeleccionada.isColocada()) {
                            intentarColocarNave(f, c);
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (naveSeleccionada != null && !naveSeleccionada.isColocada()) {
                            mostrarPreview(f, c);
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        limpiarPreview();
                    }
                });

                casillas[fila][col] = casilla;
                gridTablero.add(casilla);
            }
        }
        panelContenedor.add(gridTablero, BorderLayout.CENTER);
        panelTablero.add(panelContenedor, BorderLayout.CENTER);
    }

    private void crearPanelNaves() {
        panelNaves = UIHelper.crearPanelConBorde("Naves Disponibles");
        panelNaves.setLayout(new BoxLayout(panelNaves, BoxLayout.Y_AXIS));
        panelNaves.setPreferredSize(new Dimension(200, 420));

        lblInstrucciones = new JLabel("<html><center>Selecciona una nave<br>y haz clic en el tablero<br><br>Doble clic para rotar</center></html>");
        lblInstrucciones.setForeground(UIConstants.COLOR_TEXTO_SECUNDARIO);
        lblInstrucciones.setFont(UIConstants.FONT_TEXTO_PEQUENO);
        lblInstrucciones.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNaves.add(lblInstrucciones);
        panelNaves.add(Box.createVerticalStrut(10));
    }

    private void crearNaves() {
        Color colorJugador = obtenerColorJugador();

        agregarGrupoNaves("Portaaviones (4)", TipoNave.PORTAAVIONES, 2, colorJugador);
        agregarGrupoNaves("Cruceros (3)", TipoNave.CRUCERO, 2, colorJugador);
        agregarGrupoNaves("Submarinos (2)", TipoNave.SUBMARINO, 4, colorJugador);
        agregarGrupoNaves("Barcos (1)", TipoNave.BARCO, 3, colorJugador);
    }

    private Color obtenerColorJugador() {
        return switch (jugador.getColor()) {
            case ROJO -> new Color(220, 53, 69);
            case AZUL -> new Color(0, 123, 255);
            default -> new Color(108, 117, 125);
        };
    }

    private void agregarGrupoNaves(String titulo, TipoNave tipo, int cantidad, Color color) {
        JLabel lblTipo = new JLabel(titulo + " x" + cantidad);
        lblTipo.setForeground(UIConstants.COLOR_TEXTO_PRINCIPAL);
        lblTipo.setFont(UIConstants.FONT_TITULO_PEQUENO);
        lblTipo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNaves.add(lblTipo);
        panelNaves.add(Box.createVerticalStrut(5));

        JPanel panelGrupo = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelGrupo.setBackground(UIConstants.COLOR_FONDO_SECUNDARIO);
        panelGrupo.setMaximumSize(new Dimension(190, 60));

        for (int i = 0; i < cantidad; i++) {
            NaveArrastrable nave = new NaveArrastrable(tipo, color);
            nave.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!nave.isColocada()) {
                        seleccionarNave(nave);
                        if (e.getClickCount() == 2) {
                            nave.rotarNave();
                        }
                    }
                }
            });
            navesDisponibles.add(nave);
            panelGrupo.add(nave);
        }

        panelNaves.add(panelGrupo);
        panelNaves.add(Box.createVerticalStrut(10));
    }

    private void crearPanelInferior() {
        panelInfo = new JPanel(new BorderLayout(10, 10));
        panelInfo.setBackground(UIConstants.COLOR_FONDO_PRINCIPAL);

        lblNavesColocadas = UIHelper.crearLabel("Naves colocadas: 0/" + totalNaves);
        lblNavesColocadas.setFont(UIConstants.FONT_TITULO_MEDIO);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotones.setBackground(UIConstants.COLOR_FONDO_PRINCIPAL);

        btnRotar = UIHelper.crearBotonPrimario("↻ Rotar (R)");
        btnRotar.addActionListener(e -> rotarNaveSeleccionada());

        btnLimpiar = UIHelper.crearBotonSecundario("🗑 Limpiar Todo");
        btnLimpiar.addActionListener(e -> limpiarTablero());

        btnConfirmar = UIHelper.crearBotonExito("✓ Confirmar");
        btnConfirmar.setEnabled(false);
        btnConfirmar.addActionListener(e -> confirmarColocacion());

        panelBotones.add(btnRotar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnConfirmar);

        panelInfo.add(lblNavesColocadas, BorderLayout.WEST);
        panelInfo.add(panelBotones, BorderLayout.EAST);

        configurarAtajosTeclado();
    }

    private void configurarAtajosTeclado() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_R) {
                rotarNaveSeleccionada();
                return true;
            }
            return false;
        });
    }

    private void configurarVentana() {
        pack();
        setMinimumSize(new Dimension(700, 550));
        setLocationRelativeTo(null);
    }

    private void seleccionarNave(NaveArrastrable nave) {
        if (naveSeleccionada != null) {
            naveSeleccionada.setBorder(null);
        }
        naveSeleccionada = nave;
        nave.setBorder(new LineBorder(UIConstants.COLOR_ACENTO_DORADO, 3));
        lblInstrucciones.setText("<html><center>Nave seleccionada:<br><b>" + nave.getNombreTipo() + "</b><br><br>Clic en tablero para colocar<br>R o doble clic para rotar</center></html>");
    }

    private void rotarNaveSeleccionada() {
        if (naveSeleccionada != null && !naveSeleccionada.isColocada()) {
            naveSeleccionada.rotarNave();
            panelNaves.revalidate();
            panelNaves.repaint();
        }
    }

    private void mostrarPreview(int fila, int col) {
        if (naveSeleccionada == null) return;

        List<int[]> posiciones = calcularPosiciones(fila, col, naveSeleccionada);
        boolean valido = validarColocacion(posiciones);

        for (int[] pos : posiciones) {
            if (pos[0] >= 0 && pos[0] < TAM_TABLERO && pos[1] >= 0 && pos[1] < TAM_TABLERO) {
                casillas[pos[0]][pos[1]].setResaltada(true, valido);
            }
        }
    }

    private void limpiarPreview() {
        for (int i = 0; i < TAM_TABLERO; i++) {
            for (int j = 0; j < TAM_TABLERO; j++) {
                casillas[i][j].setResaltada(false, true);
            }
        }
    }

    private List<int[]> calcularPosiciones(int fila, int col, NaveArrastrable nave) {
        List<int[]> posiciones = new ArrayList<>();
        int tamanio = nave.getTamanio();
        OrientacionNave orientacion = nave.getOrientacion();

        for (int i = 0; i < tamanio; i++) {
            if (orientacion == OrientacionNave.HORIZONTAL) {
                posiciones.add(new int[]{fila, col + i});
            } else {
                posiciones.add(new int[]{fila + i, col});
            }
        }
        return posiciones;
    }

    private boolean validarColocacion(List<int[]> posiciones) {
        for (int[] pos : posiciones) {
            if (pos[0] < 0 || pos[0] >= TAM_TABLERO || pos[1] < 0 || pos[1] >= TAM_TABLERO) {
                return false;
            }
            if (casillas[pos[0]][pos[1]].isOcupada()) {
                return false;
            }
        }

        for (int[] pos : posiciones) {
            for (int di = -1; di <= 1; di++) {
                for (int dj = -1; dj <= 1; dj++) {
                    if (di == 0 && dj == 0) continue;
                    int ni = pos[0] + di;
                    int nj = pos[1] + dj;
                    if (ni >= 0 && ni < TAM_TABLERO && nj >= 0 && nj < TAM_TABLERO) {
                        if (casillas[ni][nj].isOcupada()) {
                            boolean esParteDeNave = posiciones.stream()
                                    .anyMatch(p -> p[0] == ni && p[1] == nj);
                            if (!esParteDeNave) {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    private void intentarColocarNave(int fila, int col) {
        if (naveSeleccionada == null || naveSeleccionada.isColocada()) return;

        List<int[]> posiciones = calcularPosiciones(fila, col, naveSeleccionada);

        if (!validarColocacion(posiciones)) {
            UIHelper.mostrarMensajeError(this, "No se puede colocar la nave aquí.\nVerifica que no se salga del tablero,\nno se superponga con otras naves\ny no esté adyacente a otra nave.");
            return;
        }

        for (int[] pos : posiciones) {
            casillas[pos[0]][pos[1]].setOcupada(true, naveSeleccionada);
        }

        List<Coordenadas> coordenadasEntidad = new ArrayList<>();
        for (int[] pos : posiciones) {
            coordenadasEntidad.add(new Coordenadas(pos[0], pos[1]));
        }

        ControlVista.getInstancia().addNave(
                jugador,
                NaveFactory.crear(naveSeleccionada.getTipo(), naveSeleccionada.getOrientacion()),
                coordenadasEntidad
        );

        naveSeleccionada.setColocada(true);
        naveSeleccionada.setVisible(false);
        naveSeleccionada.setBorder(null);
        naveSeleccionada = null;

        navesColocadas++;
        actualizarContador();
        limpiarPreview();
        lblInstrucciones.setText("<html><center>Selecciona una nave<br>y haz clic en el tablero<br><br>Doble clic para rotar</center></html>");
    }

    private void actualizarContador() {
        lblNavesColocadas.setText("Naves colocadas: " + navesColocadas + "/" + totalNaves);
        btnConfirmar.setEnabled(navesColocadas == totalNaves);
    }

    private void limpiarTablero() {
        for (int i = 0; i < TAM_TABLERO; i++) {
            for (int j = 0; j < TAM_TABLERO; j++) {
                casillas[i][j].limpiar();
            }
        }

        for (NaveArrastrable nave : navesDisponibles) {
            nave.setColocada(false);
            nave.setVisible(true);
        }

        navesColocadas = 0;
        actualizarContador();
        naveSeleccionada = null;
        lblInstrucciones.setText("<html><center>Selecciona una nave<br>y haz clic en el tablero<br><br>Doble clic para rotar</center></html>");
    }

    private void confirmarColocacion() {
        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Confirmar posición de las naves?\nUna vez confirmado no podrás modificarlas.",
                "Confirmar Tablero",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            ControlVista.getInstancia().empezarPartida();
            abrirPartida();
        }
    }

    private void abrirPartida() {
        FrmPartidaMejorada partida = new FrmPartidaMejorada(jugador);
        partida.setVisible(true);
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            models.builder.JugadorBuilder builder = new models.builder.JugadorBuilder();
            builder.setNombre("TestPlayer");
            builder.setColor(models.enums.ColorJugador.ROJO);
            builder.setEstado(models.enums.EstadoJugador.JUGANDO);
            Jugador testJugador = builder.getResult();

            FrmColocarNaves frame = new FrmColocarNaves(testJugador);
            frame.setVisible(true);
        });
    }
}
