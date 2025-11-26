package models.factories;

import models.entidades.Barco;
import models.entidades.Crucero;
import models.entidades.Nave;
import models.entidades.PortaAviones;
import models.entidades.Submarino;
import models.enums.OrientacionNave;
import models.enums.TipoNave;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NaveFactory {

    private static final Map<TipoNave, Function<OrientacionNave, Nave>> factories;

    static {
        factories = new HashMap<>();
        factories.put(TipoNave.BARCO, Barco::new);
        factories.put(TipoNave.SUBMARINO, Submarino::new);
        factories.put(TipoNave.CRUCERO, Crucero::new);
        factories.put(TipoNave.PORTAAVIONES, PortaAviones::new);
    }

    public static Nave crear(TipoNave tipo, OrientacionNave orientacion) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de nave no puede ser null");
        }
        if (orientacion == null) {
            throw new IllegalArgumentException("La orientación no puede ser null");
        }

        Function<OrientacionNave, Nave> factory = factories.get(tipo);
        if (factory == null) {
            throw new IllegalArgumentException("Tipo de nave desconocido: " + tipo);
        }

        return factory.apply(orientacion);
    }

    public static void registrarTipo(TipoNave tipo, Function<OrientacionNave, Nave> factory) {
        if (tipo == null || factory == null) {
            throw new IllegalArgumentException("El tipo y la factory no pueden ser null");
        }
        factories.put(tipo, factory);
    }

    public static boolean tipoRegistrado(TipoNave tipo) {
        return tipo != null && factories.containsKey(tipo);
    }
}
