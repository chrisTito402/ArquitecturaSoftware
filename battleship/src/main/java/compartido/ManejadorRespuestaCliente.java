package compartido;

/**
 * Interfaz para manejar respuestas del servidor.
 * Usada tanto por el cliente como por el servidor.
 *
 * @author daniel
 */
public interface ManejadorRespuestaCliente {

    public void manejarMensaje(String json);

    public void onIdSet(String id);
}
