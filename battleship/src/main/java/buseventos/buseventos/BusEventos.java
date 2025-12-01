package buseventos.buseventos;

import buseventos.Mensaje;
import buseventos.TipoAccion;
import buseventos.servidorsocket.UserServerThread;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import models.entidades.Jugador;
import models.entidades.Partida;
import models.enums.EstadoPartida;
import models.enums.ResultadoUnirsePartida;
import servidor.cronometro.Cronometro;
import servidor.modelo.GestorPartidas;
import servidor.validacion.ValidadorJugador;
import shared.dto.CrearPartidaDTO;
import shared.dto.JugadorDTO;
import shared.dto.UnirsePartidaDTO;

/**
 *
 * @author daniel
 */
public class BusEventos {

    private Map<String, Set<UserServerThread>> eventos;
    private GestorPartidas gestorPartidas;
    private Gson gson;

    public BusEventos(Map mapa) {
        this.eventos = mapa;
        this.gestorPartidas = GestorPartidas.getInstancia();
        this.gson = new Gson();
    }

    private void publicar(String evento, Mensaje mensaje) {
        System.out.println("PUBLICAR");
        String jsonMensaje = gson.toJson(mensaje);
        System.out.println(evento);
        if (!eventos.containsKey(evento)) {
            eventos.put(evento, new HashSet<>());
        }
        eventos.get(evento).forEach(s -> s.sendMessage(jsonMensaje));
    }

    private void suscribirse(String evento, UserServerThread suscriptor) {
        System.out.println("SUSCRIBIR");
        if (eventos.containsKey(evento)) {
            if (!eventos.get(evento).contains(suscriptor)) {
                eventos.get(evento).add(suscriptor);
            }
        } else {
            eventos.put(evento, new HashSet<>());
            eventos.get(evento).add(suscriptor);
        }
    }

    public void manejarEvento(String json, UserServerThread cliente) {
        Mensaje mensaje = gson.fromJson(json, Mensaje.class);
        System.out.println("ID DEL CLIENTE: " + mensaje.getIdPublicador());

        if (mensaje.getAccion() == TipoAccion.SUSCRIBIR) {
            suscribirse(mensaje.getEvento(), cliente);
        } else if (mensaje.getAccion() == TipoAccion.PUBLICAR) {
            // Procesar eventos del servidor primero
            boolean procesado = procesarEventoServidor(mensaje, cliente);

            // Si no fue un evento del servidor, hacer pub/sub normal
            if (!procesado) {
                publicar(mensaje.getEvento(), mensaje);
            }
        }
    }

    /**
     * Procesa eventos que requieren logica del servidor.
     * @return true si el evento fue procesado por el servidor
     */
    private boolean procesarEventoServidor(Mensaje mensaje, UserServerThread cliente) {
        String evento = mensaje.getEvento();

        switch (evento) {
            case "CREAR_PARTIDA":
                manejarCrearPartida(mensaje, cliente);
                return true;
            case "VALIDAR_CODIGO":
                manejarValidarCodigo(mensaje, cliente);
                return true;
            case "UNIRSE_PARTIDA":
                manejarUnirsePartida(mensaje, cliente);
                return true;
            case "IR_A_COLOCAR_NAVES":
                manejarIrAColocarNaves(mensaje, cliente);
                return true;
            case "JUGADOR_LISTO":
                manejarJugadorListo(mensaje, cliente);
                return true;
            default:
                return false;
        }
    }

    private void enviarMensajePrivado(String idCliente, String subEvento, Object datos, UserServerThread cliente) {
        Mensaje respuesta = new Mensaje(
            TipoAccion.PUBLICAR,
            "MENSAJE_CLIENTE_" + idCliente,
            gson.toJsonTree(datos),
            idCliente,
            subEvento
        );
        cliente.sendMessage(gson.toJson(respuesta));
    }

