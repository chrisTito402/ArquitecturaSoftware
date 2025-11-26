package config;

/**
 * Configuracion de red para el juego.
 *
 * INSTRUCCIONES PARA JUGAR EN RED:
 * 1. En la maquina que sera SERVIDOR: dejar SERVIDOR_HOST = "localhost"
 * 2. En la maquina que sera CLIENTE: cambiar SERVIDOR_HOST a la IP del servidor
 *    Ejemplo: SERVIDOR_HOST = "192.168.1.100"
 *
 * Para obtener la IP del servidor, ejecutar en CMD: ipconfig
 * Buscar la direccion IPv4 del adaptador de red activo.
 */
public class ConfiguracionRed {

    // Cambiar esta IP a la del servidor cuando juegues desde otra maquina
    public static final String SERVIDOR_HOST = "localhost";

    // Puerto del servidor (debe ser el mismo en servidor y cliente)
    public static final int SERVIDOR_PUERTO = 12345;

    /**
     * Verifica si la configuracion es para conexion local o remota
     */
    public static boolean esConexionLocal() {
        return "localhost".equals(SERVIDOR_HOST) || "127.0.0.1".equals(SERVIDOR_HOST);
    }
}
