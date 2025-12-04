package servidor.negocio;

import com.google.gson.Gson;
import compartido.comunicacion.Mensaje;
import compartido.comunicacion.TipoAccion;
import compartido.comunicacion.dto.AddNaveDTO;
import compartido.comunicacion.dto.CoordenadasDTO;
import compartido.comunicacion.dto.DisparoDTO;
import compartido.comunicacion.dto.JugadorDTO;
import compartido.comunicacion.dto.NaveDTO;
import compartido.comunicacion.dto.RespuestaUnirseDTO;
import compartido.comunicacion.dto.SolicitudUnirseDTO;
import compartido.comunicacion.dto.TipoNaveDTO;
import compartido.comunicacion.dto.TurnoDTO;
import compartido.enums.EstadoPartida;
import compartido.enums.ResultadoAddNave;
import compartido.enums.ResultadoDisparo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestor de la logica de negocio de una partida en el servidor.
 *
 * Esta clase maneja toda la logica del juego:
 * - Registro de jugadores
 * - Colocacion y validacion de naves
 * - Procesamiento de disparos
 * - Control de turnos
 * - Deteccion de fin de partida
 *
 * Separada del BusEventos para cumplir con:
 * - Principio de Responsabilidad Unica (SRP)
 * - Arquitectura en Capas (Capa de Negocio)
 *
 * @author Equipo
 */
public class GestorPartida {

    // Publicador de eventos (inyectado - DIP)
    private final IPublicadorEventos publicador;
    private final Gson gson;

    // Control de tableros confirmados
    private final Set<String> tablerosConfirmados;
    private final List<JugadorDTO> jugadoresEnPartida;
    private final Object lockTableros;

    // Control del turno actual
    private String nombreJugadorEnTurno;

    // Estructura para rastrear naves: Map<nombreJugador, List<NaveInfo>>
    private final ConcurrentHashMap<String, List<NaveInfo>> navesJugadores;

    // Map<nombreJugador, Set<"x,y">> - coordenadas ya disparadas al oponente
    private final ConcurrentHashMap<String, Set<String>> disparosRealizados;

    // Registro de partidas activas: Map<codigoPartida, codigoPartida>
    // (usamos el código como key y value para verificar existencia)
    private final ConcurrentHashMap<String, String> partidasActivas;

    // Mapa de ID de cliente a nombre de jugador (para manejar desconexiones)
    private final ConcurrentHashMap<String, String> clienteAJugador;

    // Nombre del jugador que es host de la partida actual
    private String nombreHostPartida;

    /**
     * Clase interna para rastrear informacion de cada nave.
     * Encapsula el estado de una nave individual.
     */
    private static class NaveInfo {
        private final TipoNaveDTO tipo;
        private final Set<String> coordenadas;
        private final Set<String> impactos;

        NaveInfo(TipoNaveDTO tipo, Set<String> coordenadas) {
            this.tipo = tipo;
            this.coordenadas = coordenadas;
            this.impactos = new HashSet<>();
        }

        TipoNaveDTO getTipo() {
            return tipo;
        }

        Set<String> getCoordenadas() {
            return coordenadas;
        }

        boolean estaHundida() {
            return impactos.size() >= coordenadas.size();
        }

        boolean recibirImpacto(String coord) {
            if (coordenadas.contains(coord)) {
                impactos.add(coord);
                return true;
            }
            return false;
        }

        /**
         * Verifica si la nave acaba de recibir su primer impacto.
         * Util para determinar si la nave paso de "sin dano" a "averiada".
         */
        boolean esPrimerImpacto() {
            return impactos.size() == 1;
        }
    }

    /**
     * Constructor del GestorPartida.
     *
     * @param publicador Interfaz para publicar eventos (inyeccion de dependencia)
     */
    public GestorPartida(IPublicadorEventos publicador) {
        this.publicador = publicador;
        this.gson = new Gson();
        this.tablerosConfirmados = new HashSet<>();
        this.jugadoresEnPartida = new ArrayList<>();
        this.lockTableros = new Object();
        this.navesJugadores = new ConcurrentHashMap<>();
        this.disparosRealizados = new ConcurrentHashMap<>();
        this.nombreJugadorEnTurno = null;
        this.partidasActivas = new ConcurrentHashMap<>();
        this.clienteAJugador = new ConcurrentHashMap<>();
    }

