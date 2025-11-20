package clientesocket;

import controllers.controller.ManejadorRespuestaCliente;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author daniel
 */
public class ClienteSocket implements IClienteSocket {
    
    private String hostname;
    private int port;
    private PrintWriter writer;
    private Socket socket;
    private ManejadorRespuestaCliente control;

    public ClienteSocket(String hostname, int port, ManejadorRespuestaCliente control) {
        this.hostname = hostname;
        this.port = port;
        this.control = control;
    }

    public void setControl(ManejadorRespuestaCliente control) {
        this.control = control;
    }
    
    public void execute() {
        try {
            socket = new Socket(hostname, port);
            System.out.println("Conectado al Servidor.");
            new UserReadClientThread(socket, this).start();
            
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (UnknownHostException ex) {
            System.out.println("No se encontró el servidor: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    
    @Override
    public void enviarMensaje(String json) {
        writer.println(json);
    }
    
    public void manejarMensaje(String json) {
        control.manejarMensaje(json);
    }
}
