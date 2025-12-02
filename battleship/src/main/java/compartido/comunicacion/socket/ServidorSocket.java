package compartido.comunicacion.socket;

import servidor.bus.BusEventos;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniel
 */
public class ServidorSocket {

    private int port;
    private Set<UserServerThread> threads = new HashSet<>();
    private BusEventos bus;
    private int id;

    public ServidorSocket(int port, BusEventos bus) {
        this.port = port;
        this.bus = bus;
        this.id = 0;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor escuchando en el puerto " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                UserServerThread newUser = new UserServerThread(socket, this);
                newUser.start();

                try {
                    newUser.waitUntilReady();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServidorSocket.class.getName()).log(Level.SEVERE, null, ex);
                }

                id++;
                String idCliente = String.valueOf(id);
                newUser.sendMessage(idCliente);

                String evento = "MENSAJE_CLIENTE_" + String.valueOf(id);
                addNewClientToEvent(evento, newUser);
            }
        } catch (IOException ex) {
            System.out.println("Error al iniciar el Servidor de Chat: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    void sendMessage(String message, UserServerThread user) {
        for (UserServerThread userThread : threads) {
            if (userThread == user) {
                userThread.sendMessage(message);
            }
        }
    }

    void sendToBus(String message, UserServerThread user) {
        bus.manejarEvento(message, user);
    }

    void removeUser(UserServerThread user) {
        threads.remove(user);
        bus.removeSuscriptor(user);
    }

    private void addNewClientToEvent(String event, UserServerThread client) {
        bus.addNewClient(event, client);
    }

}
