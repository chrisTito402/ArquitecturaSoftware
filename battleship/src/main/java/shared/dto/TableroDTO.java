package shared.dto;

/**
 *
 * @author daniel
 */
public class TableroDTO {

    private int limiteX;
    private int limiteY;

    public TableroDTO() {
    }

    public TableroDTO(int limiteX, int limiteY) {
        this.limiteX = limiteX;
        this.limiteY = limiteY;
    }

    public int getLimiteX() {
        return limiteX;
    }

    public void setLimiteX(int limiteX) {
        this.limiteX = limiteX;
    }

    public int getLimiteY() {
        return limiteY;
    }

    public void setLimiteY(int limiteY) {
        this.limiteY = limiteY;
    }

    @Override
    public String toString() {
        return "TableroDTO{" + "limiteX=" + limiteX + ", limiteY=" + limiteY + '}';
    }

}
