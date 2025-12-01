package models.control;

import java.time.Instant;
import java.util.List;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.enums.ResultadoAddNave;
import models.enums.ResultadoDisparo;
import models.observador.ISuscriptor;
import shared.dto.AddNaveDTO;
import shared.dto.DisparoDTO;
import shared.dto.JugadorDTO;
import shared.dto.NaveDTO;
import shared.dto.TableroDTO;

/**
 *
 * @author daniel
 */
public class ControlModelo implements IModeloCliente {

    private JugadorDTO jugador;
    private TableroDTO tablero;
    private List<NaveDTO> naves;
    private boolean turno;
    private List<ISuscriptor> suscriptores;
    private List<Jugador> jugadores;

    public ControlModelo() {
        this.jugadores = new java.util.ArrayList<>();
    }

    public ControlModelo(JugadorDTO jugador, TableroDTO tablero, List<NaveDTO> naves, boolean turno, List<ISuscriptor> suscriptores) {
        this.jugador = jugador;
        this.tablero = tablero;
        this.naves = naves;
        this.turno = turno;
        this.suscriptores = suscriptores;
        this.jugadores = new java.util.ArrayList<>();
    }

    @Override
    public DisparoDTO realizarDisparo(Coordenadas coordenadas) {
        if (!turno) {
            System.out.println("No es el turno del Jugador");
            return null;
        }
        if (coordenadas.getX() > tablero.getLimiteX() || coordenadas.getX() < 0) {
            System.out.println("Las Coordenadas en X estan fuera del limite");
            return null;
        }
        if (coordenadas.getY() > tablero.getLimiteY() || coordenadas.getY() < 0) {
            System.out.println("Las coordenas en Y estan fuera del limite");
            return null;
        }

        DisparoDTO disparo = new DisparoDTO(jugador, coordenadas, null, null, Instant.now().toEpochMilli());
        return disparo;
    }

    @Override
    public void manejarResultadoDisparo(DisparoDTO disparo) {
        if (disparo.getResultadoDisparo() == ResultadoDisparo.DISPARO_FUERA_TIEMPO) {
            System.out.println(disparo.getResultadoDisparo());
            return;
        }
        if (disparo.getResultadoDisparo() == ResultadoDisparo.TURNO_INCORRECTO) {
            System.out.println(disparo.getResultadoDisparo());
            return;
        }

        notificarAllSuscriptores("RESULTADO_DISPARO", disparo);
    }

    /**
     * Valida y prepara una nave para ser agregada.
     * NOTA: Este metodo solo realiza validaciones BASICAS de UI.
     * La validacion COMPLETA (colisiones, adyacencias, etc.) se hace en el SERVIDOR.
     * Esto evita duplicacion de codigo y asegura consistencia.
     *
     * @param nave nave a agregar
     * @param coordenadas coordenadas donde colocar la nave
     * @return DTO para enviar al servidor, o null si falla la validacion basica
     */
    @Override
    public AddNaveDTO addNave(NaveDTO nave, List<Coordenadas> coordenadas) {
        // === VALIDACIONES BASICAS DE UI ===
        // Estas validaciones dan feedback rapido al usuario sin necesidad de ir al servidor

        // Verificar que la nave no sea nula
        if (nave == null) {
            System.out.println("Error: Nave no seleccionada.");
            return null;
        }

        // Verificar que las coordenadas no esten vacias
        if (coordenadas == null || coordenadas.isEmpty()) {
            System.out.println("Error: No se seleccionaron coordenadas.");
            return null;
        }

        // Verificar numero de coordenadas
        if (coordenadas.size() != nave.getTamanio()) {
            System.out.println("Error: Numero de coordenadas incorrecto para la nave.");
            return null;
        }

        // Verificar limites del tablero (feedback rapido)
        if (tablero != null) {
            for (Coordenadas c : coordenadas) {
                if (c.getX() < 0 || c.getX() > tablero.getLimiteX()
                        || c.getY() < 0 || c.getY() > tablero.getLimiteY()) {
                    System.out.println("Error: Coordenadas fuera del tablero.");
                    return null;
                }
            }
        }

        // === FIN VALIDACIONES BASICAS ===
        // La validacion de orientacion, consecutividad, colisiones y adyacencias
        // se realiza en el SERVIDOR (Partida.addNave via ValidadorNave)

        // Preparar DTO para enviar al servidor
        AddNaveDTO naveDTO = new AddNaveDTO(jugador, nave, coordenadas);

        return naveDTO;
    }

    @Override
    public void manejarResultadoAddNave(ResultadoAddNave resultado) {
        System.out.println("RESULTADO ADD NAVE EN ModeloCliente: " + resultado);
    }

    @Override
    public void addJugador(Jugador j) {
        if (j != null) {
            jugadores.add(j);
            System.out.println("Jugador agregado: " + j.getNombre());
        }
    }

    @Override
    public void crearTableros() {
        // Los tableros se crean en el servidor, aquí solo inicializamos el DTO
        if (tablero == null) {
            tablero = new TableroDTO(10, 10);
        }
        System.out.println("Tableros inicializados.");
    }

    @Override
    public void suscribirAPartida(ISuscriptor suscriptor) {
        suscriptores.add(suscriptor);
    }

    @Override
    public void notificarAllSuscriptores(String contexto, Object datos) {
        suscriptores.forEach(s -> s.notificar(contexto, datos));
    }

    @Override
    public void unirsePartida(Jugador jugador) {
        if (jugador == null) {
            System.out.println("Error: Jugador nulo al unirse a partida.");
            return;
        }

        // Guardar el jugador local
        this.jugador = new JugadorDTO(
                jugador.getNombre(),
                jugador.getColor(),
                jugador.getEstado()
        );

        // Agregar a la lista de jugadores
        jugadores.add(jugador);

        System.out.println("Jugador " + jugador.getNombre() + " se unio a la partida.");
    }

    @Override
    public void empezarPartida() {
        System.out.println("Partida iniciada desde el cliente.");
        // La lógica principal está en el servidor
    }

    @Override
    public void abandonarLobby(Jugador jugador) {
        JugadorDTO dto = new JugadorDTO(jugador.getNombre(), jugador.getColor(), jugador.getEstado());
        notificarAllSuscriptores("ABANDONAR_PARTIDA", dto);
    }

    @Override
    public List<Jugador> getJugadores() {
        return jugadores;
    }

    @Override
    public JugadorDTO getJugador() {
        return jugador;
    }

    @Override
    public JugadorDTO abandonarPartida(Jugador jugador) {

        // VALIDACIONES (igualitas al estilo de addNave)
        if (jugador == null) {
            System.out.println("Error: Jugador nulo.");
            return null;
        }
        if (jugador.getNombre() == null || jugador.getNombre().isBlank()) {
            System.out.println("Error: Jugador sin nombre.");
            return null;
        }

        return new JugadorDTO(
                jugador.getNombre(),
                jugador.getColor(),
                jugador.getEstado()
        );
    }

}
