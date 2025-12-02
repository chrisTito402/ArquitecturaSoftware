package cliente;

import compartido.comunicacion.Mensaje;
import compartido.comunicacion.TipoAccion;
import compartido.comunicacion.socket.ClienteSocket;
import com.google.gson.Gson;
import cliente.controlador.ControlVista;
import cliente.controlador.Controlador;
import java.util.HashMap;
import cliente.negocio.builder.Director;
import cliente.negocio.builder.PartidaBuilder;
import cliente.negocio.IModeloCliente;
import cliente.presentacion.frames.FrmMenuPrincipal;

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

        // === EVENTOS DE LOBBY/PARTIDA ===

        // Suscribirse a jugador unido
        Mensaje m1 = new Mensaje(TipoAccion.SUSCRIBIR, "JUGADOR_UNIDO", null);
        cliente.enviarMensaje(gson.toJson(m1));

        // Suscribirse a unirse partida
        Mensaje m2 = new Mensaje(TipoAccion.SUSCRIBIR, "UNIRSE_PARTIDA", null);
        cliente.enviarMensaje(gson.toJson(m2));

        // Suscribirse a abandonar lobby
        Mensaje m3 = new Mensaje(TipoAccion.SUSCRIBIR, "ABANDONAR_LOBBY", null);
        cliente.enviarMensaje(gson.toJson(m3));

        // Suscribirse a empezar partida
        Mensaje m4 = new Mensaje(TipoAccion.SUSCRIBIR, "EMPEZAR_PARTIDA", null);
        cliente.enviarMensaje(gson.toJson(m4));

        // === EVENTOS DE COLOCACION DE NAVES ===

        // Suscribirse a resultado de agregar nave
        Mensaje m5 = new Mensaje(TipoAccion.SUSCRIBIR, "RESULTADO_ADD_NAVE", null);
        cliente.enviarMensaje(gson.toJson(m5));

        // Suscribirse a confirmacion de tablero
        Mensaje m6 = new Mensaje(TipoAccion.SUSCRIBIR, "CONFIRMAR_TABLERO", null);
        cliente.enviarMensaje(gson.toJson(m6));

        // Suscribirse a tableros listos (ambos confirmaron)
        Mensaje m7 = new Mensaje(TipoAccion.SUSCRIBIR, "TABLEROS_LISTOS", null);
        cliente.enviarMensaje(gson.toJson(m7));

        // === EVENTOS DE BATALLA ===

        // Suscribirse a resultados de disparo
        Mensaje m8 = new Mensaje(TipoAccion.SUSCRIBIR, "RESULTADO_DISPARO", null);
        cliente.enviarMensaje(gson.toJson(m8));

        // Suscribirse a cambio de turno
        Mensaje m9 = new Mensaje(TipoAccion.SUSCRIBIR, "CAMBIO_TURNO", null);
        cliente.enviarMensaje(gson.toJson(m9));

        // Suscribirse a tiempo agotado
        Mensaje m10 = new Mensaje(TipoAccion.SUSCRIBIR, "TIEMPO_AGOTADO", null);
        cliente.enviarMensaje(gson.toJson(m10));

        // Suscribirse a turno inicial
        Mensaje m13 = new Mensaje(TipoAccion.SUSCRIBIR, "TURNO_INICIAL", null);
        cliente.enviarMensaje(gson.toJson(m13));

        // === EVENTOS DE FIN DE PARTIDA ===

        // Suscribirse a abandono de partida
        Mensaje m11 = new Mensaje(TipoAccion.SUSCRIBIR, "JUGADOR_ABANDONO", null);
        cliente.enviarMensaje(gson.toJson(m11));

        // Suscribirse a fin de partida (victoria/derrota)
        Mensaje m12 = new Mensaje(TipoAccion.SUSCRIBIR, "FIN_PARTIDA", null);
        cliente.enviarMensaje(gson.toJson(m12));

        System.out.println("[CLIENTE] Suscrito a 13 eventos del bus");
    }
}
