package controlador;

import DTOs.CoordenadasDTO;
import DTOs.JugadorDTO;
import Entidades.Jugador;
import Entidades.Nave;
import builder.IPartidaBuilder;
import java.util.List;

/**
 *
 * @author daniel
 */
public interface IControlador {
    public void realizarDisparo(CoordenadasDTO c, JugadorDTO j);
    public String crearPartida(IPartidaBuilder builder, Jugador j);
    public boolean addNave(JugadorDTO jugador, Nave nave, List<CoordenadasDTO> coordenadas);
    public void addJugador(Jugador j);
    public void crearTableros();
}
