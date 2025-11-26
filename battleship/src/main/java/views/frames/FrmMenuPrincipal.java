package views.frames;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 *
 * @author Knocmare
 */
public class FrmMenuPrincipal extends javax.swing.JFrame {

    private static final Color COLOR_FONDO = new Color(13, 27, 42);
    private static final Color COLOR_PRIMARIO = new Color(27, 38, 59);
    private static final Color COLOR_ACENTO = new Color(65, 90, 119);
    private static final Color COLOR_TEXTO = new Color(238, 238, 238);
    private static final Color COLOR_HOVER = new Color(119, 141, 169);

    public FrmMenuPrincipal() {
        initComponents();
        customizeComponents();
    }

    private void customizeComponents() {
        getContentPane().setBackground(COLOR_FONDO);
        pnlCambiante.setBackground(COLOR_FONDO);

        lblTitulo.setForeground(COLOR_TEXTO);

        customizeButton(btnJugar, new Color(46, 125, 50));
        customizeButton(btnJugador, COLOR_ACENTO);
        customizeButton(btnPuntaje, COLOR_ACENTO);
        customizeButton(btnSalir, new Color(198, 40, 40));
    }

    private void customizeButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(bgColor.darker(), 2, true),
            BorderFactory.createEmptyBorder(15, 40, 15, 40)
        ));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = bgColor;

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(originalColor);
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlCambiante = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        btnJugar = new javax.swing.JButton();
        btnJugador = new javax.swing.JButton();
        btnPuntaje = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        lblSubtitulo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Battleship - Menú Principal");
        setResizable(false);

        pnlCambiante.setBackground(new java.awt.Color(13, 27, 42));

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 64)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(238, 238, 238));
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("⚓ BATTLESHIP");

        btnJugar.setBackground(new java.awt.Color(46, 125, 50));
        btnJugar.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        btnJugar.setForeground(new java.awt.Color(255, 255, 255));
        btnJugar.setText("🎮 Jugar");
        btnJugar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnJugar.setFocusPainted(false);
        btnJugar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJugarActionPerformed(evt);
            }
        });

        btnJugador.setBackground(new java.awt.Color(65, 90, 119));
        btnJugador.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        btnJugador.setForeground(new java.awt.Color(255, 255, 255));
        btnJugador.setText("👤 Gestionar Jugador");
        btnJugador.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnJugador.setFocusPainted(false);
        btnJugador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJugadorActionPerformed(evt);
            }
        });

        btnPuntaje.setBackground(new java.awt.Color(65, 90, 119));
        btnPuntaje.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        btnPuntaje.setForeground(new java.awt.Color(255, 255, 255));
        btnPuntaje.setText("🏆 Ver Puntajes");
        btnPuntaje.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPuntaje.setFocusPainted(false);
        btnPuntaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPuntajeActionPerformed(evt);
            }
        });

        btnSalir.setBackground(new java.awt.Color(198, 40, 40));
        btnSalir.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        btnSalir.setForeground(new java.awt.Color(255, 255, 255));
        btnSalir.setText("🚪 Salir");
        btnSalir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSalir.setFocusPainted(false);
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        lblSubtitulo.setFont(new java.awt.Font("Segoe UI", 2, 16)); // NOI18N
        lblSubtitulo.setForeground(new java.awt.Color(119, 141, 169));
        lblSubtitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSubtitulo.setText("Arquitectura de Software - 2025");

        javax.swing.GroupLayout pnlCambianteLayout = new javax.swing.GroupLayout(pnlCambiante);
        pnlCambiante.setLayout(pnlCambianteLayout);
        pnlCambianteLayout.setHorizontalGroup(
            pnlCambianteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCambianteLayout.createSequentialGroup()
                .addContainerGap(230, Short.MAX_VALUE)
                .addGroup(pnlCambianteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnJugar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnJugador, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPuntaje, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSalir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblSubtitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(230, Short.MAX_VALUE))
        );
        pnlCambianteLayout.setVerticalGroup(
            pnlCambianteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCambianteLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSubtitulo)
                .addGap(60, 60, 60)
                .addComponent(btnJugar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(btnJugador, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(btnPuntaje, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(80, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlCambiante, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlCambiante, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnJugarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJugarActionPerformed
        FrmRegistrarJugador r = new FrmRegistrarJugador();
        r.setLocationRelativeTo(this);
        r.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnJugarActionPerformed

    private void btnJugadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJugadorActionPerformed
        JOptionPane.showMessageDialog(this,
            "Funcionalidad de Gestionar Jugador\n\nPróximamente...",
            "Gestionar Jugador",
            JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnJugadorActionPerformed

    private void btnPuntajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPuntajeActionPerformed
        JOptionPane.showMessageDialog(this,
            "Tabla de Puntajes\n\nPróximamente...",
            "Puntajes",
            JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnPuntajeActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Estás seguro de que deseas salir?",
            "Confirmar Salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_btnSalirActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmMenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            new FrmMenuPrincipal().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnJugador;
    private javax.swing.JButton btnJugar;
    private javax.swing.JButton btnPuntaje;
    private javax.swing.JButton btnSalir;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlCambiante;
    // End of variables declaration//GEN-END:variables
}
