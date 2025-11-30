package servidor.controlador;

import buseventos.Mensaje;
import buseventos.TipoAccion;
import clientesocket.IClienteSocket;
import com.google.gson.Gson;
import controllers.controller.ManejadorRespuestaCliente;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import models.entidades.Barco;
import models.entidades.Coordenadas;
import models.entidades.Crucero;
import models.entidades.Disparo;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.entidades.PortaAviones;
import models.entidades.Puntaje;
import models.entidades.Submarino;
import models.enums.ResultadoAddNave;
import servidor.modelo.IModeloServidor;
import views.DTOs.AddNaveDTO;
import views.DTOs.DisparoDTO;
import views.DTOs.JugadorDTO;
import views.DTOs.NaveDTO;
import views.DTOs.PuntajeDTO;

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
        mapa.put("ADD_NAVE", this::addNave);
        mapa.put("UNIRSE_PARTIDA", this::manejarUnirsePartida);
        mapa.put("ABANDONAR_PARTIDA", this::manejarAbandonarPartidaSv);
    }

    // Metodo para enviar mensaje por la red.
    private void enviarMensaje(String evento, Object datos) {
        Gson gson = new Gson();
        
        String id = cliente.getId();
        if (id == null) {
            System.out.println("Error, id vacio.");
            return;
        }
        
        Mensaje mensaje = new Mensaje(TipoAccion.PUBLICAR, evento, gson.toJsonTree(datos), id);
        String json = gson.toJson(mensaje);

        cliente.enviarMensaje(json);
    }
    
    // Metodo para enviar mensaje por la red (con subEvento incluido).
    private void enviarMensaje(String evento, String subEvento, Object datos) {
        Gson gson = new Gson();
        
        String id = cliente.getId();
        if (id == null) {
            System.out.println("Error, id vacio.");
            return;
        }
        
        Mensaje mensaje = new Mensaje(TipoAccion.PUBLICAR, evento, gson.toJsonTree(datos), id, subEvento);
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
    
    // Metodo para asignar un metodo al Map cuando se asigne un id al Cliente.
    @Override
    public void onIdSet(String id) {
        manejadoresEventos.put("MENSAJE_CLIENTE_" + id, this::manejarEventoPrivado);
    }
    
    private void manejarEventoPrivado(Mensaje mensaje) {
        
    }

    private void addNave(Mensaje mensaje) {
        Gson gson = new Gson();
        AddNaveDTO dto = gson.fromJson(mensaje.getData(), AddNaveDTO.class);

        List<Coordenadas> coordenadas = dto.getCoordenadases();
        JugadorDTO jugadorDTO = dto.getJugador();
        Jugador jugador = new Jugador(
                jugadorDTO.getNombre(),
                jugadorDTO.getColor(),
                jugadorDTO.getEstado()
        );
        NaveDTO naveDTO = dto.getNave();
        Nave nave = null;

        if (null != naveDTO.getTipo()) {
            switch (naveDTO.getTipo()) {
                case BARCO ->
                    nave = new Barco(naveDTO.getOrientacion());
                case SUBMARINO ->
                    nave = new Submarino(naveDTO.getOrientacion());
                case CRUCERO ->
                    nave = new Crucero(naveDTO.getOrientacion());
                case PORTAAVIONES ->
                    nave = new PortaAviones(naveDTO.getOrientacion());
                default -> {
                }
            }
        }

        ResultadoAddNave resultado = servidor.addNave(jugador, nave, coordenadas);

        enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(), "RESULTADO_ADD_NAVE", resultado);
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

        PuntajeDTO puntajeDTO = null;
        Jugador jugadorConPuntaje = servidor.getJugadores().stream()
                .filter(j -> j.getNombre().equals(jugador.getNombre()))
                .findFirst()
                .orElse(null);

        if (jugadorConPuntaje != null && jugadorConPuntaje.getPuntaje() != null) {
            Puntaje p = jugadorConPuntaje.getPuntaje();
            puntajeDTO = new PuntajeDTO(
                    p.getPuntosTotales(),
                    p.getDisparosAcertados(),
                    p.getDisparosFallados(),
                    p.getNavesHundidas(),
                    p.getPrecision()
            );
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

        resultado.setPuntaje(puntajeDTO);

        enviarMensaje("RESULTADO_DISPARO", resultado);
    }

    private void manejarUnirsePartida(Mensaje mensaje) {
        //unicamente para pruebas este print v
        System.out.println("Servidor: Recibio 'UNIRSE_PARTIDA'.");

        Gson gson = new Gson();
        JugadorDTO jugadorDTO = gson.fromJson(mensaje.getData(), JugadorDTO.class);
        enviarMensaje("JUGADOR_UNIDO", jugadorDTO);
    }

    //Recibe el mensaje enviado por el cliente
    private void manejarAbandonarPartidaSv(Mensaje mensaje) {
        Gson gson = new Gson();
        JugadorDTO jugadorDTO = gson.fromJson(mensaje.getData(), JugadorDTO.class);

        // Convertir DTO a entidad real
        Jugador jugador = new Jugador(
                jugadorDTO.getNombre(),
                jugadorDTO.getColor(),
                jugadorDTO.getEstado()
        );

        // 1. Lógica REAL del servidor
        servidor.abandonarPartida(jugador);

        // 2. Notificar al otro jugador
        enviarMensaje("JUGADOR_ABANDONO", jugadorDTO);
    }

}