    private void manejarCrearPartida(Mensaje mensaje, UserServerThread cliente) {
        System.out.println("Servidor: Recibio 'CREAR_PARTIDA'.");
        CrearPartidaDTO solicitud = gson.fromJson(mensaje.getData(), CrearPartidaDTO.class);
        String idCliente = mensaje.getIdPublicador();

        // Validar jugador
        ValidadorJugador.ResultadoValidacion validacion =
            ValidadorJugador.validarJugadorDTO(solicitud.getJugador());

        if (!validacion.isValido()) {
            CrearPartidaDTO respuesta = new CrearPartidaDTO(false, validacion.getMensaje());
            enviarMensajePrivado(idCliente, "RESULTADO_CREAR_PARTIDA", respuesta, cliente);
            return;
        }

        String codigo = solicitud.getCodigoPartida();

        // Si no viene codigo, generar uno nuevo
        if (codigo == null || codigo.isEmpty()) {
            codigo = gestorPartidas.generarCodigo();
        }

        // Verificar que el codigo no exista
        if (gestorPartidas.existePartida(codigo)) {
            CrearPartidaDTO respuesta = new CrearPartidaDTO(false,
                "El codigo de partida ya existe. Intenta de nuevo.");
            enviarMensajePrivado(idCliente, "RESULTADO_CREAR_PARTIDA", respuesta, cliente);
            return;
        }

        // Crear la partida del servidor con cronometro
        Cronometro cronometro = new Cronometro(30000); // 30 segundos por turno
        Partida nuevaPartida = new Partida(
            null,  // turno (se asigna al empezar)
            new ArrayList<>(),  // jugadores
            0,  // cantBarcos
            0,  // cantSubmarinos
            0,  // cantCruceros
            0,  // cantPortaAviones
            0,  // totalNaves
            EstadoPartida.POR_EMPEZAR,
            new ArrayList<>(),  // suscriptores
            cronometro
        );
        cronometro.setPartida(nuevaPartida);

        // Registrar la partida
        if (!gestorPartidas.registrarPartida(codigo, nuevaPartida)) {
            CrearPartidaDTO respuesta = new CrearPartidaDTO(false,
                "Error al registrar la partida en el servidor.");
            enviarMensajePrivado(idCliente, "RESULTADO_CREAR_PARTIDA", respuesta, cliente);
            return;
        }

        // Agregar el jugador creador a la partida
        JugadorDTO jugadorDTO = solicitud.getJugador();
        Jugador jugador = new Jugador(
            jugadorDTO.getNombre(),
            jugadorDTO.getColor(),
            jugadorDTO.getEstado()
        );
        nuevaPartida.addJugador(jugador);

        // Enviar respuesta exitosa
        CrearPartidaDTO respuesta = new CrearPartidaDTO(codigo, jugadorDTO);
        respuesta.setExito(true);
        respuesta.setMensaje("Partida creada exitosamente con codigo: " + codigo);

        enviarMensajePrivado(idCliente, "RESULTADO_CREAR_PARTIDA", respuesta, cliente);

        System.out.println("Servidor: Partida creada con codigo " + codigo);
    }

    private void manejarValidarCodigo(Mensaje mensaje, UserServerThread cliente) {
        System.out.println("Servidor: Recibio 'VALIDAR_CODIGO'.");
        UnirsePartidaDTO solicitud = gson.fromJson(mensaje.getData(), UnirsePartidaDTO.class);
        String idCliente = mensaje.getIdPublicador();

        String codigo = solicitud.getCodigoPartida();
        UnirsePartidaDTO respuesta = new UnirsePartidaDTO();
        respuesta.setCodigoPartida(codigo);

        // Validar formato del codigo
        if (!gestorPartidas.validarFormatoCodigo(codigo)) {
            respuesta.setResultado(ResultadoUnirsePartida.CODIGO_INVALIDO);
            respuesta.setMensaje("El codigo debe tener 5 caracteres alfanumericos.");
            enviarMensajePrivado(idCliente, "RESULTADO_VALIDAR_CODIGO", respuesta, cliente);
            return;
        }

        // Verificar si existe la partida
        if (!gestorPartidas.existePartida(codigo)) {
            respuesta.setResultado(ResultadoUnirsePartida.PARTIDA_NO_ENCONTRADA);
            respuesta.setMensaje("No se encontro una partida con el codigo: " + codigo);
            enviarMensajePrivado(idCliente, "RESULTADO_VALIDAR_CODIGO", respuesta, cliente);
            return;
        }

        // Verificar si puede unirse
        if (!gestorPartidas.puedeUnirse(codigo)) {
            respuesta.setResultado(ResultadoUnirsePartida.PARTIDA_LLENA);
            respuesta.setMensaje("La partida ya tiene 2 jugadores.");
            enviarMensajePrivado(idCliente, "RESULTADO_VALIDAR_CODIGO", respuesta, cliente);
            return;
        }

        // Codigo valido
        respuesta.setResultado(ResultadoUnirsePartida.EXITO);
        respuesta.setMensaje("Codigo valido. Puedes unirte a la partida.");
        enviarMensajePrivado(idCliente, "RESULTADO_VALIDAR_CODIGO", respuesta, cliente);
    }

