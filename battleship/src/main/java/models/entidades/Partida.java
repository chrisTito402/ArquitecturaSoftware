package models.entidades;

import java.util.Comparator;
import models.enums.EstadoNave;
import models.enums.EstadoPartida;
import models.enums.ResultadoDisparo;
import models.observador.ISuscriptor;
import java.util.List;
import models.builder.Director;
import models.builder.TableroBuilder;
import models.enums.OrientacionNave;
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

        //Obtener al oponente
        Jugador j2 = jugadores.stream().filter(e -> e != turno)
                .findFirst()
                .orElse(null);
        
        if (j2 == null) {
            System.out.println("Error, no se encontró al oponente.");
            return null;
        }
        
        //Disparo del jugador actual
        Tablero tablero = j2.getTablero();
        ResultadoDisparo resultadoDisparo = tablero.realizarDisparo(coordenadas);

        if (resultadoDisparo == ResultadoDisparo.HUNDIMIENTO) {
            Nave nave = j2.getNaves().stream().filter(n -> n.getEstado() == EstadoNave.SIN_DAÑOS
                    || n.getEstado() == EstadoNave.AVERIADO)
                    .findFirst()
                    .orElse(null);

            if (nave == null) {
                System.out.println("Jugador: " + turno.getNombre() + " GANO!");
                estado = EstadoPartida.FINALIZADA;
                disparo = new Disparo(jugador, coordenadas, resultadoDisparo, estado);
                return disparo;
            }
        }

        disparo = new Disparo(jugador, coordenadas, resultadoDisparo, estado);
        cambiarTurno();
        return disparo;
    }

    @Override
    public boolean addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas) {
        // Verificar que el jugador existe.
        Jugador j = jugadores.stream().filter(e -> e.getNombre().equals(jugador.getNombre()))
                .findFirst()
                .orElse(null);

        if (j == null) {
            return false;
        }
        
        // Verificar que las coordenadas no se salen del limite del tablero.
        Tablero t = j.getTablero();
        for (Coordenadas coordenada : coordenadas) {
            if (coordenada.getY() < 0 || coordenada.getY() > t.getLimiteY() ||
                    coordenada.getX() < 0 || coordenada.getX() > t.getLimiteX()) {
                return false;
            }
        }
        
        // Verificar que todas las coordenas esten con la misma orientacion.
        if (nave.getOrientacion() == OrientacionNave.VERTICAL) {
            int y = coordenadas.getFirst().getY();
            for (Coordenadas coordenada : coordenadas) {
                if (coordenada.getY() != y) {
                    return false;
                }
            }
        } else if (nave.getOrientacion() == OrientacionNave.HORIZONTAL) {
            int x = coordenadas.getFirst().getX();
            for (Coordenadas coordenada : coordenadas) {
                if (coordenada.getX() != x) {
                    return false;
                }
            }
        }
        
        // Ordenar lista por "X" y "Y".
        coordenadas.sort(Comparator.comparingInt(Coordenadas::getX)
              .thenComparingInt(Coordenadas::getY));
        
        // Verificar que no haya una nave en las coordenadas seleccionadas o alrededor de estas.
        Casilla[][] casillas = j.getTablero().getCasillas();
        
        if (nave.getOrientacion() == OrientacionNave.VERTICAL) {
            for (int i = 0; i < nave.getTamanio(); i++) {
            }
        }
        
        t.addNave(nave, coordenadas);
        j.getNaves().add(nave);

        // Provisional
        turno = j;

        return true;
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
    public void abandonarLobby(Jugador jugador) {
        // Quitar al jugador que se va
        jugadores.remove(jugador);

        // Notificar
        System.out.println(jugador.getNombre() + " abandonó el lobby.");
//        notificarAllSuscriptores("JUGADOR_ABANDONO", jugador);
    }
}
