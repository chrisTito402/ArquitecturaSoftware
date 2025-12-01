import buseventos.buseventos.BusEventos;
import buseventos.servidorsocket.ServidorSocket;
import java.util.HashMap;

/**
 * Clase principal para iniciar el SERVIDOR.
 * Ejecutar PRIMERO antes de los clientes.
 *
 * @author Equipo
 */
public class MainServidor {

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("   BATTLESHIP - SERVIDOR");
        System.out.println("===========================================");
        System.out.println("Iniciando servidor en puerto 5000...");

        BusEventos bus = new BusEventos(new HashMap<>());
        ServidorSocket server = new ServidorSocket(5000, bus);

        System.out.println("Servidor listo. Esperando conexiones...");
        System.out.println("-------------------------------------------");

        server.start(); // Esto bloquea y espera conexiones
    }
}
