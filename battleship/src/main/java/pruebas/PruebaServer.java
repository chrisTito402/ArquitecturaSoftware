package pruebas;

import buseventos.Mensaje;
import buseventos.TipoAccion;
import clientesocket.ClienteSocket;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.entidades.Barco;
import models.entidades.Casilla;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.entidades.Partida;
import models.entidades.Submarino;
import models.entidades.Tablero;
import models.enums.ColorJugador;
import models.enums.EstadoCasilla;
import models.enums.EstadoJugador;
import models.enums.EstadoPartida;
import models.enums.OrientacionNave;
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
        
    }
}
