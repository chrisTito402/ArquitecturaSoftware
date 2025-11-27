package buseventos.buseventos;

import buseventos.Mensaje;
import buseventos.TipoAccion;
import buseventos.servidorsocket.UserServerThread;
import com.google.gson.Gson;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author daniel
 */
public class BusEventos {
    
    private Map<String, Set<UserServerThread>> eventos;

    public BusEventos(Map mapa) {
        this.eventos = mapa;
    }
    
    private void publicar(String evento, Mensaje mensaje) {
        System.out.println("PUBLICAR");
        Gson gson = new Gson();
        String jsonMensaje = gson.toJson(mensaje);
        eventos.get(evento).forEach(s -> s.sendMessage(jsonMensaje));
        //System.out.println(eventos);
    }
    
    private void suscribirse(String evento, UserServerThread suscriptor) {
        System.out.println("SUSCRIBIR");
        if (eventos.containsKey(evento)) {
            eventos.get(evento).add(suscriptor);
        } else {
            eventos.put(evento, new HashSet<>());
            eventos.get(evento).add(suscriptor);
        }
    }
    
    public void manejarEvento(String json, UserServerThread cliente) {
        Gson gson = new Gson();
        Mensaje mensaje = gson.fromJson(json, Mensaje.class);
        
        if (mensaje.getAccion() == TipoAccion.SUSCRIBIR) {
            suscribirse(mensaje.getEvento(), cliente);
        } else if (mensaje.getAccion() == TipoAccion.PUBLICAR) {
            publicar(mensaje.getEvento(), mensaje);
        }
    }
    
    public void removeSuscriptor(UserServerThread user) {
        if (user != null) {
            for (Set<UserServerThread> users: eventos.values()) {
                users.remove(user);
            }
        }
    }
    
}
