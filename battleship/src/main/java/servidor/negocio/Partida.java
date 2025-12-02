package servidor.negocio;

import java.util.Comparator;
import java.util.Random;
import compartido.enums.EstadoNave;
import compartido.enums.EstadoPartida;
import compartido.enums.ResultadoDisparo;
import compartido.observador.ISuscriptor;
import java.util.List;
import compartido.enums.EstadoJugador;
import compartido.enums.OrientacionNave;
import compartido.enums.ResultadoAddNave;
import compartido.entidades.Barco;
import compartido.entidades.Casilla;
import compartido.entidades.Coordenadas;
import compartido.entidades.Crucero;
import compartido.entidades.Disparo;
import compartido.entidades.Jugador;
import compartido.entidades.Nave;
import compartido.entidades.PortaAviones;
import compartido.entidades.Submarino;
import compartido.entidades.Tablero;

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

        // Verificar si ya se habia disparado a esa coordenada
        if (resultadoDisparo == ResultadoDisparo.YA_DISPARADO) {
            System.out.println("Error: Ya se disparo a esa coordenada anteriormente.");
            disparo = new Disparo(jugador, coordenadas, resultadoDisparo, estado);
            cronometro.setProcesandoDisparo(false);
            return disparo;
        }

        // Obtener la nave impactada (si la hay) para el marcador
        Nave naveImpactada = tablero.getNaveEnCoordenadas(coordenadas);
        String tipoNaveImpactada = obtenerTipoNave(naveImpactada);

        if (resultadoDisparo == ResultadoDisparo.HUNDIMIENTO) {
            Nave naveRestante = j2.getNaves().stream().filter(n -> n.getEstado() == EstadoNave.SIN_DAÑOS
                    || n.getEstado() == EstadoNave.AVERIADO)
                    .findFirst()
                    .orElse(null);

            if (naveRestante == null) {
                System.out.println("Jugador: " + turno.getNombre() + " GANO!");
                estado = EstadoPartida.FINALIZADA;
                disparo = new Disparo(jugador, coordenadas, resultadoDisparo, estado);
                disparo.setTipoNaveHundida(tipoNaveImpactada);
                return disparo;
            }
        }

        disparo = new Disparo(jugador, coordenadas, resultadoDisparo, estado);

        // Setear el tipo de nave segun el resultado
        if (resultadoDisparo == ResultadoDisparo.IMPACTO) {
            disparo.setTipoNaveImpactada(tipoNaveImpactada);
        } else if (resultadoDisparo == ResultadoDisparo.HUNDIMIENTO) {
            disparo.setTipoNaveHundida(tipoNaveImpactada);
        }

        // Solo cambiar turno si el resultado es AGUA
        // Si es IMPACTO o HUNDIMIENTO, el jugador mantiene el turno
        if (resultadoDisparo == ResultadoDisparo.AGUA) {
            cambiarTurno();
        } else {
            // Si acerto, reiniciar el cronometro pero mantener el turno
            cronometro.setProcesandoDisparo(false);
            cronometro.initCronometro();
            System.out.println("IMPACTO! " + turno.getNombre() + " tiene otro turno.");
        }

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
            if (coordenada.getY() < 0 || coordenada.getY() > t.getLimiteY()
                    || coordenada.getX() < 0 || coordenada.getX() > t.getLimiteX()) {
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
                    if (i >= 0 && k >= 0) {
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

        // Seleccionar turno inicial ALEATORIAMENTE
        Random random = new Random();
        int indiceAleatorio = random.nextInt(jugadores.size());
        turno = jugadores.get(indiceAleatorio);

        // Notificar
        System.out.println("La partida ha comenzado. Primer turno: " + turno.getNombre());
    }

    @Override
    public void abandonarPartida(Jugador jugadorQueSeVa) {

        System.out.println("Servidor: " + jugadorQueSeVa.getNombre() + " abandonó la partida.");

        // 1. Marcar su estado como ABANDONO
        jugadorQueSeVa.setEstado(EstadoJugador.ABANDONO);

        // 2. Eliminar al jugador de la lista
        jugadores.removeIf(j -> j.getNombre().equals(jugadorQueSeVa.getNombre()));

        // 3. Si queda un jugador -> ese gana automáticamente
        if (jugadores.size() == 1) {
            Jugador ganador = jugadores.get(0);

            // El ganador sigue en estado "JUGANDO" (no tienes estado GANADOR)
            ganador.setEstado(EstadoJugador.JUGANDO);

            estado = EstadoPartida.FINALIZADA;

            System.out.println("Servidor: " + ganador.getNombre() + " gana por abandono.");

            // Detener el cronómetro si existe
            try {
                cronometro.stop();
            } catch (Exception e) {
            }

            return;
        }

        // 4. Si no queda nadie -> resetear la partida
        if (jugadores.isEmpty()) {
            estado = EstadoPartida.POR_EMPEZAR;

            System.out.println("Servidor: no quedan jugadores, partida reseteada.");

            try {
                cronometro.stop();
            } catch (Exception e) {
            }

            return;
        }
    }

    /**
     * Obtiene el tipo de nave como String para el marcador.
     * @param nave La nave a identificar
     * @return El nombre del tipo de nave (PORTAAVIONES, CRUCERO, SUBMARINO, BARCO) o null
     */
    private String obtenerTipoNave(Nave nave) {
        if (nave == null) {
            return null;
        }
        if (nave instanceof PortaAviones) {
            return "PORTAAVIONES";
        } else if (nave instanceof Crucero) {
            return "CRUCERO";
        } else if (nave instanceof Submarino) {
            return "SUBMARINO";
        } else if (nave instanceof Barco) {
            return "BARCO";
        }
        return null;
    }

}