    private void manejarUnirsePartida(Mensaje mensaje, UserServerThread cliente) {
        System.out.println("Servidor: Recibio 'UNIRSE_PARTIDA'.");
        UnirsePartidaDTO solicitud = gson.fromJson(mensaje.getData(), UnirsePartidaDTO.class);
        String idCliente = mensaje.getIdPublicador();

        String codigo = solicitud.getCodigoPartida();
        JugadorDTO jugadorDTO = solicitud.getJugador();

        UnirsePartidaDTO respuesta = new UnirsePartidaDTO();
        respuesta.setCodigoPartida(codigo);
        respuesta.setJugador(jugadorDTO);

        // Validar jugador
        ValidadorJugador.ResultadoValidacion validacion =
            ValidadorJugador.validarJugadorDTO(jugadorDTO);

        if (!validacion.isValido()) {
            respuesta.setResultado(ResultadoUnirsePartida.ERROR_SERVIDOR);
            respuesta.setMensaje(validacion.getMensaje());
            enviarMensajePrivado(idCliente, "RESULTADO_UNIRSE_PARTIDA", respuesta, cliente);
            return;
        }

        // Verificar que exista la partida
        Partida partida = gestorPartidas.obtenerPartida(codigo);
        if (partida == null) {
            respuesta.setResultado(ResultadoUnirsePartida.PARTIDA_NO_ENCONTRADA);
            respuesta.setMensaje("No se encontro la partida con codigo: " + codigo);
            enviarMensajePrivado(idCliente, "RESULTADO_UNIRSE_PARTIDA", respuesta, cliente);
            return;
        }

        // Verificar que no este llena
        if (partida.getJugadores().size() >= 2) {
            respuesta.setResultado(ResultadoUnirsePartida.PARTIDA_LLENA);
            respuesta.setMensaje("La partida ya tiene 2 jugadores.");
            enviarMensajePrivado(idCliente, "RESULTADO_UNIRSE_PARTIDA", respuesta, cliente);
            return;
        }

        // Verificar nombre duplicado
        boolean nombreDuplicado = partida.getJugadores().stream()
            .anyMatch(j -> j.getNombre().equalsIgnoreCase(jugadorDTO.getNombre()));

        if (nombreDuplicado) {
            respuesta.setResultado(ResultadoUnirsePartida.NOMBRE_DUPLICADO);
            respuesta.setMensaje("Ya hay un jugador con ese nombre en la partida.");
            enviarMensajePrivado(idCliente, "RESULTADO_UNIRSE_PARTIDA", respuesta, cliente);
            return;
        }

        // Agregar jugador a la partida
        Jugador jugador = new Jugador(
            jugadorDTO.getNombre(),
            jugadorDTO.getColor(),
            jugadorDTO.getEstado()
        );
        partida.addJugador(jugador);

        // Respuesta exitosa
        respuesta.setResultado(ResultadoUnirsePartida.EXITO);
        respuesta.setMensaje("Te uniste exitosamente a la partida.");

        // Notificar al cliente que se unio
        enviarMensajePrivado(idCliente, "RESULTADO_UNIRSE_PARTIDA", respuesta, cliente);

        // Notificar a TODOS los suscritos sobre CADA jugador de la partida
        for (Jugador j : partida.getJugadores()) {
            JugadorDTO jDTO = new JugadorDTO(j.getNombre(), j.getColor(), j.getEstado());
            Mensaje notificacion = new Mensaje(
                TipoAccion.PUBLICAR,
                "JUGADOR_UNIDO",
                gson.toJsonTree(jDTO),
                idCliente
            );
            publicar("JUGADOR_UNIDO", notificacion);
        }

        System.out.println("Servidor: " + jugadorDTO.getNombre() + " se unio a partida " + codigo);
        System.out.println("Servidor: Total jugadores en partida: " + partida.getJugadores().size());
    }

    public void removeSuscriptor(UserServerThread user) {
        if (user != null) {
            for (Set<UserServerThread> users : eventos.values()) {
                users.remove(user);
            }
        }
    }

    public void addNewClient(String event, UserServerThread client) {
        System.out.println(event);
        if (!eventos.containsKey(event)) {
            eventos.put(event, new HashSet<>());
            eventos.get(event).add(client);
            System.out.println("ID ASIGNADO");
        }
    }

    /**
     * Maneja la notificacion del host para ir a colocar naves.
     * Transmite a todos los clientes suscritos.
     */
    private void manejarIrAColocarNaves(Mensaje mensaje, UserServerThread cliente) {
        System.out.println("BusEventos: Host indico ir a colocar naves, notificando a todos...");

        // Crear mensaje para transmitir
        Mensaje notificacion = new Mensaje(
            TipoAccion.PUBLICAR,
            "IR_A_COLOCAR_NAVES",
            null,
            mensaje.getIdPublicador()
        );

        // Publicar a todos los suscritos a este evento
        publicar("IR_A_COLOCAR_NAVES", notificacion);
    }

    /**
     * Maneja la notificacion de que un jugador esta listo con sus naves.
     * Transmite como OPONENTE_LISTO a todos los clientes suscritos.
     */
    private void manejarJugadorListo(Mensaje mensaje, UserServerThread cliente) {
        System.out.println("BusEventos: Jugador listo con naves, notificando al oponente...");

        // Crear mensaje para transmitir
        Mensaje notificacion = new Mensaje(
            TipoAccion.PUBLICAR,
            "OPONENTE_LISTO",
            mensaje.getData(),
            mensaje.getIdPublicador()
        );

        // Publicar a todos los suscritos a este evento
        publicar("OPONENTE_LISTO", notificacion);
    }
}
