package servidor.modelo;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import models.entidades.Partida;

/**
 * Gestiona las partidas activas del servidor usando codigos unicos.
 * Implementa el patron Singleton para acceso global.
 *
 * @author Equipo
 */
public class GestorPartidas {

    private static GestorPartidas instancia;
    private final Map<String, Partida> partidas;
    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LONGITUD_CODIGO = 5;

    private GestorPartidas() {
        this.partidas = new ConcurrentHashMap<>();
    }

    public static synchronized GestorPartidas getInstancia() {
        if (instancia == null) {
            instancia = new GestorPartidas();
        }
        return instancia;
    }

    /**
     * Genera un codigo unico de 5 caracteres alfanumericos.
     * @return codigo unico para la partida
     */
    public String generarCodigo() {
        StringBuilder codigo;
        Random random = new Random();

        do {
            codigo = new StringBuilder();
            for (int i = 0; i < LONGITUD_CODIGO; i++) {
                codigo.append(CARACTERES.charAt(random.nextInt(CARACTERES.length())));
            }
        } while (partidas.containsKey(codigo.toString()));

        return codigo.toString();
    }

    /**
     * Valida que un codigo tenga el formato correcto.
     * @param codigo codigo a validar
     * @return true si el formato es valido
     */
    public boolean validarFormatoCodigo(String codigo) {
        if (codigo == null || codigo.length() != LONGITUD_CODIGO) {
            return false;
        }
        for (char c : codigo.toUpperCase().toCharArray()) {
            if (CARACTERES.indexOf(c) == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Registra una nueva partida con el codigo especificado.
     * @param codigo codigo de la partida
     * @param partida instancia de la partida
     * @return true si se registro correctamente, false si el codigo ya existe
     */
    public boolean registrarPartida(String codigo, Partida partida) {
        if (codigo == null || partida == null) {
            return false;
        }
        String codigoUpper = codigo.toUpperCase();
        if (partidas.containsKey(codigoUpper)) {
            return false;
        }
        partidas.put(codigoUpper, partida);
        System.out.println("Partida registrada con codigo: " + codigoUpper);
        return true;
    }

    /**
     * Obtiene una partida por su codigo.
     * @param codigo codigo de la partida
     * @return la partida o null si no existe
     */
    public Partida obtenerPartida(String codigo) {
        if (codigo == null) {
            return null;
        }
        return partidas.get(codigo.toUpperCase());
    }

    /**
     * Verifica si existe una partida con el codigo dado.
     * @param codigo codigo a verificar
     * @return true si existe la partida
     */
    public boolean existePartida(String codigo) {
        if (codigo == null) {
            return false;
        }
        return partidas.containsKey(codigo.toUpperCase());
    }

    /**
     * Verifica si una partida puede aceptar mas jugadores.
     * @param codigo codigo de la partida
     * @return true si la partida puede recibir jugadores
     */
    public boolean puedeUnirse(String codigo) {
        Partida partida = obtenerPartida(codigo);
        if (partida == null) {
            return false;
        }
        return partida.getJugadores().size() < 2;
    }

    /**
     * Elimina una partida del gestor.
     * @param codigo codigo de la partida a eliminar
     * @return true si se elimino correctamente
     */
    public boolean eliminarPartida(String codigo) {
        if (codigo == null) {
            return false;
        }
        return partidas.remove(codigo.toUpperCase()) != null;
    }

    /**
     * Obtiene el numero de partidas activas.
     * @return cantidad de partidas
     */
    public int cantidadPartidas() {
        return partidas.size();
    }

    /**
     * Limpia todas las partidas (para testing o reinicio).
     */
    public void limpiarPartidas() {
        partidas.clear();
    }
}
