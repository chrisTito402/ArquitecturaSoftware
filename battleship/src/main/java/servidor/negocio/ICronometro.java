package servidor.negocio;

/**
 *
 * @author daniel
 */
public interface ICronometro {

    public void initCronometro();

    public boolean isInTime(long tiempo);

    public void setProcesandoDisparo(boolean estado);

    public void stop();
}
