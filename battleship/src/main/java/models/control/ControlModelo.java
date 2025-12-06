package models.control;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.enums.ColorJugador;
import models.enums.EstadoNave;
import models.enums.OrientacionNave;
import models.enums.ResultadoAddJugador;
import models.enums.ResultadoAddNave;
import models.enums.ResultadoConfirmarNaves;
import models.enums.ResultadoDisparo;
import models.enums.ResultadoEmpezarPartida;
import models.observador.ISuscriptor;
import views.DTOs.AddJugadorDTO;
import views.DTOs.AddNaveDTO;
import views.DTOs.DisparoDTO;
import views.DTOs.JugadorDTO;
import views.DTOs.NaveDTO;
import views.DTOs.ResultadoConfirmarNavesDTO;
import views.DTOs.TableroDTO;
import views.DTOs.TipoNaveDTO;

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
    private List<JugadorDTO> jugadores;
    private List<Coordenadas> cordsNaves;
    private Map<NaveDTO, List<Coordenadas>> navesCords;

    public ControlModelo() {
        this.jugadores = new java.util.ArrayList<>();
    }

    public ControlModelo(JugadorDTO jugador, TableroDTO tablero, List<NaveDTO> naves, boolean turno, List<ISuscriptor> suscriptores, List<Coordenadas> cordsNaves) {
        this.jugador = jugador;
        this.tablero = tablero;
        this.naves = naves;
        this.turno = turno;
        this.suscriptores = suscriptores;
        this.jugadores = new java.util.ArrayList<>();
        this.cordsNaves = cordsNaves;
    }

    public ControlModelo(JugadorDTO jugador, TableroDTO tablero, List<NaveDTO> naves, List<ISuscriptor> suscriptores, List<JugadorDTO> jugadores, List<Coordenadas> cordsNaves, Map<NaveDTO, List<Coordenadas>> navesCords) {
        this.jugador = jugador;
        this.tablero = tablero;
        this.naves = naves;
        this.suscriptores = suscriptores;
        this.jugadores = jugadores;
        this.cordsNaves = cordsNaves;
        this.navesCords = navesCords;
    }

    public ControlModelo(JugadorDTO jugador, TableroDTO tablero, List<NaveDTO> naves, List<ISuscriptor> suscriptores, List<JugadorDTO> jugadores, Map<NaveDTO, List<Coordenadas>> navesCords) {
        this.jugador = jugador;
        this.tablero = tablero;
        this.naves = naves;
        this.suscriptores = suscriptores;
        this.jugadores = jugadores;
        this.navesCords = navesCords;
    }

    @Override
    public DisparoDTO realizarDisparo(Coordenadas coordenadas) {
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

    private void verificarJugadorDisparo(Coordenadas coordenadas, EstadoNave estado) {
        System.out.println("CANTIDAD DISPAROS=" + estado);
        
        for (NaveDTO nave: navesCords.keySet()) {
            for (Coordenadas cords: navesCords.get(nave)) {
                if (cords.equals(coordenadas)) {
                    nave.setEstado(estado);
                    return;
                }
            }
        }
    }
    
    private List<Coordenadas> obtenerCoordenadasNave(Coordenadas coordenadas) {
        boolean result = false;
        for (NaveDTO nave: navesCords.keySet()) {
            for (Coordenadas cords: navesCords.get(nave)) {
                if (cords.equals(coordenadas)) {
                    result = true;
                    break;
                }
            }
            
            if (result) {
                return navesCords.get(nave);
            }
        }
        
        return null;
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
        
        if (!disparo.getJugador().getNombre().equals(jugador.getNombre())) {
            if (disparo.getResultadoDisparo() == ResultadoDisparo.IMPACTO
                    || disparo.getResultadoDisparo() == ResultadoDisparo.HUNDIMIENTO) {
                verificarJugadorDisparo(disparo.getCoordenadas(), disparo.getEstadoNave());
            }
        }
        
        if (disparo.getResultadoDisparo() == ResultadoDisparo.HUNDIMIENTO &&
                disparo.getJugador().getNombre().equals(jugador.getNombre())) {
            List<Coordenadas> cords = obtenerCoordenadasNave(disparo.getCoordenadas());
            if (cords != null) {
                notificarAllSuscriptores("HUNDIMIENTO_NAVE", cords);
            }
        }
        
        if (disparo.getJugador().getNombre().equals(jugador.getNombre())) {
            notificarAllSuscriptores("RESULTADO_DISPARO_PROPIO", disparo);
        } else {
            notificarAllSuscriptores("RESULTADO_DISPARO_ENEMIGO", disparo);
        }
    }

    @Override
    public void manejarCambiarTurno(JugadorDTO jugador) {
        if (!jugador.getNombre().equals(this.jugador.getNombre())) {
            notificarAllSuscriptores("CAMBIAR_TURNO", jugador);
        } else if (jugador.getNombre().equals(this.jugador.getNombre())) {
            notificarAllSuscriptores("CAMBIAR_TURNO", this.jugador);
        }
    }
    
    @Override
    public AddNaveDTO addNave(NaveDTO nave, List<Coordenadas> coordenadas) {
        // Verificar que la nave no sea nula.
        if (nave == null) {
            System.out.println("Error, nave vacia.");
            return null;
        }
        // Verificar que las coordenadas no esten vacias.
        if (coordenadas == null || coordenadas.isEmpty()) {
            System.out.println("Error, coordenadas vascias.");
            return null;
        }

        // Verificar que el numero de coordenadas sea el mismo que el tamaño de la Nave.
        if (coordenadas.size() != nave.getTamanio()) {
            System.out.println("Error, coordenadas extra o insuficientes para la Nave.");
            return null;
        }

        // Verificar que las coordenadas no se salen del limite del tablero.
        for (Coordenadas coordenada : coordenadas) {
            if (coordenada.getY() < 0 || coordenada.getY() > tablero.getLimiteY()
                    || coordenada.getX() < 0 || coordenada.getX() > tablero.getLimiteX()) {
                System.out.println("Error: La nave se sale de los limites del tablero.");
                return null;
            }
        }

        // Verificar que todas las coordenas esten con la misma orientacion.
        if (nave.getOrientacion() == OrientacionNave.VERTICAL) {
            int y = coordenadas.getFirst().getY();
            for (Coordenadas coordenada : coordenadas) {
                if (coordenada.getY() != y) {
                    System.out.println("Error: La nave no esta ordenada Verticalmente");
                    return null;
                }
            }
        } else if (nave.getOrientacion() == OrientacionNave.HORIZONTAL) {
            int x = coordenadas.getFirst().getX();
            for (Coordenadas coordenada : coordenadas) {
                if (coordenada.getX() != x) {
                    System.out.println("Error: La nave no esta ordenada Horizontalmente.");
                    return null;
                }
            }
        }

        // Ordenar lista por "X" y "Y".
        coordenadas.sort(Comparator.comparingInt(Coordenadas::getX)
                .thenComparingInt(Coordenadas::getY));

        // Verificar que cada Coordenada este consecutiva de la otra.
        if (nave.getOrientacion() == OrientacionNave.VERTICAL) {
            for (int i = coordenadas.size() - 1; i >= 0; i--) {
                if (i == 0) {
                    break;
                }
                if (coordenadas.get(i - 1).getX() != coordenadas.get(i).getX() - 1) {
                    System.out.println("Error: Coordenas no consecutivas en 'X'");
                    return null;
                }
            }
        } else if (nave.getOrientacion() == OrientacionNave.HORIZONTAL) {
            for (int i = coordenadas.size() - 1; i >= 0; i--) {
                if (i == 0) {
                    break;
                }
                if (coordenadas.get(i - 1).getY() != coordenadas.get(i).getY() - 1) {
                    System.out.println("Error: Coordenas no consecutivas en 'Y'");
                    return null;
                }
            }
        }

        AddNaveDTO naveDTO = new AddNaveDTO(
                jugador,
                nave,
                coordenadas
        );

        return naveDTO;
    }

    @Override
    public JugadorDTO confirmarNaves() {
        if (jugador == null) {
            System.out.println("Error: jugador nulo.");
            return null;
        }
        if (jugador.getNombre() == null || jugador.getNombre().isBlank()) {
            System.out.println("Error: Nombre vacio.");
            return null;
        }

        return jugador;
    }

    @Override
    public void manejarResultadoAddNave(AddNaveDTO resultado) {
        System.out.println("RESULTADO ADD NAVE EN ModeloCliente: " + resultado);

        if (resultado.getResultado() == ResultadoAddNave.NAVE_AÑADIDA && 
                resultado.getJugador().getNombre().equals(jugador.getNombre())) {
            
            naves.add(resultado.getNave());
            navesCords.put(resultado.getNave(), resultado.getCoordenadases());
            resultado.getCoordenadases().forEach(e -> cordsNaves.add(e));
            notificarAllSuscriptores("RESULTADO_ADD_NAVE", resultado);
            return;
        }
        
        notificarAllSuscriptores("ERROR_ADD_NAVE", resultado);
    }

    @Override
    public void manejarResultadoConfirmarNaves(ResultadoConfirmarNaves resultado) {
        if (resultado == null) {
            return;
        }
        
        if (resultado == ResultadoConfirmarNaves.EMPEZAR_PARTIDA) {
            ResultadoConfirmarNavesDTO dto = new ResultadoConfirmarNavesDTO(resultado, cordsNaves, jugador.getColor());
            notificarAllSuscriptores("RESULTADO_CONFIRMAR_NAVES", dto);
        } else {
            notificarAllSuscriptores("ERROR_CONFIRMAR_NAVES", resultado);
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

    public void suscribirAPartida(ISuscriptor suscriptor) {
        suscriptores.add(suscriptor);
    }

    private void notificarAllSuscriptores(String contexto, Object datos) {
        suscriptores.forEach(s -> s.notificar(contexto, datos));
    }

    @Override
    public JugadorDTO unirsePartida(JugadorDTO jugador) {
        if (jugador == null) {
            System.out.println("Error: Jugador nulo al unirse a partida.");
            return jugador;
        }

        // Guardar el jugador local
        this.jugador = jugador;
        
        // Agregar a la lista de jugadores
        jugadores.add(jugador);

        System.out.println("Jugador " + jugador.getNombre() + " se unio a la partida.");
        return jugador;
    }

    @Override
    public boolean empezarPartida() {
        if (jugadores.size() != 2) {
            return false;
        }
        if (jugador == null) {
            return false;
        }
        
        return true;
    }

    @Override
    public void abandonarLobby() {
        JugadorDTO dto = new JugadorDTO(jugador.getNombre(), jugador.getColor(), jugador.getEstado());
        notificarAllSuscriptores("ABANDONAR_PARTIDA", dto);
    }

    @Override
    public JugadorDTO getJugador() {
        return jugador;
    }

    //Envia
    @Override
    public JugadorDTO abandonarPartida() {

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

    //Recibe
    @Override
    public void manejarJugadorAbandono(JugadorDTO dto) {
        if (dto == null) {
            System.out.println("Error el jugador regreso null desde el servidor.");
            return;
        }
        if (!dto.getNombre().equals(jugador.getNombre())) {
            notificarAllSuscriptores("ABANDONO_PARTIDA", dto);
        }
    }

    @Override
    public void manejarJugadorUnido(AddJugadorDTO dto) {
        System.out.println("JUGADOR=" + jugador.toString());
        
        // Verificar si es el Jugador del Modelo
        if (dto.getJugador().getNombre().equals(jugador.getNombre()) && 
                dto.getResultado() == ResultadoAddJugador.AÑADIDO) {
            tablero = new TableroDTO(10, 10);
            notificarAllSuscriptores("RESULTADO_UNIRSE_PARTIDA", dto.getJugador());
        } else if (dto.getResultado() == ResultadoAddJugador.AÑADIDO &&
                !jugadores.contains(dto)) {
            notificarAllSuscriptores("JUGADOR_UNIDO", dto.getJugador());
        } else {
            jugadores.remove(dto.getJugador());
            notificarAllSuscriptores("ERROR_UNIRSE_PARTIDA", dto.getResultado());
        }
    }
    
    @Override
    public void manejarEmpezarPartida(ResultadoEmpezarPartida resultado) {
        if (resultado == ResultadoEmpezarPartida.PARTIDA_INICIADA) {
            notificarAllSuscriptores("EMPEZAR_PARTIDA", resultado);
        }
    }
    
    @Override
    public void manejarObtenerJugadorEnemigo(JugadorDTO jugador) {
        if (jugador == null) {
            return;
        }
        
        ColorJugador[] colores = ColorJugador.values();
        for (ColorJugador color : colores) {
            if (color != this.jugador.getColor()) {
                jugador.setColor(color);
            }
        }
        
        notificarAllSuscriptores("JUGADOR_ENEMIGO_OBTENIDO", jugador);
    }

    @Override
    public JugadorDTO obtenerJugadorEnemigo() {
        if (jugador == null) {
            return null;
        }
        if (jugador.getNombre() == null) {
            return null;
        }
        if (jugador.getNombre().isBlank()) {
            return null;
        }
        
        return jugador;
    }
    
    @Override
    public void obtenerMarcador() {
        System.out.println(cordsNaves);
        
        StringBuilder sb = new StringBuilder();
        String formatoH = "%-15s %10s %10s %10s\n";
        String formatoF = "%-15s %10d %10d %10d\n";

        sb.append("-------------------------------------------------------\n");
        sb.append(String.format(formatoH, "TipoNave", "Sin daños", "Averiada", "Hundida"));
        sb.append("-------------------------------------------------------\n");
        
        TipoNaveDTO[] tipos = TipoNaveDTO.values();
        for (TipoNaveDTO tipo : tipos) {
            List<NaveDTO> naves = navesCords.keySet().stream()
                    .filter(e -> e.getTipo() == tipo)
                    .collect(Collectors.toList());
            System.out.println(naves);
            
            long sinDaños = naves.stream()
                    .filter(e -> e.getEstado() == EstadoNave.SIN_DAÑOS)
                    .count();
            long averiada = naves.stream()
                    .filter(e -> e.getEstado() == EstadoNave.AVERIADO)
                    .count();
            long hundimiento = naves.stream()
                    .filter(e -> e.getEstado() == EstadoNave.HUNDIDO)
                    .count();
            
            sb.append(String.format(formatoF, tipo.name(), sinDaños, averiada, hundimiento));
        }
        
        notificarAllSuscriptores("MOSTRAR_MARCADOR", sb.toString());
    }
}
