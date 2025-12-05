package compartido.comunicacion;

/**
 * Los tipos de accion que puede tener un mensaje en el bus.
 * PUBLICAR = a todos, SUSCRIBIR = registrarse, SEND_UNICAST = a uno solo.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public enum TipoAccion {
    PUBLICAR,
    SUSCRIBIR,
    SEND_UNICAST
}
