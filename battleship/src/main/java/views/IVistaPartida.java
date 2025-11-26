package views;

import dtos.PuntajeDTO;
import models.entidades.Coordenadas;
import models.enums.EstadoPartida;
import models.enums.ResultadoDisparo;

public interface IVistaPartida {

    void mostrarCasillaImpactada(Coordenadas coordenadas, ResultadoDisparo resultado, boolean esCasillaPropia);

    void actualizarPuntaje(PuntajeDTO puntaje);

    void mostrarMensajeFinPartida(String nombreGanador, PuntajeDTO puntaje);

    void deshabilitarTableroEnemigo();

    void reiniciarTimer();

    void detenerTimer();

    void habilitarTablero();
}
