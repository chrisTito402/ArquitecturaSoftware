package cliente.presentacion.frames;

import cliente.controlador.ControlVista;
import cliente.presentacion.componentes.IndicadorTurnoPanel;
import cliente.presentacion.componentes.MarcadorNavesPanel;
import cliente.presentacion.componentes.TimerPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Pantalla principal del juego donde se desarrolla la partida.
 * Muestra ambos tableros, el timer, el indicador de turno y el marcador de naves.
 *
 * @author daniel
 */
public class FrmPartidaEnCurso extends javax.swing.JFrame {

    private IndicadorTurnoPanel indicadorTurno;
    private MarcadorNavesPanel marcadorNaves;
    private JLabel lblTableroPropio;
    private JLabel lblTableroEnemigo;

    public FrmPartidaEnCurso() {
        initComponents();

        ControlVista cV = ControlVista.getInstancia();

        // Agregar casillas a los tableros
        cV.getCasillasEnemigas().forEach(c -> jPanel2.add(c));
        cV.getCasillasPropias().forEach(c -> jPanel1.add(c));

        // Configurar timer
        TimerPanel timerPanel = cV.getTimer();
        Component a = pnlTimer.add(timerPanel);
        pnlTimer.revalidate();
        pnlTimer.repaint();

        // Configurar callback cuando el tiempo se agota
        timerPanel.setOnTiempoAgotado(() -> {
            ControlVista cv = ControlVista.getInstancia();
            if (cv.esMiTurno()) {
                // Era mi turno y se me acabó el tiempo - pierdo el turno
                JOptionPane.showMessageDialog(this,
                    "¡Se acabó el tiempo! Pierdes tu turno.",
                    "Tiempo agotado",
                    JOptionPane.WARNING_MESSAGE);
                // Llamar al método que actualiza modelo, UI y notifica al servidor
                cv.tiempoAgotado();
            }
        });

        // Crear e integrar el indicador de turno
        indicadorTurno = new IndicadorTurnoPanel();
        cV.setIndicadorTurnoPanel(indicadorTurno);

        // Crear e integrar el marcador de naves
        marcadorNaves = new MarcadorNavesPanel();
        cV.setMarcadorNavesPanel(marcadorNaves);

        // Agregar etiquetas a los tableros
        agregarEtiquetasTableros();

        // Agregar los paneles adicionales (turno y marcador)
        agregarPanelesAdicionales();

        // Configurar botón abandonar con confirmación
        configurarBotonAbandonar();

        // Verificar si es nuestro turno y habilitar casillas
        boolean esMiTurno = cV.esMiTurno();
        cV.habilitarCasillasEnemigas(esMiTurno);
        indicadorTurno.setEsMiTurno(esMiTurno);

        // Centrar la ventana
        setLocationRelativeTo(null);

        System.out.println("[FrmPartidaEnCurso] Iniciado - Es mi turno: " + esMiTurno);
    }

