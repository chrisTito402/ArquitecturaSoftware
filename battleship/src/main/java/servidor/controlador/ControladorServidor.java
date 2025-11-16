package servidor.controlador;

import buseventos.Mensaje;
import buseventos.TipoAccion;
import clientesocket.IClienteSocket;
import com.google.gson.Gson;
import java.util.Map;
import java.util.function.Consumer;
import models.entidades.Coordenadas;
import models.entidades.Disparo;
import models.entidades.Jugador;
import servidor.modelo.IModeloServidor;
import views.DTOs.DisparoDTO;
import views.DTOs.JugadorDTO;

/**
 *
 * @author daniel
 */
public class ControladorServidor implements IControladorServidor {

    private IModeloServidor servidor;
    private IClienteSocket cliente;
    private Map<String, Consumer<Mensaje>> manejadoresEventos;

    public ControladorServidor(IModeloServidor servidor, IClienteSocket cliente, Map<String, Consumer<Mensaje>> mapa) {
        this.servidor = servidor;
        this.cliente = cliente;
        this.manejadoresEventos = mapa;
        
        mapa.put("DISPARO", this::realizarDisparo);
    }
    
    // Metodo para enviar mensaje por la red.
    private void enviarMensaje(String evento, Object datos) {
        Mensaje mensaje = new Mensaje(TipoAccion.PUBLICAR, evento, datos, null);
        Gson gson = new Gson();
        String json = gson.toJson(mensaje);
        
        cliente.enviarMensaje(json);
    }
    
    // Metodo para manejar el mensaje recibido por la red.
    @Override
    public void manejarMensaje(String json) {
        Gson gson = new Gson();
        Mensaje mensaje = gson.fromJson(json, Mensaje.class);
        
        manejadoresEventos.get(mensaje.getEvento()).accept(mensaje);
    }
    
    private void realizarDisparo(Mensaje mensaje) {
        if (!(mensaje.getData() instanceof DisparoDTO)) {
            System.out.println("El mensaje no contiene un DisparoDTO");
            return;
        }
        
        DisparoDTO disparoDTO = (DisparoDTO) mensaje.getData();
        
        Coordenadas coordenadas = disparoDTO.getCoordenadas();
        JugadorDTO jugadorDTO = disparoDTO.getJugador();
        Jugador jugador = new Jugador(
                jugadorDTO.getNombre(),
                jugadorDTO.getColor(),
                jugadorDTO.getEstado()
        );
        
        Disparo disparo = servidor.realizarDisparo(coordenadas, jugador);
        enviarMensaje("RESULTADO_DISPARO", jugador);
    }
    
}
