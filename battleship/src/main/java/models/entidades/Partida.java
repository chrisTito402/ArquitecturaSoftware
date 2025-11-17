package models.entidades;

import models.enums.EstadoNave;
import models.enums.EstadoPartida;
import models.enums.ResultadoDisparo;
import models.observador.ISuscriptor;
import java.util.List;
import models.builder.Director;
import models.builder.TableroBuilder;
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
        cronometro.initCronometro(30000);
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    @Override
    public void notificarAllSuscriptores(String contexto, Object datos) {
        suscriptores.forEach(s -> s.notificar(contexto, datos));
    }

    @Override
    public void addJugador(Jugador j) {
        jugadores.add(j);
    }
    
    @Override
    public Disparo realizarDisparo(Coordenadas coordenadas, Jugador jugador, long tiempo) {
        jugadores.forEach(e -> System.out.println(e.getNombre()));
        
        // Verificar el Cronometro
        if (!cronometro.isInTime(tiempo)) {
            System.out.println("Error, el disparo no fue hecho a tiempo.");
            return null;
        }
        
        // Verificar Turno
        if (!jugador.getNombre().equals(turno.getNombre())) {
            System.out.println("Error, no es el turno del jugador seleccionado.");
            return null;
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

        // Si falla, pasa turno
        if (resultadoDisparo == ResultadoDisparo.AGUA) {
            turno = j2;

            //Si el siguiente es un Bot, dispara automáticamente
            if (turno instanceof Bot) {
                System.out.println("ES UN BOT");
                Bot bot = (Bot) turno;
                //realizarDisparo(bot.getCoordenadas(), turno);
            }
        }
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

        if (turno instanceof Bot) {
            if (resultadoDisparo == ResultadoDisparo.HUNDIMIENTO
                    || resultadoDisparo == ResultadoDisparo.IMPACTO) {
                Bot bot = (Bot) turno;
                //realizarDisparo(bot.getCoordenadas(), turno);
            }
        }

        disparo = new Disparo(jugador, coordenadas, resultadoDisparo, estado);
        System.out.println("DISPARO AQUI " + disparo.getResultadoDisparo());

        return disparo;
    }

    @Override
    public boolean addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas) {
        Jugador j = jugadores.stream().filter(e -> e.getNombre() == jugador.getNombre())
                .findFirst()
                .orElse(null);
        
        Tablero t = j.getTablero();
        t.addNave(nave, coordenadas);
        j.getNaves().add(nave);
        
        // Provisional
        turno = j;
        
        return false;
    }

    @Override
    public void crearTableros() {
        Director d = new Director();
        TableroBuilder builder = new TableroBuilder();
        
        for (Jugador j : jugadores) {
            d.makeTablero(builder);
            j.setTablero(builder.getResult());
        }
    }

    @Override
    public void suscribirAPartida(ISuscriptor suscriptor) {
        suscriptores.add(suscriptor);
    }
    
    // Caso de Uso: Unirse Partida
    @Override
    public void unirsePartida() {
        List<Jugador> jugadoresActuales = this.jugadores;
    }
    
    @Override
    public void empezarPartida() {
        
    }
    
    @Override
    public void abandonarLobby() {
        List<Jugador> jugadoresActuales = this.jugadores;
    }
}
