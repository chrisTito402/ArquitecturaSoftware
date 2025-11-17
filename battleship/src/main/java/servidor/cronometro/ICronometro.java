package servidor.cronometro;

/**
 *
 * @author daniel
 */
public interface ICronometro {
    public boolean isInTime(long tiempo);
    public void initCronometro(long tiempo);
}
