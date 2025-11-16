package clientesocket;

import buseventos.Mensaje;
import buseventos.TipoAccion;
import com.google.gson.Gson;
import controllers.controller.Controlador;
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
    
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 5000;
        
        ClienteSocket client = new ClienteSocket(hostname, port, new Controlador());
        client.execute();
        
        Mensaje mensaje = new Mensaje(TipoAccion.SUSCRIBIR, "DISPARO", null, "1");
        Gson gson = new Gson();
        String json = gson.toJson(mensaje);
        client.enviarMensaje(json);
        
        String[] textos = {"pium pium", "mira como te esquivo", "ai me diste"};
        for (int i = 0; i < 3; i++) {
            Mensaje mensaje2 = new Mensaje(TipoAccion.PUBLICAR, "DISPARO", textos[i], "1");
            String json2 = gson.toJson(mensaje2);
            client.enviarMensaje(json2);
            
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(ClienteSocket.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        
    }
    
    public void execute() {
        try {
            socket = new Socket(hostname, port);
            System.out.println("Conectado al Servidor de Chat.");
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
        System.out.println("ENVIAR MENSAJE A SERVIDOR");
        writer.println(json);
    }
    
    public void manejarMensaje(String json) {
        // Simulando lo que ocurre en el Controlador
        control.manejarMensaje(json);
    }
}