    /**
     * Procesa un evento y ejecuta la logica de negocio correspondiente.
     *
     * @param evento Nombre del evento
     * @param mensaje Mensaje con los datos del evento
     * @return true si el evento debe ser publicado (broadcast), false si ya fue manejado
     */
    public boolean procesarEvento(String evento, Mensaje mensaje) {
        switch (evento) {
            case "CREAR_PARTIDA":
                procesarCrearPartida(mensaje);
                return false; // No hacer broadcast, solo registrar

            case "UNIRSE_PARTIDA":
                return procesarUnirsePartida(mensaje);

            case "JUGADOR_UNIDO":
                return procesarJugadorUnido(mensaje);

            case "CONFIRMAR_TABLERO":
                procesarConfirmarTablero(mensaje);
                return true;

            case "ABANDONAR_LOBBY":
            case "JUGADOR_ABANDONO":
            case "ABANDONAR_PARTIDA":
                return procesarJugadorAbandono(mensaje);

            case "ADD_NAVE":
                procesarAddNave(mensaje);
                return false;

            case "LIMPIAR_TABLERO":
                procesarLimpiarTablero(mensaje);
                return false;

            case "DISPARO":
                procesarDisparo(mensaje);
                return false;

            case "TIEMPO_AGOTADO":
                procesarTiempoAgotado(mensaje);
                return false;

            default:
                return true;
        }
    }

    /**
     * Procesa la creacion de una nueva partida (registra el codigo).
     */
    private void procesarCrearPartida(Mensaje mensaje) {
        SolicitudUnirseDTO solicitud = gson.fromJson(mensaje.getData(), SolicitudUnirseDTO.class);
        String codigo = solicitud.getCodigoPartida();
        JugadorDTO jugador = solicitud.getJugador();
        String idCliente = mensaje.getIdPublicador();

        // Registrar la partida activa
        partidasActivas.put(codigo, codigo);
        nombreHostPartida = jugador.getNombre(); // Registrar quien es el host
        System.out.println("[GESTOR] Partida creada con codigo: " + codigo);
        System.out.println("[GESTOR] Partidas activas: " + partidasActivas.keySet());
        System.out.println("[GESTOR] Host de la partida: " + nombreHostPartida);

        // Registrar al host como jugador
        synchronized (lockTableros) {
            boolean existe = jugadoresEnPartida.stream()
                    .anyMatch(j -> j.getNombre().equals(jugador.getNombre()));
            if (!existe) {
                jugadoresEnPartida.add(jugador);
                System.out.println("[GESTOR] Host registrado: " + jugador.getNombre());
            }
        }

        // Registrar mapeo cliente -> jugador para manejar desconexiones
        clienteAJugador.put(idCliente, jugador.getNombre());
        System.out.println("[GESTOR] Cliente " + idCliente + " asociado a jugador " + jugador.getNombre());

        // Enviar confirmacion al host
        RespuestaUnirseDTO respuesta = RespuestaUnirseDTO.exito(jugador);
        enviarRespuestaUnirse(idCliente, respuesta);
    }

    /**
     * Procesa la solicitud de unirse a una partida (valida el codigo).
     */
    private boolean procesarUnirsePartida(Mensaje mensaje) {
        // Intentar parsear como SolicitudUnirseDTO primero
        SolicitudUnirseDTO solicitud = null;
        JugadorDTO jugador = null;
        String codigo = null;
        boolean esHost = false;

        try {
            solicitud = gson.fromJson(mensaje.getData(), SolicitudUnirseDTO.class);
            if (solicitud != null && solicitud.getCodigoPartida() != null) {
                codigo = solicitud.getCodigoPartida();
                jugador = solicitud.getJugador();
                esHost = solicitud.isEsHost();
            }
        } catch (Exception e) {
            // Si falla, intentar como JugadorDTO (compatibilidad)
        }

        // Si no se pudo parsear como SolicitudUnirseDTO, intentar como JugadorDTO
        if (codigo == null) {
            jugador = gson.fromJson(mensaje.getData(), JugadorDTO.class);
            // Sin codigo, permitir por compatibilidad (comportamiento anterior)
            if (jugador != null) {
                return registrarJugador(jugador);
            }
            return true;
        }

        // Variable final para usar en lambda
        final JugadorDTO jugadorFinal = jugador;
        final String codigoFinal = codigo;

        System.out.println("[GESTOR] Solicitud de union - Codigo: " + codigoFinal + ", Jugador: " + jugadorFinal.getNombre() + ", EsHost: " + esHost);

        // Si es host, ya fue procesado en CREAR_PARTIDA, solo hacer broadcast
        if (esHost) {
            return registrarJugador(jugadorFinal);
        }

        // Validar que el codigo exista
        if (!partidasActivas.containsKey(codigoFinal)) {
            System.out.println("[GESTOR] ERROR: Codigo invalido: " + codigoFinal);
            RespuestaUnirseDTO respuesta = RespuestaUnirseDTO.errorCodigoInvalido(codigoFinal);
            enviarRespuestaUnirse(mensaje.getIdPublicador(), respuesta);
            return false; // No hacer broadcast
        }

        // Validar que no haya mas de 2 jugadores
        synchronized (lockTableros) {
            if (jugadoresEnPartida.size() >= 2) {
                System.out.println("[GESTOR] ERROR: Partida llena");
                RespuestaUnirseDTO respuesta = RespuestaUnirseDTO.errorPartidaLlena();
                enviarRespuestaUnirse(mensaje.getIdPublicador(), respuesta);
                return false;
            }

            // Validar nombre duplicado
            boolean nombreExiste = jugadoresEnPartida.stream()
                    .anyMatch(j -> j.getNombre().equals(jugadorFinal.getNombre()));
            if (nombreExiste) {
                System.out.println("[GESTOR] ERROR: Nombre duplicado: " + jugadorFinal.getNombre());
                RespuestaUnirseDTO respuesta = RespuestaUnirseDTO.errorNombreDuplicado(jugadorFinal.getNombre());
                enviarRespuestaUnirse(mensaje.getIdPublicador(), respuesta);
                return false;
            }
        }

        // Todo valido, registrar jugador
        registrarJugador(jugadorFinal);

        // Registrar mapeo cliente -> jugador para manejar desconexiones
        String idCliente = mensaje.getIdPublicador();
        clienteAJugador.put(idCliente, jugadorFinal.getNombre());
        System.out.println("[GESTOR] Cliente " + idCliente + " asociado a jugador " + jugadorFinal.getNombre());

        // Enviar confirmacion al jugador que se une
        RespuestaUnirseDTO respuesta = RespuestaUnirseDTO.exito(jugadorFinal);
        enviarRespuestaUnirse(idCliente, respuesta);

        // Publicar evento con JugadorDTO (NO el mensaje original que tiene SolicitudUnirseDTO)
        publicarJugadorUnido(jugadorFinal);

        return false; // No hacer broadcast del mensaje original
    }

