package servidor;

import servidor.bus.BusEventos;
import compartido.comunicacion.socket.ServidorSocket;
import java.util.HashMap;

/**
 * El main del servidor. Primero hay que correr esto y luego los clientes.
 * Crea el bus de eventos y abre el socket en el puerto 5000 para que
 * los clientes se puedan conectar.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
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
