package servidor.controlador;

import buseventos.EventoJuego;
import buseventos.Mensaje;
import buseventos.util.MensajeriaHelper;
import controllers.controller.ManejadorRespuestaCliente;
import dtos.AddNaveDTO;
import dtos.DisparoDTO;
import dtos.JugadorDTO;
import dtos.NaveDTO;
import dtos.PuntajeDTO;
import dtos.TiempoDTO;
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
import models.enums.ResultadoDisparo;
import servidor.modelo.IModeloServidor;

public class ControladorServidor implements ManejadorRespuestaCliente {

    private final IModeloServidor servidor;
    private final IOutputChannel outputChannel;
    private final Map<String, Consumer<Mensaje>> manejadoresEventos;

    private static final long TIEMPO_TURNO_MS = 30000;

    public ControladorServidor(IModeloServidor servidor, IOutputChannel outputChannel,
                               Map<String, Consumer<Mensaje>> mapa) {
        this.servidor = servidor;
        this.outputChannel = outputChannel;
        this.manejadoresEventos = mapa;

        registrarManejadores();
    }

    private void registrarManejadores() {
        manejadoresEventos.put(EventoJuego.DISPARO.getValor(), this::realizarDisparo);
        manejadoresEventos.put(EventoJuego.ADD_NAVE.getValor(), this::addNave);
        manejadoresEventos.put(EventoJuego.UNIRSE_PARTIDA.getValor(), this::manejarUnirsePartida);
        manejadoresEventos.put(EventoJuego.ABANDONAR_PARTIDA.getValor(), this::manejarAbandonarPartidaSv);
    }

    private void enviarMensaje(String evento, Object datos) {
        String json = MensajeriaHelper.crearMensajeJSON(evento, datos);
        outputChannel.enviarMensaje(json);
    }

    private void enviarMensaje(EventoJuego evento, Object datos) {
        enviarMensaje(evento.getValor(), datos);
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
        if (dto == null) {
            return;
        }

        List<Coordenadas> coordenadas = CoordenadasMapper.toEntityList(dto.getCoordenadas());
        Jugador jugador = JugadorMapper.toEntity(dto.getJugador());
        NaveDTO naveDTO = dto.getNave();
        Nave nave = NaveMapper.toEntity(naveDTO);

        ResultadoAddNave resultado = servidor.addNave(jugador, nave, coordenadas);

        enviarMensaje(EventoJuego.RESULTADO_ADD_NAVE, ResultadoAddNaveMapper.toDTO(resultado));
    }

    private void realizarDisparo(Mensaje mensaje) {
        DisparoDTO disparoDTO = MensajeriaHelper.extraerDatos(mensaje, DisparoDTO.class);
        if (disparoDTO == null) {
            return;
        }

        Coordenadas coordenadas = CoordenadasMapper.toEntity(disparoDTO.getCoordenadas());
        Jugador jugador = JugadorMapper.toEntity(disparoDTO.getJugador());

        Disparo disparo = servidor.realizarDisparo(coordenadas, jugador, disparoDTO.getTiempo());

        if (disparo == null) {
            DisparoDTO errorDTO = new DisparoDTO(
                    disparoDTO.getJugador(),
                    disparoDTO.getCoordenadas(),
                    dtos.enums.ResultadoDisparoDTO.COORDENADAS_INVALIDAS,
                    null,
                    disparoDTO.getTiempo()
            );
            enviarMensaje(EventoJuego.RESULTADO_DISPARO, errorDTO);
            return;
        }

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

        enviarMensaje(EventoJuego.RESULTADO_DISPARO, resultado);

        enviarTiempoActualizado();
    }

    private void manejarUnirsePartida(Mensaje mensaje) {
        JugadorDTO jugadorDTO = MensajeriaHelper.extraerDatos(mensaje, JugadorDTO.class);
        if (jugadorDTO == null) {
            return;
        }

        Jugador jugador = JugadorMapper.toEntity(jugadorDTO);

        servidor.unirsePartida(jugador);
        servidor.crearTableros();

        System.out.println("[Servidor] Jugador unido: " + jugador.getNombre());
        System.out.println("[Servidor] Jugadores en partida: " + servidor.getJugadores().size());

        for (Jugador j : servidor.getJugadores()) {
            JugadorDTO dto = JugadorMapper.toDTO(j);
            enviarMensaje(EventoJuego.JUGADOR_UNIDO, dto);
        }
    }

    private void manejarAbandonarPartidaSv(Mensaje mensaje) {
        JugadorDTO jugadorDTO = MensajeriaHelper.extraerDatos(mensaje, JugadorDTO.class);
        if (jugadorDTO == null) {
            return;
        }

        Jugador jugador = JugadorMapper.toEntity(jugadorDTO);

        servidor.abandonarPartida(jugador);

        enviarMensaje(EventoJuego.JUGADOR_ABANDONO, jugadorDTO);
    }

    private void enviarTiempoActualizado() {
        long tiempoRestante = servidor.getTiempoRestante();
        String nombreTurno = "";

        Jugador turno = servidor.getTurnoActual();
        if (turno != null) {
            nombreTurno = turno.getNombre();
        }

        TiempoDTO tiempoDTO = new TiempoDTO(tiempoRestante, TIEMPO_TURNO_MS, nombreTurno);
        enviarMensaje(EventoJuego.TIEMPO_ACTUALIZADO, tiempoDTO);
    }

    public void onTiempoAgotado() {
        servidor.cambiarTurno();
        enviarTiempoActualizado();

        Jugador turnoActual = servidor.getTurnoActual();
        if (turnoActual != null) {
            DisparoDTO cambioTurnoDTO = new DisparoDTO(
                    JugadorMapper.toDTO(turnoActual),
                    null,
                    dtos.enums.ResultadoDisparoDTO.DISPARO_FUERA_TIEMPO,
                    null,
                    System.currentTimeMillis()
            );
            enviarMensaje(EventoJuego.CAMBIO_TURNO, cambioTurnoDTO);
        }
    }
}
