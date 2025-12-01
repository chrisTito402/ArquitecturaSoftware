package controllers.controller;

/**
 *
 * @author daniel
 */
public interface ManejadorRespuestaCliente {

    public void manejarMensaje(String json);

    public void onIdSet(String id);
}
