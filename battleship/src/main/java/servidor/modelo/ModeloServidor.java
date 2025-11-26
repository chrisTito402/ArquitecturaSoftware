package servidor.modelo;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import models.entidades.Coordenadas;
import models.entidades.Disparo;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.entidades.Partida;
import models.enums.EstadoPartida;
import models.enums.ResultadoAddNave;
import models.enums.ResultadoDisparo;
import models.enums.ResultadoUnirse;
import models.services.DisparoService;
import models.services.IDisparoService;
import models.services.IPartidaService;
import models.services.ITableroService;
import models.services.PartidaService;
import models.services.TableroService;
import servidor.cronometro.ICronometro;

public class ModeloServidor implements IModeloServidor {

    private final Partida partida;
    private final ICronometro cronometro;
    private final IPartidaService partidaService;
    private final IDisparoService disparoService;
    private final ITableroService tableroService;
    private final ReentrantLock lockDisparo;

    public ModeloServidor(Partida partida, ICronometro cronometro) {
        this.partida = partida;
        this.cronometro = cronometro;
        this.partidaService = new PartidaService();
        this.disparoService = new DisparoService();
        this.tableroService = new TableroService();
        this.lockDisparo = new ReentrantLock();
    }

    public ModeloServidor(Partida partida, ICronometro cronometro,
                          IPartidaService partidaService, IDisparoService disparoService,
                          ITableroService tableroService) {
        this.partida = partida;
        this.cronometro = cronometro;
        this.partidaService = partidaService;
        this.disparoService = disparoService;
        this.tableroService = tableroService;
        this.lockDisparo = new ReentrantLock();
    }

    @Override
    public Disparo realizarDisparo(Coordenadas coordenadas, Jugador jugador, long tiempo) {
        lockDisparo.lock();
        try {
            if (cronometro != null) {
                cronometro.setProcesandoDisparo(true);

                if (!cronometro.isInTime(tiempo)) {
                    partida.cambiarTurno();
                    reiniciarCronometro();
                    return new Disparo(jugador, coordenadas, ResultadoDisparo.DISPARO_FUERA_TIEMPO, partida.getEstado());
                }
            }

            Disparo disparo = disparoService.realizarDisparo(partida, jugador, coordenadas, tiempo);

            if (disparo != null) {
                ResultadoDisparo resultado = disparo.getResultado();

                if (resultado == ResultadoDisparo.AGUA
                        || resultado == ResultadoDisparo.YA_DISPARADO
                        || resultado == ResultadoDisparo.COORDENADAS_INVALIDAS) {
                    partida.cambiarTurno();
                }

                if (disparo.getEstadoPartida() == EstadoPartida.FINALIZADA) {
                    detenerCronometro();
                } else {
                    reiniciarCronometro();
                }
            }

            return disparo;
        } finally {
            if (cronometro != null) {
                cronometro.setProcesandoDisparo(false);
            }
            lockDisparo.unlock();
        }
    }

    @Override
    public ResultadoAddNave addNave(Jugador jugador, Nave nave, List<Coordenadas> coordenadas) {
        Jugador jugadorEnPartida = partida.getJugadores().stream()
                .filter(j -> j.equals(jugador))
                .findFirst()
                .orElse(null);

        if (jugadorEnPartida == null) {
            return ResultadoAddNave.JUGADOR_NO_ENCONTRADO;
        }

        return tableroService.colocarNave(
                jugadorEnPartida.getTablero(),
                jugadorEnPartida,
                nave,
                coordenadas,
                partida.getJugadores()
        );
    }

    @Override
    public void addJugador(Jugador j) {
        partida.addJugador(j);
    }

    @Override
    public List<Jugador> getJugadores() {
        return partida.getJugadores();
    }

    @Override
    public void unirsePartida(Jugador jugador) {
        ResultadoUnirse resultado = partidaService.unirsePartida(partida, jugador);
        if (resultado != ResultadoUnirse.EXITO) {
            System.out.println("[ModeloServidor] Error al unirse: " + resultado);
        }
    }

    @Override
    public void crearTableros() {
        tableroService.inicializarTablerosJugadores(partida.getJugadores());
    }

    @Override
    public void empezarPartida() {
        partidaService.iniciarPartida(partida);

        if (cronometro != null && partida.getEstado() == EstadoPartida.EN_CURSO) {
            cronometro.initCronometro();
        }
    }

    @Override
    public void abandonarPartida(Jugador jugadorQueSeVa) {
        partidaService.abandonarPartida(partida, jugadorQueSeVa);
        detenerCronometro();
    }

    public Partida getPartida() {
        return partida;
    }

    @Override
    public long getTiempoRestante() {
        if (cronometro != null) {
            return cronometro.getTiempoRestante();
        }
        return 0;
    }

    @Override
    public Jugador getTurnoActual() {
        return partida.getTurno();
    }

    @Override
    public void cambiarTurno() {
        partida.cambiarTurno();
    }

    private void reiniciarCronometro() {
        if (cronometro != null) {
            cronometro.initCronometro();
        }
    }

    private void detenerCronometro() {
        if (cronometro != null) {
            try {
                cronometro.stop();
            } catch (Exception ignored) {
            }
        }
    }
}
