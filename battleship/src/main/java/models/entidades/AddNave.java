package models.entidades;

import models.enums.ResultadoAddNave;

/**
 *
 * @author daniel
 */
public class AddNave {
    
    private ResultadoAddNave resultado;
    private Nave nave;

    public AddNave(ResultadoAddNave resultado, Nave nave) {
        this.resultado = resultado;
        this.nave = nave;
    }

    public ResultadoAddNave getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoAddNave resultado) {
        this.resultado = resultado;
    }

    public Nave getNave() {
        return nave;
    }

    public void setNave(Nave nave) {
        this.nave = nave;
    }

    @Override
    public String toString() {
        return "AddNave{" + "resultado=" + resultado + ", nave=" + nave + '}';
    }
    
}
