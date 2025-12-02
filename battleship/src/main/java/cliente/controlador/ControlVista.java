package cliente.controlador;

import compartido.comunicacion.dto.CoordenadasDTO;
import compartido.comunicacion.dto.DisparoDTO;
import compartido.comunicacion.dto.JugadorDTO;
import compartido.comunicacion.dto.NaveDTO;
import compartido.comunicacion.dto.TurnoDTO;
import compartido.entidades.Jugador;
import compartido.enums.ResultadoDisparo;
import compartido.observador.ISuscriptor;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import compartido.enums.EstadoPartida;
import cliente.presentacion.componentes.CasillaButton;
import cliente.presentacion.componentes.CasillaPanel;
import cliente.presentacion.frames.FrmFinPartida;
import cliente.presentacion.frames.FrmPartidaEnCurso;
import cliente.presentacion.componentes.IndicadorTurnoPanel;
import cliente.presentacion.componentes.MarcadorNavesPanel;
import cliente.presentacion.componentes.TimerPanel;

/**
 *
 * @author daniel
 */
public class ControlVista implements ISuscriptor {

    private static ControlVista controlVista;

    private IControlador control;
    private List<CasillaPanel> casillasPropias;
    private List<CasillaButton> casillasEnemigas;
    private TimerPanel timer;
    private MarcadorNavesPanel marcadorNavesPanel;
    private IndicadorTurnoPanel indicadorTurnoPanel;
    private Map<String, Consumer<Object>> manejadoresNoti;
    private List<ISuscriptor> suscriptoresLobby;
    private String codigoPartida;
    private boolean esHost;

    private ControlVista() {
        manejadoresNoti = new HashMap<>();
        manejadoresNoti.put("RESULTADO_DISPARO", this::manejarDisparo);
        manejadoresNoti.put("ABANDONO_PARTIDA", this::manejarAbandono);
        manejadoresNoti.put("UNIRSE_PARTIDA", this::manejarUnirsePartida);
        manejadoresNoti.put("JUGADOR_UNIDO", this::manejarUnirsePartida);  // El servidor envía con esta clave
        manejadoresNoti.put("EMPEZAR_PARTIDA", this::manejarEmpezarPartida);
        manejadoresNoti.put("ABANDONAR_LOBBY", this::manejarAbandonarLobby);
        manejadoresNoti.put("TABLEROS_LISTOS", this::manejarTablerosListos);
        manejadoresNoti.put("TURNO_INICIAL", this::manejarTurnoInicial);
        manejadoresNoti.put("CONFIRMAR_TABLERO", this::manejarConfirmarTablero);
        manejadoresNoti.put("FIN_PARTIDA", this::manejarFinPartida);
        manejadoresNoti.put("ERROR_UNIRSE", this::manejarErrorUnirse);
        manejadoresNoti.put("CAMBIO_TURNO", this::manejarCambioTurno);
        suscriptoresLobby = new ArrayList<>();
    }

    public void suscribirLobby(ISuscriptor suscriptor) {
        if (suscriptor != null && !suscriptoresLobby.contains(suscriptor)) {
            suscriptoresLobby.add(suscriptor);
        }
    }

    public void desuscribirLobby(ISuscriptor suscriptor) {
        suscriptoresLobby.remove(suscriptor);
    }

    private void notificarLobby(String contexto, Object datos) {
        for (ISuscriptor s : suscriptoresLobby) {
            s.notificar(contexto, datos);
        }
    }

    public String getCodigoPartida() {
        return codigoPartida;
    }

    public void setCodigoPartida(String codigoPartida) {
        this.codigoPartida = codigoPartida;
    }

    public boolean isEsHost() {
        return esHost;
    }

    public void setEsHost(boolean esHost) {
        this.esHost = esHost;
    }

    /**
     * Retorna la instancia única de ControlVista (Patrón Singleton).
     * Synchronized para garantizar thread-safety en entornos multi-hilo.
     */
    public static synchronized ControlVista getInstancia() {
        if (controlVista == null) {
            controlVista = new ControlVista();
        }
        return controlVista;
    }

    public IControlador getControl() {
        return this.control;
    }

    public List<CasillaPanel> getCasillasPropias() {
        return casillasPropias;
    }

    public List<CasillaButton> getCasillasEnemigas() {
        return casillasEnemigas;
    }

    public void setControl(IControlador control) {
        this.control = control;
    }

    public TimerPanel getTimer() {
        return timer;
    }

    public void setTimer(TimerPanel timer) {
        this.timer = timer;
    }

    public MarcadorNavesPanel getMarcadorNavesPanel() {
        return marcadorNavesPanel;
    }

    public void setMarcadorNavesPanel(MarcadorNavesPanel panel) {
        this.marcadorNavesPanel = panel;
        System.out.println("MarcadorNavesPanel registrado correctamente");
    }

