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
    private final int duracionTurnoSeg;
    private Partida partida;
    
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> tareaCambioTurno;
    private final AtomicBoolean procesandoDisparo;
    
    public Cronometro(int seg) {
        this.duracionTurnoSeg = seg;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.procesandoDisparo = new AtomicBoolean(false);
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }
    
    private void initInstance(long tiempo) {
        this.min = Instant.now();
        this.max = min.plusMillis(tiempo);
    }
    
    @Override
    public void initCronometro() {
        // 1. Si ya hay una tarea programada (el cambio de turno anterior), la cancelamos.
        if (tareaCambioTurno != null && !tareaCambioTurno.isDone()) {
            tareaCambioTurno.cancel(false); 
        }

        // 2. Definimos los Instants para validación
        initInstance(30000);

        // 3. Programamos el cambio de turno para dentro de 30 segundos EXACTOS
        tareaCambioTurno = scheduler.schedule(() -> {
            // Verificamos si se está procesando un disparo antes de cambiar
            if (!procesandoDisparo.get()) {
                partida.cambiarTurno();
            }
        }, duracionTurnoSeg, TimeUnit.SECONDS);
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
