package compartido.observador;

/**
 * Interfaz del patron Observer. Las clases que la implementen
 * pueden registrarse para que les avisen cuando pase algo
 * (como un disparo, cambio de turno, fin de partida, etc).
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public interface ISuscriptor {

    /**
     * Se llama cuando hay una notificacion.
     */
    public void notificar(String contexto, Object datos);
}