    public IndicadorTurnoPanel getIndicadorTurnoPanel() {
        return indicadorTurnoPanel;
    }

    public void setIndicadorTurnoPanel(IndicadorTurnoPanel panel) {
        this.indicadorTurnoPanel = panel;
        System.out.println("IndicadorTurnoPanel registrado correctamente");
    }

    public void actualizarIndicadorTurno(boolean esMiTurno) {
        if (indicadorTurnoPanel != null) {
            indicadorTurnoPanel.setEsMiTurno(esMiTurno);
        }
    }

    public void realizarDisparo(CoordenadasDTO c) {
        System.out.println("[ControlVista] realizarDisparo llamado en coordenadas: " + c.getX() + "," + c.getY());
        System.out.println("[ControlVista] esMiTurno: " + esMiTurno());
        control.realizarDisparo(c);
    }

    private Component getCasillaPropia(CoordenadasDTO c) {
        Component cP = casillasPropias.stream().filter(e -> e.getCoordenadas().getX() == c.getX()
                && e.getCoordenadas().getY() == c.getY())
                .findFirst()
                .orElse(null);

        return cP;
    }

    private Component getCasillaEnemiga(CoordenadasDTO c) {
        Component cB = casillasEnemigas.stream().filter(e -> e.getCoordenadas().getX() == c.getX()
                && e.getCoordenadas().getY() == c.getY())
                .findFirst()
                .orElse(null);

        return cB;
    }

    @Override
    public void notificar(String contexto, Object datos) {
        Consumer<Object> manejador = manejadoresNoti.get(contexto);
        if (manejador == null) {
            // Contexto no manejado - ignorar silenciosamente
            System.out.println("[ControlVista] Contexto no manejado: " + contexto);
            return;
        }
        if (datos == null) {
            System.out.println("[ControlVista] Datos vacios para contexto: " + contexto);
            return;
        }
        manejador.accept(datos);
    }

    private void manejarDisparo(Object datos) {
        if (!(datos instanceof DisparoDTO)) {
            System.out.println("Los datos no son un objeto DisparoDTO");
            return;
        }

        DisparoDTO d = (DisparoDTO) datos;
        CoordenadasDTO c = d.getCoordenadas();
        JugadorDTO jugador = control.getJugador();
        boolean fueYoQuienDisparo = d.getJugador().getNombre().equals(jugador.getNombre());

        // Manejar caso de coordenada ya disparada
        if (d.getResultadoDisparo() == ResultadoDisparo.YA_DISPARADO) {
            if (fueYoQuienDisparo) {
                JOptionPane.showMessageDialog(null,
                        "Ya disparaste a esa coordenada anteriormente.\nElige otra casilla.",
                        "Coordenada repetida",
                        JOptionPane.WARNING_MESSAGE);
            }
            return;
        }

        // Reiniciar Temporizador
        timer.initTimer();

        Component componente;
        if (fueYoQuienDisparo) {
            componente = getCasillaEnemiga(d.getCoordenadas());
        } else {
            componente = getCasillaPropia(d.getCoordenadas());
        }

        // Pintar resultado del disparo y actualizar marcador
        if (d.getResultadoDisparo() == ResultadoDisparo.IMPACTO) {
            componente.setBackground(Color.YELLOW);
            // Actualizar marcador de naves - nave averiada
            if (fueYoQuienDisparo && marcadorNavesPanel != null && d.getTipoNaveImpactada() != null) {
                marcadorNavesPanel.naveAveriada(d.getTipoNaveImpactada());
            }
        }
        if (d.getResultadoDisparo() == ResultadoDisparo.HUNDIMIENTO) {
            componente.setBackground(Color.RED);
            // Actualizar marcador de naves - nave hundida
            if (fueYoQuienDisparo && marcadorNavesPanel != null && d.getTipoNaveHundida() != null) {
                marcadorNavesPanel.naveHundida(d.getTipoNaveHundida());
            }
        }
        if (d.getResultadoDisparo() == ResultadoDisparo.AGUA) {
            componente.setBackground(Color.BLUE);
        }

        // Actualizar indicador de turno
        // Si acerté (IMPACTO o HUNDIMIENTO), sigo yo. Si fallé (AGUA), turno del otro.
        if (fueYoQuienDisparo) {
            boolean mantengoTurno = (d.getResultadoDisparo() == ResultadoDisparo.IMPACTO ||
                                      d.getResultadoDisparo() == ResultadoDisparo.HUNDIMIENTO);
            // IMPORTANTE: Actualizar el modelo para que el próximo click funcione
            control.setTurno(mantengoTurno);
            if (indicadorTurnoPanel != null) {
                indicadorTurnoPanel.setEsMiTurno(mantengoTurno);
            }
            // Habilitar/deshabilitar casillas enemigas segun turno
            casillasEnemigas.forEach(casilla -> casilla.setEnabled(mantengoTurno));
        } else {
            // El oponente disparo, verificar si ahora es mi turno
            boolean esMiTurno = (d.getResultadoDisparo() == ResultadoDisparo.AGUA);
            // IMPORTANTE: Actualizar el modelo para que el próximo click funcione
            control.setTurno(esMiTurno);
            if (indicadorTurnoPanel != null) {
                indicadorTurnoPanel.setEsMiTurno(esMiTurno);
            }
            casillasEnemigas.forEach(casilla -> casilla.setEnabled(esMiTurno));
        }

        System.out.println(c.getX() + " " + c.getY());
        System.out.println(d.getResultadoDisparo().toString());

        if (d.getEstadoPartida() == EstadoPartida.FINALIZADA) {
            casillasEnemigas.forEach(e -> e.setEnabled(false));
            timer.stopTimer();
            System.out.println("EL JUGADOR " + d.getJugador().getNombre() + " GANO LA PARTIDA!!");

            // Determinar si yo gane o perdi
            JugadorDTO ganador = d.getJugador();
            JugadorDTO miJugador = control.getJugador();
            boolean gane = miJugador != null && ganador.getNombre().equals(miJugador.getNombre());

            // Mostrar pantalla de fin de partida
            mostrarPantallaFinPartida(ganador, gane);
        }
    }

