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
import cliente.presentacion.frames.FrmColocarNaves;
import cliente.presentacion.frames.FrmFinPartida;
import cliente.presentacion.frames.FrmLobby;
import cliente.presentacion.frames.FrmPartidaEnCurso;
import cliente.presentacion.componentes.IndicadorTurnoPanel;
import cliente.presentacion.componentes.MarcadorNavesPanel;
import cliente.presentacion.componentes.TimerPanel;

/**
 * Esta clase es como el "puente" entre las pantallas y el controlador principal.
 * Es un Singleton porque necesitamos que solo exista una instancia que maneje
 * todas las vistas del juego.
 *
 * Tambien implementa ISuscriptor para que cuando algo cambie en el modelo
 * (como un disparo o cambio de turno), esta clase se entere y actualice la pantalla.
 *
 * Basicamente se encarga de:
 * - Manejar las casillas de los tableros (el tuyo y el del enemigo)
 * - El timer de 30 segundos por turno
 * - Mostrar quien va ganando y de quien es el turno
 * - Cambiar de pantalla cuando toca (lobby -> colocar naves -> juego, etc)
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public class ControlVista implements ISuscriptor {

    /** Instancia unica del singleton */
    private static ControlVista controlVista;

    /** Referencia al controlador principal */
    private IControlador control;

    /** Lista de paneles que representan las casillas del tablero propio */
    private List<CasillaPanel> casillasPropias;

    /** Lista de botones que representan las casillas del tablero enemigo */
    private List<CasillaButton> casillasEnemigas;

    /** Panel que muestra el temporizador del turno */
    private TimerPanel timer;

    /** Panel que muestra el marcador de naves (hundidas, averiadas, etc) */
    private MarcadorNavesPanel marcadorNavesPanel;

    /** Panel que indica de quien es el turno actual */
    private IndicadorTurnoPanel indicadorTurnoPanel;

    /** Mapa de manejadores para cada tipo de notificacion */
    private Map<String, Consumer<Object>> manejadoresNoti;

    /** Lista de suscriptores del lobby para notificar cambios */
    private List<ISuscriptor> suscriptoresLobby;

    /** Codigo unico de la partida actual */
    private String codigoPartida;

    /** Indica si este cliente es el host de la partida */
    private boolean esHost;

    /** Bandera para evitar mostrar multiples pantallas de fin */
    private boolean partidaFinalizada = false;

    /** Bandera para evitar abrir multiples ventanas de partida */
    private boolean navegandoAPartida = false;

    /** Indica si hay una partida registrada en el servidor */
    private boolean partidaActiva = false;

    /**
     * Constructor privado del singleton.
     * Inicializa el mapa de manejadores para cada tipo de evento.
     */
    private ControlVista() {
        manejadoresNoti = new HashMap<>();
        manejadoresNoti.put("RESULTADO_DISPARO", this::manejarDisparo);
        manejadoresNoti.put("JUGADOR_ABANDONO", this::manejarAbandono);
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
        manejadoresNoti.put("JUGADOR_LISTO", this::manejarJugadorListo);
        suscriptoresLobby = new ArrayList<>();
    }

    /**
     * Suscribe un observador para recibir eventos del lobby.
     * @param suscriptor el componente que quiere recibir notificaciones
     */
    public void suscribirLobby(ISuscriptor suscriptor) {
        if (suscriptor != null && !suscriptoresLobby.contains(suscriptor)) {
            suscriptoresLobby.add(suscriptor);
        }
    }

    /**
     * Elimina un observador de la lista del lobby.
     * @param suscriptor el observador a eliminar
     */
    public void desuscribirLobby(ISuscriptor suscriptor) {
        suscriptoresLobby.remove(suscriptor);
    }

    /**
     * Notifica a todos los suscriptores del lobby sobre un evento.
     * @param contexto el tipo de evento
     * @param datos la informacion del evento
     */
    private void notificarLobby(String contexto, Object datos) {
        for (ISuscriptor s : suscriptoresLobby) {
            s.notificar(contexto, datos);
        }
    }

    /** @return el codigo de la partida actual */
    public String getCodigoPartida() {
        return codigoPartida;
    }

    /** @param codigoPartida el codigo unico de la partida */
    public void setCodigoPartida(String codigoPartida) {
        this.codigoPartida = codigoPartida;
    }

    /** @return true si este cliente es el host */
    public boolean isEsHost() {
        return esHost;
    }

    /** @param esHost indica si somos el host de la partida */
    public void setEsHost(boolean esHost) {
        this.esHost = esHost;
    }

    /**
     * Retorna la instancia unica de ControlVista (Patron Singleton).
     * El metodo es synchronized para evitar problemas en entornos multi-hilo.
     *
     * @return la unica instancia de ControlVista
     */
    public static synchronized ControlVista getInstancia() {
        if (controlVista == null) {
            controlVista = new ControlVista();
        }
        return controlVista;
    }

    /** @return referencia al controlador principal */
    public IControlador getControl() {
        return this.control;
    }

    /** @return lista de casillas del tablero propio */
    public List<CasillaPanel> getCasillasPropias() {
        return casillasPropias;
    }

    /** @return lista de casillas del tablero enemigo */
    public List<CasillaButton> getCasillasEnemigas() {
        return casillasEnemigas;
    }

    /** @param control el controlador principal a usar */
    public void setControl(IControlador control) {
        this.control = control;
    }

    /** @return el panel del temporizador */
    public TimerPanel getTimer() {
        return timer;
    }

    /** @param timer el panel del temporizador a usar */
    public void setTimer(TimerPanel timer) {
        this.timer = timer;
    }

    /** @return el panel del marcador de naves */
    public MarcadorNavesPanel getMarcadorNavesPanel() {
        return marcadorNavesPanel;
    }

    /** @param panel el panel del marcador a registrar */
    public void setMarcadorNavesPanel(MarcadorNavesPanel panel) {
        this.marcadorNavesPanel = panel;
        System.out.println("MarcadorNavesPanel registrado correctamente");
    }

    /** @return el panel indicador de turno */
    public IndicadorTurnoPanel getIndicadorTurnoPanel() {
        return indicadorTurnoPanel;
    }

    /** @param panel el panel indicador de turno a registrar */
    public void setIndicadorTurnoPanel(IndicadorTurnoPanel panel) {
        this.indicadorTurnoPanel = panel;
        System.out.println("IndicadorTurnoPanel registrado correctamente");
    }

    /**
     * Actualiza el indicador visual del turno.
     * @param esMiTurno true si es nuestro turno
     */
    public void actualizarIndicadorTurno(boolean esMiTurno) {
        if (indicadorTurnoPanel != null) {
            indicadorTurnoPanel.setEsMiTurno(esMiTurno);
        }
    }

    /**
     * Solicita al controlador que realice un disparo en las coordenadas dadas.
     * @param c las coordenadas donde se quiere disparar
     */
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
            if (fueYoQuienDisparo) {
                // En tablero enemigo: fondo amarillo
                componente.setBackground(Color.YELLOW);
                // Actualizar marcador de naves - nave averiada (solo primer impacto)
                if (marcadorNavesPanel != null && d.getTipoNaveImpactada() != null) {
                    marcadorNavesPanel.naveAveriada(d.getTipoNaveImpactada());
                }
            } else {
                // En mi tablero: mantener color de nave + indicador visual de impacto
                if (componente instanceof CasillaPanel casillaPropia) {
                    casillaPropia.setEstado(CasillaPanel.EstadoCasilla.IMPACTO);
                }
            }
        }
        if (d.getResultadoDisparo() == ResultadoDisparo.HUNDIMIENTO) {
            if (fueYoQuienDisparo) {
                // En tablero enemigo: fondo rojo
                componente.setBackground(Color.RED);
                // Actualizar marcador de naves - nave hundida
                if (marcadorNavesPanel != null && d.getTipoNaveHundida() != null) {
                    marcadorNavesPanel.naveHundida(d.getTipoNaveHundida());
                }
            } else {
                // En mi tablero: indicador visual de hundimiento
                if (componente instanceof CasillaPanel casillaPropia) {
                    casillaPropia.setEstado(CasillaPanel.EstadoCasilla.HUNDIDA);
                }
            }
        }
        if (d.getResultadoDisparo() == ResultadoDisparo.AGUA) {
            if (fueYoQuienDisparo) {
                // En tablero enemigo: fondo azul
                componente.setBackground(Color.BLUE);
            } else {
                // En mi tablero: fondo azul claro para agua (no deberia pasar en casilla con nave)
                componente.setBackground(new Color(100, 149, 237)); // Azul acero
            }
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
            System.out.println("EL JUGADOR " + d.getJugador().getNombre() + " GANO LA PARTIDA!!");

            // Determinar si yo gane o perdi
            JugadorDTO ganador = d.getJugador();
            JugadorDTO miJugador = control.getJugador();
            boolean gane = miJugador != null && ganador.getNombre().equals(miJugador.getNombre());

            // Mostrar pantalla de fin de partida (maneja timer y casillas internamente)
            mostrarPantallaFinPartida(ganador, gane);
        }
    }

    private void manejarAbandono(Object datos) {
        // Evitar procesar el abandono múltiples veces
        if (partidaFinalizada) {
            return;
        }

        JugadorDTO dto = (JugadorDTO) datos;
        JugadorDTO yo = control.getJugador();

        // Verificación null defensiva
        if (yo == null || dto == null || dto.getNombre() == null) {
            System.out.println("[ControlVista] Datos incompletos en manejarAbandono");
            return;
        }

        // Si YO soy el que abandono, no mostrar nada
        if (dto.getNombre().equals(yo.getNombre())) {
            return;
        }

        System.out.println("Partida finalizada por abandono del rival: " + dto.getNombre());

        // Si hay casillas enemigas, significa que estamos en FrmPartidaEnCurso
        if (casillasEnemigas != null && !casillasEnemigas.isEmpty()) {
            // mostrarPantallaFinPartida establece partidaFinalizada internamente
            mostrarPantallaFinPartidaPorAbandono(yo);
        } else {
            // Estamos en FrmColocarNaves - notificar al lobby para que lo maneje
            partidaFinalizada = true;
            notificarLobby("JUGADOR_ABANDONO", dto);
        }
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

    public void limpiarNaves() {
        control.limpiarNaves();
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
        // Evitar abrir múltiples ventanas
        if (navegandoAPartida) {
            System.out.println("[ControlVista] Ya se está navegando a partida, ignorando...");
            return;
        }
        navegandoAPartida = true;

        new FrmPartidaEnCurso().setVisible(true);
        timer.initTimer();
    }

    // Caso de Uso: Unirse Partida
    public void unirsePartida(JugadorDTO jugador) {
        control.unirsePartida(jugador);
    }

    /**
     * Crea una partida y la registra en el servidor (para el Host).
     */
    public void crearPartidaConCodigo(JugadorDTO jugador, String codigo) {
        control.crearPartidaConCodigo(jugador, codigo);
        partidaActiva = true; // Marcar que hay partida registrada en servidor
    }

    /**
     * Solicita unirse a una partida existente con un codigo.
     */
    public void unirsePartidaConCodigo(JugadorDTO jugador, String codigo) {
        control.unirsePartidaConCodigo(jugador, codigo);
        partidaActiva = true; // Marcar que hay partida registrada en servidor
    }

    public void empezarPartida() {
        control.empezarPartida();
    }

    public void jugadorListo() {
        control.jugadorListo();
    }

    private void manejarJugadorListo(Object datos) {
        System.out.println("[ControlVista] Jugador listo recibido");
        notificarLobby("JUGADOR_LISTO", datos);
    }

    public void abandonarLobby(JugadorDTO jugador) {
        control.abandonarLobby(jugador);
    }

    /**
     * Cancela una partida que fue creada pero el host retrocedio antes de ir al lobby.
     * Notifica al servidor para eliminar la partida fantasma.
     */
    public void cancelarPartidaCreada() {
        JugadorDTO jugador = control.getJugador();
        if (jugador != null) {
            // Usar abandonarLobby para notificar al servidor
            control.abandonarLobby(jugador);
        }
        // Limpiar estado local (no notificar de nuevo, ya se hizo arriba)
        reiniciarEstado(false);
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
        System.out.println("[ControlVista] Empezar partida recibido - notificando al lobby");
        // Notificar al lobby para ir a la pantalla de colocar naves
        notificarLobby("EMPEZAR_PARTIDA", datos);
    }

    private void manejarAbandonarLobby(Object datos) {
        JugadorDTO dto = (JugadorDTO) datos;
        System.out.println("[ControlVista] Jugador abandono lobby: " + dto.getNombre());

        // Notificar al lobby para que actualice la UI
        notificarLobby("ABANDONAR_LOBBY", dto);
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

            // Mostrar pantalla de fin de partida (maneja timer y casillas internamente)
            mostrarPantallaFinPartida(ganador, gane);
        }
    }

    /**
     * Muestra la pantalla de fin de partida.
     * Solo se muestra una vez gracias a la bandera partidaFinalizada.
     */
    public void mostrarPantallaFinPartida(JugadorDTO ganador, boolean gane) {
        // Evitar mostrar múltiples pantallas
        if (partidaFinalizada) {
            System.out.println("[ControlVista] Pantalla de fin ya mostrada, ignorando...");
            return;
        }
        partidaFinalizada = true;

        finalizarYMostrarPantalla(ganador, gane, false);
    }

    /**
     * Muestra la pantalla de fin de partida por abandono del oponente.
     */
    private void mostrarPantallaFinPartidaPorAbandono(JugadorDTO ganador) {
        if (partidaFinalizada) {
            System.out.println("[ControlVista] Pantalla de fin ya mostrada, ignorando...");
            return;
        }
        partidaFinalizada = true;

        finalizarYMostrarPantalla(ganador, true, true);
    }

    /**
     * Lógica compartida para finalizar partida y mostrar pantalla de fin.
     */
    private void finalizarYMostrarPantalla(JugadorDTO ganador, boolean gane, boolean porAbandono) {
        // Detener timer si existe
        if (timer != null) {
            timer.stopTimer();
        }

        // Deshabilitar casillas
        if (casillasEnemigas != null) {
            casillasEnemigas.forEach(c -> c.setEnabled(false));
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            // Cerrar la ventana de partida en curso
            cerrarVentanaPartida();

            // Mostrar pantalla de fin
            FrmFinPartida frmFin = new FrmFinPartida(ganador, gane, porAbandono);
            frmFin.setVisible(true);
        });
    }

    /**
     * Cierra las ventanas de juego si existen (partida en curso, colocar naves, lobby).
     */
    private void cerrarVentanaPartida() {
        java.awt.Window[] windows = java.awt.Window.getWindows();
        for (java.awt.Window window : windows) {
            if (window instanceof FrmPartidaEnCurso ||
                window instanceof FrmColocarNaves ||
                window instanceof FrmLobby) {
                window.dispose();
            }
        }
    }

    /**
     * Reinicia el estado para una nueva partida.
     * Si hay una partida activa que no fue notificada al servidor, la notifica.
     */
    public void reiniciarEstado() {
        reiniciarEstado(true);
    }

    /**
     * Reinicia el estado para una nueva partida.
     *
     * @param notificarServidor si es true, notifica al servidor si hay partida activa
     */
    public void reiniciarEstado(boolean notificarServidor) {
        // Detener timer si existe
        if (timer != null) {
            timer.stopTimer();
        }

        // Si hay partida activa y se debe notificar, informar al servidor
        if (notificarServidor && partidaActiva && control != null) {
            JugadorDTO jugador = control.getJugador();
            if (jugador != null) {
                // Notificar abandono al servidor para limpiar la partida
                control.abandonarLobby(jugador);
            }
        }

        partidaFinalizada = false;
        navegandoAPartida = false;
        partidaActiva = false; // Marcar que ya no hay partida activa
        casillasPropias = null;
        casillasEnemigas = null;
        timer = null;
        marcadorNavesPanel = null;
        indicadorTurnoPanel = null;
        esHost = false;
        codigoPartida = null;

        // Limpiar suscriptores del lobby para evitar notificaciones huérfanas
        suscriptoresLobby.clear();

        // Reiniciar el modelo (limpia jugadores, tablero, etc.)
        if (control != null) {
            control.reiniciarModelo();
        }

        // Cerrar ventanas de partida si existen
        cerrarVentanaPartida();

        System.out.println("[ControlVista] Estado reiniciado para nueva partida");
    }

    /**
     * Marca la partida como no activa (ya se notificó al servidor por otro medio).
     */
    public void marcarPartidaInactiva() {
        partidaActiva = false;
    }

    /**
     * Maneja errores al intentar unirse a la partida.
     */
    private void manejarErrorUnirse(Object datos) {
        System.out.println("[ControlVista] Error al unirse recibido");
        // Resetear bandera ya que el jugador no se unio exitosamente
        partidaActiva = false;
        notificarLobby("ERROR_UNIRSE", datos);
    }

    /**
     * Verifica si la partida ya terminó.
     */
    public boolean isPartidaFinalizada() {
        return partidaFinalizada;
    }
}
