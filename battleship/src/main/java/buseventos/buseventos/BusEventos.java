package buseventos.buseventos;

import buseventos.IBusEventos;
import buseventos.IEventSuscriptor;
import buseventos.Mensaje;
import buseventos.TipoAccion;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BusEventos implements IBusEventos {

    private static final Gson GSON = new Gson();

    private final Map<String, Set<IEventSuscriptor>> eventos;
    private final Map<String, IEventSuscriptor> suscriptoresPorId;

    public BusEventos() {
        this.eventos = new ConcurrentHashMap<>();
        this.suscriptoresPorId = new ConcurrentHashMap<>();
    }

    @Override
    public void publicar(String evento, Mensaje mensaje) {
        Set<IEventSuscriptor> suscriptores = eventos.get(evento);
        if (suscriptores == null || suscriptores.isEmpty()) {
            return;
        }

        String jsonMensaje = GSON.toJson(mensaje);
        List<IEventSuscriptor> fallidos = new ArrayList<>();

        for (IEventSuscriptor suscriptor : suscriptores) {
            try {
                suscriptor.recibirEvento(jsonMensaje);
            } catch (Exception e) {
                System.err.println("[BusEventos] Error al enviar evento '" + evento
                        + "' a suscriptor " + suscriptor.getSuscriptorId() + ": " + e.getMessage());
                fallidos.add(suscriptor);
            }
        }

        fallidos.forEach(this::removeSuscriptor);
    }

    @Override
    public void suscribirse(String evento, IEventSuscriptor suscriptor) {
        if (suscriptor == null) {
            return;
        }

        eventos.computeIfAbsent(evento, k -> ConcurrentHashMap.newKeySet()).add(suscriptor);
        suscriptoresPorId.put(suscriptor.getSuscriptorId(), suscriptor);
    }

    @Override
    public void manejarEvento(String json, IEventSuscriptor cliente) {
        Mensaje mensaje = GSON.fromJson(json, Mensaje.class);

        if (mensaje.getAccion() == TipoAccion.SUSCRIBIR) {
            suscribirse(mensaje.getEvento(), cliente);
        } else if (mensaje.getAccion() == TipoAccion.PUBLICAR) {
            publicar(mensaje.getEvento(), mensaje);
        } else if (mensaje.getAccion() == TipoAccion.SEND_UNICAST) {
            enviarUnicast(mensaje);
        }
    }

    private void enviarUnicast(Mensaje mensaje) {
        String destinatarioId = mensaje.getIdPublicador();
        IEventSuscriptor destinatario = suscriptoresPorId.get(destinatarioId);

        if (destinatario != null) {
            try {
                destinatario.recibirEvento(GSON.toJson(mensaje));
            } catch (Exception e) {
                System.err.println("[BusEventos] Error en unicast a " + destinatarioId + ": " + e.getMessage());
                removeSuscriptor(destinatario);
            }
        }
    }

    @Override
    public void removeSuscriptor(IEventSuscriptor suscriptor) {
        if (suscriptor == null) {
            return;
        }

        for (Set<IEventSuscriptor> suscriptores : eventos.values()) {
            suscriptores.remove(suscriptor);
        }

        suscriptoresPorId.remove(suscriptor.getSuscriptorId());
    }


    @Override
    public IEventSuscriptor getSuscriptor(String id) {
        return suscriptoresPorId.get(id);
    }

    @Override
    public int getCantidadSuscriptores(String evento) {
        Set<IEventSuscriptor> suscriptores = eventos.get(evento);
        return suscriptores != null ? suscriptores.size() : 0;
    }

    @Override
    public boolean existeEvento(String evento) {
        return eventos.containsKey(evento) && !eventos.get(evento).isEmpty();
    }
}
