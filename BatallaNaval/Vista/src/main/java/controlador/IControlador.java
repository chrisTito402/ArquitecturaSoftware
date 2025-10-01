package controlador;

import DTOs.CoordenadasDTO;
import DTOs.JugadorDTO;

/**
 *
 * @author daniel
 */
public interface IControlador {
    public void realizarDisparo(CoordenadasDTO c, JugadorDTO j);
}
