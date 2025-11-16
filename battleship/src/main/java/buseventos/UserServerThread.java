package buseventos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 *
 * @author daniel
 */
public class UserServerThread extends Thread {
    
    private Socket socket;
    private ServidorSocket server;
    private PrintWriter writer;
    private BufferedReader reader;

    public UserServerThread(Socket socket, ServidorSocket server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            
            String clientMessage;
            while (true) {
                try {
                    System.out.println("ESPERANDO MENSAJE EN SERVIDOR");
                    clientMessage = reader.readLine();
                    System.out.println("RECIBIR MENSAJE EN SERVIDOR");
                    server.sendToBus(clientMessage, this);
                } catch (SocketException ex) {
                    server.removeUser(this);
                    break;
                }
            }
            
        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    void sendMessage(String message) {
        writer.println(message);
    }
    
    
}
