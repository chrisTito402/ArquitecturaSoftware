import buseventos.IEventSuscriptor;
import buseventos.Mensaje;
import buseventos.TipoAccion;
import buseventos.buseventos.BusEventos;
import buseventos.servidorsocket.ServidorSocket;
import config.ConfiguracionRed;
import models.entidades.Partida;
import models.enums.EstadoPartida;
import servidor.controlador.ControladorServidor;
import servidor.controlador.IOutputChannel;
import servidor.cronometro.Cronometro;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class MainServidor {

    private static BusEventos busEventos;

    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("   BATTLESHIP - SERVIDOR");
        System.out.println("=================================");

        try {
            InetAddress localHost = InetAddress.getLocalHost();
            System.out.println("IP del servidor: " + localHost.getHostAddress());
            System.out.println("Nombre del host: " + localHost.getHostName());
        } catch (Exception e) {
            System.out.println("No se pudo obtener la IP local");
        }

        System.out.println("Puerto: " + ConfiguracionRed.SERVIDOR_PUERTO);
        System.out.println("---------------------------------");

        busEventos = new BusEventos();

        Cronometro cronometro = new Cronometro(30000);
        Partida partida = new Partida(
                null,
                new ArrayList<>(),
                3, 4, 2, 2, 11,
                EstadoPartida.POR_EMPEZAR,
                new ArrayList<>(),
                cronometro
        );
        cronometro.setPartida(partida);

        IOutputChannel outputChannel = json -> {
            // Enviar directamente el JSON a todos los suscriptores de BROADCAST
            java.util.Set<buseventos.IEventSuscriptor> suscriptores = busEventos.getSuscriptores("BROADCAST");
            if (suscriptores != null) {
                for (buseventos.IEventSuscriptor suscriptor : suscriptores) {
                    try {
                        suscriptor.recibirEvento(json);
                    } catch (Exception e) {
                        System.err.println("[Servidor] Error enviando a cliente: " + e.getMessage());
                    }
                }
            }
        };

        HashMap<String, Consumer<Mensaje>> manejadores = new HashMap<>();
        ControladorServidor controladorServidor = new ControladorServidor(partida, outputChannel, manejadores);

        IEventSuscriptor suscriptorServidor = new IEventSuscriptor() {
            @Override
            public void recibirEvento(String eventoJSON) {
                controladorServidor.manejarMensaje(eventoJSON);
            }

            @Override
            public String getSuscriptorId() {
                return "servidor-principal";
            }
        };

        busEventos.suscribirse("DISPARO", suscriptorServidor);
        busEventos.suscribirse("ADD_NAVE", suscriptorServidor);
        busEventos.suscribirse("UNIRSE_PARTIDA", suscriptorServidor);
        busEventos.suscribirse("ABANDONAR_PARTIDA", suscriptorServidor);

        ServidorSocket servidor = new ServidorSocket(ConfiguracionRed.SERVIDOR_PUERTO, busEventos);

        System.out.println("Servidor listo. Esperando jugadores...");

        servidor.start();
    }
}
