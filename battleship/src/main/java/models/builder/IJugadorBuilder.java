package models.builder;

import models.entidades.Nave;
import models.enums.ColorJugador;
import models.enums.EstadoJugador;
import java.util.List;
import models.entidades.Jugador;
import models.entidades.Tablero;

/**
 *
 * @author daniel
 */
public interface IJugadorBuilder {
    
    public void setNombre(String nombre);
    public void setColor(ColorJugador color);
    public void setNaves(List<Nave> naves);
    public void setTablero(Tablero tablero);
    public void setEstado(EstadoJugador estado);
    public Jugador getResult();

}
