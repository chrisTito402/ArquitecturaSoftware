package buseventos;

/**
 *
 * @author daniel
 */
public class Prueba {
    
    public static void main(String[] args) {
        BusEventos bus = new BusEventos();
        ServidorSocket server = new ServidorSocket(5000, bus);
        server.start();
    }
}
