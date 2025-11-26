package models.observador;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GestorSuscriptores {

    private final List<ISuscriptor> suscriptores;

    public GestorSuscriptores() {
        this.suscriptores = new CopyOnWriteArrayList<>();
    }

    public GestorSuscriptores(List<ISuscriptor> suscriptoresIniciales) {
        this.suscriptores = suscriptoresIniciales != null
                ? new CopyOnWriteArrayList<>(suscriptoresIniciales)
                : new CopyOnWriteArrayList<>();
    }

    public void suscribir(ISuscriptor suscriptor) {
        if (suscriptor != null && !suscriptores.contains(suscriptor)) {
            suscriptores.add(suscriptor);
        }
    }

    public void desuscribir(ISuscriptor suscriptor) {
        if (suscriptor != null) {
            suscriptores.remove(suscriptor);
        }
    }

    public void notificarTodos(String contexto, Object datos) {
        for (ISuscriptor suscriptor : suscriptores) {
            suscriptor.notificar(contexto, datos);
        }
    }

    public int getCantidadSuscriptores() {
        return suscriptores.size();
    }

    public boolean tieneSuscriptores() {
        return !suscriptores.isEmpty();
    }
}
