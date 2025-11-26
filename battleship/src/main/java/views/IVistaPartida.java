package views;

import dtos.CoordenadasDTO;
import dtos.PuntajeDTO;
import dtos.enums.ResultadoDisparoDTO;

public interface IVistaPartida {

    void mostrarCasillaImpactada(CoordenadasDTO coordenadas, ResultadoDisparoDTO resultado, boolean esCasillaPropia);

    void actualizarPuntaje(PuntajeDTO puntaje);

    void mostrarMensajeFinPartida(String nombreGanador, PuntajeDTO puntaje);

    void deshabilitarTableroEnemigo();

    void reiniciarTimer();

    void detenerTimer();

    void habilitarTablero();
}
