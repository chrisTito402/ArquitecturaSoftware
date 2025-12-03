package models.entidades;

import java.util.Comparator;
import models.enums.EstadoNave;
import models.enums.EstadoPartida;
import models.enums.ResultadoDisparo;
import models.observador.ISuscriptor;
import java.util.List;
import models.builder.Director;
import models.builder.TableroBuilder;
import models.enums.EstadoCasilla;
import models.enums.EstadoJugador;
import models.enums.OrientacionNave;
import models.enums.ResultadoAddNave;
import models.enums.ResultadoConfirmarNaves;
import servidor.cronometro.ICronometro;
import servidor.modelo.IModeloServidor;

/**
 *
 * @author daniel
 */
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
    private List<ISuscriptor> suscriptores;
    private ICronometro cronometro;

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

        this.turno = jugadores.get(0);
        // PROVISIONAL para simular cuando empieza la patida
        cronometro.initCronometro();
    }

    public boolean cambiarTurno() {
        turno = jugadores.stream().filter(e -> e != turno)
                .findFirst()
                .orElse(null);
        if (turno == null) {
            System.out.println("Error al cambiar el turno");
            return false;
        }
        cronometro.setProcesandoDisparo(false);
        cronometro.initCronometro();
        System.out.println("SE CAMBIO EL TURNO");
        return true;
    }

    @Override
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
            System.out.println("Error, no es el turno del jugador seleccionado.");
            disparo = new Disparo(jugador, coordenadas, ResultadoDisparo.TURNO_INCORRECTO, estado);
            return disparo;
        }

        cronometro.setProcesandoDisparo(true);

        // Verificar el Cronometro
        if (!cronometro.isInTime(tiempo)) {
            cambiarTurno();
            System.out.println("Error, el disparo no fue hecho a tiempo.");
            disparo = new Disparo(jugador, coordenadas, ResultadoDisparo.DISPARO_FUERA_TIEMPO, estado);
            return disparo;
        }

        // Obtener al oponente
        Jugador j2 = jugadores.stream().filter(e -> e != turno)
                .findFirst()
                .orElse(null);

        if (j2 == null) {
            System.out.println("Error, no se encontró al oponente.");
            return null;
        }

        // Disparo del jugador actual
        Tablero tablero = j2.getTablero();
        ResultadoDisparo resultadoDisparo = tablero.realizarDisparo(coordenadas);

        //Puntaje
        Jugador jugadorActual = jugadores.stream()
                .filter(e -> e.getNombre().equals(jugador.getNombre()))
                .findFirst()
                .orElse(null);

        if (jugadorActual != null && jugadorActual.getPuntaje() != null) {
            int puntosObtenidos = jugadorActual.getPuntaje().calcularPuntos(resultadoDisparo);
            System.out.println("Jugador " + jugadorActual.getNombre()
                    + " obtuvo " + puntosObtenidos + " puntos. "
                    + "Total: " + jugadorActual.getPuntaje().getPuntosTotales());
        }

        if (resultadoDisparo == ResultadoDisparo.HUNDIMIENTO) {
            Nave nave = j2.getNaves().stream().filter(n -> n.getEstado() == EstadoNave.SIN_DAÑOS
                    || n.getEstado() == EstadoNave.AVERIADO)
                    .findFirst()
                    .orElse(null);

            if (nave == null) {
                System.out.println("Jugador: " + turno.getNombre() + " GANO!");
                estado = EstadoPartida.FINALIZADA;

                if (jugadorActual != null && jugadorActual.getPuntaje() != null) {
                    jugadorActual.getPuntaje().sumarVictoria();
                    System.out.println("Puntaje final de " + jugadorActual.getNombre()
                            + ": " + jugadorActual.getPuntaje().getPuntosTotales());
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
        if (jugador == null || jugador.getNombre() == null || jugador.getNombre().isBlank()) {
            System.out.println("Error: Informacion insuficiente del Jugador.");
            return ResultadoAddNave.JUGADOR_NULL;
        }

        if (nave == null) {
            System.out.println("Error: Informacion insuficiente de la Nave");
            return ResultadoAddNave.NAVE_NULL;
        }

        if (coordenadas == null || coordenadas.isEmpty()) {
            System.out.println("Error: Informacion insuficiente de las Coordenadas");
            return ResultadoAddNave.COORDENADAS_NULL;
        }

        // Verificar que el jugador existe.
        Jugador j = jugadores.stream()
                .filter(e -> e.getNombre().equals(jugador.getNombre()))
                .findFirst()
                .orElse(null);

        if (j == null) {
            System.out.println("Error: No se encontro al Jugador.");
            return ResultadoAddNave.JUGADOR_NO_ENCONTRADO;
        }

        // Verificar que el numero de coordenadas sea el mismo que el tamaño de la Nave.
        if (coordenadas.size() != nave.getTamanio()) {
            System.out.println("Error: Coordenadas extra para la nave.");
            return ResultadoAddNave.COORDENADAS_EXTRA;
        }

        // Verificar que las coordenadas no se salen del limite del tablero.
        Tablero t = j.getTablero();
        for (Coordenadas coordenada : coordenadas) {
            if (coordenada.getY() < 0 || coordenada.getY() > t.getLimiteY() - 1
                    || coordenada.getX() < 0 || coordenada.getX() > t.getLimiteX() - 1) {
                System.out.println("Error: La nave se sale de los limites del tablero.");
                return ResultadoAddNave.COORDENADAS_FUERA_LIMITE;
            }
        }

        // Verificar que todas las coordenas esten con la misma orientacion.
        if (nave.getOrientacion() == OrientacionNave.VERTICAL) {
            int y = coordenadas.getFirst().getY();
            for (Coordenadas coordenada : coordenadas) {
                if (coordenada.getY() != y) {
                    System.out.println("Error: La nave no esta ordenada Verticalmente");
                    return ResultadoAddNave.NO_ORDENADA_VERTICLMENTE;
                }
            }
        } else if (nave.getOrientacion() == OrientacionNave.HORIZONTAL) {
            int x = coordenadas.getFirst().getX();
            for (Coordenadas coordenada : coordenadas) {
                if (coordenada.getX() != x) {
                    System.out.println("Error: La nave no esta ordenada Horizontalmente.");
                    return ResultadoAddNave.NO_ORDENADA_HORIZONTALMENTE;
                }
            }
        }

        // Ordenar lista por "X" y "Y".
        coordenadas.sort(Comparator.comparingInt(Coordenadas::getX)
                .thenComparingInt(Coordenadas::getY));

        // Verificar que cada Coordenada este consecutiva de la otra.
        if (nave.getOrientacion() == OrientacionNave.VERTICAL) {
            for (int i = coordenadas.size() - 1; i >= 0; i--) {
                if (i == 0) {
                    break;
                }
                if (coordenadas.get(i - 1).getX() != coordenadas.get(i).getX() - 1) {
                    System.out.println("Error: Coordenas no consecutivas en 'X'");
                    return ResultadoAddNave.NO_CONSECUTIVO_X;
                }
            }
        } else if (nave.getOrientacion() == OrientacionNave.HORIZONTAL) {
            for (int i = coordenadas.size() - 1; i >= 0; i--) {
                if (i == 0) {
                    break;
                }
                if (coordenadas.get(i - 1).getY() != coordenadas.get(i).getY() - 1) {
                    System.out.println("Error: Coordenas no consecutivas en 'Y'");
                    return ResultadoAddNave.NO_CONSECUTIVO_Y;
                }
            }
        }

        // Verificar que no haya una nave en las coordenadas seleccionadas o alrededor de estas.
        Casilla[][] casillas = t.getCasillas();
        for (Coordenadas c : coordenadas) {
            for (int i = c.getX() - 1; i < c.getX() + 2; i++) {
                for (int k = c.getY() - 1; k < c.getY() + 2; k++) {
                    if (i >= 0 && k >= 0
                            && i < t.getLimiteX() && k < t.getLimiteY()) {
                        Nave n = casillas[i][k].getNave();
                        if (n != null) {
                            if (n != nave) {
                                System.out.println("Error: Nave encima de otra.");
                                return ResultadoAddNave.ESPACIO_YA_OCUPADO;
                            }
                        }
                    }
                }
            }
        }

        t.addNave(nave, coordenadas);
        j.getNaves().add(nave);

        return ResultadoAddNave.NAVE_AÑADIDA;
    }

    @Override
    public ResultadoConfirmarNaves setConfirmarNaves(Jugador jugador) {
        // Validar que el Jugador exista.
        if (jugador == null) {
            System.out.println("Error: jugador no encontrado.");
            return ResultadoConfirmarNaves.JUGADOR_NO_ENCONTRADO;
        }

        Jugador j = jugadores.stream()
                .filter(e -> e.getNombre().equals(jugador.getNombre()))
                .findFirst()
                .orElse(null);

        if (j == null) {
            System.out.println("Error: jugador no encontrado.");
            return ResultadoConfirmarNaves.JUGADOR_NO_ENCONTRADO;
        }

        if (j.getNaves().size() != totalNaves) {
            System.out.println("Error: jugador no ha puesto todas sus naves.");
            return ResultadoConfirmarNaves.NAVES_SIN_COLOCAR;
        }

        // Actualizar estado jugador.
        j.setEstado(EstadoJugador.NAVES_CONFIRMADAS);

        // Comprobar que todos los Jugadores esten listos.
        boolean todosListos = true;
        for (Jugador e : jugadores) {
            if (e.getEstado() == EstadoJugador.NAVES_CONFIRMADAS) {
                todosListos = false;
                break;
            }
        }

        if (todosListos) {
            estado = EstadoPartida.EN_CURSO;
            return ResultadoConfirmarNaves.EMPEZAR_PARTIDA;
        }

        return ResultadoConfirmarNaves.NAVES_CONFIRMADAS;
    }

    private void crearTablero(Jugador jugador) {
        Casilla[][] casillas = new Casilla[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                casillas[i][j] = new Casilla(EstadoCasilla.AGUA, new Coordenadas(i, j));
            }
        }

        Tablero tablero = new Tablero(casillas, 10, 10);
        jugador.setTablero(tablero);
    }

    // Caso de Uso: Unirse Partida
    @Override
    public void unirsePartida(Jugador jugador) {
        // Validar estado
        if (estado == EstadoPartida.EN_CURSO) {
            System.out.println("La partida ya inicio. No se puede unir.");
            return;
        }

        // Comprobar que no este llena
        if (jugadores.size() >= 2) {
            System.out.println("La partida ya tiene 2 jugadores.");
            return;
        }

        // Agregar nuevo jugador (Pendiente para unir con el caso de uso: Gestionar jugador)
        this.addJugador(jugador);
        System.out.println(jugador.getNombre() + " se unio a la partida.");

        // Notificar a observadores (socktes)
//        notificarAllSuscriptores("JUGADOR_UNIDO", jugador);
    }

    @Override
    public void empezarPartida() {
        // Comprobar que este llena
        if (jugadores.size() < 2) {
            System.out.println("No hay suficientes jugadores para iniciar.");
            return;
        }

        // Cambiar estado
        estado = EstadoPartida.EN_CURSO;

        // Crear tableros
//        this.crearTableros();
        // Asignar turno inicial (jugador 0)
        turno = jugadores.get(0);

        // Notificar
        System.out.println("La partida ha comenzado.");
//        notificarAllSuscriptores("PARTIDA_INICIADA", null);
    }

    @Override
    public Jugador abandonarPartida(Jugador jugadorQueSeVa) {
        System.out.println("Servidor: " + jugadorQueSeVa.getNombre() + " abandonó la partida.");

        jugadorQueSeVa.setEstado(EstadoJugador.ABANDONO);
        jugadores.removeIf(j -> j.getNombre().equals(jugadorQueSeVa.getNombre()));

        if (jugadores.size() == 1) {
            Jugador ganador = jugadores.get(0);
            ganador.setEstado(EstadoJugador.JUGANDO);
            estado = EstadoPartida.FINALIZADA;
            System.out.println("Servidor: " + ganador.getNombre() + " gana por abandono.");
            try {
                cronometro.stop();
            } catch (Exception e) {
            }
        } else if (jugadores.isEmpty()) {
            estado = EstadoPartida.POR_EMPEZAR;
            System.out.println("Servidor: no quedan jugadores, partida reseteada.");
            try {
                cronometro.stop();
            } catch (Exception e) {
            }
        }

        return jugadorQueSeVa;
    }

}