    private void manejarAbandono(Object datos) {
        JugadorDTO dto = (JugadorDTO) datos;
        JugadorDTO yo = control.getJugador();

        // Si YO soy el que abandono, no mostrar nada
        if (dto.getNombre().equals(yo.getNombre())) {
            return;
        }

        // Deshabilitar controles
        if (casillasEnemigas != null) {
            casillasEnemigas.forEach(c -> c.setEnabled(false));
        }
        if (timer != null) {
            timer.stopTimer();
        }

        System.out.println("Partida finalizada por abandono del rival: " + dto.getNombre());

        // El rival abandono, YO gano por abandono
        mostrarPantallaFinPartida(yo, true);
    }

    public void initTableroPropio() {
        casillasPropias = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                CoordenadasDTO coordenadas = new CoordenadasDTO(i, j);
                CasillaPanel cP = new CasillaPanel(coordenadas);
                cP.setBackground(new Color(242, 242, 242)); // O cualquier color que necesites
                cP.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                casillasPropias.add(cP);
            }
        }
    }

    public void initTableroEnemigo() {
        casillasEnemigas = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                CoordenadasDTO coordenadas = new CoordenadasDTO(i, j);
                CasillaButton cB = new CasillaButton(coordenadas);
                cB.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                cB.setEnabled(false); // Empiezan deshabilitadas hasta que sea nuestro turno
                cB.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        realizarDisparo(coordenadas);
                    }
                });
                casillasEnemigas.add(cB);
            }
        }
    }

    /**
     * Habilita o deshabilita las casillas enemigas segun el turno.
     */
    public void habilitarCasillasEnemigas(boolean habilitar) {
        if (casillasEnemigas != null) {
            casillasEnemigas.forEach(c -> c.setEnabled(habilitar));
        }
    }

    /**
     * Consulta si es el turno del jugador actual.
     */
    public boolean esMiTurno() {
        return control.esMiTurno();
    }

    public void crearPartida(Jugador j) {
        control.crearPartida(j);
    }

    public void addNave(NaveDTO nave, List<CoordenadasDTO> coordenadas) {
        control.addNave(nave, coordenadas);
    }

    public void addJugador(Jugador j) {
        control.addJugador(j);
    }

    public void crearTableros() {
        control.crearTableros();
    }

    public void suscribirAModelo() {
        control.suscribirAPartida(this);
    }

    public void mostrarFrmPartidaEnCurso() {
        new FrmPartidaEnCurso().setVisible(true);
        timer.initTimer();
    }

    // Caso de Uso: Unirse Partida
    public void unirsePartida(JugadorDTO jugador) {
        control.unirsePartida(jugador);
    }

    public void empezarPartida() {
        control.empezarPartida();
    }

    public void abandonarLobby(JugadorDTO jugador) {
        control.abandonarLobby(jugador);
    }

    public List<JugadorDTO> getJugadores() {
        return control.getJugadores();
    }

    public void abandonarPartida() {

        // 1. Obtener DTO del jugador actual
        JugadorDTO dto = control.getJugador();

        // 2. Convertir DTO → ENTIDAD
        Jugador jugador = new Jugador(dto.getNombre(), dto.getColor(), dto.getEstado());

        control.abandonarPartida(jugador);
    }

    private void manejarUnirsePartida(Object datos) {
        JugadorDTO dto = (JugadorDTO) datos;
        System.out.println("=== ControlVista: manejarUnirsePartida ===");
        System.out.println("Jugador recibido: " + dto.getNombre());
        System.out.println("Suscriptores del lobby: " + suscriptoresLobby.size());

        // Notificar a los suscriptores del lobby para actualizar la UI
        notificarLobby("JUGADOR_UNIDO", dto);
    }

    private void manejarEmpezarPartida(Object datos) {
        JugadorDTO dto = (JugadorDTO) datos;
        JOptionPane.showMessageDialog(null, "El jugador " + dto.getNombre() + " empezo la partida.");
    }

    private void manejarAbandonarLobby(Object datos) {
        JugadorDTO dto = (JugadorDTO) datos;
        JOptionPane.showMessageDialog(null, "El jugador " + dto.getNombre() + " abandono el lobby.");
    }

    private void manejarTablerosListos(Object datos) {
        System.out.println("[ControlVista] Tableros listos - notificando a suscriptores");
        notificarLobby("TABLEROS_LISTOS", datos);
    }

    private void manejarTurnoInicial(Object datos) {
        System.out.println("[ControlVista] Turno inicial recibido");
        notificarLobby("TURNO_INICIAL", datos);

        // Actualizar indicador de turno y casillas si existen
        if (datos instanceof TurnoDTO turno) {
            JugadorDTO miJugador = control.getJugador();
            boolean esMiTurno = miJugador != null &&
                               turno.getNombreJugadorEnTurno().equals(miJugador.getNombre());

            System.out.println("[ControlVista] Es mi turno: " + esMiTurno + " (Jugador en turno: " + turno.getNombreJugadorEnTurno() + ")");

            // IMPORTANTE: Asegurar que el modelo esté sincronizado
            control.setTurno(esMiTurno);

            if (indicadorTurnoPanel != null) {
                indicadorTurnoPanel.setEsMiTurno(esMiTurno);
            }

            // Habilitar/deshabilitar casillas enemigas
            habilitarCasillasEnemigas(esMiTurno);
        }
    }

    private void manejarConfirmarTablero(Object datos) {
        System.out.println("[ControlVista] Confirmacion de tablero recibida");
        notificarLobby("CONFIRMAR_TABLERO", datos);
    }

    private void manejarCambioTurno(Object datos) {
        System.out.println("[ControlVista] Cambio de turno recibido");
        if (datos instanceof TurnoDTO turno) {
            JugadorDTO miJugador = control.getJugador();
            boolean esMiTurno = miJugador != null &&
                               turno.getNombreJugadorEnTurno().equals(miJugador.getNombre());

            System.out.println("[ControlVista] Cambio turno - Es mi turno: " + esMiTurno);

            // Actualizar el modelo
            control.setTurno(esMiTurno);

            // Actualizar la UI
            if (indicadorTurnoPanel != null) {
                indicadorTurnoPanel.setEsMiTurno(esMiTurno);
            }
            habilitarCasillasEnemigas(esMiTurno);

            // Reiniciar timer
            if (timer != null) {
                timer.initTimer();
            }
        }
    }

    /**
     * Metodo llamado cuando el timer se agota.
     * Actualiza el modelo y notifica al servidor.
     */
    public void tiempoAgotado() {
        System.out.println("[ControlVista] Tiempo agotado - perdiendo turno");

        // Actualizar modelo y notificar servidor
        control.notificarTiempoAgotado();

        // Actualizar UI
        if (indicadorTurnoPanel != null) {
            indicadorTurnoPanel.setEsMiTurno(false);
        }
        habilitarCasillasEnemigas(false);
    }

    private void manejarFinPartida(Object datos) {
        System.out.println("[ControlVista] Fin de partida recibido");
        if (datos instanceof JugadorDTO ganador) {
            JugadorDTO miJugador = control.getJugador();
            boolean gane = miJugador != null && ganador.getNombre().equals(miJugador.getNombre());

            // Detener timer
            if (timer != null) {
                timer.stopTimer();
            }

            // Deshabilitar casillas
            if (casillasEnemigas != null) {
                casillasEnemigas.forEach(c -> c.setEnabled(false));
            }

            // Mostrar pantalla de fin de partida
            mostrarPantallaFinPartida(ganador, gane);
        }
    }

    /**
     * Muestra la pantalla de fin de partida.
     */
    public void mostrarPantallaFinPartida(JugadorDTO ganador, boolean gane) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            FrmFinPartida frmFin = new FrmFinPartida(ganador, gane);
            frmFin.setVisible(true);
        });
    }

    /**
     * Maneja errores al intentar unirse a la partida.
     */
    private void manejarErrorUnirse(Object datos) {
        System.out.println("[ControlVista] Error al unirse recibido");
        notificarLobby("ERROR_UNIRSE", datos);
    }
}
