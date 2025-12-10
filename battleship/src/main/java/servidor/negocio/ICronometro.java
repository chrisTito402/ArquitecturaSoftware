package servidor.negocio;

/**
 * Interfaz del cronometro. Define como iniciar, parar y checar el tiempo.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public interface ICronometro {

    public void initCronometro();

    public boolean isInTime(long tiempo);

    public void setProcesandoDisparo(boolean estado);

    public void stop();
}
