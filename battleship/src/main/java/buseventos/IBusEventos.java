package buseventos;

import java.util.Set;

public interface IBusEventos {

    void publicar(String evento, Mensaje mensaje);

    void suscribirse(String evento, IEventSuscriptor suscriptor);

    void removeSuscriptor(IEventSuscriptor suscriptor);

    IEventSuscriptor getSuscriptor(String id);

    Set<IEventSuscriptor> getSuscriptores(String evento);

    int getCantidadSuscriptores(String evento);

    boolean existeEvento(String evento);

    void manejarEvento(String json, IEventSuscriptor cliente);
}
