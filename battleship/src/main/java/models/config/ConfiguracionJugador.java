package models.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import models.enums.ColorJugador;

/**
 * Gestiona la persistencia de las preferencias del jugador.
 * Guarda y carga configuraciones como nombre y color preferido.
 * Implementa patron Singleton.
 *
 * @author Equipo
 */
public class ConfiguracionJugador {

    private static ConfiguracionJugador instancia;
    private static final String CONFIG_FILE = "battleship_config.properties";
    private static final String KEY_NOMBRE = "jugador.nombre";
    private static final String KEY_COLOR = "jugador.color";

    private Properties propiedades;
    private String rutaArchivo;

    private ConfiguracionJugador() {
        propiedades = new Properties();
        // Guardar en la carpeta del usuario
        String userHome = System.getProperty("user.home");
        rutaArchivo = userHome + File.separator + CONFIG_FILE;
        cargarConfiguracion();
    }

    /**
     * Obtiene la instancia unica de ConfiguracionJugador.
     * @return instancia singleton
     */
    public static synchronized ConfiguracionJugador getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracionJugador();
        }
        return instancia;
    }

    /**
     * Carga la configuracion desde el archivo.
     */
    private void cargarConfiguracion() {
        File archivo = new File(rutaArchivo);
        if (archivo.exists()) {
            try (FileInputStream fis = new FileInputStream(archivo)) {
                propiedades.load(fis);
                System.out.println("Configuracion cargada desde: " + rutaArchivo);
            } catch (IOException e) {
                System.err.println("Error al cargar configuracion: " + e.getMessage());
            }
        }
    }

    /**
     * Guarda la configuracion en el archivo.
     */
    private void guardarConfiguracion() {
        try (FileOutputStream fos = new FileOutputStream(rutaArchivo)) {
            propiedades.store(fos, "Configuracion de Battleship");
            System.out.println("Configuracion guardada en: " + rutaArchivo);
        } catch (IOException e) {
            System.err.println("Error al guardar configuracion: " + e.getMessage());
        }
    }

    /**
     * Obtiene el nombre guardado del jugador.
     * @return nombre guardado o cadena vacia si no existe
     */
    public String getNombreGuardado() {
        return propiedades.getProperty(KEY_NOMBRE, "");
    }

    /**
     * Guarda el nombre del jugador.
     * @param nombre nombre a guardar
     */
    public void setNombreGuardado(String nombre) {
        if (nombre != null && !nombre.trim().isEmpty()) {
            propiedades.setProperty(KEY_NOMBRE, nombre.trim());
            guardarConfiguracion();
        }
    }

    /**
     * Obtiene el color guardado del jugador.
     * @return color guardado o null si no existe
     */
    public ColorJugador getColorGuardado() {
        String colorStr = propiedades.getProperty(KEY_COLOR);
        if (colorStr != null) {
            try {
                return ColorJugador.valueOf(colorStr);
            } catch (IllegalArgumentException e) {
                System.err.println("Color invalido en configuracion: " + colorStr);
            }
        }
        return null;
    }

    /**
     * Guarda el color preferido del jugador.
     * @param color color a guardar
     */
    public void setColorGuardado(ColorJugador color) {
        if (color != null) {
            propiedades.setProperty(KEY_COLOR, color.name());
            guardarConfiguracion();
        }
    }

    /**
     * Guarda nombre y color juntos.
     * @param nombre nombre del jugador
     * @param color color preferido
     */
    public void guardarPreferencias(String nombre, ColorJugador color) {
        if (nombre != null && !nombre.trim().isEmpty()) {
            propiedades.setProperty(KEY_NOMBRE, nombre.trim());
        }
        if (color != null) {
            propiedades.setProperty(KEY_COLOR, color.name());
        }
        guardarConfiguracion();
    }

    /**
     * Limpia todas las preferencias guardadas.
     */
    public void limpiarConfiguracion() {
        propiedades.clear();
        File archivo = new File(rutaArchivo);
        if (archivo.exists()) {
            archivo.delete();
        }
    }

    /**
     * Verifica si hay configuracion guardada.
     * @return true si existe configuracion previa
     */
    public boolean tieneConfiguracionGuardada() {
        return !getNombreGuardado().isEmpty() || getColorGuardado() != null;
    }
}
