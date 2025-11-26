package models.entidades;

import java.util.Comparator;
import models.enums.EstadoNave;
import models.enums.EstadoPartida;
import models.enums.ResultadoDisparo;
import models.observador.ISuscriptor;
import java.util.List;
import models.builder.Director;
import models.builder.TableroBuilder;
import models.enums.EstadoJugador;
import models.enums.OrientacionNave;
import models.enums.ResultadoAddNave;
import servidor.cronometro.ICronometro;
import servidor.modelo.IModeloServidor;
import models.control.IModeloCliente;
import dtos.AddNaveDTO;
import dtos.DisparoDTO;
import dtos.JugadorDTO;
import dtos.NaveDTO;
import models.validators.ValidadorNave;

/**
 * Clase Partida que representa una partida de Battleship.
 * Implementa tanto IModeloServidor como IModeloCliente para permitir
 * su uso en ambos contextos (arquitectura cliente-servidor).
 *
 * @author daniel
 */
public class Partida implements IModeloServidor, IModeloCliente {

    private Jugador turno;
    private List<Jugador> jugadores;
    private int cantBarcos;
    private int cantSubmarinos;
    private int cantCruceros;
    private int cantPortaAviones;
    private int totalNaves;
    private EstadoPartida estado;
    private Disparo disparo;
    private List<ISuscriptor> suscriptores;
    private ICronometro cronometro;
    private ValidadorNave validadorNave;

    public Partida(Jugador turno, List<Jugador> jugadores, int cantBarcos, int cantSubmarinos, int cantCruceros, int cantPortaAviones, int totalNaves, EstadoPartida estado, List<ISuscriptor> suscriptores, ICronometro cronometro) {
        this.turno = turno;
        this.jugadores = jugadores;
        this.cantBarcos = cantBarcos;
        this.cantSubmarinos = cantSubmarinos;
        this.cantCruceros = cantCruceros;
        this.cantPortaAviones = cantPortaAviones;
        this.totalNaves = totalNaves;
        this.estado = estado;
        this.suscriptores = suscriptores;
        this.cronometro = cronometro;
        this.validadorNave = new ValidadorNave();

        if (cronometro != null) {
            cronometro.initCronometro();
        }
    }

    // Constructor sobrecargado sin cronómetro (para uso del Builder)
    public Partida(Jugador turno, List<Jugador> jugadores, int cantBarcos, int cantSubmarinos, int cantCruceros, int cantPortaAviones, int totalNaves, EstadoPartida estado, List<ISuscriptor> suscriptores) {
        this.turno = turno;
        this.jugadores = jugadores;
        this.cantBarcos = cantBarcos;
        this.cantSubmarinos = cantSubmarinos;
        this.cantCruceros = cantCruceros;
        this.cantPortaAviones = cantPortaAviones;
        this.totalNaves = totalNaves;
        this.estado = estado;
        this.suscriptores = suscriptores;
        this.cronometro = null;
        this.validadorNave = new ValidadorNave();
    }

    public boolean cambiarTurno() {
        turno = jugadores.stream().filter(e -> e != turno)
                .findFirst()
                .orElse(null);
        if (turno == null) {
            return false;
        }
        if (cronometro != null) {
            cronometro.setProcesandoDisparo(false);
            cronometro.initCronometro();
        }
        return true;
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    @Override
    public void addJugador(Jugador j) {
        jugadores.add(j);
    }

    @Override
    public Disparo realizarDisparo(Coordenadas coordenadas, Jugador jugador, long tiempo) {
        // Verificar Turno
        if (!jugador.getNombre().equals(turno.getNombre())) {
            disparo = new Disparo(jugador, coordenadas, ResultadoDisparo.TURNO_INCORRECTO, estado);
            return disparo;
        }

        if (cronometro != null) {
            cronometro.setProcesandoDisparo(true);

            // Verificar el Cronometro
            if (!cronometro.isInTime(tiempo)) {
                cambiarTurno();
                disparo = new Disparo(jugador, coordenadas, ResultadoDisparo.DISPARO_FUERA_TIEMPO, estado);
                return disparo;
            }
        }

        // Obtener al oponente
        Jugador j2 = jugadores.stream().filter(e -> e != turno)
                .findFirst()
                .orElse(null);

        if (j2 == null) {
            return null;
        }

        // Disparo del jugador actual
        Tablero tablero = j2.getTablero();
        ResultadoDisparo resultadoDisparo = tablero.realizarDisparo(coordenadas);

        // Gestionar Puntaje: Calcular puntos del disparo
        Jugador jugadorActual = jugadores.stream()
                .filter(e -> e.getNombre().equals(jugador.getNombre()))
                .findFirst()
                .orElse(null);

        if (jugadorActual != null && jugadorActual.getPuntaje() != null) {
            jugadorActual.getPuntaje().calcularPuntos(resultadoDisparo);
        }

        // Si el disparo resultó en hundimiento, verificar si el oponente perdió todas sus naves
        if (resultadoDisparo == ResultadoDisparo.HUNDIMIENTO) {
            // Contar cuántas naves del oponente aún NO están hundidas
            long navesVivas = j2.getNaves().stream()
                    .filter(n -> n.getEstado() != EstadoNave.HUNDIDO)
                    .count();

            // Si no quedan naves vivas, el jugador actual GANA
            if (navesVivas == 0) {
                estado = EstadoPartida.FINALIZADA;

                // Gestionar Puntaje: Sumar bonus de victoria
                if (jugadorActual != null && jugadorActual.getPuntaje() != null) {
                    jugadorActual.getPuntaje().sumarVictoria();
                }

                disparo = new Disparo(jugador, coordenadas, resultadoDisparo, estado);
                return disparo;
            }
        }

        disparo = new Disparo(jugador, coordenadas, resultadoDisparo, estado);
        cambiarTurno();
        return disparo;
    }

    @Override
    public ResultadoAddNave addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas) {
        Jugador j = jugadores.stream()
                .filter(e -> e.getNombre().equals(jugador.getNombre()))
                .findFirst()
                .orElse(null);

        if (j == null) {
            return ResultadoAddNave.JUGADOR_NO_ENCONTRADO;
        }

        Tablero t = j.getTablero();
        ResultadoAddNave resultado = validadorNave.validarCompleto(jugador, jugadores, nave, coordenadas, t);

        if (resultado != null) {
            return resultado;
        }

        t.addNave(nave, coordenadas);
        j.getNaves().add(nave);

        return ResultadoAddNave.NAVE_AÑADIDA;
    }

