package servidor.negocio;

import compartido.comunicacion.Mensaje;

/**
 * Interfaz para que GestorPartida pueda mandar mensajes sin depender
 * directamente del BusEventos. Esto es por el DIP (Dependency Inversion
 * Principle) - asi si despues queremos cambiar como se mandan los
 * mensajes nomas cambiamos la implementacion y ya.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public interface IPublicadorEventos {

    /**
     * Envia mensaje a todos los suscritos.
     */
    void publicar(String evento, Mensaje mensaje);

    /**
     * Envia mensaje a un solo cliente.
     */
    void enviarUnicast(String idDestino, Mensaje mensaje);
}
