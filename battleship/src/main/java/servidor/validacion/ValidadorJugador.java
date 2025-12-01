package servidor.validacion;

import models.entidades.Jugador;
import models.enums.ColorJugador;
import shared.dto.JugadorDTO;

/**
 * Servicio de validacion centralizado para jugadores.
 * Contiene toda la logica de validacion de negocio.
 *
 * @author Equipo
 */
public class ValidadorJugador {

    private static final int NOMBRE_MIN_LENGTH = 2;
    private static final int NOMBRE_MAX_LENGTH = 20;

    /**
     * Valida que el nombre del jugador sea correcto.
     * @param nombre nombre a validar
     * @return resultado de la validacion
     */
    public static ResultadoValidacion validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return new ResultadoValidacion(false, "El nombre no puede estar vacio");
        }

        String nombreTrimmed = nombre.trim();

        if (nombreTrimmed.length() < NOMBRE_MIN_LENGTH) {
            return new ResultadoValidacion(false,
                "El nombre debe tener al menos " + NOMBRE_MIN_LENGTH + " caracteres");
        }

        if (nombreTrimmed.length() > NOMBRE_MAX_LENGTH) {
            return new ResultadoValidacion(false,
                "El nombre no puede tener mas de " + NOMBRE_MAX_LENGTH + " caracteres");
        }

        // Validar que solo contenga caracteres permitidos (letras, numeros, espacios)
        if (!nombreTrimmed.matches("^[a-zA-Z0-9\\s]+$")) {
            return new ResultadoValidacion(false,
                "El nombre solo puede contener letras, numeros y espacios");
        }

        return new ResultadoValidacion(true, "Nombre valido");
    }

    /**
     * Valida que el color sea valido.
     * @param color color a validar
     * @return resultado de la validacion
     */
    public static ResultadoValidacion validarColor(ColorJugador color) {
        if (color == null) {
            return new ResultadoValidacion(false, "Debe seleccionar un color");
        }
        return new ResultadoValidacion(true, "Color valido");
    }

    /**
     * Valida un JugadorDTO completo.
     * @param jugadorDTO jugador a validar
     * @return resultado de la validacion
     */
    public static ResultadoValidacion validarJugadorDTO(JugadorDTO jugadorDTO) {
        if (jugadorDTO == null) {
            return new ResultadoValidacion(false, "Datos del jugador no proporcionados");
        }

        ResultadoValidacion resultadoNombre = validarNombre(jugadorDTO.getNombre());
        if (!resultadoNombre.isValido()) {
            return resultadoNombre;
        }

        ResultadoValidacion resultadoColor = validarColor(jugadorDTO.getColor());
        if (!resultadoColor.isValido()) {
            return resultadoColor;
        }

        return new ResultadoValidacion(true, "Jugador valido");
    }

    /**
     * Valida una entidad Jugador completa.
     * @param jugador jugador a validar
     * @return resultado de la validacion
     */
    public static ResultadoValidacion validarJugador(Jugador jugador) {
        if (jugador == null) {
            return new ResultadoValidacion(false, "Jugador no proporcionado");
        }

        ResultadoValidacion resultadoNombre = validarNombre(jugador.getNombre());
        if (!resultadoNombre.isValido()) {
            return resultadoNombre;
        }

        ResultadoValidacion resultadoColor = validarColor(jugador.getColor());
        if (!resultadoColor.isValido()) {
            return resultadoColor;
        }

        return new ResultadoValidacion(true, "Jugador valido");
    }

    /**
     * Clase interna para representar el resultado de una validacion.
     */
    public static class ResultadoValidacion {
        private final boolean valido;
        private final String mensaje;

        public ResultadoValidacion(boolean valido, String mensaje) {
            this.valido = valido;
            this.mensaje = mensaje;
        }

        public boolean isValido() {
            return valido;
        }

        public String getMensaje() {
            return mensaje;
        }
    }
}
