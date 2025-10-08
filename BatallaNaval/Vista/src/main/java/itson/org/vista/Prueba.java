package itson.org.vista;

import DTOs.CoordenadasDTO;
import DTOs.JugadorDTO;
import Entidades.Barco;
import Entidades.Bot;
import Entidades.Jugador;
import Entidades.Partida;
import Entidades.Tablero;
import Enums.ColorJugador;
import Enums.EstadoJugador;
import Enums.OrientacionNave;
import builder.Director;
import builder.JugadorBuilder;
import builder.PartidaBuilder;
import builder.TableroBuilder;
import control.IModelo;
import controlador.ControlVista;
import controlador.Controlador;
import java.util.Arrays;

/**
 *
 * @author daniel
 */
public class Prueba {
    public static void main(String[] args) {
        Controlador controlador = new Controlador();
        ControlVista controlVista = ControlVista.getInstancia();
        controlVista.setControl(new Controlador());
        
        PartidaBuilder builder = new PartidaBuilder();
        Jugador j1 = new Jugador("J1", ColorJugador.AZUL, EstadoJugador.JUGANDO);
        controlVista.crearPartida(builder, j1);
        
        controlVista.addJugador(new Bot("Bot"));
        
        controlVista.crearTableros();
        
        JugadorDTO j = new JugadorDTO("J1", ColorJugador.AZUL, EstadoJugador.JUGANDO);
        
        controlVista.addNave(
                j,
                new Barco(OrientacionNave.HORIZONTAL),
                Arrays.asList(new CoordenadasDTO(0, 0))
        );
        
        
        
        
//        JugadorBuilder jugadorBuilder = new JugadorBuilder();
//        TableroBuilder tableroBuilder = new TableroBuilder();
        
//        d.makeJugador(jugadorBuilder);
//        d.makeTablero(tableroBuilder);
        
//        Jugador jugador = jugadorBuilder.getResult();
//        Tablero tablero = tableroBuilder.getResult();
//        
//        jugador.setTablero(tablero);
//        partida.setJugadores(Arrays.asList(jugador));
        
    }
    
}