    // Caso de Uso: Unirse Partida
    @Override
    public void unirsePartida(Jugador jugador) {
        // Validar estado
        if (estado == EstadoPartida.EN_CURSO) {
            return;
        }

        // Comprobar que no este llena
        if (jugadores.size() >= 2) {
            return;
        }

        // Agregar nuevo jugador
        this.addJugador(jugador);
    }

    @Override
    public void empezarPartida() {
        // Comprobar que este llena
        if (jugadores.size() < 2) {
            return;
        }

        // Cambiar estado
        estado = EstadoPartida.EN_CURSO;

        // Asignar turno inicial (jugador 0)
        turno = jugadores.get(0);
    }

    @Override
    public void abandonarPartida(Jugador jugadorQueSeVa) {
        // 1. Marcar su estado como ABANDONO
        jugadorQueSeVa.setEstado(EstadoJugador.ABANDONO);

        // 2. Eliminar al jugador de la lista
        jugadores.removeIf(j -> j.getNombre().equals(jugadorQueSeVa.getNombre()));

        // 3. Si queda un jugador -> ese gana automáticamente
        if (jugadores.size() == 1) {
            Jugador ganador = jugadores.get(0);
            ganador.setEstado(EstadoJugador.JUGANDO);
            estado = EstadoPartida.FINALIZADA;

            // Detener el cronómetro si existe
            if (cronometro != null) {
                try {
                    cronometro.stop();
                } catch (Exception ignored) {
                }
            }
            return;
        }

        // 4. Si no queda nadie -> resetear la partida
        if (jugadores.isEmpty()) {
            estado = EstadoPartida.POR_EMPEZAR;

            if (cronometro != null) {
                try {
                    cronometro.stop();
                } catch (Exception ignored) {
                }
            }
        }
    }

    // ==================== IMPLEMENTACIÓN DE IModeloCliente ====================

    /**
     * Método del cliente para realizar disparo.
     * Retorna un DTO en lugar de un objeto Disparo.
     */
    @Override
    public DisparoDTO realizarDisparo(Coordenadas coordenadas) {
        // Este método es usado por el cliente para crear un DTO de disparo
        // No ejecuta la lógica completa, solo crea el DTO para enviar al servidor
        if (turno == null || jugadores.isEmpty()) {
            return null;
        }

        JugadorDTO jugadorDTO = new JugadorDTO(
            turno.getNombre(),
            turno.getColor(),
            turno.getEstado()
        );

        return new DisparoDTO(jugadorDTO, coordenadas, null, estado, System.currentTimeMillis());
    }

    /**
     * Método del cliente para agregar nave.
     */
    @Override
    public AddNaveDTO addNave(NaveDTO nave, List<Coordenadas> coordenadas) {
        // Implementación del cliente - crea DTO para enviar al servidor
        return new AddNaveDTO(getJugador(), nave, coordenadas, null);
    }

    /**
     * Crea los tableros para los jugadores.
     */
    @Override
    public void crearTableros() {
        Director director = new Director();
        for (Jugador jugador : jugadores) {
            if (jugador.getTablero() == null) {
                TableroBuilder builder = new TableroBuilder();
                director.makeTablero(builder);
                Tablero tablero = builder.getResult();
                jugador.setTablero(tablero);
            }
        }
    }

    /**
     * Suscribe un observador a la partida.
     */
    @Override
    public void suscribirAPartida(ISuscriptor suscriptor) {
        if (!suscriptores.contains(suscriptor)) {
            suscriptores.add(suscriptor);
        }
    }

    /**
     * Notifica a todos los suscriptores de un evento.
     */
    @Override
    public void notificarAllSuscriptores(String contexto, Object datos) {
        for (ISuscriptor suscriptor : suscriptores) {
            suscriptor.notificar(contexto, datos);
        }
    }

    /**
     * Obtiene el jugador actual como DTO.
     */
    @Override
    public JugadorDTO getJugador() {
        if (turno != null) {
            return new JugadorDTO(turno.getNombre(), turno.getColor(), turno.getEstado());
        }
        return null;
    }

    /**
     * Maneja el resultado de un disparo recibido del servidor.
     */
    @Override
    public void manejarResultadoDisparo(DisparoDTO disparo) {
        // Notificar a los suscriptores (vistas) del resultado
        notificarAllSuscriptores("RESULTADO_DISPARO", disparo);
    }

    /**
     * Abandona el lobby (antes de empezar la partida).
     */
    @Override
    public void abandonarLobby(Jugador jugador) {
        jugadores.removeIf(j -> j.getNombre().equals(jugador.getNombre()));
        notificarAllSuscriptores("JUGADOR_ABANDONO_LOBBY", jugador);
    }

}
