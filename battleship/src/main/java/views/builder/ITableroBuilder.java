package views.builder;

import models.entidades.Casilla;

/**
 *
 * @author daniel
 */
public interface ITableroBuilder {
    
    public void setCasillas(Casilla[][] casillas);
    public void setLimiteX(int limiteX);
    public void setLimiteY(int limiteY);
}
