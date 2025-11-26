package servidor.cronometro;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class Cronometro implements ICronometro {

    private volatile Instant min;
    private volatile Instant max;
    private final long duracionTurnoMili;

    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> tareaCambioTurno;
    private final AtomicBoolean procesandoDisparo;
    private final AtomicBoolean activo;
    private final ReentrantLock lock;
    private final Thread shutdownHook;

    private Runnable onTiempoAgotado;

    public Cronometro(long mili) {
        this.duracionTurnoMili = mili;
        ThreadFactory threadFactory = r -> {
            Thread t = new Thread(r, "Cronometro-Thread");
            t.setDaemon(true);
            return t;
        };
        this.scheduler = Executors.newSingleThreadScheduledExecutor(threadFactory);
        this.procesandoDisparo = new AtomicBoolean(false);
        this.activo = new AtomicBoolean(false);
        this.lock = new ReentrantLock();

        this.shutdownHook = new Thread(this::shutdownInterno, "Cronometro-ShutdownHook");
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    @Override
    public void setOnTiempoAgotado(Runnable callback) {
        this.onTiempoAgotado = callback;
    }

    private void initInstance(long tiempo) {
        this.min = Instant.now();
        this.max = min.plusMillis(tiempo);
    }

    @Override
    public void initCronometro() {
        lock.lock();
        try {
            if (tareaCambioTurno != null && !tareaCambioTurno.isDone()) {
                tareaCambioTurno.cancel(false);
            }

            initInstance(duracionTurnoMili);
            activo.set(true);

            tareaCambioTurno = scheduler.schedule(() -> {
                if (!procesandoDisparo.get() && activo.get()) {
                    ejecutarCambioTurno();
                }
            }, duracionTurnoMili, TimeUnit.MILLISECONDS);
        } finally {
            lock.unlock();
        }
    }

    private void ejecutarCambioTurno() {
        lock.lock();
        try {
            if (onTiempoAgotado != null && activo.get()) {
                onTiempoAgotado.run();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isInTime(long tiempo) {
        Instant horaAccion = Instant.ofEpochMilli(tiempo);
        Instant minActual = min;
        Instant maxActual = max;

        if (minActual == null || maxActual == null) {
            return false;
        }

        return horaAccion.isAfter(minActual) && horaAccion.isBefore(maxActual);
    }

    @Override
    public void setProcesandoDisparo(boolean estado) {
        procesandoDisparo.set(estado);
    }

    @Override
    public long getTiempoRestante() {
        if (!activo.get() || max == null) {
            return 0;
        }

        long restante = max.toEpochMilli() - Instant.now().toEpochMilli();
        return Math.max(0, restante);
    }

    @Override
    public void stop() {
        shutdownInterno();
        try {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        } catch (IllegalStateException ignored) {
        }
    }

    private void shutdownInterno() {
        lock.lock();
        try {
            if (!activo.getAndSet(false)) {
                return;
            }

            if (tareaCambioTurno != null) {
                tareaCambioTurno.cancel(true);
            }

            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean isActivo() {
        return activo.get();
    }
}
