package compartido.comunicacion;

import com.google.gson.JsonElement;

/**
 * Es como un "sobre" que usamos para mandar informacion por el bus.
 * Tiene que tipo de accion es (suscribir, publicar, unicast),
 * el nombre del evento, los datos en JSON y quien lo manda.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public class Mensaje {

    // tipo de accion
    private TipoAccion accion;

    // nombre del evento
    private String evento;

    // datos en JSON
    private JsonElement data;

    // ID del cliente
    private String idPublicador;

    // subevento para mensajes privados
    private String subEvento;

    /**
     * Constructor vacio (para Gson).
     */
    public Mensaje() {
    }

    /**
     * Constructor principal.
     */
    public Mensaje(TipoAccion accion, String evento, JsonElement data, String idPublicador) {
        this.accion = accion;
        this.evento = evento;
        this.data = data;
        this.idPublicador = idPublicador;
    }

    /**
     * Sin ID (para el servidor).
     */
    public Mensaje(TipoAccion accion, String evento, JsonElement data) {
        this.accion = accion;
        this.evento = evento;
        this.data = data;
    }

    /**
     * Constructor completo.
     */
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
