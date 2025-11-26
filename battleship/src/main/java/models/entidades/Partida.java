package models.entidades;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import models.builder.Director;
import models.builder.TableroBuilder;
import models.enums.EstadoJugador;
import models.enums.EstadoNave;
import models.enums.EstadoPartida;
import models.enums.ResultadoAddNave;
import models.enums.ResultadoDisparo;
import models.observador.GestorSuscriptores;
import models.observador.ISuscriptor;
import models.validators.ValidadorNave;
import servidor.cronometro.ICronometro;
import servidor.modelo.IModeloServidor;

public class Partida implements IModeloServidor {

    private Jugador turno;
    private List<Jugador> jugadores;
    private int cantBarcos;
    private int cantSubmarinos;
    private int cantCruceros;
    private int cantPortaAviones;
    private int totalNaves;
    private EstadoPartida estado;
    private Disparo disparo;
    private GestorSuscriptores gestorSuscriptores;
    private ICronometro cronometro;
    private ValidadorNave validadorNave;

    public Partida(Jugador turno, List<Jugador> jugadores, int cantBarcos, int cantSubmarinos, int cantCruceros, int cantPortaAviones, int totalNaves, EstadoPartida estado, List<ISuscriptor> suscriptores, ICronometro cronometro) {
        this.turno = turno;
        this.jugadores = new CopyOnWriteArrayList<>(jugadores);
        this.cantBarcos = cantBarcos;
        this.cantSubmarinos = cantSubmarinos;
        this.cantCruceros = cantCruceros;
        this.cantPortaAviones = cantPortaAviones;
        this.totalNaves = totalNaves;
        this.estado = estado;
        this.gestorSuscriptores = new GestorSuscriptores(suscriptores);
        this.cronometro = cronometro;
        this.validadorNave = new ValidadorNave();

        if (cronometro != null) {
            cronometro.initCronometro();
        }
    }

    public Partida(Jugador turno, List<Jugador> jugadores, int cantBarcos, int cantSubmarinos, int cantCruceros, int cantPortaAviones, int totalNaves, EstadoPartida estado, List<ISuscriptor> suscriptores) {
        this.turno = turno;
        this.jugadores = new CopyOnWriteArrayList<>(jugadores);
        this.cantBarcos = cantBarcos;
        this.cantSubmarinos = cantSubmarinos;
        this.cantCruceros = cantCruceros;
        this.cantPortaAviones = cantPortaAviones;
        this.totalNaves = totalNaves;
        this.estado = estado;
        this.gestorSuscriptores = new GestorSuscriptores(suscriptores);
        this.cronometro = null;
        this.validadorNave = new ValidadorNave();
    }

    public boolean cambiarTurno() {
        turno = jugadores.stream()
                .filter(e -> !e.equals(turno))
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
        if (!jugador.equals(turno)) {
            disparo = new Disparo(jugador, coordenadas, ResultadoDisparo.TURNO_INCORRECTO, estado);
            return disparo;
        }

        if (cronometro != null) {
            cronometro.setProcesandoDisparo(true);

            if (!cronometro.isInTime(tiempo)) {
                cambiarTurno();
                disparo = new Disparo(jugador, coordenadas, ResultadoDisparo.DISPARO_FUERA_TIEMPO, estado);
                return disparo;
            }
        }

        Jugador j2 = jugadores.stream()
                .filter(e -> !e.equals(turno))
                .findFirst()
                .orElse(null);

        if (j2 == null) {
            return null;
        }

        Tablero tablero = j2.getTablero();
        ResultadoDisparo resultadoDisparo = tablero.realizarDisparo(coordenadas);

        Jugador jugadorActual = jugadores.stream()
                .filter(e -> e.equals(jugador))
                .findFirst()
                .orElse(null);

        if (jugadorActual != null && jugadorActual.getPuntaje() != null) {
            jugadorActual.getPuntaje().calcularPuntos(resultadoDisparo);
        }

        if (resultadoDisparo == ResultadoDisparo.HUNDIMIENTO) {
            long navesVivas = j2.getNaves().stream()
                    .filter(n -> n.getEstado() != EstadoNave.HUNDIDO)
                    .count();

            if (navesVivas == 0) {
                estado = EstadoPartida.FINALIZADA;

                if (jugadorActual != null && jugadorActual.getPuntaje() != null) {
                    jugadorActual.getPuntaje().sumarVictoria();
                }

                disparo = new Disparo(jugador, coordenadas, resultadoDisparo, estado);
                return disparo;
            }
        }

        disparo = new Disparo(jugador, coordenadas, resultadoDisparo, estado);

        if (resultadoDisparo == ResultadoDisparo.AGUA
                || resultadoDisparo == ResultadoDisparo.YA_DISPARADO
                || resultadoDisparo == ResultadoDisparo.COORDENADAS_INVALIDAS) {
            cambiarTurno();
        } else {
            if (cronometro != null) {
                cronometro.setProcesandoDisparo(false);
                cronometro.initCronometro();
            }
        }

        return disparo;
    }

    @Override
    public ResultadoAddNave addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas) {
        Jugador j = jugadores.stream()
                .filter(e -> e.equals(jugador))
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

    @Override
    public void unirsePartida(Jugador jugador) {
        if (estado == EstadoPartida.EN_CURSO) {
            return;
        }

        if (jugadores.size() >= 2) {
            return;
        }

        this.addJugador(jugador);
    }

    @Override
    public void empezarPartida() {
        if (jugadores.size() < 2) {
            return;
        }

        estado = EstadoPartida.EN_CURSO;
        Random random = new Random();
        turno = jugadores.get(random.nextInt(jugadores.size()));
    }

    @Override
    public void abandonarPartida(Jugador jugadorQueSeVa) {
        jugadorQueSeVa.setEstado(EstadoJugador.ABANDONO);
        jugadores.removeIf(j -> j.equals(jugadorQueSeVa));

        if (jugadores.size() == 1) {
            Jugador ganador = jugadores.get(0);
            ganador.setEstado(EstadoJugador.JUGANDO);
            estado = EstadoPartida.FINALIZADA;

            if (cronometro != null) {
                try {
                    cronometro.stop();
                } catch (Exception ignored) {
                }
            }
            return;
        }

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

    public Jugador getTurno() {
        return turno;
    }

    public EstadoPartida getEstado() {
        return estado;
    }

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

    public void suscribirAPartida(ISuscriptor suscriptor) {
        gestorSuscriptores.suscribir(suscriptor);
    }

    public void notificarAllSuscriptores(String contexto, Object datos) {
        gestorSuscriptores.notificarTodos(contexto, datos);
    }

    public void abandonarLobby(Jugador jugador) {
        jugadores.removeIf(j -> j.equals(jugador));
        notificarAllSuscriptores("JUGADOR_ABANDONO_LOBBY", jugador);
    }
}
