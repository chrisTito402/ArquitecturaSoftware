package servidor.negocio;

import compartido.comunicacion.Mensaje;

/**
 * Interfaz que define las operaciones de publicacion de eventos.
 * Permite desacoplar la logica de negocio (GestorPartida) del
 * mecanismo de transporte (BusEventos).
 *
 * Principio de Inversion de Dependencias (DIP):
 * La capa de negocio depende de esta abstraccion, no de la implementacion concreta.
 *
 * @author Equipo
 */
public interface IPublicadorEventos {

    /**
     * Publica un mensaje a todos los suscriptores de un evento.
     *
     * @param evento Nombre del evento/canal
     * @param mensaje Mensaje a publicar
     */
    void publicar(String evento, Mensaje mensaje);

    /**
     * Envia un mensaje unicast a un cliente especifico.
     *
     * @param idDestino ID del cliente destino
     * @param mensaje Mensaje a enviar
     */
    void enviarUnicast(String idDestino, Mensaje mensaje);
}