    /**
     * Agrega etiquetas identificativas sobre cada tablero.
     */
    private void agregarEtiquetasTableros() {
        // Crear etiqueta para tablero propio
        lblTableroPropio = new JLabel("TU FLOTA", SwingConstants.CENTER);
        lblTableroPropio.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTableroPropio.setForeground(new Color(0, 102, 0));
        lblTableroPropio.setPreferredSize(new Dimension(451, 25));

        // Crear etiqueta para tablero enemigo
        lblTableroEnemigo = new JLabel("TABLERO ENEMIGO (Click para disparar)", SwingConstants.CENTER);
        lblTableroEnemigo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTableroEnemigo.setForeground(new Color(153, 0, 0));
        lblTableroEnemigo.setPreferredSize(new Dimension(451, 25));

        // Agregar bordes a los tableros para mejor visualización
        jPanel1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 0), 2),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));

        jPanel2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(153, 0, 0), 2),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
    }

    /**
     * Agrega los paneles de indicador de turno y marcador de naves.
     * Solución: Envolver el contenido existente en un nuevo panel con BorderLayout.
     */
    private void agregarPanelesAdicionales() {
        // Obtener el contenido actual del frame
        java.awt.Container contenidoOriginal = getContentPane();

        // Crear nuevo panel principal con BorderLayout
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Crear panel para el contenido original (centro)
        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));

        // Mover todos los componentes al panel centro
        Component[] componentes = contenidoOriginal.getComponents();
        contenidoOriginal.removeAll();

        // Crear panel que contenga el layout original
        JPanel wrapperOriginal = new JPanel();
        wrapperOriginal.setLayout(new javax.swing.GroupLayout(wrapperOriginal));

        // Reconstruir el contenido en un nuevo panel
        JPanel contenidoJuego = new JPanel();
        contenidoJuego.setLayout(new BorderLayout(10, 10));
        contenidoJuego.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior: título y timer
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        panelSuperior.add(jLabel1);
        panelSuperior.add(pnlTimer);

        // Panel central: los dos tableros con GridBagLayout para control preciso
        JPanel panelTableros = new JPanel(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 20, 5, 20);
        gbc.fill = java.awt.GridBagConstraints.BOTH;

        // Establecer tamaño fijo para los tableros
        Dimension tamTablero = new Dimension(350, 350);
        jPanel1.setPreferredSize(tamTablero);
        jPanel1.setMinimumSize(tamTablero);
        jPanel2.setPreferredSize(tamTablero);
        jPanel2.setMinimumSize(tamTablero);

        // Tablero propio con etiqueta
        JPanel panelTableroPropio = new JPanel(new BorderLayout(0, 5));
        JLabel lblPropio = new JLabel("TU FLOTA", SwingConstants.CENTER);
        lblPropio.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPropio.setForeground(new Color(0, 102, 0));
        panelTableroPropio.add(lblPropio, BorderLayout.NORTH);
        panelTableroPropio.add(jPanel1, BorderLayout.CENTER);

        // Tablero enemigo con etiqueta
        JPanel panelTableroEnemigo = new JPanel(new BorderLayout(0, 5));
        JLabel lblEnemigo = new JLabel("TABLERO ENEMIGO (Click para disparar)", SwingConstants.CENTER);
        lblEnemigo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEnemigo.setForeground(new Color(153, 0, 0));
        panelTableroEnemigo.add(lblEnemigo, BorderLayout.NORTH);
        panelTableroEnemigo.add(jPanel2, BorderLayout.CENTER);

        // Agregar tableros al panel con GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panelTableros.add(panelTableroPropio, gbc);

        gbc.gridx = 1;
        panelTableros.add(panelTableroEnemigo, gbc);

        // Panel inferior: botón abandonar
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelInferior.add(btnAbandonar);

        contenidoJuego.add(panelSuperior, BorderLayout.NORTH);
        contenidoJuego.add(panelTableros, BorderLayout.CENTER);
        contenidoJuego.add(panelInferior, BorderLayout.SOUTH);

        // Panel derecho: indicador de turno y marcador
        JPanel pnlDerecho = new JPanel();
        pnlDerecho.setLayout(new BoxLayout(pnlDerecho, BoxLayout.Y_AXIS));
        pnlDerecho.setBorder(BorderFactory.createEmptyBorder(70, 10, 10, 10));
        pnlDerecho.setBackground(new Color(240, 240, 240));
        pnlDerecho.setPreferredSize(new Dimension(240, 400));

        // Agregar indicador de turno
        indicadorTurno.setMaximumSize(new Dimension(220, 70));
        indicadorTurno.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlDerecho.add(indicadorTurno);
        pnlDerecho.add(Box.createVerticalStrut(20));

        // Agregar marcador de naves
        marcadorNaves.setMaximumSize(new Dimension(220, 180));
        marcadorNaves.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlDerecho.add(marcadorNaves);
        pnlDerecho.add(Box.createVerticalGlue());

        // Ensamblar todo
        panelPrincipal.add(contenidoJuego, BorderLayout.CENTER);
        panelPrincipal.add(pnlDerecho, BorderLayout.EAST);

        // Reemplazar el contenido del frame
        setContentPane(panelPrincipal);

        // Ajustar tamaño
        setSize(1280, 600);
        setMinimumSize(new Dimension(1200, 550));

        revalidate();
        repaint();
    }

    /**
     * Configura el botón abandonar para mostrar confirmación.
     */
    private void configurarBotonAbandonar() {
        btnAbandonar.setForeground(Color.WHITE);
        btnAbandonar.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        pnlTimer = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnAbandonar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));
        jPanel2.setLayout(new java.awt.GridLayout(10, 10));

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setLayout(new java.awt.GridLayout(10, 10));

        pnlTimer.setMaximumSize(new java.awt.Dimension(100, 30));
        pnlTimer.setMinimumSize(new java.awt.Dimension(100, 30));
        pnlTimer.setName(""); // NOI18N
        pnlTimer.setPreferredSize(new java.awt.Dimension(100, 30));
        pnlTimer.setLayout(new java.awt.GridLayout(1, 0));

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 2, 48)); // NOI18N
        jLabel1.setText("Battleship");

        btnAbandonar.setBackground(new java.awt.Color(204, 0, 51));
        btnAbandonar.setText("Abandonar partida");
        btnAbandonar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbandonarActionPerformed(evt);
            }
        });

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
                        .addComponent(pnlTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(pnlTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnAbandonar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(33, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAbandonarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbandonarActionPerformed
        int opcion = JOptionPane.showConfirmDialog(
            this,
            "¿Estás seguro de que quieres abandonar la partida?\nEl oponente será declarado ganador.",
            "Confirmar abandono",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (opcion == JOptionPane.YES_OPTION) {
            ControlVista.getInstancia().abandonarPartida();
        }
    }//GEN-LAST:event_btnAbandonarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbandonar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel pnlTimer;
    // End of variables declaration//GEN-END:variables
}
