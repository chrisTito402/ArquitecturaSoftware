package models.observador;

/**
 *
 * @author daniel
 */
public interface ISuscriptor {
    
    public void notificar(String contexto, Object datos);
}