    /**
     * Envia la respuesta de unirse al cliente.
     */
    private void enviarRespuestaUnirse(String idCliente, RespuestaUnirseDTO respuesta) {
        Mensaje msgRespuesta = new Mensaje(
                TipoAccion.PUBLICAR,
                "RESPUESTA_UNIRSE",
                gson.toJsonTree(respuesta),
                "SERVER"
        );
        publicador.enviarUnicast(idCliente, msgRespuesta);
    }

    /**
     * Publica el evento de jugador unido a todos los clientes.
     */
    private void publicarJugadorUnido(JugadorDTO jugador) {
        Mensaje msgJugador = new Mensaje(
                TipoAccion.PUBLICAR,
                "UNIRSE_PARTIDA",
                gson.toJsonTree(jugador),
                "SERVER"
        );
        publicador.publicar("UNIRSE_PARTIDA", msgJugador);
        System.out.println("[GESTOR] Publicado UNIRSE_PARTIDA con jugador: " + jugador.getNombre());
    }

    /**
     * Registra un jugador en la partida.
     */
    private boolean registrarJugador(JugadorDTO jugador) {
        synchronized (lockTableros) {
            boolean existe = jugadoresEnPartida.stream()
                    .anyMatch(j -> j.getNombre().equals(jugador.getNombre()));
            if (!existe) {
                jugadoresEnPartida.add(jugador);
                System.out.println("[GESTOR] Jugador registrado: " + jugador.getNombre() +
                                  " (Total: " + jugadoresEnPartida.size() + ")");
            }
        }
        return true;
    }

    /**
     * Registra un jugador cuando se une a la partida.
     */
    private boolean procesarJugadorUnido(Mensaje mensaje) {
        JugadorDTO jugador = gson.fromJson(mensaje.getData(), JugadorDTO.class);
        if (jugador != null) {
            synchronized (lockTableros) {
                boolean existe = jugadoresEnPartida.stream()
                        .anyMatch(j -> j.getNombre().equals(jugador.getNombre()));
                if (!existe) {
                    jugadoresEnPartida.add(jugador);
                    System.out.println("[GESTOR] Jugador registrado: " + jugador.getNombre() +
                                      " (Total: " + jugadoresEnPartida.size() + ")");
                }
            }
        }
        return true;
    }

