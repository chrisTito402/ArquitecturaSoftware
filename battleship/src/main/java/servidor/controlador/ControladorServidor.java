package servidor.controlador;

import buseventos.Mensaje;
import buseventos.TipoAccion;
import clientesocket.IClienteSocket;
import com.google.gson.Gson;
import controllers.controller.ManejadorRespuestaCliente;
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
public class ControladorServidor implements ManejadorRespuestaCliente {

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
        Gson gson = new Gson();
        Mensaje mensaje = new Mensaje(TipoAccion.PUBLICAR, evento, gson.toJsonTree(datos), null);
        String json = gson.toJson(mensaje);
        
        cliente.enviarMensaje(json);
    }
    
    // Metodo para manejar el mensaje recibido por la red.
    @Override
    public void manejarMensaje(String json) {
        System.out.println(json);
        Gson gson = new Gson();
        Mensaje mensaje = gson.fromJson(json, Mensaje.class);
        
        manejadoresEventos.get(mensaje.getEvento()).accept(mensaje);
    }
    
    private void realizarDisparo(Mensaje mensaje) {
        Gson gson = new Gson();
        DisparoDTO disparoDTO = gson.fromJson(mensaje.getData(), DisparoDTO.class);
        
        Coordenadas coordenadas = disparoDTO.getCoordenadas();
        JugadorDTO jugadorDTO = disparoDTO.getJugador();
        Jugador jugador = new Jugador(
                jugadorDTO.getNombre(),
                jugadorDTO.getColor(),
                jugadorDTO.getEstado()
        );
        
        Disparo disparo = servidor.realizarDisparo(coordenadas, jugador, disparoDTO.getTiempo());
        
        if (disparo == null) {
            System.out.println("Error en el Servidor");
            return;
        }
        
        DisparoDTO resultado = new DisparoDTO(
                new JugadorDTO(
                        disparo.getJugador().getNombre(), 
                        disparo.getJugador().getColor(), 
                        disparo.getJugador().getEstado()), 
                disparo.getCoordenadas(), 
                disparo.getResultadoDisparo(), 
                disparo.getEstadoPartida()
        );
        enviarMensaje("RESULTADO_DISPARO", resultado);
    }
    
}
