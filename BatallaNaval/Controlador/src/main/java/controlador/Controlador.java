package controlador;

import DTOs.CoordenadasDTO;
import DTOs.JugadorDTO;
import Entidades.Coordenadas;
import Entidades.Jugador;
import Entidades.Partida;

/**
 *
 * @author daniel
 */
public class Controlador implements IControlador{
    
    private Partida partida;

    public Controlador(Partida partida) {
        this.partida = partida;
    }
    
    @Override
    public void realizarDisparo(CoordenadasDTO c, JugadorDTO j) {
        Coordenadas coordenadas = new Coordenadas(
                c.getX(),
                c.getY()
        );
        Jugador jugador = new Jugador(
                j.getNombre(),
                j.getColor(),
                j.getEstado()
        );
        
        partida.realizarDisparo(coordenadas, jugador);
    }
}
