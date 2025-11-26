package buseventos.buseventos;

import buseventos.IEventSuscriptor;
import buseventos.Mensaje;
import buseventos.TipoAccion;
import buseventos.servidorsocket.UserServerThread;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BusEventos {

    private final Map<String, Set<IEventSuscriptor>> eventos;
    private final Map<String, IEventSuscriptor> suscriptoresPorId;

    public BusEventos() {
        this.eventos = new ConcurrentHashMap<>();
        this.suscriptoresPorId = new ConcurrentHashMap<>();
    }
    
    private synchronized void publicar(String evento, Mensaje mensaje) {
        Set<IEventSuscriptor> suscriptores = eventos.get(evento);
        if (suscriptores == null || suscriptores.isEmpty()) {
            return;
        }

        Gson gson = new Gson();
        String jsonMensaje = gson.toJson(mensaje);

        List<IEventSuscriptor> fallidos = new ArrayList<>();

        suscriptores.forEach(suscriptor -> {
            try {
                suscriptor.recibirEvento(jsonMensaje);
            } catch (Exception e) {
                fallidos.add(suscriptor);
            }
        });

        fallidos.forEach(this::removeSuscriptor);
    }
    
    public synchronized void suscribirse(String evento, IEventSuscriptor suscriptor) {
        if (suscriptor == null) {
            return;
        }

        eventos.computeIfAbsent(evento, k -> ConcurrentHashMap.newKeySet()).add(suscriptor);
        suscriptoresPorId.put(suscriptor.getSuscriptorId(), suscriptor);
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
    
    public synchronized void removeSuscriptor(IEventSuscriptor suscriptor) {
        if (suscriptor == null) {
            return;
        }

        for (Set<IEventSuscriptor> suscriptores : eventos.values()) {
            suscriptores.remove(suscriptor);
        }

        suscriptoresPorId.remove(suscriptor.getSuscriptorId());
    }

    public synchronized void removeSuscriptor(UserServerThread user) {
        removeSuscriptor((IEventSuscriptor) user);
    }

    public synchronized IEventSuscriptor getSuscriptor(String id) {
        return suscriptoresPorId.get(id);
    }
}
