package clientesocket;

import controllers.controller.ManejadorRespuestaCliente;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClienteSocket implements IClienteSocket {

    private final String hostname;
    private final int port;
    private PrintWriter writer;
    private Socket socket;
    private ManejadorRespuestaCliente control;
    private final AtomicBoolean conectado;
    private UserReadClientThread readThread;
    private ConexionListener conexionListener;

    public interface ConexionListener {
        void onConexionExitosa();
        void onConexionFallida(String mensaje);
        void onDesconexion(String mensaje);
    }

    public ClienteSocket(String hostname, int port, ManejadorRespuestaCliente control) {
        this.hostname = hostname;
        this.port = port;
        this.control = control;
        this.conectado = new AtomicBoolean(false);
    }

    public void setConexionListener(ConexionListener listener) {
        this.conexionListener = listener;
    }

    public void setControl(ManejadorRespuestaCliente control) {
        this.control = control;
    }

    public void execute() {
        try {
            socket = new Socket(hostname, port);
            conectado.set(true);
            System.out.println("Conectado al Servidor.");

            readThread = new UserReadClientThread(socket, this);
            readThread.start();

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            if (conexionListener != null) {
                conexionListener.onConexionExitosa();
            }
        } catch (UnknownHostException ex) {
            String mensaje = "No se encontró el servidor: " + ex.getMessage();
            System.out.println(mensaje);
            conectado.set(false);
            if (conexionListener != null) {
                conexionListener.onConexionFallida(mensaje);
            }
        } catch (IOException ex) {
            String mensaje = "Error de conexión: " + ex.getMessage();
            System.out.println(mensaje);
            conectado.set(false);
            if (conexionListener != null) {
                conexionListener.onConexionFallida(mensaje);
            }
        }
    }

    @Override
    public void enviarMensaje(String json) {
        if (!conectado.get()) {
            System.err.println("[ClienteSocket] Error: No hay conexion establecida");
            return;
        }

        if (writer == null) {
            System.err.println("[ClienteSocket] Error: Writer no inicializado");
            return;
        }

        if (json == null || json.isEmpty()) {
            return;
        }

        writer.println(json);
    }

    public void manejarMensaje(String json) {
        if (control != null) {
            control.manejarMensaje(json);
        }
    }

    public void desconectar() {
        conectado.set(false);

        if (readThread != null) {
            readThread.interrupt();
        }

        if (writer != null) {
            writer.close();
        }

        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("[ClienteSocket] Error al cerrar socket: " + e.getMessage());
            }
        }
    }

    void notificarDesconexion(String mensaje) {
        conectado.set(false);
        if (conexionListener != null) {
            conexionListener.onDesconexion(mensaje);
        }
    }

    public boolean isConectado() {
        return conectado.get() && socket != null && !socket.isClosed();
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }
}
