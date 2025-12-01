package buseventos;

import com.google.gson.JsonElement;

/**
 *
 * @author daniel
 */
public class Mensaje {

    private TipoAccion accion;
    private String evento;
    private JsonElement data;
    private String idPublicador;
    private String subEvento;

    public Mensaje() {
    }

    public Mensaje(TipoAccion accion, String evento, JsonElement data, String idPublicador) {
        this.accion = accion;
        this.evento = evento;
        this.data = data;
        this.idPublicador = idPublicador;
    }

    public Mensaje(TipoAccion accion, String evento, JsonElement data) {
        this.accion = accion;
        this.evento = evento;
        this.data = data;
    }

    public Mensaje(TipoAccion accion, String evento, JsonElement data, String idPublicador, String subEvento) {
        this.accion = accion;
        this.evento = evento;
        this.data = data;
        this.idPublicador = idPublicador;
        this.subEvento = subEvento;
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

    public JsonElement getData() {
        return data;
    }

    public void setData(JsonElement data) {
        this.data = data;
    }

    public String getIdPublicador() {
        return idPublicador;
    }

    public void setIdPublicador(String idPublicador) {
        this.idPublicador = idPublicador;
    }

    public String getSubEvento() {
        return subEvento;
    }

    public void setSubEvento(String subEvento) {
        this.subEvento = subEvento;
    }

    @Override
    public String toString() {
        return "Mensaje{" + "accion=" + accion + ", evento=" + evento + ", data=" + data + ", idPublicador=" + idPublicador + ", subEvento=" + subEvento + '}';
    }

}
