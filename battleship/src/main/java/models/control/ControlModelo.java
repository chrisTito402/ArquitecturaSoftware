package models.control;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import models.entidades.Coordenadas;
import models.entidades.Jugador;
import models.enums.OrientacionNave;
import models.enums.ResultadoAddNave;
import models.enums.ResultadoConfirmarNaves;
import models.enums.ResultadoDisparo;
import models.observador.ISuscriptor;
import views.DTOs.AddNaveDTO;
import views.DTOs.DisparoDTO;
import views.DTOs.JugadorDTO;
import views.DTOs.NaveDTO;
import views.DTOs.TableroDTO;

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
        
        if (resultado.getResultado() == ResultadoAddNave.NAVE_AÑADIDA) {
            notificarAllSuscriptores("RESULTADO_ADD_NAVE", resultado);
        }
    }
    
    @Override
    public void manejarResultadoConfirmarNaves(ResultadoConfirmarNaves resultado) {
        if (resultado == null) {
            return;
        }
        
        notificarAllSuscriptores("RESULTADO_CONFIRMAR_NAVES", naves);
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
