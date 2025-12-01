package servidor.cronometro;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import models.entidades.Partida;

/**
 *
 * @author daniel
 */
public class Cronometro implements ICronometro {

    private Instant min;
    private Instant max;
    private final long duracionTurnoMili;
    private Partida partida;

    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> tareaCambioTurno;
    private final AtomicBoolean procesandoDisparo;

    public Cronometro(long mili) {
        this.duracionTurnoMili = mili;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.procesandoDisparo = new AtomicBoolean(false);
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    private void initInstance(long tiempo) {
        this.min = Instant.now();
        this.max = min.plusMillis(tiempo);
        System.out.println("SE CAMBIO EL LAS INSTANCIAS UNIX");
    }

    @Override
    public void initCronometro() {
        System.out.println("INIT CRONOMETRO");
        if (tareaCambioTurno != null && !tareaCambioTurno.isDone()) {
            tareaCambioTurno.cancel(false);
        }

        initInstance(duracionTurnoMili);

        tareaCambioTurno = scheduler.schedule(() -> {
            if (!procesandoDisparo.get()) {
                partida.cambiarTurno();
            }
        }, duracionTurnoMili, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isInTime(long tiempo) {
        Instant horaAccion = Instant.ofEpochMilli(tiempo);
        return horaAccion.isAfter(min) && horaAccion.isBefore(max);
    }

    @Override
    public void setProcesandoDisparo(boolean estado) {
        procesandoDisparo.set(estado);
    }

    @Override
    public void stop() {
        if (tareaCambioTurno != null) {
            tareaCambioTurno.cancel(true);
        }
        scheduler.shutdown();
    }
}
