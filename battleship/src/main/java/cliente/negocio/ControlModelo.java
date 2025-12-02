package cliente.negocio;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import compartido.entidades.Jugador;
import compartido.enums.OrientacionNave;
import compartido.enums.ResultadoAddNave;
import compartido.enums.ResultadoDisparo;
import compartido.observador.ISuscriptor;
import compartido.comunicacion.dto.AddNaveDTO;
import compartido.comunicacion.dto.CoordenadasDTO;
import compartido.comunicacion.dto.DisparoDTO;
import compartido.comunicacion.dto.JugadorDTO;
import compartido.comunicacion.dto.NaveDTO;
import compartido.comunicacion.dto.TableroDTO;
import compartido.comunicacion.dto.TurnoDTO;

/**
 * Modelo del cliente para el juego Batalla Naval.
 * Mantiene el estado local del jugador y maneja la logica del cliente.
 *
 * @author daniel
 */
public class ControlModelo implements IModeloCliente {

    private JugadorDTO jugador;
    private TableroDTO tablero;
    private List<NaveDTO> naves;
    private boolean turno;
    private boolean tableroConfirmado;
    private List<ISuscriptor> suscriptores;
    private List<Jugador> jugadores;

    public ControlModelo() {
        this.jugadores = new java.util.ArrayList<>();
        this.tableroConfirmado = false;
        this.turno = false;
        this.tablero = new TableroDTO(10, 10); // Inicializar tablero por defecto
        this.suscriptores = new java.util.ArrayList<>();
    }

    public ControlModelo(JugadorDTO jugador, TableroDTO tablero, List<NaveDTO> naves, boolean turno, List<ISuscriptor> suscriptores) {
        this.jugador = jugador;
        this.tablero = tablero;
        this.naves = naves;
        this.turno = turno;
        this.suscriptores = suscriptores;
        this.jugadores = new java.util.ArrayList<>();
        this.tableroConfirmado = false;
    }

    @Override
    public DisparoDTO realizarDisparo(CoordenadasDTO coordenadas) {
        if (!turno) {
            System.out.println("No es el turno del Jugador");
            return null;
        }
        if (coordenadas.getX() > tablero.getLimiteX() || coordenadas.getX() < 0) {
            System.out.println("Las Coordenadas en X estan fuera del limite");
            return null;
        }
        if (coordenadas.getY() > tablero.getLimiteY() || coordenadas.getY() < 0) {
            System.out.println("Las coordenas en Y estan fuera del limite");
            return null;
        }

        DisparoDTO disparo = new DisparoDTO(jugador, coordenadas, null, null, Instant.now().toEpochMilli());
        return disparo;
    }

    @Override
    public void manejarResultadoDisparo(DisparoDTO disparo) {
        if (disparo.getResultadoDisparo() == ResultadoDisparo.DISPARO_FUERA_TIEMPO) {
            System.out.println(disparo.getResultadoDisparo());
            return;
        }
        if (disparo.getResultadoDisparo() == ResultadoDisparo.TURNO_INCORRECTO) {
            System.out.println(disparo.getResultadoDisparo());
            return;
        }

        notificarAllSuscriptores("RESULTADO_DISPARO", disparo);
    }

