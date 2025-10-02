package controlador;

import DTOs.CoordenadasDTO;
import DTOs.JugadorDTO;
import Entidades.Coordenadas;
import Entidades.Jugador;
import control.IModelo;

/**
 *
 * @author daniel
 */
public class Controlador implements IControlador{
    
    private IModelo partida;

    public Controlador(IModelo partida) {
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
