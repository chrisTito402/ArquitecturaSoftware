package views.frames;

import controllers.controller.ControlVista;
import dtos.CoordenadasDTO;
import dtos.PuntajeDTO;
import dtos.enums.EstadoNaveDTO;
import dtos.enums.ResultadoDisparoDTO;
import models.entidades.Jugador;
import views.IVistaPartida;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FrmPartidaMejorada extends JFrame implements IVistaPartida {

    private static final int TAM_TABLERO = 10;
    private static final int TAM_CASILLA = 40;

    private Jugador jugador;
    private CasillaTableroColocacion[][] casillasPropia;
    private CasillaButton[][] casillasEnemigo;
    private TimerPanel timerPanel;
    private PuntajePanel puntajePanel;
    private MarcadorNavesPanel marcadorPropio;
    private MarcadorNavesPanel marcadorEnemigo;
    private JLabel lblTurno;
    private JLabel lblMensaje;
    private JButton btnAbandonar;
    private boolean miTurno;

    public FrmPartidaMejorada(Jugador jugador) {
        this.jugador = jugador;
        this.miTurno = false;

        setTitle("Battleship - Partida en Curso");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        inicializarComponentes();
        configurarVista();
        configurarVentana();
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(UIConstants.COLOR_FONDO_PRINCIPAL);
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));

        panelPrincipal.add(crearPanelSuperior(), BorderLayout.NORTH);
        panelPrincipal.add(crearPanelCentral(), BorderLayout.CENTER);
        panelPrincipal.add(crearPanelInferior(), BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBackground(UIConstants.COLOR_FONDO_PRINCIPAL);

        JLabel lblTitulo = UIHelper.crearTitulo("⚓ BATTLESHIP");
        panel.add(lblTitulo, BorderLayout.WEST);

        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        panelInfo.setBackground(UIConstants.COLOR_FONDO_PRINCIPAL);

        timerPanel = new TimerPanel(1000, 30);
        timerPanel.setPreferredSize(new Dimension(200, 80));
        panelInfo.add(timerPanel);

        lblTurno = new JLabel("Esperando turno...");
        lblTurno.setFont(UIConstants.FONT_TITULO_MEDIO);
        lblTurno.setForeground(UIConstants.COLOR_TEXTO_SECUNDARIO);
        panelInfo.add(lblTurno);

        panel.add(panelInfo, BorderLayout.CENTER);

        puntajePanel = new PuntajePanel();
        puntajePanel.setPreferredSize(new Dimension(220, 80));
        panel.add(puntajePanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        panel.setBackground(UIConstants.COLOR_FONDO_PRINCIPAL);

        JPanel panelIzquierdo = new JPanel(new BorderLayout(0, 10));
        panelIzquierdo.setBackground(UIConstants.COLOR_FONDO_PRINCIPAL);

        JPanel tableroPropio = crearTableroPropio();
        panelIzquierdo.add(tableroPropio, BorderLayout.CENTER);

        marcadorPropio = new MarcadorNavesPanel();
        panelIzquierdo.add(marcadorPropio, BorderLayout.SOUTH);

        panel.add(panelIzquierdo);

        JPanel separador = new JPanel();
        separador.setBackground(UIConstants.COLOR_ACENTO_AZUL);
        separador.setPreferredSize(new Dimension(3, 400));
        panel.add(separador);

        JPanel panelDerecho = new JPanel(new BorderLayout(0, 10));
        panelDerecho.setBackground(UIConstants.COLOR_FONDO_PRINCIPAL);

        JPanel tableroEnemigo = crearTableroEnemigo();
        panelDerecho.add(tableroEnemigo, BorderLayout.CENTER);

        marcadorEnemigo = new MarcadorNavesPanel();
        panelDerecho.add(marcadorEnemigo, BorderLayout.SOUTH);

        panel.add(panelDerecho);

        return panel;
    }

    private JPanel crearTableroPropio() {
        JPanel contenedor = UIHelper.crearPanelConBorde("Tu Tablero - " + jugador.getNombre());
        contenedor.setLayout(new BorderLayout(5, 5));

        JPanel encabezado = crearEncabezadoColumnas();
        contenedor.add(encabezado, BorderLayout.NORTH);

        JPanel panelGrid = new JPanel(new BorderLayout());
        panelGrid.setBackground(UIConstants.COLOR_FONDO_SECUNDARIO);

        JPanel filas = crearEtiquetasFilas();
        panelGrid.add(filas, BorderLayout.WEST);

        JPanel grid = new JPanel(new GridLayout(TAM_TABLERO, TAM_TABLERO, 1, 1));
        grid.setBackground(UIConstants.COLOR_FONDO_SECUNDARIO);

        casillasPropia = new CasillaTableroColocacion[TAM_TABLERO][TAM_TABLERO];
        for (int i = 0; i < TAM_TABLERO; i++) {
            for (int j = 0; j < TAM_TABLERO; j++) {
                CasillaTableroColocacion casilla = new CasillaTableroColocacion(new CoordenadasDTO(i, j));
                casillasPropia[i][j] = casilla;
                grid.add(casilla);
            }
        }

        panelGrid.add(grid, BorderLayout.CENTER);
        contenedor.add(panelGrid, BorderLayout.CENTER);

        return contenedor;
    }

    private JPanel crearTableroEnemigo() {
        JPanel contenedor = UIHelper.crearPanelConBorde("Tablero Enemigo - Haz clic para disparar");
        contenedor.setLayout(new BorderLayout(5, 5));

        JPanel encabezado = crearEncabezadoColumnas();
        contenedor.add(encabezado, BorderLayout.NORTH);

        JPanel panelGrid = new JPanel(new BorderLayout());
        panelGrid.setBackground(UIConstants.COLOR_FONDO_SECUNDARIO);

        JPanel filas = crearEtiquetasFilas();
        panelGrid.add(filas, BorderLayout.WEST);

        JPanel grid = new JPanel(new GridLayout(TAM_TABLERO, TAM_TABLERO, 1, 1));
        grid.setBackground(UIConstants.COLOR_FONDO_SECUNDARIO);

        casillasEnemigo = new CasillaButton[TAM_TABLERO][TAM_TABLERO];
        for (int i = 0; i < TAM_TABLERO; i++) {
            for (int j = 0; j < TAM_TABLERO; j++) {
                CoordenadasDTO coords = new CoordenadasDTO(i, j);
                CasillaButton casilla = new CasillaButton(coords);
                final int fila = i;
                final int col = j;
                casilla.addActionListener(e -> realizarDisparo(fila, col));
                casillasEnemigo[i][j] = casilla;
                grid.add(casilla);
            }
        }

        panelGrid.add(grid, BorderLayout.CENTER);
        contenedor.add(panelGrid, BorderLayout.CENTER);

        return contenedor;
    }

    private JPanel crearEncabezadoColumnas() {
        JPanel panel = new JPanel(new GridLayout(1, TAM_TABLERO + 1));
        panel.setBackground(UIConstants.COLOR_FONDO_SECUNDARIO);
        panel.add(new JLabel(""));
        for (int i = 0; i < TAM_TABLERO; i++) {
            JLabel lbl = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
            lbl.setForeground(UIConstants.COLOR_TEXTO_PRINCIPAL);
            lbl.setFont(UIConstants.FONT_TEXTO_PEQUENO);
            panel.add(lbl);
        }
        return panel;
    }

    private JPanel crearEtiquetasFilas() {
        JPanel panel = new JPanel(new GridLayout(TAM_TABLERO, 1));
        panel.setBackground(UIConstants.COLOR_FONDO_SECUNDARIO);
        for (int i = 0; i < TAM_TABLERO; i++) {
            JLabel lbl = new JLabel(String.valueOf((char) ('A' + i)), SwingConstants.CENTER);
            lbl.setForeground(UIConstants.COLOR_TEXTO_PRINCIPAL);
            lbl.setFont(UIConstants.FONT_TEXTO_PEQUENO);
            lbl.setPreferredSize(new Dimension(25, TAM_CASILLA));
            panel.add(lbl);
        }
        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBackground(UIConstants.COLOR_FONDO_PRINCIPAL);

        lblMensaje = new JLabel("Prepárate para la batalla", SwingConstants.CENTER);
        lblMensaje.setFont(UIConstants.FONT_TITULO_MEDIO);
        lblMensaje.setForeground(UIConstants.COLOR_TEXTO_SECUNDARIO);
        panel.add(lblMensaje, BorderLayout.CENTER);

        btnAbandonar = UIHelper.crearBotonPeligro("🚪 Abandonar Partida");
        btnAbandonar.addActionListener(e -> confirmarAbandono());
        panel.add(btnAbandonar, BorderLayout.EAST);

        return panel;
    }

    private void configurarVista() {
        ControlVista.getInstancia().setVista(this);
    }

    private void configurarVentana() {
        pack();
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
    }

    private void realizarDisparo(int fila, int col) {
        if (!miTurno) {
            mostrarMensaje("No es tu turno", UIConstants.COLOR_FALLO);
            return;
        }

        CasillaButton casilla = casillasEnemigo[fila][col];
        if (casilla.getEstado() != CasillaButton.EstadoCasilla.NO_DISPARADO) {
            mostrarMensaje("Ya disparaste a esa casilla", UIConstants.COLOR_FALLO);
            return;
        }

        CoordenadasDTO coords = new CoordenadasDTO(fila, col);
        ControlVista.getInstancia().realizarDisparoDTO(coords);
    }

    @Override
    public void mostrarCasillaImpactada(CoordenadasDTO coordenadas, ResultadoDisparoDTO resultado, boolean esCasillaPropia) {
        if (esCasillaPropia) {
            CasillaTableroColocacion casilla = casillasPropia[coordenadas.getX()][coordenadas.getY()];
            actualizarCasillaPropia(casilla, resultado);

            if (resultado == ResultadoDisparoDTO.IMPACTO) {
                marcadorPropio.actualizarNave(obtenerTipoNave(casilla), EstadoNaveDTO.AVERIADO);
            } else if (resultado == ResultadoDisparoDTO.HUNDIMIENTO) {
                marcadorPropio.actualizarNave(obtenerTipoNave(casilla), EstadoNaveDTO.HUNDIDO);
            }
        } else {
            CasillaButton casilla = casillasEnemigo[coordenadas.getX()][coordenadas.getY()];
            casilla.marcarResultado(resultado);

            if (resultado == ResultadoDisparoDTO.HUNDIMIENTO) {
                marcadorEnemigo.actualizarNave("Barcos", EstadoNaveDTO.HUNDIDO);
            }

            mostrarMensajeResultado(resultado);
        }
    }

    private void actualizarCasillaPropia(CasillaTableroColocacion casilla, ResultadoDisparoDTO resultado) {
        Color color = switch (resultado) {
            case AGUA -> UIConstants.COLOR_CASILLA_AGUA;
            case IMPACTO -> UIConstants.COLOR_CASILLA_IMPACTO;
            case HUNDIMIENTO -> UIConstants.COLOR_CASILLA_HUNDIDO;
            default -> casilla.getBackground();
        };
        casilla.setBackground(color);
    }

    private String obtenerTipoNave(CasillaTableroColocacion casilla) {
        NaveArrastrable nave = casilla.getNaveAsociada();
        if (nave != null) {
            return nave.getNombreTipo() + "s";
        }
        return "Barcos";
    }

    private void mostrarMensajeResultado(ResultadoDisparoDTO resultado) {
        String mensaje = switch (resultado) {
            case AGUA -> "¡Agua! Fallaste el disparo";
            case IMPACTO -> "¡Impacto! Has dañado una nave";
            case HUNDIMIENTO -> "¡HUNDIDO! Destruiste una nave enemiga";
            case YA_DISPARADO -> "Ya disparaste a esa casilla";
            case TURNO_INCORRECTO -> "No es tu turno";
            case DISPARO_FUERA_TIEMPO -> "Se acabó tu tiempo";
            default -> "Disparo procesado";
        };

        Color color = switch (resultado) {
            case AGUA, TURNO_INCORRECTO, DISPARO_FUERA_TIEMPO -> UIConstants.COLOR_FALLO;
            case IMPACTO -> UIConstants.COLOR_HUNDIDO;
            case HUNDIMIENTO -> UIConstants.COLOR_ACIERTO;
            default -> UIConstants.COLOR_TEXTO_SECUNDARIO;
        };

        mostrarMensaje(mensaje, color);
    }

    private void mostrarMensaje(String mensaje, Color color) {
        lblMensaje.setText(mensaje);
        lblMensaje.setForeground(color);
    }

    public void setMiTurno(boolean miTurno) {
        this.miTurno = miTurno;
        if (miTurno) {
            lblTurno.setText("¡TU TURNO!");
            lblTurno.setForeground(UIConstants.COLOR_ACIERTO);
            habilitarTablero();
        } else {
            lblTurno.setText("Turno del oponente");
            lblTurno.setForeground(UIConstants.COLOR_TEXTO_SECUNDARIO);
            deshabilitarTableroEnemigo();
        }
    }

    @Override
    public void actualizarPuntaje(PuntajeDTO puntaje) {
        if (puntajePanel != null && puntaje != null) {
            puntajePanel.actualizarPuntaje(puntaje);
        }
    }

    @Override
    public void mostrarMensajeFinPartida(String nombreGanador, PuntajeDTO puntaje) {
        detenerTimer();
        deshabilitarTableroEnemigo();

        StringBuilder mensaje = new StringBuilder();
        mensaje.append("🏆 PARTIDA TERMINADA 🏆\n\n");

        if (puntaje != null) {
            mensaje.append(String.format("Ganador: %s\n\n", nombreGanador));
            mensaje.append(String.format("📊 ESTADÍSTICAS:\n"));
            mensaje.append(String.format("   Puntos: %d\n", puntaje.getPuntosTotales()));
            mensaje.append(String.format("   Aciertos: %d\n", puntaje.getDisparosAcertados()));
            mensaje.append(String.format("   Fallos: %d\n", puntaje.getDisparosFallados()));
            mensaje.append(String.format("   Naves hundidas: %d\n", puntaje.getNavesHundidas()));
            mensaje.append(String.format("   Precisión: %.1f%%", puntaje.getPrecision()));
        } else {
            mensaje.append(nombreGanador);
        }

        JOptionPane.showMessageDialog(this, mensaje.toString(), "Fin de Partida",
                JOptionPane.INFORMATION_MESSAGE);

        preguntarNuevaPartida();
    }

    private void preguntarNuevaPartida() {
        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas jugar otra partida?",
                "Nueva Partida",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            FrmMenuPrincipal menu = new FrmMenuPrincipal();
            menu.setVisible(true);
        }
        this.dispose();
    }

    @Override
    public void deshabilitarTableroEnemigo() {
        for (int i = 0; i < TAM_TABLERO; i++) {
            for (int j = 0; j < TAM_TABLERO; j++) {
                casillasEnemigo[i][j].setEnabled(false);
            }
        }
    }

    @Override
    public void habilitarTablero() {
        for (int i = 0; i < TAM_TABLERO; i++) {
            for (int j = 0; j < TAM_TABLERO; j++) {
                casillasEnemigo[i][j].setEnabled(true);
            }
        }
    }

    @Override
    public void reiniciarTimer() {
        if (timerPanel != null) {
            timerPanel.initTimer();
        }
    }

    @Override
    public void detenerTimer() {
        if (timerPanel != null) {
            timerPanel.stopTimer();
        }
    }

    private void confirmarAbandono() {
        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que deseas abandonar la partida?\nPerderás automáticamente.",
                "Confirmar Abandono",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            ControlVista.getInstancia().abandonarLobby(jugador);
            FrmMenuPrincipal menu = new FrmMenuPrincipal();
            menu.setVisible(true);
            this.dispose();
        }
    }

    public void cargarNavesEnTablero(java.util.Map<CoordenadasDTO, NaveArrastrable> naves) {
        for (java.util.Map.Entry<CoordenadasDTO, NaveArrastrable> entry : naves.entrySet()) {
            CoordenadasDTO coord = entry.getKey();
            NaveArrastrable nave = entry.getValue();
            casillasPropia[coord.getX()][coord.getY()].setOcupada(true, nave);
        }
    }
}
