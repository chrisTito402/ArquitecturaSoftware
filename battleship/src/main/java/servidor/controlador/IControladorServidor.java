package servidor.controlador;

/**
 * Interfaz del controlador del servidor. Solo tiene el metodo para
 * recibir mensajes en formato JSON.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public interface IControladorServidor {

    public void manejarMensaje(String json);
}
