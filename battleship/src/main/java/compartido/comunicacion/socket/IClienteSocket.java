package compartido.comunicacion.socket;

/**
 *
 * @author daniel
 */
public interface IClienteSocket {

    public void enviarMensaje(String json);

    public String getId();
}
