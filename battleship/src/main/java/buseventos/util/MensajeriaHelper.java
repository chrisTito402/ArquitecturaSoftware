package buseventos.util;

import buseventos.Mensaje;
import buseventos.TipoAccion;
import com.google.gson.Gson;

public class MensajeriaHelper {

    private static final Gson gson = new Gson();

    public static String crearMensajeJSON(String evento, Object datos) {
        return crearMensajeJSON(evento, datos, null);
    }

    public static String crearMensajeJSON(String evento, Object datos, String idPublicador) {
        Mensaje mensaje = new Mensaje(
            TipoAccion.PUBLICAR,
            evento,
            gson.toJsonTree(datos),
            idPublicador
        );
        return gson.toJson(mensaje);
    }

    public static Mensaje parsearMensaje(String json) {
        return gson.fromJson(json, Mensaje.class);
    }

    public static <T> T extraerDatos(Mensaje mensaje, Class<T> tipo) {
        return gson.fromJson(mensaje.getData(), tipo);
    }

    public static Gson getGson() {
        return gson;
    }
}
