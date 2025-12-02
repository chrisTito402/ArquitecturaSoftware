package servidor.negocio;

import servidor.bus.BusEventos;
import compartido.comunicacion.socket.ServidorSocket;
import java.util.HashMap;

/**
 *
 * @author Angel
 */
public class ServidorManager {

    private static boolean iniciado = false;

    public static void iniciar() {
        if (!iniciado) {
            BusEventos bus = new BusEventos(new HashMap());
            ServidorSocket server = new ServidorSocket(5000, bus);
            server.start();
            iniciado = true;
        } else {
            System.out.println("Servidor ya estaba activo.");
        }
    }
}
