package pruebas;

import buseventos.Mensaje;
import buseventos.TipoAccion;
import clientesocket.ClienteSocket;
import com.google.gson.Gson;
import controllers.controller.ControlVista;
import controllers.controller.Controlador;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import models.control.ControlModelo;
import models.entidades.Coordenadas;
import models.enums.ColorJugador;
import models.enums.EstadoJugador;
import models.enums.EstadoNave;
import models.enums.OrientacionNave;
import views.DTOs.JugadorDTO;
import views.DTOs.NaveDTO;
import views.DTOs.TableroDTO;
import views.DTOs.TipoNaveDTO;
import views.frames.TimerPanel;

/**
 *
 * @author daniel
 */
public class PruebaCliente2 {

    public static void main(String[] args) {
        ControlVista cV = ControlVista.getInstancia();

        JugadorDTO jugador = new JugadorDTO("J2", ColorJugador.ROJO, EstadoJugador.JUGANDO);
        TableroDTO tablero = new TableroDTO(10, 10);

        ControlModelo cM = new ControlModelo(jugador, tablero, new ArrayList<>(), true, new ArrayList<>());
        cM.suscribirAPartida(cV);

        ClienteSocket cS = new ClienteSocket("localhost", 5000, null);
        Controlador c = new Controlador(cM, cS, new HashMap<>());
        cS.setControl(c);
        cV.setControl(c);
        cV.setTimer(new TimerPanel(1000, 30));
        cV.initTableroPropio();
        cV.initTableroEnemigo();

        cV.mostrarFrmPartidaEnCurso();

        cS.execute();

        Mensaje m = new Mensaje(TipoAccion.SUSCRIBIR, "RESULTADO_DISPARO", null);
        Gson gson = new Gson();
        String json = gson.toJson(m);
        cS.enviarMensaje(json);

        m = new Mensaje(TipoAccion.SUSCRIBIR, "RESULTADO_CONFIRMAR_NAVES", null);
        gson = new Gson();
        json = gson.toJson(m);
        cS.enviarMensaje(json);

        m = new Mensaje(TipoAccion.SUSCRIBIR, "JUGADOR_ABANDONO", null);
        gson = new Gson();
        json = gson.toJson(m);
        cS.enviarMensaje(json);

        NaveDTO nave = new NaveDTO(EstadoNave.SIN_DAÑOS, OrientacionNave.HORIZONTAL, TipoNaveDTO.BARCO, 1);
        List<Coordenadas> coordenadas = new ArrayList<>();
        coordenadas.add(new Coordenadas(2, 2));
        //cV.addNave(nave, coordenadas);
    }

}
