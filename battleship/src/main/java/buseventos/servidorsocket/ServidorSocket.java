package buseventos.servidorsocket;

import buseventos.buseventos.BusEventos;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

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
    
}
