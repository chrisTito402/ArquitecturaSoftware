package models.services;

import models.builder.Director;
import models.builder.TableroBuilder;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.entidades.Tablero;
import models.enums.ResultadoAddNave;
import models.validators.ValidadorNave;
import java.util.List;

public class TableroService implements ITableroService {

    private static final int TOTAL_NAVES_REQUERIDAS = 11;
    private final ValidadorNave validadorNave;

    public TableroService() {
        this.validadorNave = new ValidadorNave();
    }

    public TableroService(ValidadorNave validadorNave) {
        this.validadorNave = validadorNave;
    }

    @Override
    public Tablero crearTablero(int filas, int columnas) {
        Director director = new Director();
        TableroBuilder builder = new TableroBuilder();
        director.makeTablero(builder, filas, columnas);
        return builder.getResult();
    }

    @Override
    public ResultadoAddNave colocarNave(Tablero tablero, Jugador jugador, Nave nave,
                                         List<Coordenadas> coordenadas, List<Jugador> jugadores) {
        if (tablero == null) {
            return ResultadoAddNave.JUGADOR_NO_ENCONTRADO;
        }

        ResultadoAddNave resultado = validadorNave.validarCompleto(jugador, jugadores, nave, coordenadas, tablero);

        if (resultado != null) {
            return resultado;
        }

        tablero.addNave(nave, coordenadas);
        jugador.getNaves().add(nave);

        return ResultadoAddNave.NAVE_AÑADIDA;
    }

    @Override
    public boolean tableroCompleto(Jugador jugador, int totalNavesRequeridas) {
        if (jugador == null || jugador.getNaves() == null) {
            return false;
        }
        return jugador.getNaves().size() >= totalNavesRequeridas;
    }

    @Override
    public void inicializarTablerosJugadores(List<Jugador> jugadores) {
        Director director = new Director();
        for (Jugador jugador : jugadores) {
            if (jugador.getTablero() == null) {
                TableroBuilder builder = new TableroBuilder();
                director.makeTablero(builder);
                Tablero tablero = builder.getResult();
                jugador.setTablero(tablero);
            }
        }
    }

    public int getTotalNavesRequeridas() {
        return TOTAL_NAVES_REQUERIDAS;
    }
}