    /**
     * Procesa cuando un jugador abandona la partida o el lobby.
     * - Si el HOST abandona: limpia toda la partida
     * - Si un jugador NO-HOST abandona el lobby: solo lo remueve, mantiene la partida
     * - Si un jugador abandona durante la partida: notifica victoria al otro
     */
    private boolean procesarJugadorAbandono(Mensaje mensaje) {
        JugadorDTO jugadorAbandono = gson.fromJson(mensaje.getData(), JugadorDTO.class);
        if (jugadorAbandono == null) {
            return true;
        }

        String nombreAbandono = jugadorAbandono.getNombre();
        boolean esHost = nombreAbandono.equals(nombreHostPartida);
        boolean enLobby = tablerosConfirmados.isEmpty();

        System.out.println("[GESTOR] Procesando abandono de: " + nombreAbandono +
                          " (esHost: " + esHost + ", enLobby: " + enLobby + ")");

        synchronized (lockTableros) {
            // Remover al jugador que abandonó
            tablerosConfirmados.remove(nombreAbandono);
            jugadoresEnPartida.removeIf(j -> j.getNombre().equals(nombreAbandono));
            navesJugadores.remove(nombreAbandono);
            disparosRealizados.remove(nombreAbandono);
            clienteAJugador.values().removeIf(nombre -> nombre.equals(nombreAbandono));
            System.out.println("[GESTOR] Jugador removido: " + nombreAbandono);

            // Determinar si limpiar toda la partida
            if (enLobby && esHost) {
                // El HOST abandono el lobby - limpiar toda la partida
                System.out.println("[GESTOR] Host abandono el lobby - limpiando partida completa");
                limpiarPartidaCompleta();
            } else if (!enLobby) {
                // Abandono durante la partida (tableros ya confirmados)
                // El otro jugador gana, pero la partida se mantiene para mostrar resultado
                System.out.println("[GESTOR] Abandono durante partida - el otro jugador gana");
            } else {
                // Jugador NO-HOST abandono el lobby - mantener partida para el host
                System.out.println("[GESTOR] Jugador no-host abandono lobby - partida sigue activa para el host");
            }
        }
        return true;
    }

    /**
     * Limpia todos los datos de la partida excepto la estructura base.
     */
    private void limpiarPartidaCompleta() {
        for (JugadorDTO j : jugadoresEnPartida) {
            navesJugadores.remove(j.getNombre());
            disparosRealizados.remove(j.getNombre());
            clienteAJugador.values().removeIf(nombre -> nombre.equals(j.getNombre()));
        }
        jugadoresEnPartida.clear();
        partidasActivas.clear();
        tablerosConfirmados.clear();
        nombreJugadorEnTurno = null;
        nombreHostPartida = null;
        System.out.println("[GESTOR] Partida limpiada completamente");
    }

    /**
     * Maneja la adicion de una nave al tablero de un jugador.
     * Valida que no haya solapamiento con naves existentes.
     */
    private void procesarAddNave(Mensaje mensaje) {
        AddNaveDTO dto = gson.fromJson(mensaje.getData(), AddNaveDTO.class);

        String nombreJugador = dto.getJugador().getNombre();
        NaveDTO naveDTO = dto.getNave();
        List<CoordenadasDTO> coordenadasDTO = dto.getCoordenadas();

        System.out.println("[GESTOR] ADD_NAVE de " + nombreJugador +
                          " - Tipo: " + naveDTO.getTipo() +
                          " - Coords: " + coordenadasDTO.size());

        // Convertir coordenadas a strings "x,y"
        Set<String> coordenadas = new HashSet<>();
        for (CoordenadasDTO coord : coordenadasDTO) {
            coordenadas.add(coord.getX() + "," + coord.getY());
        }

        // Verificar que no se solape con naves existentes
        List<NaveInfo> navesExistentes = navesJugadores.computeIfAbsent(nombreJugador, k -> new ArrayList<>());

        boolean solapamiento = verificarSolapamiento(navesExistentes, coordenadas);

        ResultadoAddNave resultado;
        if (solapamiento) {
            resultado = ResultadoAddNave.ESPACIO_YA_OCUPADO;
            System.out.println("[GESTOR] ERROR: Solapamiento de naves");
        } else {
            NaveInfo nuevaNave = new NaveInfo(naveDTO.getTipo(), coordenadas);
            navesExistentes.add(nuevaNave);
            resultado = ResultadoAddNave.NAVE_AÑADIDA;
            System.out.println("[GESTOR] Nave agregada. Total naves de " + nombreJugador + ": " + navesExistentes.size());
        }

        // Enviar respuesta al cliente
        enviarResultadoAddNave(mensaje.getIdPublicador(), resultado);
    }

