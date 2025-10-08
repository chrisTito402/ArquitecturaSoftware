package views.builder;

import models.entidades.Nave;
import models.enums.ColorJugador;
import models.enums.EstadoJugador;
import java.util.List;

/**
 *
 * @author daniel
 */
public interface IJugadorBuilder {
    
    public void setNombre(String nombre);
    public void setColor(ColorJugador color);
    public void setNaves(List<Nave> naves);
    public void setTablero();
    public void setEstado(EstadoJugador estado);
}
