package cliente.presentacion.frames;

import cliente.controlador.ControlVista;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import compartido.observador.ISuscriptor;
import compartido.comunicacion.dto.JugadorDTO;

/**
 * La sala de espera antes de empezar a jugar.
 * Aqui se ve el codigo de la partida para pasarselo a tu amigo,
 * y los nombres de los que ya estan conectados. Cuando los dos
 * estan listos el host le da "Iniciar" y empieza el juego.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public class FrmLobby extends JFrame implements ISuscriptor {

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
    private static final Color COLOR_TEXTO_OSCURO = Color.BLACK;

    private ControlVista controlVista;
    private String codigoPartida;
    private boolean esHost;
    private List<JugadorDTO> jugadores;
    private boolean jugador2Listo = false;
    private boolean yoDiListo = false; // Si yo (jugador 2) di click en Listo
    private boolean navegando = false; // Evita navegación duplicada
    private boolean accionEnProceso = false; // Evita doble click en botones

    // Componentes UI
    private JLabel lblJugador1Nombre;
    private JLabel lblJugador1Listo;
    private JLabel lblJugador2Nombre;
    private JLabel lblJugador2Listo;
    private JButton btnComenzarListo;
    private JButton btnAbandonar;

    public FrmLobby(String codigoPartida, boolean esHost) {
        this.controlVista = ControlVista.getInstancia();
        this.codigoPartida = codigoPartida;
        this.esHost = esHost;
        this.jugadores = new ArrayList<>();

        initComponents();

        // Suscribirse a notificaciones
        controlVista.suscribirLobby(this);

        // Cargar jugadores existentes
        cargarJugadores();
    }

    // Constructor por defecto (para compatibilidad)
    public FrmLobby() {
        this(ControlVista.getInstancia().getCodigoPartida(),
             ControlVista.getInstancia().isEsHost());
    }

    public void setCodigoPartida(String codigo) {
        this.codigoPartida = codigo;
        setTitle("Battleship - Lobby [" + codigo + "]");
    }

    private void initComponents() {
        setTitle("Battleship - Lobby [" + codigoPartida + "]");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 500);
        setResizable(false);
        setLocationRelativeTo(null);

        // Manejar cierre con X para notificar abandono
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                abandonar();
            }
        });

        // Panel principal con gradiente
        JPanel pnlPrincipal = new JPanel() {
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
        pnlPrincipal.setLayout(new BorderLayout());

        // Panel superior con titulo y codigo
        JPanel pnlSuperior = crearPanelSuperior();
        pnlPrincipal.add(pnlSuperior, BorderLayout.NORTH);

        // Panel central con lista de jugadores
        JPanel pnlCentral = crearPanelJugadores();
        pnlPrincipal.add(pnlCentral, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel pnlInferior = crearPanelBotones();
        pnlPrincipal.add(pnlInferior, BorderLayout.SOUTH);

        setContentPane(pnlPrincipal);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        // Codigo a la izquierda
        JLabel lblCodigo = new JLabel(codigoPartida);
        lblCodigo.setFont(new Font("Consolas", Font.BOLD, 24));
        lblCodigo.setForeground(COLOR_TEXTO);
        panel.add(lblCodigo, BorderLayout.WEST);

        // Titulo "Lobby" al centro
        JLabel lblTitulo = new JLabel("Lobby", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitulo.setForeground(COLOR_TEXTO);
        panel.add(lblTitulo, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelJugadores() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        // Panel contenedor con fondo claro
        JPanel pnlContenedor = new JPanel(new GridBagLayout());
        pnlContenedor.setBackground(COLOR_PANEL);
        pnlContenedor.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        pnlContenedor.setPreferredSize(new Dimension(500, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);

        // Encabezados
        JLabel lblNombreHeader = new JLabel("Nombre");
        lblNombreHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        pnlContenedor.add(lblNombreHeader, gbc);

        JLabel lblListoHeader = new JLabel("Listo");
        lblListoHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        pnlContenedor.add(lblListoHeader, gbc);

        // Separador
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel separador = new JPanel();
        separador.setBackground(Color.GRAY);
        separador.setPreferredSize(new Dimension(400, 1));
        pnlContenedor.add(separador, gbc);

        // Jugador 1 (Host)
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;

        JPanel pnlJugador1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlJugador1.setBackground(COLOR_PANEL);

        JLabel lblHost = new JLabel("Host");
        lblHost.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblHost.setForeground(new Color(0, 100, 0));
        pnlJugador1.add(lblHost);

        lblJugador1Nombre = new JLabel("Buscando...");
        lblJugador1Nombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlJugador1.add(lblJugador1Nombre);

        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        pnlContenedor.add(pnlJugador1, gbc);

        lblJugador1Listo = new JLabel("Si");
        lblJugador1Listo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblJugador1Listo.setForeground(new Color(0, 128, 0));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        pnlContenedor.add(lblJugador1Listo, gbc);

        // Jugador 2
        gbc.gridy = 3;

        lblJugador2Nombre = new JLabel("Buscando...");
        lblJugador2Nombre.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblJugador2Nombre.setForeground(Color.GRAY);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        pnlContenedor.add(lblJugador2Nombre, gbc);

        lblJugador2Listo = new JLabel("No");
        lblJugador2Listo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblJugador2Listo.setForeground(Color.RED);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        pnlContenedor.add(lblJugador2Listo, gbc);

        panel.add(pnlContenedor);
        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));

        // Boton Comenzar (para Host) o Listo (para Jugador 2)
        String textBoton = esHost ? "Comenzar" : "Listo";
        // Crear con colores normales, el hover funciona solo si esta habilitado
        btnComenzarListo = crearBoton(textBoton, COLOR_BOTON, COLOR_BOTON_HOVER);
        btnComenzarListo.setEnabled(false);
        btnComenzarListo.setBackground(COLOR_BOTON_DESHABILITADO); // Inicialmente deshabilitado
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
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setForeground(COLOR_TEXTO);
        btn.setBackground(colorNormal);
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) {
                    btn.setBackground(colorHover);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) {
                    btn.setBackground(colorNormal);
                }
            }
        });

        return btn;
    }

    private void habilitarBotonComenzar(boolean habilitar) {
        btnComenzarListo.setEnabled(habilitar);
        // Solo cambiar el color, el listener ya esta configurado en crearBoton()
        if (habilitar) {
            btnComenzarListo.setBackground(COLOR_BOTON);
        } else {
            btnComenzarListo.setBackground(COLOR_BOTON_DESHABILITADO);
        }
    }

    private void cargarJugadores() {
        List<JugadorDTO> listaControlador = controlVista.getJugadores();
        if (listaControlador != null) {
            for (JugadorDTO j : listaControlador) {
                boolean existe = jugadores.stream()
                        .anyMatch(existing -> existing.getNombre().equals(j.getNombre()));
                if (!existe) {
                    jugadores.add(j);
                }
            }
        }
        actualizarUI();
    }

    private void actualizarUI() {
        SwingUtilities.invokeLater(() -> {
            // Obtener mi jugador
            JugadorDTO miJugador = controlVista.getControl().getJugador();
            String miNombre = (miJugador != null) ? miJugador.getNombre() : "";

            // Determinar quién es el host y quién es el invitado
            String nombreHost = "";
            String nombreInvitado = "";

            for (JugadorDTO j : jugadores) {
                if (esHost) {
                    // Si yo soy host, yo voy primero
                    if (j.getNombre().equals(miNombre)) {
                        nombreHost = j.getNombre();
                    } else {
                        nombreInvitado = j.getNombre();
                    }
                } else {
                    // Si yo NO soy host, el otro es el host
                    if (j.getNombre().equals(miNombre)) {
                        nombreInvitado = j.getNombre();
                    } else {
                        nombreHost = j.getNombre();
                    }
                }
            }

            // Mostrar Host (Jugador 1)
            if (!nombreHost.isEmpty()) {
                lblJugador1Nombre.setText(nombreHost);
                lblJugador1Nombre.setForeground(COLOR_TEXTO_OSCURO);
                lblJugador1Nombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                lblJugador1Listo.setText("Si");
                lblJugador1Listo.setForeground(new Color(0, 128, 0));
            } else {
                lblJugador1Nombre.setText("Buscando...");
                lblJugador1Nombre.setForeground(Color.GRAY);
                lblJugador1Nombre.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                lblJugador1Listo.setText("No");
                lblJugador1Listo.setForeground(Color.RED);
            }

            // Mostrar Invitado (Jugador 2)
            if (!nombreInvitado.isEmpty()) {
                lblJugador2Nombre.setText(nombreInvitado);
                lblJugador2Nombre.setForeground(COLOR_TEXTO_OSCURO);
                lblJugador2Nombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                if (esHost) {
                    // Si soy host, habilitar comenzar cuando jugador 2 esté listo
                    if (jugador2Listo) {
                        lblJugador2Listo.setText("Si");
                        lblJugador2Listo.setForeground(new Color(0, 128, 0));
                        habilitarBotonComenzar(true);
                    } else {
                        lblJugador2Listo.setText("No");
                        lblJugador2Listo.setForeground(Color.RED);
                        habilitarBotonComenzar(false);
                    }
                } else {
                    // Si soy jugador 2, mostrar mi estado de "Listo"
                    if (yoDiListo) {
                        lblJugador2Listo.setText("Si");
                        lblJugador2Listo.setForeground(new Color(0, 128, 0));
                        habilitarBotonComenzar(false); // Ya di listo, esperar al host
                    } else {
                        lblJugador2Listo.setText("No");
                        lblJugador2Listo.setForeground(Color.RED);
                        habilitarBotonComenzar(true); // Puedo dar "Listo"
                    }
                }
            } else {
                lblJugador2Nombre.setText("Buscando...");
                lblJugador2Nombre.setForeground(Color.GRAY);
                lblJugador2Nombre.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                lblJugador2Listo.setText("No");
                lblJugador2Listo.setForeground(Color.RED);
                habilitarBotonComenzar(false);
            }
        });
    }

    @Override
    public void notificar(String contexto, Object datos) {
        System.out.println("[FrmLobby] Notificacion: " + contexto);

        switch (contexto) {
            case "JUGADOR_UNIDO" -> {
                if (datos instanceof JugadorDTO nuevoJugador) {
                    SwingUtilities.invokeLater(() -> {
                        boolean existe = jugadores.stream()
                                .anyMatch(j -> j.getNombre().equals(nuevoJugador.getNombre()));
                        if (!existe) {
                            jugadores.add(nuevoJugador);
                            System.out.println("[FrmLobby] Jugador agregado: " + nuevoJugador.getNombre());
                        }
                        actualizarUI();
                    });
                }
            }
            case "JUGADOR_LISTO" -> {
                // Cuando el jugador 2 da click en "Listo"
                SwingUtilities.invokeLater(() -> {
                    jugador2Listo = true;
                    actualizarUI();
                });
            }
            case "ABANDONAR_LOBBY" -> {
                // Cuando un jugador abandona el lobby
                if (datos instanceof JugadorDTO jugadorAbandono) {
                    SwingUtilities.invokeLater(() -> {
                        // Si la ventana ya fue cerrada, ignorar
                        if (!isDisplayable()) {
                            return;
                        }

                        // Verificar si yo soy el que abandonó (ignorar mi propio mensaje)
                        // Si miJugador es null, significa que reiniciarEstado() ya se ejecutó
                        // y probablemente soy yo quien abandonó
                        JugadorDTO miJugador = controlVista.getControl().getJugador();
                        if (miJugador == null || jugadorAbandono.getNombre().equals(miJugador.getNombre())) {
                            return; // Estado limpiado o soy yo quien abandonó, ignorar
                        }

                        // Si yo NO soy el Host, significa que el Host abandonó → ir al menú
                        if (!esHost) {
                            JOptionPane.showMessageDialog(this,
                                    "El Host abandonó la partida.\nSerás redirigido al menú principal.",
                                    "Host abandonó",
                                    JOptionPane.WARNING_MESSAGE);

                            controlVista.desuscribirLobby(this);
                            // No notificar al servidor, el Host ya lo hizo al abandonar
                            controlVista.reiniciarEstado(false);

                            FrmMenuPrincipal menu = new FrmMenuPrincipal();
                            menu.setVisible(true);
                            dispose();
                            return;
                        }

                        // Si soy el Host, solo actualizar la lista (J2 abandonó)
                        jugadores.removeIf(j -> j.getNombre().equals(jugadorAbandono.getNombre()));
                        jugador2Listo = false;
                        System.out.println("[FrmLobby] Jugador removido: " + jugadorAbandono.getNombre());

                        JOptionPane.showMessageDialog(this,
                                "El jugador " + jugadorAbandono.getNombre() + " abandonó el lobby.",
                                "Jugador abandonó",
                                JOptionPane.INFORMATION_MESSAGE);

                        actualizarUI();
                    });
                }
            }
            case "EMPEZAR_PARTIDA", "TABLEROS_LISTOS" -> {
                // Ir a la pantalla de colocar naves
                SwingUtilities.invokeLater(this::irAColocarNaves);
            }
        }
    }

    private void accionComenzarListo() {
        // Evitar doble click
        if (accionEnProceso) {
            return;
        }
        accionEnProceso = true;
        btnComenzarListo.setEnabled(false);

        if (esHost) {
            // El Host da click en "Comenzar"
            if (jugadores.size() >= 2 && jugador2Listo) {
                controlVista.empezarPartida();
                irAColocarNaves();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Esperando a que el otro jugador esté listo.",
                        "Esperando jugador",
                        JOptionPane.INFORMATION_MESSAGE);
                // Rehabilitar si no se pudo comenzar
                accionEnProceso = false;
                btnComenzarListo.setEnabled(true);
            }
        } else {
            // El Jugador 2 da click en "Listo"
            // Notificar al servidor que está listo
            controlVista.jugadorListo();
            yoDiListo = true;

            btnComenzarListo.setText("Listo!");
            actualizarUI();

            JOptionPane.showMessageDialog(this,
                    "Esperando a que el Host inicie la partida...",
                    "Listo",
                    JOptionPane.INFORMATION_MESSAGE);
            // No rehabilitar - J2 ya dio listo
        }
    }

    private void irAColocarNaves() {
        // Evitar navegación duplicada
        if (navegando) {
            return;
        }
        navegando = true;

        controlVista.desuscribirLobby(this);
        FrmColocarNaves frmNaves = new FrmColocarNaves();
        frmNaves.setVisible(true);
        dispose();
    }

    private void abandonar() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que quieres abandonar el lobby?",
                "Confirmar abandono",
                JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            JugadorDTO jugador = controlVista.getControl().getJugador();
            controlVista.abandonarLobby(jugador);
            controlVista.desuscribirLobby(this);

            // Limpiar estado para nueva partida (no notificar de nuevo)
            controlVista.reiniciarEstado(false);

            FrmMenuPrincipal menu = new FrmMenuPrincipal();
            menu.setVisible(true);
            dispose();
        }
    }
}
