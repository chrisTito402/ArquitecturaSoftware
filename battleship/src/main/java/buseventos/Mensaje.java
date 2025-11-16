package buseventos;

/**
 *
 * @author daniel
 */
public class Mensaje {
    
    private TipoAccion accion;
    private String evento;
    private Object data;
    private String idPublicador;

    public Mensaje() {
    }

    public Mensaje(TipoAccion accion, String evento, Object data, String idPublicador) {
        this.accion = accion;
        this.evento = evento;
        this.data = data;
        this.idPublicador = idPublicador;
    }

    public Mensaje(TipoAccion accion, String evento) {
        this.accion = accion;
        this.evento = evento;
    }

    public TipoAccion getAccion() {
        return accion;
    }

    public void setAccion(TipoAccion accion) {
        this.accion = accion;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getIdPublicador() {
        return idPublicador;
    }

    public void setIdPublicador(String idPublicador) {
        this.idPublicador = idPublicador;
    }

    @Override
    public String toString() {
        return "Mensaje{" + "accion=" + accion + ", evento=" + evento + ", data=" + data + ", idPublicador=" + idPublicador + '}';
    }
    
}
