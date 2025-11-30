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
        System.out.println(evento);
        if (!eventos.containsKey(evento)) {
            eventos.put(evento, new HashSet<>());
        }
        eventos.get(evento).forEach(s -> s.sendMessage(jsonMensaje));
    }
    
    private void suscribirse(String evento, UserServerThread suscriptor) {
        System.out.println("SUSCRIBIR");
        if (eventos.containsKey(evento)) {
            if (!eventos.get(evento).contains(suscriptor)) {
                eventos.get(evento).add(suscriptor);
            }
        } else {
            eventos.put(evento, new HashSet<>());
            eventos.get(evento).add(suscriptor);
        }
    }
    
    public void manejarEvento(String json, UserServerThread cliente) {
        Gson gson = new Gson();
        Mensaje mensaje = gson.fromJson(json, Mensaje.class);
        System.out.println("ID DEL CLIENTE: " + mensaje.getIdPublicador());
        
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
    
    public void addNewClient(String event, UserServerThread client) {
        System.out.println(event);
        if (!eventos.containsKey(event)) {
            eventos.put(event, new HashSet<>());
            eventos.get(event).add(client);
            System.out.println("ID ASIGNADO");
        }
    }
}
