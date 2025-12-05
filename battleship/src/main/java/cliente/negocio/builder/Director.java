package cliente.negocio.builder;

import java.util.ArrayList;
import compartido.entidades.Casilla;
import compartido.entidades.Coordenadas;
import compartido.entidades.Tablero;
import compartido.enums.ColorJugador;
import compartido.enums.EstadoCasilla;
import compartido.enums.EstadoJugador;
import compartido.enums.EstadoPartida;
import cliente.negocio.IModeloCliente;

/**
 * El Director del patron Builder, es el que sabe en que orden
 * se tienen que llamar los metodos del builder para armar los objetos.
 *
 * Lo usamos porque los constructores de Partida, Jugador y Tablero
 * tenian como 10 parametros y era un relajo. Asi queda mas limpio.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public class Director {

    /**
     * Construye una partida con valores iniciales.
     */
    public IModeloCliente makePartida(IPartidaBuilder builder) {
        builder.setCantBarcos(0);
        builder.setCantSubmarinos(0);
        builder.setCantCruceros(0);
        builder.setCantPortaAviones(0);
        builder.setTotalNaves(0);
        builder.setEstado(EstadoPartida.EN_CURSO);
        builder.setJugadores(new ArrayList<>());
        builder.setSuscriptores(new ArrayList<>());

        return builder.getResult();
    }

    /**
     * Construye un jugador con valores por defecto.
     */
    public void makeJugador(IJugadorBuilder builder) {
        builder.setColor(ColorJugador.AZUL);
        builder.setEstado(EstadoJugador.JUGANDO);
        builder.setNombre("Jugador1");
    }

    /**
     * Construye un tablero 10x10.
     */
    public void makeTablero(ITableroBuilder builder) {
        builder.setLimiteX(10);
        builder.setLimiteY(10);
        Casilla[][] casillas = new Casilla[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Coordenadas c = new Coordenadas(i, j);
                casillas[i][j] = new Casilla(EstadoCasilla.NO_DISPARADO, c);
            }
        }
        builder.setCasillas(casillas);
    }
}
