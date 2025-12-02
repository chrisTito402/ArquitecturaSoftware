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
 *
 * @author daniel
 */
public class Director {

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

    public void makeJugador(IJugadorBuilder builder) {
        builder.setColor(ColorJugador.AZUL);
        builder.setEstado(EstadoJugador.JUGANDO);
        builder.setNombre("Jugador1");
    }

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
