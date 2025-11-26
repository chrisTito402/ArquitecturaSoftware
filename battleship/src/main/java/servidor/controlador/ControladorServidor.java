package servidor.controlador;

import buseventos.Mensaje;
import buseventos.util.MensajeriaHelper;
import clientesocket.IClienteSocket;
import controllers.controller.ManejadorRespuestaCliente;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import models.entidades.Coordenadas;
import models.entidades.Disparo;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.enums.ResultadoAddNave;
import models.factories.NaveFactory;
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
        String json = MensajeriaHelper.crearMensajeJSON(evento, datos);
        cliente.enviarMensaje(json);
    }

    @Override
    public void manejarMensaje(String json) {
        Mensaje mensaje = MensajeriaHelper.parsearMensaje(json);

        Consumer<Mensaje> handler = manejadoresEventos.get(mensaje.getEvento());
        if (handler != null) {
            handler.accept(mensaje);
        }
    }

    private void addNave(Mensaje mensaje) {
        AddNaveDTO dto = MensajeriaHelper.extraerDatos(mensaje, AddNaveDTO.class);

        List<Coordenadas> coordenadas = dto.getCoordenadases();

        // Usar JugadorMapper para convertir DTO a entidad
        Jugador jugador = dtos.mappers.JugadorMapper.toEntity(dto.getJugador());

        // Crear nave según el tipo especificado
        NaveDTO naveDTO = dto.getNave();
        Nave nave = crearNave(naveDTO);

        ResultadoAddNave resultado = servidor.addNave(jugador, nave, coordenadas);

        enviarMensaje("RESULTADO_ADD_NAVE", resultado);
    }

    private Nave crearNave(NaveDTO naveDTO) {
        if (naveDTO == null || naveDTO.getTipo() == null) {
            return null;
        }

        // Convertir TipoNaveDTO a TipoNave
        models.enums.TipoNave tipoNave = models.enums.TipoNave.valueOf(naveDTO.getTipo().name());
        return NaveFactory.crear(tipoNave, naveDTO.getOrientacion());
    }

    private void realizarDisparo(Mensaje mensaje) {
        DisparoDTO disparoDTO = MensajeriaHelper.extraerDatos(mensaje, DisparoDTO.class);

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
        JugadorDTO jugadorDTO = MensajeriaHelper.extraerDatos(mensaje, JugadorDTO.class);
        enviarMensaje("JUGADOR_UNIDO", jugadorDTO);
    }

    private void manejarAbandonarPartidaSv(Mensaje mensaje) {
        JugadorDTO jugadorDTO = MensajeriaHelper.extraerDatos(mensaje, JugadorDTO.class);

        // Usar JugadorMapper para convertir DTO a entidad
        Jugador jugador = dtos.mappers.JugadorMapper.toEntity(jugadorDTO);

        // 1. Lógica REAL del servidor
        servidor.abandonarPartida(jugador);

        // 2. Notificar al otro jugador
        enviarMensaje("JUGADOR_ABANDONO", jugadorDTO);
    }

}