    @Override
    public AddNaveDTO addNave(NaveDTO nave, List<CoordenadasDTO> coordenadas) {
        // Verificar que la nave no sea nula.
        if (nave == null) {
            System.out.println("Error, nave vacia.");
            return null;
        }
        // Verificar que las coordenadas no esten vacias.
        if (coordenadas == null || coordenadas.isEmpty()) {
            System.out.println("Error, coordenadas vascias.");
            return null;
        }

        // Verificar que el numero de coordenadas sea el mismo que el tamaño de la Nave.
        if (coordenadas.size() != nave.getTamanio()) {
            System.out.println("Error, coordenadas extra o insuficientes para la Nave.");
            return null;
        }

        // Verificar que las coordenadas no se salen del limite del tablero.
        for (CoordenadasDTO coordenada : coordenadas) {
            if (coordenada.getY() < 0 || coordenada.getY() > tablero.getLimiteY()
                    || coordenada.getX() < 0 || coordenada.getX() > tablero.getLimiteX()) {
                System.out.println("Error: La nave se sale de los limites del tablero.");
                return null;
            }
        }

        // Verificar que todas las coordenas esten con la misma orientacion.
        if (nave.getOrientacion() == OrientacionNave.VERTICAL) {
            int y = coordenadas.getFirst().getY();
            for (CoordenadasDTO coordenada : coordenadas) {
                if (coordenada.getY() != y) {
                    System.out.println("Error: La nave no esta ordenada Verticalmente");
                    return null;
                }
            }
        } else if (nave.getOrientacion() == OrientacionNave.HORIZONTAL) {
            int x = coordenadas.getFirst().getX();
            for (CoordenadasDTO coordenada : coordenadas) {
                if (coordenada.getX() != x) {
                    System.out.println("Error: La nave no esta ordenada Horizontalmente.");
                    return null;
                }
            }
        }

        // Ordenar lista por "X" y "Y".
        coordenadas.sort(Comparator.comparingInt(CoordenadasDTO::getX)
                .thenComparingInt(CoordenadasDTO::getY));

        // Verificar que cada Coordenada este consecutiva de la otra.
        if (nave.getOrientacion() == OrientacionNave.VERTICAL) {
            for (int i = coordenadas.size() - 1; i >= 0; i--) {
                if (i == 0) {
                    break;
                }
                if (coordenadas.get(i - 1).getX() != coordenadas.get(i).getX() - 1) {
                    System.out.println("Error: Coordenas no consecutivas en 'X'");
                    return null;
                }
            }
        } else if (nave.getOrientacion() == OrientacionNave.HORIZONTAL) {
            for (int i = coordenadas.size() - 1; i >= 0; i--) {
                if (i == 0) {
                    break;
                }
                if (coordenadas.get(i - 1).getY() != coordenadas.get(i).getY() - 1) {
                    System.out.println("Error: Coordenas no consecutivas en 'Y'");
                    return null;
                }
            }
        }

        AddNaveDTO naveDTO = new AddNaveDTO(
                jugador,
                nave,
                coordenadas
        );

        return naveDTO;
    }

    @Override
    public void manejarResultadoAddNave(ResultadoAddNave resultado) {
        System.out.println("RESULTADO ADD NAVE EN ModeloCliente: " + resultado);
    }

    @Override
    public void addJugador(Jugador j) {
        if (j != null) {
            jugadores.add(j);
            System.out.println("Jugador agregado: " + j.getNombre());
        }
    }

    @Override
    public void crearTableros() {
        // Los tableros se crean en el servidor, aquí solo inicializamos el DTO
        if (tablero == null) {
            tablero = new TableroDTO(10, 10);
        }
        System.out.println("Tableros inicializados.");
    }

    @Override
    public void suscribirAPartida(ISuscriptor suscriptor) {
        suscriptores.add(suscriptor);
    }

    @Override
    public void notificarAllSuscriptores(String contexto, Object datos) {
        suscriptores.forEach(s -> s.notificar(contexto, datos));
    }

    @Override
    public void unirsePartida(Jugador jugador) {
        if (jugador == null) {
            System.out.println("Error: Jugador nulo al unirse a partida.");
            return;
        }

        // Guardar el jugador local
        this.jugador = new JugadorDTO(
                jugador.getNombre(),
                jugador.getColor(),
                jugador.getEstado()
        );

        // Agregar a la lista de jugadores
        jugadores.add(jugador);

        System.out.println("Jugador " + jugador.getNombre() + " se unio a la partida.");
    }

    @Override
    public void empezarPartida() {
        System.out.println("Partida iniciada desde el cliente.");
        // La lógica principal está en el servidor
    }

