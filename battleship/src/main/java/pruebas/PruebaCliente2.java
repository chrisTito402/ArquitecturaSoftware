package pruebas;

import buseventos.Mensaje;
import buseventos.TipoAccion;
import clientesocket.ClienteSocket;
import com.google.gson.Gson;
import controllers.controller.ControlVista;
import controllers.controller.Controlador;
import java.util.ArrayList;
import java.util.HashMap;
import models.control.ControlModelo;
import views.DTOs.JugadorDTO;
import views.DTOs.NaveDTO;
import views.DTOs.TableroDTO;
import views.frames.TimerPanel;

/**
 *
 * @author daniel
 */
public class PruebaCliente2 {

    public static void main(String[] args) {
        ControlVista cV = ControlVista.getInstancia();

        ControlModelo cM = new ControlModelo(
                new JugadorDTO(), 
                new TableroDTO(), 
                new ArrayList<>(), 
                new ArrayList<>(), 
                new ArrayList<>(), 
                new ArrayList<>(), 
                new HashMap<>()
        );
        cM.suscribirAPartida(cV);

        ClienteSocket cS = new ClienteSocket("localhost", 5000, null);
        Controlador c = new Controlador(cM, cS, new HashMap<>());
        cS.setControl(c);
        cV.setControl(c);
        cV.setTimer(new TimerPanel(1000, 30));
        cV.mostrarFrmRegistrarJugador();
        
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
        
        m = new Mensaje(TipoAccion.SUSCRIBIR, "CAMBIAR_TURNO", null);
        gson = new Gson();
        json = gson.toJson(m);
        cS.enviarMensaje(json);
        
        m = new Mensaje(TipoAccion.SUSCRIBIR, "JUGADOR_UNIDO", null);
        gson = new Gson();
        json = gson.toJson(m);
        cS.enviarMensaje(json);
        
        m = new Mensaje(TipoAccion.SUSCRIBIR, "RESULTADO_EMPEZAR_PARTIDA", null);
        gson = new Gson();
        json = gson.toJson(m);
        cS.enviarMensaje(json);
    }

}
