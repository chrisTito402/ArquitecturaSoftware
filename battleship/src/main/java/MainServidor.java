import buseventos.buseventos.BusEventos;
import buseventos.servidorsocket.ServidorSocket;
import config.ConfiguracionRed;
import java.net.InetAddress;

public class MainServidor {

    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("   BATTLESHIP - SERVIDOR");
        System.out.println("=================================");

        // Mostrar IPs disponibles para conexion
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            System.out.println("IP del servidor: " + localHost.getHostAddress());
            System.out.println("Nombre del host: " + localHost.getHostName());
        } catch (Exception e) {
            System.out.println("No se pudo obtener la IP local");
        }

        System.out.println("Puerto: " + ConfiguracionRed.SERVIDOR_PUERTO);
        System.out.println("---------------------------------");
        System.out.println("Para conectar desde otra maquina:");
        System.out.println("1. Edita ConfiguracionRed.java");
        System.out.println("2. Cambia SERVIDOR_HOST a la IP mostrada arriba");
        System.out.println("---------------------------------");

        BusEventos busEventos = new BusEventos();
        ServidorSocket servidor = new ServidorSocket(ConfiguracionRed.SERVIDOR_PUERTO, busEventos);

        System.out.println("Servidor listo. Esperando jugadores...");

        servidor.start();
    }
}