    @Override
    public void abandonarLobby(Jugador jugador) {
        JugadorDTO dto = new JugadorDTO(jugador.getNombre(), jugador.getColor(), jugador.getEstado());
        notificarAllSuscriptores("ABANDONAR_PARTIDA", dto);
    }

    @Override
    public List<Jugador> getJugadores() {
        return jugadores;
    }

    @Override
    public JugadorDTO getJugador() {
        return jugador;
    }

    @Override
    public JugadorDTO abandonarPartida(Jugador jugador) {

        // VALIDACIONES (igualitas al estilo de addNave)
        if (jugador == null) {
            System.out.println("Error: Jugador nulo.");
            return null;
        }
        if (jugador.getNombre() == null || jugador.getNombre().isBlank()) {
            System.out.println("Error: Jugador sin nombre.");
            return null;
        }

        return new JugadorDTO(
                jugador.getNombre(),
                jugador.getColor(),
                jugador.getEstado()
        );
    }

    // =========================================================================
    // NUEVOS METODOS PARA EL BUS DE EVENTOS
    // =========================================================================

    @Override
    public void confirmarTablero() {
        this.tableroConfirmado = true;
        System.out.println("[MODELO] Tablero confirmado localmente");
        notificarAllSuscriptores("TABLERO_CONFIRMADO_LOCAL", jugador);
    }

    @Override
    public boolean tableroConfirmado() {
        return this.tableroConfirmado;
    }

    @Override
    public boolean esMiTurno() {
        return this.turno;
    }

    @Override
    public void setTurno(boolean esMiTurno) {
        this.turno = esMiTurno;
        System.out.println("[MODELO] Turno actualizado: " + (esMiTurno ? "ES MI TURNO" : "TURNO DEL OPONENTE"));
    }

    @Override
    public void manejarCambioTurno(TurnoDTO turnoDTO) {
        if (turnoDTO == null) {
            System.out.println("[MODELO] Error: TurnoDTO nulo");
            return;
        }

        // Determinar si es mi turno comparando con mi jugador
        boolean esMiTurno = jugador != null &&
                           jugador.getNombre() != null &&
                           jugador.getNombre().equals(turnoDTO.getNombreJugadorEnTurno());

        setTurno(esMiTurno);
        notificarAllSuscriptores("CAMBIO_TURNO", turnoDTO);
    }

    @Override
    public void manejarTiempoAgotado(String idJugador) {
        System.out.println("[MODELO] Tiempo agotado para: " + idJugador);

        // Si el tiempo agotado es mio, pierdo el turno
        if (jugador != null && idJugador != null) {
            setTurno(false);
        }

        notificarAllSuscriptores("TIEMPO_AGOTADO", idJugador);
    }

    @Override
    public void manejarConfirmacionTablero(JugadorDTO jugadorQueConfirmo) {
        System.out.println("[MODELO] Tablero confirmado por: " +
                          (jugadorQueConfirmo != null ? jugadorQueConfirmo.getNombre() : "Desconocido"));
        notificarAllSuscriptores("CONFIRMAR_TABLERO", jugadorQueConfirmo);
    }

    @Override
    public void manejarTablerosListos() {
        System.out.println("[MODELO] Ambos tableros listos - Batalla puede comenzar");
        notificarAllSuscriptores("TABLEROS_LISTOS", Boolean.TRUE);
    }

    @Override
    public void manejarFinPartida(JugadorDTO ganador) {
        System.out.println("[MODELO] Fin de partida - Ganador: " +
                          (ganador != null ? ganador.getNombre() : "Ninguno (empate o abandono)"));

        // Determinar si gane o perdi
        boolean gane = ganador != null &&
                      jugador != null &&
                      jugador.getNombre() != null &&
                      jugador.getNombre().equals(ganador.getNombre());

        if (gane) {
            System.out.println("[MODELO] VICTORIA!");
        } else {
            System.out.println("[MODELO] DERROTA");
        }

        notificarAllSuscriptores("FIN_PARTIDA", ganador);
    }

}
