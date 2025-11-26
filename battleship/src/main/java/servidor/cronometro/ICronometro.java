package servidor.cronometro;

public interface ICronometro {

    void initCronometro();

    boolean isInTime(long tiempo);

    void setProcesandoDisparo(boolean estado);

    void stop();

    long getTiempoRestante();

    void setOnTiempoAgotado(Runnable callback);
}
