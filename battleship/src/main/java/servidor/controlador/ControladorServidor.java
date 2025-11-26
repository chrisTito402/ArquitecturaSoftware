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
import dtos.AddNaveDTO;
import dtos.DisparoDTO;
import dtos.JugadorDTO;
import dtos.NaveDTO;
import dtos.PuntajeDTO;

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
        Mensaje mensaje = new Mensaje(TipoAccion.PUBLICAR, evento, gson.toJsonTree(datos), null);
        String json = gson.toJson(mensaje);

        cliente.enviarMensaje(json);
    }

    // Metodo para manejar el mensaje recibido por la red.
    @Override
    public void manejarMensaje(String json) {
        Gson gson = new Gson();
        Mensaje mensaje = gson.fromJson(json, Mensaje.class);

        Consumer<Mensaje> handler = manejadoresEventos.get(mensaje.getEvento());
        if (handler != null) {
            handler.accept(mensaje);
        } else {
            System.err.println("Evento no registrado en servidor: " + mensaje.getEvento());
        }
    }

    private void addNave(Mensaje mensaje) {
        Gson gson = new Gson();
        AddNaveDTO dto = gson.fromJson(mensaje.getData(), AddNaveDTO.class);

        List<Coordenadas> coordenadas = dto.getCoordenadases();

        // Usar JugadorMapper para convertir DTO a entidad
        Jugador jugador = dtos.mappers.JugadorMapper.toEntity(dto.getJugador());

        // Crear nave según el tipo especificado
        NaveDTO naveDTO = dto.getNave();
        Nave nave = crearNave(naveDTO);

        ResultadoAddNave resultado = servidor.addNave(jugador, nave, coordenadas);

        enviarMensaje("RESULTADO_ADD_NAVE", resultado);
    }

    /**
     * Factory method para crear una Nave según su tipo.
     * Aplica el patrón Factory para eliminar el switch repetitivo.
     *
     * @param naveDTO El DTO con la información de la nave
     * @return Una instancia de la Nave correspondiente, o null si el tipo es inválido
     */
    private Nave crearNave(NaveDTO naveDTO) {
        if (naveDTO == null || naveDTO.getTipo() == null) {
            return null;
        }

        return switch (naveDTO.getTipo()) {
            case BARCO -> new Barco(naveDTO.getOrientacion());
            case SUBMARINO -> new Submarino(naveDTO.getOrientacion());
            case CRUCERO -> new Crucero(naveDTO.getOrientacion());
            case PORTAAVIONES -> new PortaAviones(naveDTO.getOrientacion());
        };
    }

    private void realizarDisparo(Mensaje mensaje) {
        Gson gson = new Gson();
        DisparoDTO disparoDTO = gson.fromJson(mensaje.getData(), DisparoDTO.class);

        Coordenadas coordenadas = disparoDTO.getCoordenadas();

        // Usar JugadorMapper para convertir DTO a entidad
        Jugador jugador = dtos.mappers.JugadorMapper.toEntity(disparoDTO.getJugador());

        // Realizar el disparo en el servidor
        Disparo disparo = servidor.realizarDisparo(coordenadas, jugador, disparoDTO.getTiempo());

        // Obtener el puntaje actualizado del jugador usando el Mapper
        PuntajeDTO puntajeDTO = null;
        Jugador jugadorConPuntaje = servidor.getJugadores().stream()
                .filter(j -> j.getNombre().equals(jugador.getNombre()))
                .findFirst()
                .orElse(null);

        if (jugadorConPuntaje != null && jugadorConPuntaje.getPuntaje() != null) {
            // Usar PuntajeMapper para convertir entidad a DTO
            puntajeDTO = dtos.mappers.PuntajeMapper.toDTO(jugadorConPuntaje.getPuntaje());
        }

        // Construir el DisparoDTO de respuesta usando Mappers
        DisparoDTO resultado = new DisparoDTO(
                dtos.mappers.JugadorMapper.toDTO(disparo.getJugador()),
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

        // Usar JugadorMapper para convertir DTO a entidad
        Jugador jugador = dtos.mappers.JugadorMapper.toEntity(jugadorDTO);

        // 1. Lógica REAL del servidor
        servidor.abandonarPartida(jugador);

        // 2. Notificar al otro jugador
        enviarMensaje("JUGADOR_ABANDONO", jugadorDTO);
    }

}
