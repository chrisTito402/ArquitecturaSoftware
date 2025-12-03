package views.frames;

import java.awt.Rectangle;
import models.enums.OrientacionNave;
import views.DTOs.TipoNaveDTO;

/**
 *
 * @author daniel
 */
public class RectangleNave {
    
    private TipoNaveDTO nave;
    private OrientacionNave orientacion;
    private Rectangle regtRectangle;

    public RectangleNave() {
    }

    public RectangleNave(TipoNaveDTO nave, OrientacionNave orientacion, Rectangle regtRectangle) {
        this.nave = nave;
        this.orientacion = orientacion;
        this.regtRectangle = regtRectangle;
    }

    public TipoNaveDTO getNave() {
        return nave;
    }

    public void setNave(TipoNaveDTO nave) {
        this.nave = nave;
    }

    public OrientacionNave getOrientacion() {
        return orientacion;
    }

    public void setOrientacion(OrientacionNave orientacion) {
        this.orientacion = orientacion;
    }

    public Rectangle getRegtRectangle() {
        return regtRectangle;
    }

    public void setRegtRectangle(Rectangle regtRectangle) {
        this.regtRectangle = regtRectangle;
    }

    
}
