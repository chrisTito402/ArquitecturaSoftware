package builder;

import Entidades.Casilla;
import Entidades.Coordenadas;
import Entidades.Jugador;
import Entidades.Partida;
import Entidades.Tablero;
import Enums.ColorJugador;
import Enums.EstadoCasilla;
import Enums.EstadoJugador;
import Enums.EstadoPartida;
import control.IModelo;

/**
 *
 * @author daniel
 */
public class Director {
    
    public IModelo makePartida(IPartidaBuilder builder) {
        builder.setCantBarcos(0);
        builder.setCantSubmarinos(0);
        builder.setCantCruceros(0);
        builder.setCantPortaAviones(0);
        builder.setTotalNaves(0);
        builder.setEstado(EstadoPartida.EN_CURSO);
        
        return builder.getResult();
    }
    
    public void makeJugador(IJugadorBuilder builder) {
        builder.setColor(ColorJugador.AZUL);
        builder.setEstado(EstadoJugador.JUGANDO);
        builder.setNombre("Jugador1");
    }
    
    public void makeBot(IJugadorBuilder builder) {
        
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
