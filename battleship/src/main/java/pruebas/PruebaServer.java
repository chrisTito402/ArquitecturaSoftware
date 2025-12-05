package pruebas;

import buseventos.Mensaje;
import buseventos.TipoAccion;
import clientesocket.ClienteSocket;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import models.entidades.Partida;
import models.enums.EstadoPartida;
import servidor.controlador.ControladorServidor;
import servidor.cronometro.Cronometro;

/**
 *
 * @author daniel
 */
public class PruebaServer {

    public static void main(String[] args) {

        Cronometro cronometro = new Cronometro(5000);
        Partida p = new Partida(
                null,
                new ArrayList<>(),
                3,
                4,
                2,
                2,
                11,
                EstadoPartida.POR_EMPEZAR,
                new ArrayList<>(),
                cronometro
        );
        cronometro.setPartida(p);

        ClienteSocket cliente = new ClienteSocket("localhost", 5000, null);
        ControladorServidor control = new ControladorServidor(p, cliente, new HashMap<>(), new HashMap<>());
        cliente.setControl(control);
        p.setNotificador(control);
        cliente.execute();

        Mensaje m = new Mensaje(TipoAccion.SUSCRIBIR, "DISPARO", null);
        Gson gson = new Gson();
        String json = gson.toJson(m);
        cliente.enviarMensaje(json);
        

        m = new Mensaje(TipoAccion.SUSCRIBIR, "ADD_NAVE", null);
        gson = new Gson();
        json = gson.toJson(m);
        cliente.enviarMensaje(json);
        
        m = new Mensaje(TipoAccion.SUSCRIBIR, "CONFIRMAR_NAVES", null);
        gson = new Gson();
        json = gson.toJson(m);
        cliente.enviarMensaje(json);

        m = new Mensaje(TipoAccion.SUSCRIBIR, "ABANDONAR_PARTIDA", null);
        gson = new Gson();
        json = gson.toJson(m);
        cliente.enviarMensaje(json);
        
        m = new Mensaje(TipoAccion.SUSCRIBIR, "UNIRSE_PARTIDA", null);
        gson = new Gson();
        json = gson.toJson(m);
        cliente.enviarMensaje(json);
        
        m = new Mensaje(TipoAccion.SUSCRIBIR, "EMPEZAR_PARTIDA", null);
        gson = new Gson();
        json = gson.toJson(m);
        cliente.enviarMensaje(json);
        
        m = new Mensaje(TipoAccion.SUSCRIBIR, "OBTENER_JUGADOR_ENEMIGO", null);
        gson = new Gson();
        json = gson.toJson(m);
        cliente.enviarMensaje(json);
        
    }
}
