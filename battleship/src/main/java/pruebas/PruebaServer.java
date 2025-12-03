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
        Casilla[][] casilla1 = new Casilla[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                casilla1[i][j] = new Casilla(EstadoCasilla.AGUA, new Coordenadas(i, j));
            }
        }

        Nave n1 = new Barco(OrientacionNave.HORIZONTAL);
        casilla1[0][0].setNave(n1);

        Tablero t1 = new Tablero(casilla1, 10, 10);

        Jugador j1 = new Jugador(
                "J1",
                ColorJugador.AZUL,
                new ArrayList<>(),
                t1,
                EstadoJugador.JUGANDO);
        j1.getNaves().add(n1);

        // JUGADOR 2 ---------------------------------------------------
        Casilla[][] casilla2 = new Casilla[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                casilla2[i][j] = new Casilla(EstadoCasilla.AGUA, new Coordenadas(i, j));
            }
        }

        Nave n2 = new Barco(OrientacionNave.HORIZONTAL);
        casilla2[0][0].setNave(n2);

        Tablero t2 = new Tablero(casilla2, 10, 10);

        Jugador j2 = new Jugador(
                "J2",
                ColorJugador.ROJO,
                new ArrayList<>(),
                t2,
                EstadoJugador.JUGANDO);
        j2.getNaves().add(n2);

        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(j1);
        jugadores.add(j2);

        Cronometro cronometro = new Cronometro(5000);
        Partida p = new Partida(
                j1,
                jugadores,
                1,
                0,
                0,
                0,
                0,
                EstadoPartida.EN_CURSO,
                new ArrayList<>(),
                cronometro
        );
        cronometro.setPartida(p);

        ClienteSocket cliente = new ClienteSocket("localhost", 5000, null);
        ControladorServidor control = new ControladorServidor(p, cliente, new HashMap<>());
        cliente.setControl(control);
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
        
//        Mensaje m = new Mensaje(TipoAccion.SUSCRIBIR, "ADD_NAVE", null, "1");
//        Gson gson = new Gson();
//        String json = gson.toJson(m);
//        cliente.enviarMensaje(json);
//        List<Coordenadas> cords = new ArrayList<>();
//        cords.add(
//                new Coordenadas(2, 1)
//        );
//        cords.add(
//                new Coordenadas(3, 1)
//        );
//        
//        System.out.println("ADD NAVE: " + p.addNave(
//                    j1, 
//                    new Submarino(OrientacionNave.VERTICAL), 
//                    cords
//                    ));
    }
}
