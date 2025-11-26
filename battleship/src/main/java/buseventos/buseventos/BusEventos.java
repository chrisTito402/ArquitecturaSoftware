package buseventos.buseventos;

import buseventos.Mensaje;
import buseventos.TipoAccion;
import buseventos.servidorsocket.UserServerThread;
import com.google.gson.Gson;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author daniel
 */
public class BusEventos {

    private final Map<String, Set<UserServerThread>> eventos;
    private final Map<Integer, UserServerThread> idClientes;
    private final Map<UserServerThread, Integer> clienteIds;

    public BusEventos(Map<String, Set<UserServerThread>> mapa) {
        this.eventos = mapa;
        this.idClientes = new ConcurrentHashMap<>();
        this.clienteIds = new ConcurrentHashMap<>();
    }

    public BusEventos() {
        this.eventos = new ConcurrentHashMap<>();
        this.idClientes = new ConcurrentHashMap<>();
        this.clienteIds = new ConcurrentHashMap<>();
    }
    
    private synchronized void publicar(String evento, Mensaje mensaje) {
        System.out.println("[BusEventos] Publicando evento: " + evento);

        Set<UserServerThread> suscriptores = eventos.get(evento);
        if (suscriptores == null || suscriptores.isEmpty()) {
            System.out.println("[BusEventos] No hay suscriptores para el evento: " + evento);
            return;
        }

        Gson gson = new Gson();
        String jsonMensaje = gson.toJson(mensaje);

        // Enviar a todos los suscriptores
        suscriptores.forEach(suscriptor -> {
            try {
                suscriptor.sendMessage(jsonMensaje);
            } catch (Exception e) {
                System.err.println("[BusEventos] Error al enviar mensaje a suscriptor: " + e.getMessage());
            }
        });

        System.out.println("[BusEventos] Evento publicado a " + suscriptores.size() + " suscriptor(es)");
    }
    
    private synchronized void suscribirse(String evento, UserServerThread suscriptor) {
        System.out.println("[BusEventos] Suscribiendo cliente al evento: " + evento);

        if (suscriptor == null) {
            System.err.println("[BusEventos] Error: No se puede suscribir un cliente nulo");
            return;
        }

        // Usar computeIfAbsent para thread-safety y simplicidad
        eventos.computeIfAbsent(evento, k -> ConcurrentHashMap.newKeySet()).add(suscriptor);

        System.out.println("[BusEventos] Cliente suscrito exitosamente. Total suscriptores: "
            + eventos.get(evento).size());
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
    
    public synchronized void removeSuscriptor(UserServerThread user) {
        if (user == null) {
            System.err.println("[BusEventos] Error: No se puede remover un cliente nulo");
            return;
        }

        System.out.println("[BusEventos] Removiendo suscriptor de todos los eventos");

        int eventosAfectados = 0;
        for (Set<UserServerThread> suscriptores : eventos.values()) {
            if (suscriptores.remove(user)) {
                eventosAfectados++;
            }
        }

        // Remover de mapas de IDs
        Integer clienteId = clienteIds.remove(user);
        if (clienteId != null) {
            idClientes.remove(clienteId);
        }

        System.out.println("[BusEventos] Suscriptor removido de " + eventosAfectados + " evento(s)");
    }

    public synchronized void registrarCliente(Integer clienteId, UserServerThread cliente) {
        if (clienteId == null || cliente == null) {
            System.err.println("[BusEventos] Error: ClienteId o cliente es nulo");
            return;
        }

        idClientes.put(clienteId, cliente);
        clienteIds.put(cliente, clienteId);
        System.out.println("[BusEventos] Cliente registrado con ID: " + clienteId);
    }

    public synchronized UserServerThread getCliente(Integer clienteId) {
        return idClientes.get(clienteId);
    }

    public synchronized Integer getClienteId(UserServerThread cliente) {
        return clienteIds.get(cliente);
    }
}
