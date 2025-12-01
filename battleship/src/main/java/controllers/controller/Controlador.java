package controllers.controller;

import buseventos.Mensaje;
import buseventos.TipoAccion;
import clientesocket.ClienteSocket;
import clientesocket.IClienteSocket;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.entidades.Nave;
import models.builder.Director;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import models.builder.PartidaBuilder;
import models.control.ControlModelo;
import models.enums.ResultadoUnirsePartida;
import models.observador.ISuscriptor;
import shared.dto.CrearPartidaDTO;
import shared.dto.DisparoDTO;
import models.control.IModeloCliente;
import models.enums.ResultadoAddNave;
import servidor.modelo.ServidorManager;
import shared.dto.AddNaveDTO;
import shared.dto.JugadorDTO;
import shared.dto.NaveDTO;
import shared.dto.UnirsePartidaDTO;

public class Controlador implements IControlador, ManejadorRespuestaCliente {

    private IModeloCliente partida;
    private IClienteSocket cliente;
    private Map<String, Consumer<Mensaje>> manejadorEventos;

    public Controlador() {
    }

    public Controlador(IModeloCliente partida, IClienteSocket cliente, Map<String, Consumer<Mensaje>> mapa) {
        this.partida = partida;
        this.cliente = cliente;
        this.manejadorEventos = mapa;

        // Registrar manejadores de eventos
        manejadorEventos.put("RESULTADO_DISPARO", this::manejarResultadoDisparo);
        manejadorEventos.put("JUGADOR_UNIDO", this::manejarJugadorUnido);
        manejadorEventos.put("JUGADOR_ABANDONO", this::manejarAbandonarPartida);
        manejadorEventos.put("UNIRSE_PARTIDA", this::manejarUnirsePartidaLegacy);
        manejadorEventos.put("EMPEZAR_PARTIDA", this::manejarEmpezarPartida);
        manejadorEventos.put("ABANDONAR_LOBBY", this::manejarAbandonarLobby);
        manejadorEventos.put("RESULTADO_ADD_NAVE", this::manejarResultadoAddNave);
        manejadorEventos.put("RESULTADO_CREAR_PARTIDA", this::manejarResultadoCrearPartida);
        manejadorEventos.put("RESULTADO_VALIDAR_CODIGO", this::manejarResultadoValidarCodigo);
        manejadorEventos.put("RESULTADO_UNIRSE_PARTIDA", this::manejarResultadoUnirsePartida);
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

    // Metodo para manejar el mensaje recibido por la red.
    @Override
    public void manejarMensaje(String json) {
        Gson gson = new Gson();
        Mensaje mensaje = gson.fromJson(json, Mensaje.class);

        manejadorEventos.get(mensaje.getEvento()).accept(mensaje);
    }

    // Metodo para asignar un metodo al Map cuando se asigne un id al Cliente.
    @Override
    public void onIdSet(String id) {
        manejadorEventos.put("MENSAJE_CLIENTE_" + id, this::manejarEventoPrivado);
    }

    private void manejarEventoPrivado(Mensaje mensaje) {
        manejadorEventos.get(mensaje.getSubEvento()).accept(mensaje);
    }

    private void manejarResultadoAddNave(Mensaje mensaje) {
        Gson gson = new Gson();
        ResultadoAddNave resultado = gson.fromJson(mensaje.getData(), ResultadoAddNave.class);

        partida.manejarResultadoAddNave(resultado);
    }

    private void manejarResultadoDisparo(Mensaje mensaje) {
        Gson gson = new Gson();
        DisparoDTO d = gson.fromJson(mensaje.getData(), DisparoDTO.class);

        partida.manejarResultadoDisparo(d);
    }

    //Recibe el mensaje del servidor
    public void manejarAbandonarPartida(Mensaje mensaje) {
        Gson gson = new Gson();
        JugadorDTO jugadorDTO = gson.fromJson(mensaje.getData(), JugadorDTO.class);
        System.out.println("El jugador " + jugadorDTO.getNombre() + " abandono la partida.");
        partida.notificarAllSuscriptores("ABANDONO_PARTIDA", jugadorDTO);

    }

    //Manda mensaje* al servidor
    @Override
    public void abandonarPartida(Jugador jugador) {
        // Validaciones del modelo
        JugadorDTO dto = partida.abandonarPartida(jugador);

        if (dto == null) {
            System.out.println("No se pudo abandonar la partida.");
            return;
        }
        enviarMensaje("ABANDONAR_PARTIDA", dto);
    }

    @Override
    @Deprecated
    public String crearPartida(Jugador j) {
        Director d = new Director();
        IModeloCliente modelo = d.makePartida(new PartidaBuilder());
        this.partida = modelo;
        return "Partida creada correctamente";
    }

    @Override
    public void crearPartida(JugadorDTO jugadorDTO, String codigoPartida) {
        // Crear modelo local
        Director d = new Director();
        IModeloCliente modelo = d.makePartida(new PartidaBuilder());
        this.partida = modelo;

        // IMPORTANTE: Suscribir ControlVista al nuevo modelo
        ControlVista controlVista = ControlVista.getInstancia();
        this.partida.suscribirAPartida(controlVista);

        // Agregar el jugador local al modelo recién creado
        Jugador jugadorLocal = new Jugador(
            jugadorDTO.getNombre(),
            jugadorDTO.getColor(),
            jugadorDTO.getEstado()
        );
        this.partida.addJugador(jugadorLocal);

        // Preparar DTO para enviar al servidor
        CrearPartidaDTO crearDTO = new CrearPartidaDTO(codigoPartida, jugadorDTO);

        // Enviar solicitud al servidor
        enviarMensaje("CREAR_PARTIDA", crearDTO);
    }

    @Override
    public void validarCodigoPartida(String codigo) {
        UnirsePartidaDTO validarDTO = new UnirsePartidaDTO();
        validarDTO.setCodigoPartida(codigo);
        enviarMensaje("VALIDAR_CODIGO", validarDTO);
    }

    @Override
    public void unirsePartida(JugadorDTO jugadorDTO, String codigoPartida) {
        // Crear modelo local
        Director d = new Director();
        IModeloCliente modelo = d.makePartida(new PartidaBuilder());
        this.partida = modelo;

        // IMPORTANTE: Suscribir ControlVista al nuevo modelo
        ControlVista controlVista = ControlVista.getInstancia();
        this.partida.suscribirAPartida(controlVista);

        // Agregar el jugador local al modelo recién creado
        Jugador jugadorLocal = new Jugador(
            jugadorDTO.getNombre(),
            jugadorDTO.getColor(),
            jugadorDTO.getEstado()
        );
        this.partida.addJugador(jugadorLocal);

        // Preparar DTO para enviar al servidor
        UnirsePartidaDTO unirseDTO = new UnirsePartidaDTO(codigoPartida, jugadorDTO);

        // Enviar solicitud al servidor
        enviarMensaje("UNIRSE_PARTIDA", unirseDTO);
    }

    private void manejarResultadoCrearPartida(Mensaje mensaje) {
        Gson gson = new Gson();
        CrearPartidaDTO resultado = gson.fromJson(mensaje.getData(), CrearPartidaDTO.class);

        if (resultado.isExito()) {
            System.out.println("Partida creada exitosamente: " + resultado.getCodigoPartida());
            // Guardar datos del jugador en el modelo local
            if (resultado.getJugador() != null) {
                Jugador jugador = new Jugador(
                    resultado.getJugador().getNombre(),
                    resultado.getJugador().getColor(),
                    resultado.getJugador().getEstado()
                );
                partida.addJugador(jugador);
            }
        } else {
            System.out.println("Error al crear partida: " + resultado.getMensaje());
        }

        // Notificar a la vista
        partida.notificarAllSuscriptores("RESULTADO_CREAR_PARTIDA", resultado);
    }

    private void manejarResultadoValidarCodigo(Mensaje mensaje) {
        Gson gson = new Gson();
        UnirsePartidaDTO resultado = gson.fromJson(mensaje.getData(), UnirsePartidaDTO.class);

        System.out.println("Resultado validar codigo: " + resultado.getResultado());

        // Notificar a la vista
        partida.notificarAllSuscriptores("RESULTADO_VALIDAR_CODIGO", resultado);
    }

    private void manejarResultadoUnirsePartida(Mensaje mensaje) {
        Gson gson = new Gson();
        UnirsePartidaDTO resultado = gson.fromJson(mensaje.getData(), UnirsePartidaDTO.class);

        if (resultado.isExito()) {
            System.out.println("Te uniste exitosamente a la partida: " + resultado.getCodigoPartida());
            // Guardar datos del jugador en el modelo local
            if (resultado.getJugador() != null) {
                Jugador jugador = new Jugador(
                    resultado.getJugador().getNombre(),
                    resultado.getJugador().getColor(),
                    resultado.getJugador().getEstado()
                );
                partida.addJugador(jugador);
            }
        } else {
            System.out.println("Error al unirse: " + resultado.getMensaje());
        }

        // Notificar a la vista
        partida.notificarAllSuscriptores("RESULTADO_UNIRSE_PARTIDA", resultado);
    }

    @Override
    public void realizarDisparo(Coordenadas coordenadas) {
        DisparoDTO disparo = partida.realizarDisparo(coordenadas);
        if (disparo != null) {
            enviarMensaje("DISPARO", disparo);
        }
    }

    private void manejarJugadorUnido(Mensaje mensaje) {
        System.out.println("=== RECIBIDO JUGADOR_UNIDO ===");
        System.out.println("Data recibida: " + mensaje.getData());
        Gson gson = new Gson();
        JugadorDTO jugadorDTO = gson.fromJson(mensaje.getData(), JugadorDTO.class);
        System.out.println("Jugador deserializado - Nombre: '" + jugadorDTO.getNombre() + "', Color: " + jugadorDTO.getColor());

        // Agregar el jugador al modelo local si no existe
        boolean existe = partida.getJugadores().stream()
            .anyMatch(j -> j.getNombre().equals(jugadorDTO.getNombre()));

        if (!existe) {
            Jugador nuevoJugador = new Jugador(jugadorDTO.getNombre(), jugadorDTO.getColor(), jugadorDTO.getEstado());
            partida.addJugador(nuevoJugador);
            System.out.println("Jugador agregado al modelo local: " + jugadorDTO.getNombre());
        }

        // Notificar a los suscriptores locales (esto actualiza el lobby)
        System.out.println("Notificando a suscriptores del modelo...");
        partida.notificarAllSuscriptores("JUGADOR_UNIDO", jugadorDTO);
    }

    @Override
    public void addNave(NaveDTO nave, List<Coordenadas> coordenadas) {
        AddNaveDTO addDTO = partida.addNave(nave, coordenadas);
        if (addDTO != null) {
            enviarMensaje("ADD_NAVE", addDTO);
        }
    }

    @Override
    public void addJugador(Jugador jugador) {
        partida.addJugador(jugador);
    }

    @Override
    public void crearTableros() {
        partida.crearTableros();
    }

    @Override
    public void suscribirAPartida(ISuscriptor suscriptor) {
        partida.suscribirAPartida(suscriptor);
    }

    @Override
    public JugadorDTO getJugador() {
        return partida.getJugador();
    }

    // Caso de Uso: Unirse Partida
    @Override
    public void unirsePartida(JugadorDTO jugadorDTO) {
        Jugador jugador = new Jugador(jugadorDTO.getNombre(), jugadorDTO.getColor(), jugadorDTO.getEstado());
        partida.unirsePartida(jugador);
        enviarMensaje("UNIRSE_PARTIDA", jugadorDTO);
    }

    // Metodo legacy para compatibilidad con el flujo anterior
    private void manejarUnirsePartidaLegacy(Mensaje mensaje) {
        JugadorDTO dto = new Gson().fromJson(mensaje.getData(), JugadorDTO.class);
        System.out.println("=== RECIBIDO UNIRSE_PARTIDA (Legacy) ===");
        System.out.println("Jugador que se unio: " + dto.getNombre());

        partida.notificarAllSuscriptores("UNIRSE_PARTIDA", dto);

        // Si soy el HOST, enviar mi info al nuevo jugador para que me vea
        ControlVista cv = ControlVista.getInstancia();
        System.out.println("Soy HOST? " + cv.isEsHost());

        if (cv.isEsHost()) {
            JugadorDTO miJugador = partida.getJugador();
            System.out.println("Mi jugador: " + (miJugador != null ? miJugador.getNombre() : "NULL"));

            // Solo enviar si tengo info y no soy yo mismo
            if (miJugador != null && !miJugador.getNombre().equals(dto.getNombre())) {
                System.out.println(">>> HOST: Enviando JUGADOR_UNIDO con mi info: " + miJugador.getNombre());
                enviarMensaje("JUGADOR_UNIDO", miJugador);
            } else {
                System.out.println("No envio porque es el mismo jugador o miJugador es null");
            }
        }
    }

    @Override
    public void empezarPartida() {
        partida.empezarPartida();
        enviarMensaje("EMPEZAR_PARTIDA", null);
    }

    private void manejarEmpezarPartida(Mensaje mensaje) {
        JugadorDTO jugadorDTO = new Gson().fromJson(mensaje.getData(), JugadorDTO.class);
        System.out.println("La partida esta comenzando.");
        partida.notificarAllSuscriptores("EMPEZAR_PARTIDA", jugadorDTO);
    }

    @Override
    public void abandonarLobby(JugadorDTO jugadorDTO) {
        Jugador jugador = new Jugador(jugadorDTO.getNombre(), jugadorDTO.getColor(), jugadorDTO.getEstado());
        partida.abandonarLobby(jugador);
        //Mandar mensaje al servidor para avisar al rival
        enviarMensaje("ABANDONAR_LOBBY", jugadorDTO);
    }

    private void manejarAbandonarLobby(Mensaje mensaje) {
        JugadorDTO jugadorDTO = new Gson().fromJson(mensaje.getData(), JugadorDTO.class);
        System.out.println("El jugador " + jugadorDTO.getNombre() + " abandono el lobby.");
        partida.notificarAllSuscriptores("ABANDONAR_LOBBY", jugadorDTO);
    }

    @Override
    public List<JugadorDTO> getJugadores() {
        return partida.getJugadores()
                .stream()
                .map(jugadorEntidad -> new JugadorDTO(jugadorEntidad.getNombre(), jugadorEntidad.getColor(), jugadorEntidad.getEstado()))
                .toList();
    }
}
