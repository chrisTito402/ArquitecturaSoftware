package buseventos.servidorsocket;

import buseventos.IEventSuscriptor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class UserServerThread extends Thread implements IEventSuscriptor {
    
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
                    clientMessage = reader.readLine();
                    if (clientMessage == null) {
                        System.out.println("Cliente desconectado");
                        server.removeUser(this);
                        break;
                    }
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
    
    public void sendMessage(String message) {
        writer.println(message);
    }

    @Override
    public void recibirEvento(String eventoJSON) {
        sendMessage(eventoJSON);
    }

    @Override
    public String getSuscriptorId() {
        return "UserThread-" + Thread.currentThread().getId() + "-" + this.hashCode();
    }
}
