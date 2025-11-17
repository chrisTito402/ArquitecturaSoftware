package pruebas;

import buseventos.Mensaje;
import buseventos.TipoAccion;
import clientesocket.ClienteSocket;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import models.entidades.Barco;
import models.entidades.Casilla;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Partida;
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
        
        casilla1[0][0].setNave(new Barco(OrientacionNave.HORIZONTAL));
        
        Tablero t1 = new Tablero(casilla1, 10, 10);
        
        Jugador j1 = new Jugador(
                "J1", 
                ColorJugador.AZUL, 
                new ArrayList<>(), 
                t1, 
                EstadoJugador.JUGANDO);
        
        // JUGADOR 2 ---------------------------------------------------
        Casilla[][] casilla2 = new Casilla[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                casilla2[i][j] = new Casilla(EstadoCasilla.AGUA, new Coordenadas(i, j));
            }
        }
        
        casilla2[0][0].setNave(new Barco(OrientacionNave.HORIZONTAL));
        
        Tablero t2 = new Tablero(casilla1, 10, 10);
        
        Jugador j2 = new Jugador(
                "J2", 
                ColorJugador.ROJO, 
                new ArrayList<>(), 
                t1, 
                EstadoJugador.JUGANDO);
        
        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(j1);
        jugadores.add(j2);
        
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
                new Cronometro()
        );
        
        ClienteSocket cliente = new ClienteSocket("localhost", 5000, null);
        ControladorServidor control = new ControladorServidor(p, cliente, new HashMap<>());
        cliente.setControl(control);
        cliente.execute();
        
        Mensaje m = new Mensaje(TipoAccion.SUSCRIBIR, "DISPARO", null, "1");
        Gson gson = new Gson();
        String json = gson.toJson(m);
        cliente.enviarMensaje(json);
    }
}
