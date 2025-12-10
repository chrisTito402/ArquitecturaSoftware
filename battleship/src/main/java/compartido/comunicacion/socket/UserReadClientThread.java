package compartido.comunicacion.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

/**
 *
 * @author daniel
 */
public class UserReadClientThread extends Thread {

    private BufferedReader reader;
    private Socket socket;
    private ClienteSocket client;

    public UserReadClientThread(Socket socket, ClienteSocket client) {
        this.socket = socket;
        this.client = client;

        try {
            InputStream input = this.socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            try {
                String id = reader.readLine();
                System.out.println("CLIENTE, ID QUE LLEGO: " + id);
                client.setId(id);
            } catch (SocketException ex) {
                socket.close();
            }
            while (true) {
                try {
                    String response = reader.readLine();
                    client.manejarMensaje(response);
                } catch (SocketException ex) {
                    socket.close();
                    break;
                }
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
