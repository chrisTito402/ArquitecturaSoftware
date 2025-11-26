package views.frames;

import controllers.controller.ControlVista;
import dtos.CoordenadasDTO;
import dtos.PuntajeDTO;
import dtos.enums.ResultadoDisparoDTO;
import dtos.mappers.CoordenadasMapper;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import views.IVistaPartida;

public class FrmPartidaEnCurso extends javax.swing.JFrame implements IVistaPartida {

    private List<CasillaPanel> casillasPropias;
    private List<CasillaButton> casillasEnemigas;
    private TimerPanel timerPanel;
    private PuntajePanel puntajePanel;

    public FrmPartidaEnCurso() {
        initComponents();
        inicializarTableros();
        configurarVista();
    }

    private void inicializarTableros() {
        casillasPropias = new ArrayList<>();
        casillasEnemigas = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                CoordenadasDTO coordenadas = new CoordenadasDTO(i, j);

                CasillaPanel cP = new CasillaPanel(coordenadas);
                cP.setBackground(new Color(242, 242, 242));
                cP.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                casillasPropias.add(cP);
                jPanel1.add(cP);

                CasillaButton cB = new CasillaButton(coordenadas);
                cB.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                final CoordenadasDTO coords = coordenadas;
                cB.addActionListener(evt -> {
                    ControlVista.getInstancia().realizarDisparo(CoordenadasMapper.toEntity(coords));
                });
                casillasEnemigas.add(cB);
                jPanel2.add(cB);
            }
        }

        timerPanel = new TimerPanel(1000, 30);
        pnlTimer.add(timerPanel);
        pnlTimer.revalidate();
        pnlTimer.repaint();

        puntajePanel = (PuntajePanel) pnlPuntaje;
    }

    private void configurarVista() {
        ControlVista.getInstancia().setVista(this);
    }

    @Override
    public void mostrarCasillaImpactada(CoordenadasDTO coordenadas, ResultadoDisparoDTO resultado, boolean esCasillaPropia) {
        if (esCasillaPropia) {
            CasillaPanel casilla = obtenerCasillaPropia(coordenadas);
            if (casilla != null) {
                casilla.setBackground(obtenerColorResultado(resultado));
            }
        } else {
            CasillaButton casilla = obtenerCasillaEnemiga(coordenadas);
            if (casilla != null) {
                casilla.marcarResultado(resultado);
            }
        }
    }

    private Color obtenerColorResultado(ResultadoDisparoDTO resultado) {
        if (resultado == null) return Color.GRAY;

        return switch (resultado) {
            case IMPACTO -> Color.YELLOW;
            case HUNDIMIENTO -> Color.RED;
            case AGUA -> Color.BLUE;
            default -> Color.GRAY;
        };
    }

    private CasillaPanel obtenerCasillaPropia(CoordenadasDTO c) {
        return casillasPropias.stream()
                .filter(e -> e.getCoordenadas().getX() == c.getX()
                        && e.getCoordenadas().getY() == c.getY())
                .findFirst()
                .orElse(null);
    }

    private CasillaButton obtenerCasillaEnemiga(CoordenadasDTO c) {
        return casillasEnemigas.stream()
                .filter(e -> e.getCoordenadas().getX() == c.getX()
                        && e.getCoordenadas().getY() == c.getY())
                .findFirst()
                .orElse(null);
    }

    @Override
    public void actualizarPuntaje(PuntajeDTO puntaje) {
        if (puntajePanel != null && puntaje != null) {
            puntajePanel.actualizarPuntaje(puntaje);
        }
    }

    @Override
    public void mostrarMensajeFinPartida(String nombreGanador, PuntajeDTO puntaje) {
        String mensaje;

        if (puntaje != null) {
            mensaje = String.format(
                    "Partida terminada!\n\n" +
                    "Ganador: %s\n" +
                    "Puntaje final: %d puntos\n" +
                    "Aciertos: %d\n" +
                    "Fallos: %d\n" +
                    "Naves hundidas: %d\n" +
                    "Precision: %.2f%%",
                    nombreGanador,
                    puntaje.getPuntosTotales(),
                    puntaje.getDisparosAcertados(),
                    puntaje.getDisparosFallados(),
                    puntaje.getNavesHundidas(),
                    puntaje.getPrecision()
            );
        } else {
            mensaje = nombreGanador;
        }

        JOptionPane.showMessageDialog(this, mensaje, "Fin de Partida", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void deshabilitarTableroEnemigo() {
        casillasEnemigas.forEach(c -> c.setEnabled(false));
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

    @Override
    public void habilitarTablero() {
        casillasEnemigas.forEach(c -> c.setEnabled(true));
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        pnlTimer = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnAbandonar = new javax.swing.JButton();
        pnlPuntaje = new views.frames.PuntajePanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));
        jPanel2.setLayout(new GridLayout(10, 10));

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setLayout(new GridLayout(10, 10));

        pnlTimer.setMaximumSize(new java.awt.Dimension(100, 30));
        pnlTimer.setMinimumSize(new java.awt.Dimension(100, 30));
        pnlTimer.setName("");
        pnlTimer.setPreferredSize(new java.awt.Dimension(100, 30));
        pnlTimer.setLayout(new GridLayout(1, 0));

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 2, 48));
        jLabel1.setText("Battleship");

        btnAbandonar.setBackground(new java.awt.Color(204, 0, 51));
        btnAbandonar.setText("Abandonar partida");

        javax.swing.GroupLayout pnlPuntajeLayout = new javax.swing.GroupLayout(pnlPuntaje);
        pnlPuntaje.setLayout(pnlPuntajeLayout);
        pnlPuntajeLayout.setHorizontalGroup(
            pnlPuntajeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 168, Short.MAX_VALUE)
        );
        pnlPuntajeLayout.setVerticalGroup(
            pnlPuntajeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(pnlTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlPuntaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(32, 32, 32))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnAbandonar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(pnlTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlPuntaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnAbandonar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(33, Short.MAX_VALUE))
        );

        pack();
    }

    private javax.swing.JButton btnAbandonar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel pnlPuntaje;
    private javax.swing.JPanel pnlTimer;
}
