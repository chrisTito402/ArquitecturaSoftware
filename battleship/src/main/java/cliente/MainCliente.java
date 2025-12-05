
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
import javax.swing.JOptionPane;

/**
 * El main del cliente. Aqui es donde arranca todo el programa
 * del lado del jugador. Arma el modelo con el Builder, se conecta
 * al servidor, configura el controlador y abre el menu principal.
 *
 * OJO: el servidor tiene que estar corriendo antes de ejecutar esto,
 * si no va a tronar la conexion.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public class MainCliente {

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("   BATTLESHIP - CLIENTE");
        System.out.println("===========================================");

        // 1. Crear el modelo del cliente usando el Builder
        Director director = new Director();
        IModeloCliente modelo = director.makePartida(new PartidaBuilder());

        // 2. Pedir IP del servidor al usuario
        String ip = JOptionPane.showInputDialog(
                null,
                "Ingrese la IP del servidor:\n(Dejar 'localhost' si el servidor está en esta computadora)",
                "localhost"
        );

        // Si el usuario cancela o deja vacío, salir
        if (ip == null || ip.trim().isEmpty()) {
            System.out.println("Conexión cancelada por el usuario.");
            System.exit(0);
        }

        // 3. Crear conexion al servidor
        System.out.println("Conectando a: " + ip.trim() + ":5000");
        ClienteSocket clienteSocket = new ClienteSocket(ip.trim(), 5000, null);

        // 4. Crear el controlador
        Controlador controlador = new Controlador(modelo, clienteSocket, new HashMap<>());
        clienteSocket.setControl(controlador);

        // 5. Configurar ControlVista (singleton)
        ControlVista controlVista = ControlVista.getInstancia();
        controlVista.setControl(controlador);

        // 6. Suscribir la vista al modelo
        modelo.suscribirAPartida(controlVista);

        // 7. Conectar al servidor
        System.out.println("Conectando al servidor...");
        boolean conexionExitosa = clienteSocket.execute();

        // Verificar si la conexión fue exitosa
        if (!conexionExitosa) {
            JOptionPane.showMessageDialog(
                    null,
                    "No se pudo conectar al servidor.\n\n" +
                    "Verifique que:\n" +
                    "1. El servidor esté ejecutándose\n" +
                    "2. La IP sea correcta: " + ip.trim() + "\n" +
                    "3. El puerto 5000 esté disponible",
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }

        // 8. Suscribirse a los eventos del servidor
        suscribirseAEventos(clienteSocket);

        System.out.println("Conexion establecida!");
        System.out.println("-------------------------------------------");

        // 9. Mostrar menu principal
        java.awt.EventQueue.invokeLater(() -> {
            new FrmMenuPrincipal().setVisible(true);
        });
    }

    /**
     * Suscribe al cliente a los eventos del servidor.
     *
     * Registra el cliente en el bus de eventos para recibir
     * notificaciones de lobby, colocacion de naves, batalla y fin de partida.
     *
     * @param cliente el socket del cliente que se va a suscribir
     */
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

        // Suscribirse a jugador listo
        Mensaje m14 = new Mensaje(TipoAccion.SUSCRIBIR, "JUGADOR_LISTO", null);
        cliente.enviarMensaje(gson.toJson(m14));

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

        // Suscribirse a abandono del lobby
        Mensaje m15 = new Mensaje(TipoAccion.SUSCRIBIR, "ABANDONAR_LOBBY", null);
        cliente.enviarMensaje(gson.toJson(m15));

        // Suscribirse a fin de partida (victoria/derrota)
        Mensaje m12 = new Mensaje(TipoAccion.SUSCRIBIR, "FIN_PARTIDA", null);
        cliente.enviarMensaje(gson.toJson(m12));

        System.out.println("[CLIENTE] Suscrito a 15 eventos del bus");
    }
}
