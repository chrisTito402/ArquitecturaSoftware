package pruebas;

import clientesocket.ClienteSocket;
import controllers.controller.ControlVista;
import controllers.controller.Controlador;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import models.control.ControlModelo;
import models.entidades.Jugador;
import models.entidades.Partida;
import models.enums.ColorJugador;
import models.enums.EstadoJugador;
import models.enums.EstadoPartida;
import servidor.controlador.ControladorServidor;
import servidor.cronometro.Cronometro;
import views.DTOs.JugadorDTO;
import views.DTOs.TableroDTO;
import views.frames.FrmLobby;

/**
 *
 * @author Knocmare
 */
public class PruebaLobby {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Jugador j1 = new Jugador(
                "J1", 
                ColorJugador.AZUL, 
                new ArrayList<>(), 
                null, 
                EstadoJugador.JUGANDO);
        
        Jugador j2 = new Jugador(
                "J2", 
                ColorJugador.ROJO, 
                new ArrayList<>(), 
                null, 
                EstadoJugador.JUGANDO);
        
        List<Jugador> jugadores = new ArrayList<>();
        
        Cronometro cronometro = new Cronometro(5000);
        Partida p = new Partida(
                j1, 
                jugadores, 
                1, 
                0, 
                0, 
                0, 
                0, 
                EstadoPartida.POR_EMPEZAR, 
                new ArrayList<>(),
                cronometro
        );
        cronometro.setPartida(p);
        
        ClienteSocket cliente = new ClienteSocket("localhost", 5000, null);
        ControladorServidor control = new ControladorServidor(p, cliente, new HashMap<>());
        cliente.setControl(control);
        cliente.execute();
        
        p.unirsePartida(j2);
        
        FrmLobby lobby = new FrmLobby(p);
        lobby.setVisible(true);
    }
    
}
