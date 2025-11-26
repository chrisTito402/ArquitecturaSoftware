package buseventos.servidorsocket;

import buseventos.IBusEventos;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorSocket {

    private int port;
    private IBusEventos bus;

    public ServidorSocket(int port, IBusEventos bus) {
        this.port = port;
        this.bus = bus;
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
            System.out.println("Error al iniciar el Servidor: " + ex.getMessage());
        }
    }

    void sendToBus(String message, UserServerThread user) {
        bus.manejarEvento(message, user);
    }

    void removeUser(UserServerThread user) {
        bus.removeSuscriptor(user);
    }
}