    /**
     * Verifica si hay solapamiento entre las coordenadas nuevas y las naves existentes.
     */
    private boolean verificarSolapamiento(List<NaveInfo> navesExistentes, Set<String> coordenadasNuevas) {
        for (NaveInfo naveExistente : navesExistentes) {
            for (String coord : coordenadasNuevas) {
                if (naveExistente.getCoordenadas().contains(coord)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Envia el resultado de agregar una nave al cliente.
     */
    private void enviarResultadoAddNave(String idCliente, ResultadoAddNave resultado) {
        Mensaje respuesta = new Mensaje(
                TipoAccion.PUBLICAR,
                "MENSAJE_CLIENTE_" + idCliente,
                gson.toJsonTree(resultado),
                "SERVER",
                "RESULTADO_ADD_NAVE"
        );
        publicador.enviarUnicast(idCliente, respuesta);
    }

    /**
     * Maneja la limpieza del tablero de un jugador.
     */
    private void procesarLimpiarTablero(Mensaje mensaje) {
        JugadorDTO jugadorDTO = gson.fromJson(mensaje.getData(), JugadorDTO.class);

        if (jugadorDTO == null || jugadorDTO.getNombre() == null) {
            System.out.println("[GESTOR] ERROR: Jugador nulo en LIMPIAR_TABLERO");
            return;
        }

        String nombreJugador = jugadorDTO.getNombre();
        List<NaveInfo> navesEliminadas = navesJugadores.remove(nombreJugador);
        int cantidadEliminada = navesEliminadas != null ? navesEliminadas.size() : 0;

        System.out.println("[GESTOR] LIMPIAR_TABLERO de " + nombreJugador +
                          " - Naves eliminadas: " + cantidadEliminada);
    }

    /**
     * Procesa un disparo del cliente.
     * Valida turno, coordenadas repetidas y determina el resultado.
     */
    private void procesarDisparo(Mensaje mensaje) {
        DisparoDTO disparoDTO = gson.fromJson(mensaje.getData(), DisparoDTO.class);
        JugadorDTO jugadorDTO = disparoDTO.getJugador();
        CoordenadasDTO coordDTO = disparoDTO.getCoordenadas();
        String coordKey = coordDTO.getX() + "," + coordDTO.getY();

        System.out.println("[GESTOR] Procesando disparo de: " + jugadorDTO.getNombre() +
                          " en (" + coordDTO.getX() + "," + coordDTO.getY() + ")");

        // Verificar que sea el turno del jugador
        if (!verificarTurno(jugadorDTO.getNombre())) {
            enviarResultadoDisparo(jugadorDTO, coordDTO, ResultadoDisparo.TURNO_INCORRECTO,
                                   EstadoPartida.EN_CURSO, null, null);
            return;
        }

        // Encontrar el oponente
        String nombreOponente = encontrarOponente(jugadorDTO.getNombre());
        if (nombreOponente == null) {
            System.out.println("[GESTOR] ERROR: No se encontro oponente");
            return;
        }

        // Verificar si ya se disparo en esa coordenada
        if (yaDisparoEnCoordenada(jugadorDTO.getNombre(), coordKey)) {
            enviarResultadoDisparo(jugadorDTO, coordDTO, ResultadoDisparo.YA_DISPARADO,
                                   EstadoPartida.EN_CURSO, null, null);
            return;
        }

        // Registrar el disparo
        registrarDisparo(jugadorDTO.getNombre(), coordKey);

        // Procesar el impacto
        ResultadoImpacto resultadoImpacto = procesarImpacto(nombreOponente, coordKey);

        // Actualizar turno segun resultado
        if (resultadoImpacto.resultado == ResultadoDisparo.AGUA) {
            nombreJugadorEnTurno = nombreOponente;
            System.out.println("[GESTOR] Cambio de turno a: " + nombreJugadorEnTurno);
        }

        // Publicar resultado
        enviarResultadoDisparo(jugadorDTO, coordDTO, resultadoImpacto.resultado,
                               resultadoImpacto.estadoPartida,
                               resultadoImpacto.tipoNaveImpactada,
                               resultadoImpacto.tipoNaveHundida);

        // Si la partida termino, publicar FIN_PARTIDA
        if (resultadoImpacto.estadoPartida == EstadoPartida.FINALIZADA) {
            publicarFinPartida(jugadorDTO);
        }
    }

    /**
     * Clase auxiliar para encapsular el resultado de un impacto.
     */
    private static class ResultadoImpacto {
        ResultadoDisparo resultado;
        EstadoPartida estadoPartida;
        TipoNaveDTO tipoNaveImpactada;
        TipoNaveDTO tipoNaveHundida;

        ResultadoImpacto(ResultadoDisparo resultado, EstadoPartida estadoPartida,
                         TipoNaveDTO tipoNaveImpactada, TipoNaveDTO tipoNaveHundida) {
            this.resultado = resultado;
            this.estadoPartida = estadoPartida;
            this.tipoNaveImpactada = tipoNaveImpactada;
            this.tipoNaveHundida = tipoNaveHundida;
        }
    }

    /**
     * Procesa el impacto de un disparo en las naves del oponente.
     */
    private ResultadoImpacto procesarImpacto(String nombreOponente, String coordKey) {
        List<NaveInfo> navesOponente = navesJugadores.get(nombreOponente);
        ResultadoDisparo resultadoDisparo = ResultadoDisparo.AGUA;
        TipoNaveDTO tipoNaveImpactada = null;
        TipoNaveDTO tipoNaveHundida = null;
        EstadoPartida estadoPartida = EstadoPartida.EN_CURSO;

        if (navesOponente != null) {
            for (NaveInfo nave : navesOponente) {
                if (nave.getCoordenadas().contains(coordKey)) {
                    nave.recibirImpacto(coordKey);

                    if (nave.estaHundida()) {
                        resultadoDisparo = ResultadoDisparo.HUNDIMIENTO;
                        tipoNaveHundida = nave.getTipo();
                        // tipoNaveImpactada solo si es el primer impacto (nave pasa de intacta a averiada)
                        // En hundimiento, si impactos.size() == coordenadas.size() y es 1 celda, fue primer impacto
                        if (nave.esPrimerImpacto()) {
                            tipoNaveImpactada = nave.getTipo();
                        }
                        System.out.println("[GESTOR] HUNDIDO! " + nave.getTipo());

                        // Verificar si todas las naves estan hundidas
                        if (todasLasNavesHundidas(navesOponente)) {
                            estadoPartida = EstadoPartida.FINALIZADA;
                            System.out.println("[GESTOR] *** PARTIDA FINALIZADA ***");
                        }
                    } else {
                        resultadoDisparo = ResultadoDisparo.IMPACTO;
                        // Solo marcar como "averiada" si es el PRIMER impacto en esta nave
                        if (nave.esPrimerImpacto()) {
                            tipoNaveImpactada = nave.getTipo();
                            System.out.println("[GESTOR] PRIMER IMPACTO! Nave averiada: " + nave.getTipo());
                        } else {
                            System.out.println("[GESTOR] IMPACTO! en " + nave.getTipo() + " (ya averiada)");
                        }
                    }
                    break;
                }
            }
        }

        if (resultadoDisparo == ResultadoDisparo.AGUA) {
            System.out.println("[GESTOR] AGUA en " + coordKey);
        }

        return new ResultadoImpacto(resultadoDisparo, estadoPartida, tipoNaveImpactada, tipoNaveHundida);
    }

    /**
     * Verifica si todas las naves de un jugador estan hundidas.
     */
    private boolean todasLasNavesHundidas(List<NaveInfo> naves) {
        return naves.stream().allMatch(NaveInfo::estaHundida);
    }

    /**
     * Verifica si es el turno del jugador especificado.
     */
    private boolean verificarTurno(String nombreJugador) {
        if (nombreJugadorEnTurno != null && !nombreJugadorEnTurno.equals(nombreJugador)) {
            System.out.println("[GESTOR] ERROR: No es el turno de " + nombreJugador +
                              " (turno actual: " + nombreJugadorEnTurno + ")");
            return false;
        }
        return true;
    }

    /**
     * Encuentra el nombre del oponente de un jugador.
     */
    private String encontrarOponente(String nombreJugador) {
        for (JugadorDTO j : jugadoresEnPartida) {
            if (!j.getNombre().equals(nombreJugador)) {
                return j.getNombre();
            }
        }
        return null;
    }

    /**
     * Verifica si el jugador ya disparo en una coordenada.
     */
    private boolean yaDisparoEnCoordenada(String nombreJugador, String coordKey) {
        Set<String> misDisparos = disparosRealizados.computeIfAbsent(nombreJugador, k -> new HashSet<>());
        return misDisparos.contains(coordKey);
    }

    /**
     * Registra un disparo realizado.
     */
    private void registrarDisparo(String nombreJugador, String coordKey) {
        Set<String> misDisparos = disparosRealizados.computeIfAbsent(nombreJugador, k -> new HashSet<>());
        misDisparos.add(coordKey);
    }

    /**
     * Envia el resultado de un disparo a todos los clientes.
     */
    private void enviarResultadoDisparo(JugadorDTO jugador, CoordenadasDTO coord,
                                        ResultadoDisparo resultado, EstadoPartida estado,
                                        TipoNaveDTO tipoImpactada, TipoNaveDTO tipoHundida) {
        DisparoDTO disparoResultado = new DisparoDTO(jugador, coord, resultado, estado);
        disparoResultado.setTipoNaveImpactada(tipoImpactada != null ? tipoImpactada.name() : null);
        disparoResultado.setTipoNaveHundida(tipoHundida != null ? tipoHundida.name() : null);

        Mensaje msgResultado = new Mensaje(
                TipoAccion.PUBLICAR,
                "RESULTADO_DISPARO",
                gson.toJsonTree(disparoResultado),
                "SERVER"
        );
        publicador.publicar("RESULTADO_DISPARO", msgResultado);
    }

    /**
     * Publica el evento de fin de partida y limpia el estado del servidor.
     */
    private void publicarFinPartida(JugadorDTO ganador) {
        Mensaje msgFin = new Mensaje(
                TipoAccion.PUBLICAR,
                "FIN_PARTIDA",
                gson.toJsonTree(ganador),
                "SERVER"
        );
        publicador.publicar("FIN_PARTIDA", msgFin);

        // Limpiar el estado de la partida para permitir nuevas partidas
        limpiarEstadoPartida();
    }

    /**
     * Limpia todo el estado de la partida actual en el servidor.
     * Permite que los jugadores puedan crear/unirse a nuevas partidas.
     */
    private void limpiarEstadoPartida() {
        System.out.println("[GESTOR] Limpiando estado de partida finalizada...");

        // Limpiar partidas activas
        partidasActivas.clear();

        // Limpiar jugadores
        jugadoresEnPartida.clear();

        // Limpiar tableros confirmados
        tablerosConfirmados.clear();

        // Limpiar naves de jugadores
        navesJugadores.clear();

        // Limpiar registro de disparos
        disparosRealizados.clear();

        // Limpiar mapeo cliente-jugador
        clienteAJugador.clear();

        // Reiniciar turno y host
        nombreJugadorEnTurno = null;
        nombreHostPartida = null;

        System.out.println("[GESTOR] Estado limpiado. Listo para nueva partida.");
    }

    /**
     * Maneja cuando el tiempo de un jugador se agota.
     */
    private void procesarTiempoAgotado(Mensaje mensaje) {
        String nombreJugador = gson.fromJson(mensaje.getData(), String.class);

        System.out.println("[GESTOR] Tiempo agotado para: " + nombreJugador);
        System.out.println("[GESTOR] Jugadores en partida: " + jugadoresEnPartida.size());

        // Verificar que sea el turno del jugador que reporta tiempo agotado
        if (nombreJugadorEnTurno != null && !nombreJugadorEnTurno.equals(nombreJugador)) {
            System.out.println("[GESTOR] Ignorando TIEMPO_AGOTADO de " + nombreJugador +
                              " porque el turno es de " + nombreJugadorEnTurno);
            return;
        }

        // Cambiar turno al otro jugador
        String nuevoJugadorEnTurno = encontrarOponente(nombreJugador);
        if (nuevoJugadorEnTurno != null) {
            nombreJugadorEnTurno = nuevoJugadorEnTurno;
            TurnoDTO turno = new TurnoDTO(nombreJugadorEnTurno, nombreJugadorEnTurno, 30, 0);
            Mensaje msgTurno = new Mensaje(
                    TipoAccion.PUBLICAR,
                    "CAMBIO_TURNO",
                    gson.toJsonTree(turno),
                    "SERVER"
            );
            publicador.publicar("CAMBIO_TURNO", msgTurno);
            System.out.println("[GESTOR] Turno cambiado a: " + nombreJugadorEnTurno);
        } else {
            System.out.println("[GESTOR] ERROR: No se encontro otro jugador para cambiar turno");
        }
    }

    /**
     * Maneja la confirmacion del tablero de un jugador.
     * Cuando ambos confirman, envia TABLEROS_LISTOS y TURNO_INICIAL.
     */
    private void procesarConfirmarTablero(Mensaje mensaje) {
        JugadorDTO jugadorDTO = gson.fromJson(mensaje.getData(), JugadorDTO.class);

        System.out.println("[GESTOR] Tablero confirmado por: " + jugadorDTO.getNombre());

        boolean ambosListos = false;
        TurnoDTO turnoInicial = null;

        synchronized (lockTableros) {
            // Registrar el jugador si no existe
            boolean existeJugador = jugadoresEnPartida.stream()
                    .anyMatch(j -> j.getNombre().equals(jugadorDTO.getNombre()));
            if (!existeJugador) {
                jugadoresEnPartida.add(jugadorDTO);
                System.out.println("[GESTOR] Jugador registrado: " + jugadorDTO.getNombre());
            }

            // Agregar a tableros confirmados
            tablerosConfirmados.add(jugadorDTO.getNombre());

            System.out.println("[GESTOR] Tableros confirmados: " + tablerosConfirmados.size() +
                              "/2 -> " + tablerosConfirmados);

            // Verificar si ambos jugadores han confirmado
            if (tablerosConfirmados.size() >= 2) {
                System.out.println("[GESTOR] *** AMBOS TABLEROS LISTOS - INICIANDO PARTIDA ***");
                ambosListos = true;

                // Seleccionar jugador inicial aleatoriamente
                turnoInicial = seleccionarTurnoInicial();

                // Limpiar tableros confirmados para futuras partidas
                tablerosConfirmados.clear();
            }
        }

        // Publicar eventos FUERA del synchronized para evitar deadlocks
        if (ambosListos && turnoInicial != null) {
            publicarTablerosListos();
            publicarTurnoInicial(turnoInicial);
        }
    }

    /**
     * Selecciona aleatoriamente el jugador que inicia la partida.
     */
    private TurnoDTO seleccionarTurnoInicial() {
        List<String> nombres = new ArrayList<>(tablerosConfirmados);
        Random random = new Random();
        String nombreInicial = nombres.get(random.nextInt(nombres.size()));

        nombreJugadorEnTurno = nombreInicial;
        System.out.println("[GESTOR] Turno inicial asignado a: " + nombreInicial);

        return new TurnoDTO(nombreInicial, nombreInicial, 30, 1);
    }

    /**
     * Publica el evento de tableros listos.
     */
    private void publicarTablerosListos() {
        Mensaje msgTablerosListos = new Mensaje(
                TipoAccion.PUBLICAR,
                "TABLEROS_LISTOS",
                gson.toJsonTree(Boolean.TRUE),
                "SERVER"
        );
        publicador.publicar("TABLEROS_LISTOS", msgTablerosListos);
    }

    /**
     * Publica el turno inicial.
     */
    private void publicarTurnoInicial(TurnoDTO turnoInicial) {
        Mensaje msgTurnoInicial = new Mensaje(
                TipoAccion.PUBLICAR,
                "TURNO_INICIAL",
                gson.toJsonTree(turnoInicial),
                "SERVER"
        );
        publicador.publicar("TURNO_INICIAL", msgTurnoInicial);
    }

    /**
     * Reinicia el estado del gestor para una nueva partida.
     */
    public void resetPartida() {
        synchronized (lockTableros) {
            tablerosConfirmados.clear();
            jugadoresEnPartida.clear();
            navesJugadores.clear();
            disparosRealizados.clear();
            partidasActivas.clear();
            clienteAJugador.clear();
            nombreJugadorEnTurno = null;
            System.out.println("[GESTOR] Estado de partida reiniciado");
        }
    }

    /**
     * Obtiene el nombre del jugador en turno actual.
     */
    public String getNombreJugadorEnTurno() {
        return nombreJugadorEnTurno;
    }

    /**
     * Obtiene la cantidad de jugadores en la partida.
     */
    public int getCantidadJugadores() {
        return jugadoresEnPartida.size();
    }

    /**
     * Maneja la desconexion inesperada de un cliente.
     * Limpia la partida fantasma y notifica al oponente si existe.
     *
     * @param idCliente ID del cliente que se desconecto
     */
    public void manejarDesconexionCliente(String idCliente) {
        String nombreJugador = clienteAJugador.remove(idCliente);

        if (nombreJugador == null) {
            System.out.println("[GESTOR] Cliente " + idCliente + " no tenia jugador asociado");
            return;
        }

        System.out.println("[GESTOR] Desconexion detectada del jugador: " + nombreJugador);

        // Buscar al jugador en la lista
        JugadorDTO jugadorDesconectado = null;
        JugadorDTO oponente = null;

        synchronized (lockTableros) {
            for (JugadorDTO j : jugadoresEnPartida) {
                if (j.getNombre().equals(nombreJugador)) {
                    jugadorDesconectado = j;
                } else {
                    oponente = j;
                }
            }

            // Limpiar estructuras del jugador desconectado
            tablerosConfirmados.remove(nombreJugador);
            jugadoresEnPartida.removeIf(j -> j.getNombre().equals(nombreJugador));
            navesJugadores.remove(nombreJugador);
            disparosRealizados.remove(nombreJugador);
        }

        // Si habia un oponente, notificarle del abandono
        if (oponente != null && jugadorDesconectado != null) {
            System.out.println("[GESTOR] Notificando abandono a: " + oponente.getNombre());

            // Publicar evento de abandono
            Mensaje msgAbandono = new Mensaje(
                    TipoAccion.PUBLICAR,
                    "ABANDONO_PARTIDA",
                    gson.toJsonTree(jugadorDesconectado),
                    "SERVER"
            );
            publicador.publicar("ABANDONO_PARTIDA", msgAbandono);
        }

        // Si no quedan jugadores, limpiar la partida completamente
        synchronized (lockTableros) {
            if (jugadoresEnPartida.isEmpty()) {
                System.out.println("[GESTOR] No quedan jugadores, limpiando partida fantasma");
                partidasActivas.clear();
                nombreJugadorEnTurno = null;
            }
        }
    }
}
