package servidor.controlador;

import buseventos.Mensaje;
import buseventos.TipoAccion;
import clientesocket.IClienteSocket;
import com.google.gson.Gson;
import controllers.controller.ManejadorRespuestaCliente;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import models.entidades.AddNave;
import models.entidades.Barco;
import models.entidades.Coordenadas;
import models.entidades.Crucero;
import models.entidades.Disparo;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.entidades.PortaAviones;
import models.entidades.Puntaje;
import models.entidades.Submarino;
import models.enums.ResultadoAddJugador;
import models.enums.ResultadoAddNave;
import models.enums.ResultadoConfirmarNaves;
import models.enums.ResultadoEmpezarPartida;
import servidor.modelo.IModeloServidor;
import views.DTOs.AddJugadorDTO;
import views.DTOs.AddNaveDTO;
import views.DTOs.DisparoDTO;
import views.DTOs.JugadorDTO;
import views.DTOs.NaveDTO;
import views.DTOs.PuntajeDTO;
import views.DTOs.TipoNaveDTO;

/**
 *
 * @author daniel
 */
public class ControladorServidor implements ManejadorRespuestaCliente, INotificadorServidor {

    private IModeloServidor servidor;
    private IClienteSocket cliente;
    private Map<String, Consumer<Mensaje>> manejadoresEventos;
    private Map<String, Consumer<Object>> manejadorNotificaciones;

    public ControladorServidor(IModeloServidor servidor, IClienteSocket cliente, Map<String, Consumer<Mensaje>> mapa, Map<String, Consumer<Object>> mapaNotis) {
        this.servidor = servidor;
        this.cliente = cliente;
        this.manejadoresEventos = mapa;
        this.manejadorNotificaciones = mapaNotis;

        manejadoresEventos.put("DISPARO", this::realizarDisparo);
        manejadoresEventos.put("ADD_NAVE", this::addNave);
        manejadoresEventos.put("UNIRSE_PARTIDA", this::manejarUnirsePartida);
        manejadoresEventos.put("ABANDONAR_PARTIDA", this::manejarAbandonarPartidaSv);
        manejadoresEventos.put("CONFIRMAR_NAVES", this::setConfirmarNaves);
        manejadoresEventos.put("EMPEZAR_PARTIDA", this::manejarEmpezarPartida);
        manejadoresEventos.put("OBTENER_JUGADOR_ENEMIGO", this::manejarObtenerEnemigo);
        
        manejadorNotificaciones.put("CAMBIAR_TURNO", this::notificarCambiarTurno);
    }

    @Override
    public void notificar(String contexto, Object datos) {
        manejadorNotificaciones.get(contexto).accept(datos);
    }
    
    private void notificarCambiarTurno(Object datos) {
        Jugador j = (Jugador) datos;
        JugadorDTO jugador = new JugadorDTO(
                j.getNombre(), 
                j.getColor(), 
                j.getEstado()
        );
        
        enviarMensaje("CAMBIAR_TURNO", jugador);
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

        AddNave resultado = servidor.addNave(jugador, nave, coordenadas);
        
        nave = resultado.getNave();
        if (nave != null) {
            naveDTO.setEstado(nave.getEstado());
            naveDTO.setOrientacion(nave.getOrientacion());
            naveDTO.setTamanio(nave.getTamanio());
        }
        
        AddNaveDTO resultadoDTO = new AddNaveDTO(jugadorDTO, naveDTO, coordenadas, resultado.getResultado());

        enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(), "RESULTADO_ADD_NAVE", resultadoDTO);
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
                disparo.getEstadoPartida(),
                disparo.getCantImpact(),
                disparo.getEstadoNave()
        );

        resultado.setPuntaje(puntajeDTO);

        enviarMensaje("RESULTADO_DISPARO", resultado);
    }

    private void setConfirmarNaves(Mensaje mensaje) {
        Gson gson = new Gson();
        Jugador jugador = gson.fromJson(mensaje.getData(), Jugador.class);

        ResultadoConfirmarNaves resultado = servidor.setConfirmarNaves(jugador);
        if (resultado == ResultadoConfirmarNaves.EMPEZAR_PARTIDA) {
            enviarMensaje("RESULTADO_CONFIRMAR_NAVES", resultado);
            return;
        }

        enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(), "RESULTADO_CONFIRMAR_NAVES", resultado);
    }

    private void manejarUnirsePartida(Mensaje mensaje) {
        //unicamente para pruebas este print v
        System.out.println("Servidor: Recibio 'UNIRSE_PARTIDA'.");

        Gson gson = new Gson();
        JugadorDTO jugadorDTO = gson.fromJson(mensaje.getData(), JugadorDTO.class);
        
        Jugador j = new Jugador(jugadorDTO.getNombre(), jugadorDTO.getColor(), jugadorDTO.getEstado());
        
        ResultadoAddJugador resultado = servidor.unirsePartida(j);
        AddJugadorDTO addJugador = new AddJugadorDTO(resultado, jugadorDTO);
        
        if (resultado != ResultadoAddJugador.AÑADIDO) {
            enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(), "JUGADOR_UNIDO", addJugador);
            return;
        }
        
        enviarMensaje("JUGADOR_UNIDO", addJugador);
    }

    //Recibe el mensaje enviado por el cliente
    private void manejarAbandonarPartidaSv(Mensaje mensaje) {
        Gson gson = new Gson();
        JugadorDTO jugadorDTO = gson.fromJson(mensaje.getData(), JugadorDTO.class);

        Jugador jugador = new Jugador(
                jugadorDTO.getNombre(),
                jugadorDTO.getColor(),
                jugadorDTO.getEstado()
        );

        // Recibo el jugador ya actualizado
        Jugador jugadorActual = servidor.abandonarPartida(jugador);

        JugadorDTO jugadordto = new JugadorDTO(
                jugadorActual.getNombre(),
                jugadorActual.getColor(),
                jugadorActual.getEstado()
        );

        enviarMensaje("JUGADOR_ABANDONO", jugadordto);
    }
    
    private void manejarEmpezarPartida(Mensaje mensaje) {
        ResultadoEmpezarPartida resultado = servidor.empezarPartida();
        enviarMensaje("RESULTADO_EMPEZAR_PARTIDA", resultado);
    }
    
    private void manejarObtenerEnemigo(Mensaje mensaje) {
        Gson gson = new Gson();
        
        JugadorDTO dto = gson.fromJson(mensaje.getData(), JugadorDTO.class);
        Jugador jugador = new Jugador(dto.getNombre(), dto.getColor(), dto.getEstado());
        
        Jugador j = servidor.obtenerJugadorEnemigo(jugador);
        
        enviarMensaje("MENSAJE_CLIENTE_" + mensaje.getIdPublicador(), "OBTENER_JUGADOR_ENEMIGO", j);
    }

}
