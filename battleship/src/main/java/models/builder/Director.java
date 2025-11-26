package models.builder;

import java.util.ArrayList;
import models.entidades.Casilla;
import models.entidades.Coordenadas;
import models.entidades.Partida;
import models.enums.EstadoCasilla;
import models.enums.EstadoPartida;

public class Director {

    public Partida makePartida(IPartidaBuilder builder) {
        builder.setCantBarcos(0);
        builder.setCantSubmarinos(0);
        builder.setCantCruceros(0);
        builder.setCantPortaAviones(0);
        builder.setTotalNaves(0);
        builder.setEstado(EstadoPartida.POR_EMPEZAR);
        builder.setJugadores(new ArrayList<>());
        builder.setSuscriptores(new ArrayList<>());

        return builder.getResult();
    }

    public void makeJugador(IJugadorBuilder builder, String nombre) {
        builder.setNombre(nombre);
        builder.setNaves(new ArrayList<>());
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
