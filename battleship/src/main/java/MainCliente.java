import buseventos.Mensaje;
import buseventos.TipoAccion;
import clientesocket.ClienteSocket;
import com.google.gson.Gson;
import controllers.controller.ControlVista;
import controllers.controller.Controlador;
import java.util.ArrayList;
import java.util.HashMap;
import models.builder.Director;
import models.builder.PartidaBuilder;
import models.control.IModeloCliente;
import views.frames.FrmMenuPrincipal;

/**
 * Clase principal para iniciar el CLIENTE.
 * Ejecutar DESPUES de iniciar el servidor.
 *
 * @author Equipo
 */
public class MainCliente {

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("   BATTLESHIP - CLIENTE");
        System.out.println("===========================================");

        // 1. Crear el modelo del cliente usando el Builder
        Director director = new Director();
        IModeloCliente modelo = director.makePartida(new PartidaBuilder());

        // 2. Crear conexion al servidor
        ClienteSocket clienteSocket = new ClienteSocket("localhost", 5000, null);

        // 3. Crear el controlador
        Controlador controlador = new Controlador(modelo, clienteSocket, new HashMap<>());
        clienteSocket.setControl(controlador);

        // 4. Configurar ControlVista (singleton)
        ControlVista controlVista = ControlVista.getInstancia();
        controlVista.setControl(controlador);

        // 5. Suscribir la vista al modelo
        modelo.suscribirAPartida(controlVista);

        // 6. Conectar al servidor
        System.out.println("Conectando al servidor...");
        clienteSocket.execute();

        // 7. Suscribirse a los eventos del servidor
        suscribirseAEventos(clienteSocket);

        System.out.println("Conexion establecida!");
        System.out.println("-------------------------------------------");

        // 8. Mostrar menu principal
        java.awt.EventQueue.invokeLater(() -> {
            new FrmMenuPrincipal().setVisible(true);
        });
    }

    private static void suscribirseAEventos(ClienteSocket cliente) {
        Gson gson = new Gson();

        // Suscribirse a resultados de disparo
        Mensaje m1 = new Mensaje(TipoAccion.SUSCRIBIR, "RESULTADO_DISPARO", null);
        cliente.enviarMensaje(gson.toJson(m1));

        // Suscribirse a jugador unido
        Mensaje m2 = new Mensaje(TipoAccion.SUSCRIBIR, "JUGADOR_UNIDO", null);
        cliente.enviarMensaje(gson.toJson(m2));

        // Suscribirse a abandono de partida
        Mensaje m3 = new Mensaje(TipoAccion.SUSCRIBIR, "JUGADOR_ABANDONO", null);
        cliente.enviarMensaje(gson.toJson(m3));

        // Suscribirse a empezar partida
        Mensaje m4 = new Mensaje(TipoAccion.SUSCRIBIR, "EMPEZAR_PARTIDA", null);
        cliente.enviarMensaje(gson.toJson(m4));

        // Suscribirse a unirse partida
        Mensaje m5 = new Mensaje(TipoAccion.SUSCRIBIR, "UNIRSE_PARTIDA", null);
        cliente.enviarMensaje(gson.toJson(m5));

        // Suscribirse a abandonar lobby
        Mensaje m6 = new Mensaje(TipoAccion.SUSCRIBIR, "ABANDONAR_LOBBY", null);
        cliente.enviarMensaje(gson.toJson(m6));
    }
}
