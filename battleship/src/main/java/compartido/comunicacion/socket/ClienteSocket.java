package compartido.comunicacion.socket;

import compartido.ManejadorRespuestaCliente;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Maneja la conexion del cliente con el servidor mediante sockets.
 *
 * Se encarga de establecer la conexion TCP con el servidor, enviar
 * mensajes y recibir respuestas. Usa un hilo separado para leer
 * los mensajes entrantes sin bloquear la interfaz.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public class ClienteSocket implements IClienteSocket {

    private String hostname;
    private int port;
    private PrintWriter writer;
    private Socket socket;
    private ManejadorRespuestaCliente control;
    private String id;

    public ClienteSocket(String hostname, int port, ManejadorRespuestaCliente control) {
        this.hostname = hostname;
        this.port = port;
        this.control = control;
    }

    public void setControl(ManejadorRespuestaCliente control) {
        this.control = control;
    }

    public boolean execute() {
        try {
            socket = new Socket(hostname, port);
            System.out.println("Conectado al Servidor.");
            new UserReadClientThread(socket, this).start();

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            return true;
        } catch (UnknownHostException ex) {
            System.out.println("No se encontró el servidor: " + ex.getMessage());
            return false;
        } catch (IOException ex) {
            System.out.println("Error de conexión: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public void enviarMensaje(String json) {
        writer.println(json);
    }

    public void manejarMensaje(String json) {
        control.manejarMensaje(json);
    }

    void setId(String id) {
        this.id = id;
        control.onIdSet(id);
    }

    @Override
    public String getId() {
        return this.id;
    }
}
