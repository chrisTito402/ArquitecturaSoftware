package servidor.controlador;

import buseventos.Mensaje;
import buseventos.util.MensajeriaHelper;
import controllers.controller.ManejadorRespuestaCliente;
import dtos.AddNaveDTO;
import dtos.DisparoDTO;
import dtos.JugadorDTO;
import dtos.NaveDTO;
import dtos.PuntajeDTO;
import dtos.mappers.CoordenadasMapper;
import dtos.mappers.DisparoMapper;
import dtos.mappers.JugadorMapper;
import dtos.mappers.NaveMapper;
import dtos.mappers.PuntajeMapper;
import dtos.mappers.ResultadoAddNaveMapper;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import models.entidades.Coordenadas;
import models.entidades.Disparo;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.enums.ResultadoAddNave;
import servidor.modelo.IModeloServidor;

public class ControladorServidor implements ManejadorRespuestaCliente {

    private IModeloServidor servidor;
    private IOutputChannel outputChannel;
    private Map<String, Consumer<Mensaje>> manejadoresEventos;

    public ControladorServidor(IModeloServidor servidor, IOutputChannel outputChannel, Map<String, Consumer<Mensaje>> mapa) {
        this.servidor = servidor;
        this.outputChannel = outputChannel;
        this.manejadoresEventos = mapa;

        mapa.put("DISPARO", this::realizarDisparo);
        mapa.put("ADD_NAVE", this::addNave);
        mapa.put("UNIRSE_PARTIDA", this::manejarUnirsePartida);
        mapa.put("ABANDONAR_PARTIDA", this::manejarAbandonarPartidaSv);
    }

    private void enviarMensaje(String evento, Object datos) {
        String json = MensajeriaHelper.crearMensajeJSON(evento, datos);
        outputChannel.enviarMensaje(json);
    }

    @Override
    public void manejarMensaje(String json) {
        if (json == null || json.isBlank()) {
            return;
        }

        Mensaje mensaje = MensajeriaHelper.parsearMensaje(json);
        if (mensaje == null || mensaje.getEvento() == null) {
            return;
        }

        Consumer<Mensaje> handler = manejadoresEventos.get(mensaje.getEvento());
        if (handler != null) {
            handler.accept(mensaje);
        }
    }

    private void addNave(Mensaje mensaje) {
        AddNaveDTO dto = MensajeriaHelper.extraerDatos(mensaje, AddNaveDTO.class);

        List<Coordenadas> coordenadas = CoordenadasMapper.toEntityList(dto.getCoordenadas());
        Jugador jugador = JugadorMapper.toEntity(dto.getJugador());
        NaveDTO naveDTO = dto.getNave();
        Nave nave = NaveMapper.toEntity(naveDTO);

        ResultadoAddNave resultado = servidor.addNave(jugador, nave, coordenadas);

        enviarMensaje("RESULTADO_ADD_NAVE", ResultadoAddNaveMapper.toDTO(resultado));
    }

    private void realizarDisparo(Mensaje mensaje) {
        DisparoDTO disparoDTO = MensajeriaHelper.extraerDatos(mensaje, DisparoDTO.class);

        Coordenadas coordenadas = CoordenadasMapper.toEntity(disparoDTO.getCoordenadas());
        Jugador jugador = JugadorMapper.toEntity(disparoDTO.getJugador());

        Disparo disparo = servidor.realizarDisparo(coordenadas, jugador, disparoDTO.getTiempo());

        PuntajeDTO puntajeDTO = null;
        Jugador jugadorConPuntaje = servidor.getJugadores().stream()
                .filter(j -> j.equals(jugador))
                .findFirst()
                .orElse(null);

        if (jugadorConPuntaje != null && jugadorConPuntaje.getPuntaje() != null) {
            puntajeDTO = PuntajeMapper.toDTO(jugadorConPuntaje.getPuntaje());
        }

        DisparoDTO resultado = DisparoMapper.toDTO(disparo);
        resultado.setPuntaje(puntajeDTO);

        enviarMensaje("RESULTADO_DISPARO", resultado);
    }

    private void manejarUnirsePartida(Mensaje mensaje) {
        JugadorDTO jugadorDTO = MensajeriaHelper.extraerDatos(mensaje, JugadorDTO.class);
        Jugador jugador = JugadorMapper.toEntity(jugadorDTO);

        servidor.unirsePartida(jugador);
        servidor.crearTableros();

        System.out.println("[Servidor] Jugador unido: " + jugador.getNombre());
        System.out.println("[Servidor] Jugadores en partida: " + servidor.getJugadores().size());

        // Notificar a todos sobre cada jugador en la partida
        for (Jugador j : servidor.getJugadores()) {
            JugadorDTO dto = JugadorMapper.toDTO(j);
            enviarMensaje("JUGADOR_UNIDO", dto);
        }
    }

    private void manejarAbandonarPartidaSv(Mensaje mensaje) {
        JugadorDTO jugadorDTO = MensajeriaHelper.extraerDatos(mensaje, JugadorDTO.class);
        Jugador jugador = JugadorMapper.toEntity(jugadorDTO);

        servidor.abandonarPartida(jugador);

        enviarMensaje("JUGADOR_ABANDONO", jugadorDTO);
    }
}
