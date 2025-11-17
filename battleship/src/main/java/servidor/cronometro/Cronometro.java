package servidor.cronometro;

import java.time.Instant;

/**
 *
 * @author daniel
 */
public class Cronometro implements ICronometro {
    
    private Instant min;
    private Instant max;

    public Cronometro() {
    }
    
    @Override
    public boolean isInTime(long tiempo) {
        Instant horaAccion = Instant.ofEpochMilli(tiempo);
        return horaAccion.isAfter(min) && horaAccion.isBefore(max);
    }
    
    @Override
    public void initCronometro(long tiempo) {
        this.min = Instant.now();
        this.max = min.plusMillis(tiempo);
    }
}
