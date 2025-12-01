package pruebas;

import buseventos.buseventos.BusEventos;
import buseventos.servidorsocket.ServidorSocket;
import java.util.HashMap;

/**
 *
 * @author daniel
 */
public class PruebaBus {

    public static void main(String[] args) {
        BusEventos bus = new BusEventos(new HashMap());
        ServidorSocket server = new ServidorSocket(5000, bus);
        server.start();
    }

}
