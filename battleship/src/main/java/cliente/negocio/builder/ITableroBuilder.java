package cliente.negocio.builder;

import compartido.entidades.Casilla;

/**
 * Interfaz del Builder para el Tablero.
 * Nada mas tiene para poner las casillas y los limites X e Y.
 *
 * @author Freddy Ali Castro Roman - 252191
 * @author Christopher Alvarez Centeno - 251954
 * @author Ethan Gael Valdez Romero - 253298
 * @author Daniel Buelna Andujo - 260378
 * @author Angel Ruiz Garcia - 248171
 */
public interface ITableroBuilder {

    public void setCasillas(Casilla[][] casillas);

    public void setLimiteX(int limiteX);

    public void setLimiteY(int limiteY);
}
