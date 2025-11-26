package buseventos;

public enum EventoJuego {

    DISPARO("DISPARO"),
    ADD_NAVE("ADD_NAVE"),
    UNIRSE_PARTIDA("UNIRSE_PARTIDA"),
    ABANDONAR_PARTIDA("ABANDONAR_PARTIDA"),

    RESULTADO_DISPARO("RESULTADO_DISPARO"),
    RESULTADO_ADD_NAVE("RESULTADO_ADD_NAVE"),
    JUGADOR_UNIDO("JUGADOR_UNIDO"),
    JUGADOR_ABANDONO("JUGADOR_ABANDONO"),

    CAMBIO_TURNO("CAMBIO_TURNO"),
    PARTIDA_INICIADA("PARTIDA_INICIADA"),
    PARTIDA_FINALIZADA("PARTIDA_FINALIZADA"),

    TIEMPO_ACTUALIZADO("TIEMPO_ACTUALIZADO"),

    BROADCAST("BROADCAST");

    private final String valor;

    EventoJuego(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EventoJuego fromString(String texto) {
        for (EventoJuego evento : EventoJuego.values()) {
            if (evento.valor.equalsIgnoreCase(texto)) {
                return evento;
            }
        }
        return null;
    }
}
