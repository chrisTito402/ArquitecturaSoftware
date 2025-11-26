package clientesocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class UserReadClientThread extends Thread {

    private BufferedReader reader;
    private final Socket socket;
    private final ClienteSocket client;
    private volatile boolean running;

    public UserReadClientThread(Socket socket, ClienteSocket client) {
        this.socket = socket;
        this.client = client;
        this.running = true;

        try {
            InputStream input = this.socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            System.out.println("[UserReadClientThread] Error al inicializar: " + ex.getMessage());
            running = false;
        }
    }

    @Override
    public void run() {
        try {
            while (running && !Thread.currentThread().isInterrupted()) {
                try {
                    String response = reader.readLine();
                    if (response == null) {
                        System.out.println("[UserReadClientThread] Conexión cerrada por el servidor");
                        client.notificarDesconexion("Conexión cerrada por el servidor");
                        break;
                    }
                    client.manejarMensaje(response);
                } catch (SocketException ex) {
                    if (running) {
                        System.out.println("[UserReadClientThread] Socket cerrado: " + ex.getMessage());
                        client.notificarDesconexion("Se perdió la conexión con el servidor");
                    }
                    break;
                }
            }
        } catch (IOException ex) {
            if (running) {
                System.out.println("[UserReadClientThread] Error de lectura: " + ex.getMessage());
                client.notificarDesconexion("Error de comunicación: " + ex.getMessage());
            }
        } finally {
            cerrarRecursos();
        }
    }

    private void cerrarRecursos() {
        running = false;
        try {
            if (reader != null) {
                reader.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("[UserReadClientThread] Error al cerrar recursos: " + e.getMessage());
        }
    }

    public void detener() {
        running = false;
        cerrarRecursos();
    }
}
