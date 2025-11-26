package views.frames;

import dtos.CoordenadasDTO;
import java.awt.FlowLayout;

public class FrmPartidaEnCursoMetodoBotones extends javax.swing.JFrame {

    private PnlTableroJugador panelJugador;
    private PnlTableroBot panelBot;
    private CasillaButton[][] casillasJugador;
    private CasillaButton[][] casillasBot;

    public FrmPartidaEnCursoMetodoBotones() {
        initComponents();

        panelJugador = new PnlTableroJugador();
        panelBot = new PnlTableroBot();

        inicializarTableros();

        this.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 20));
        this.add(panelJugador);
        this.add(panelBot);

        this.pack();
    }

    private void inicializarTableros() {
        int filas = 10, columnas = 10;

        casillasJugador = new CasillaButton[filas][columnas];
        casillasBot = new CasillaButton[filas][columnas];

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                casillasJugador[i][j] = new CasillaButton(new CoordenadasDTO(i, j));
                panelJugador.add(casillasJugador[i][j]);

                casillasBot[i][j] = new CasillaButton(new CoordenadasDTO(i, j));
                panelBot.add(casillasBot[i][j]);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 675, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 413, Short.MAX_VALUE)
        );

        pack();
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmPartidaEnCursoMetodoBotones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new FrmPartidaEnCursoMetodoBotones().setVisible(true));
    }
}
