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
import models.entidades.Casilla;
import models.enums.ColorJugador;
import models.enums.EstadoJugador;
import views.DTOs.JugadorDTO;
import views.DTOs.TableroDTO;

/**
 *
 * @author daniel
 */
public class PruebaCliente1 {

    public static void main(String[] args) {
        ControlVista cV = ControlVista.getInstancia();
        
        JugadorDTO jugador = new JugadorDTO("J1", ColorJugador.AZUL, EstadoJugador.JUGANDO);
        TableroDTO tablero = new TableroDTO(10, 10);
        
        ControlModelo cM = new ControlModelo(jugador, tablero, new ArrayList<>(), true, new ArrayList<>());
        cM.suscribirAPartida(cV);
        
        ClienteSocket cS = new ClienteSocket("localhost", 5000, null);
        Controlador c = new Controlador(cM, cS, new HashMap<>());
        cS.setControl(c);
        cV.setControl(c);
        
        cV.initTableroPropio();
        cV.initTableroEnemigo();
        cV.mostrarFrmPartidaEnCurso();
        
        cS.execute();
        
        Mensaje m = new Mensaje(TipoAccion.SUSCRIBIR, "RESULTADO_DISPARO", null, "1");
        Gson gson = new Gson();
        String json = gson.toJson(m);
        cS.enviarMensaje(json);
    }
    
}
