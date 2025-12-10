package compartido.comunicacion.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;

/**
 * Hilo que maneja la comunicacion con un cliente conectado en el servidor.
 *
 * Cada cliente que se conecta tiene su propio hilo que se encarga de
 * leer los mensajes entrantes y enviar respuestas. Cuando el cliente
 * se desconecta, el hilo notifica al servidor para limpiar los recursos.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public class UserServerThread extends Thread {

    private Socket socket;
    private ServidorSocket server;
    private PrintWriter writer;
    private BufferedReader reader;
    private final CountDownLatch readyLatch = new CountDownLatch(1);

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

            readyLatch.countDown();

            String clientMessage;
            while (true) {
                try {
                    clientMessage = reader.readLine();

                    // Si readLine() retorna null, el cliente se desconectó
                    if (clientMessage == null) {
                        System.out.println("[UserServerThread] Cliente desconectado (readLine null)");
                        server.removeUser(this);
                        break;
                    }

                    server.sendToBus(clientMessage, this);
                } catch (SocketException ex) {
                    System.out.println("[UserServerThread] Cliente desconectado (SocketException)");
                    server.removeUser(this);
                    break;
                }
            }

        } catch (IOException ex) {
            System.out.println("[UserServerThread] Error: " + ex.getMessage());
            server.removeUser(this);
        } finally {
            // Asegurar que el socket se cierre
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.out.println("[UserServerThread] Error cerrando socket: " + e.getMessage());
            }
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    public void waitUntilReady() throws InterruptedException {
        readyLatch.await();
    }

}
