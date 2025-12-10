package servidor.negocio;

import servidor.bus.BusEventos;
import compartido.comunicacion.socket.ServidorSocket;
import java.util.HashMap;

/**
 * Clase auxiliar para iniciar el servidor desde otro lugar.
 * Checa si ya esta corriendo para no iniciarlo dos veces.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
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
