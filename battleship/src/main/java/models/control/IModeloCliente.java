package models.control;

import models.entidades.Coordenadas;
import java.util.List;
import models.enums.ResultadoConfirmarNaves;
import models.enums.ResultadoEmpezarPartida;
import views.DTOs.AddJugadorDTO;
import views.DTOs.AddNaveDTO;
import views.DTOs.DisparoDTO;
import views.DTOs.JugadorDTO;
import views.DTOs.NaveDTO;

/**
 *
 * @author daniel
 */
public interface IModeloCliente {

    public DisparoDTO realizarDisparo(Coordenadas coordenadas);

    public AddNaveDTO addNave(NaveDTO nave, List<Coordenadas> coordenadas);
    
    public JugadorDTO confirmarNaves();

    public void crearTableros();

    public JugadorDTO getJugador();

    // Caso de Uso: Unirse Partida
    public JugadorDTO unirsePartida(JugadorDTO jugador);

    public boolean empezarPartida();

    public void abandonarLobby();

    public JugadorDTO abandonarPartida();
    
    public JugadorDTO obtenerJugadorEnemigo();
    
    public void obtenerMarcador();

    // Manejadores de Resultados
    public void manejarResultadoDisparo(DisparoDTO disparo);
    
    public void manejarResultadoAddNave(AddNaveDTO resultado);
    
    public void manejarCambiarTurno(JugadorDTO jugador);
    
    public void manejarResultadoConfirmarNaves(ResultadoConfirmarNaves resultado);
    
    public void manejarJugadorAbandono(JugadorDTO J);
    
    public void manejarJugadorUnido(AddJugadorDTO dto);
    
    public void manejarEmpezarPartida(ResultadoEmpezarPartida resultado);
    
    public void manejarObtenerJugadorEnemigo(JugadorDTO jugador);
}
