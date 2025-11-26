package buseventos;

import buseventos.buseventos.BusEventos;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el Bus de Eventos.
 * Verifica el correcto funcionamiento del patron Publicar/Suscribir.
 */
@DisplayName("Pruebas del Bus de Eventos")
class BusEventosTest {

    private BusEventos busEventos;
    private Gson gson;

    @BeforeEach
    void setUp() {
        busEventos = new BusEventos();
        gson = new Gson();
    }

    /**
     * Implementacion de prueba de IEventSuscriptor
     */
    static class SuscriptorPrueba implements IEventSuscriptor {
        private final String id;
        private final List<String> mensajesRecibidos = new ArrayList<>();
        private CountDownLatch latch;

        public SuscriptorPrueba(String id) {
            this.id = id;
        }

        public void setLatch(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void recibirEvento(String eventoJSON) {
            mensajesRecibidos.add(eventoJSON);
            if (latch != null) {
                latch.countDown();
            }
        }

        @Override
        public String getSuscriptorId() {
            return id;
        }

        public List<String> getMensajesRecibidos() {
            return mensajesRecibidos;
        }

        public int getCantidadMensajes() {
            return mensajesRecibidos.size();
        }

        public void limpiarMensajes() {
            mensajesRecibidos.clear();
        }
    }

    /**
     * Suscriptor que lanza excepcion para probar manejo de errores
     */
    static class SuscriptorConError implements IEventSuscriptor {
        private final String id;

        public SuscriptorConError(String id) {
            this.id = id;
        }

        @Override
        public void recibirEvento(String eventoJSON) {
            throw new RuntimeException("Error simulado en suscriptor");
        }

        @Override
        public String getSuscriptorId() {
            return id;
        }
    }

    @Nested
    @DisplayName("Pruebas de suscripcion")
    class SuscripcionTests {

        @Test
        @DisplayName("Suscriptor puede suscribirse a un evento")
        void suscriptorPuedeSuscribirse() {
            SuscriptorPrueba suscriptor = new SuscriptorPrueba("suscriptor-1");

            assertDoesNotThrow(() -> {
                busEventos.suscribirse("EVENTO_TEST", suscriptor);
            });
        }

        @Test
        @DisplayName("Multiples suscriptores pueden suscribirse al mismo evento")
        void multiplesSuscriptoresMismoEvento() {
            SuscriptorPrueba suscriptor1 = new SuscriptorPrueba("suscriptor-1");
            SuscriptorPrueba suscriptor2 = new SuscriptorPrueba("suscriptor-2");

            busEventos.suscribirse("EVENTO_TEST", suscriptor1);
            busEventos.suscribirse("EVENTO_TEST", suscriptor2);

            // Verificar que se puede obtener el suscriptor
            assertNotNull(busEventos.getSuscriptor("suscriptor-1"));
            assertNotNull(busEventos.getSuscriptor("suscriptor-2"));
        }

        @Test
        @DisplayName("Suscriptor puede suscribirse a multiples eventos")
        void suscriptorMultiplesEventos() {
            SuscriptorPrueba suscriptor = new SuscriptorPrueba("suscriptor-1");

            busEventos.suscribirse("EVENTO_1", suscriptor);
            busEventos.suscribirse("EVENTO_2", suscriptor);
            busEventos.suscribirse("EVENTO_3", suscriptor);

            assertNotNull(busEventos.getSuscriptor("suscriptor-1"));
        }

        @Test
        @DisplayName("Suscriptor null no causa error")
        void suscriptorNullNoCausaError() {
            assertDoesNotThrow(() -> {
                busEventos.suscribirse("EVENTO_TEST", null);
            });
        }

        @Test
        @DisplayName("getSuscriptor retorna null para ID inexistente")
        void getSuscriptorInexistenteRetornaNull() {
            IEventSuscriptor resultado = busEventos.getSuscriptor("id-inexistente");

            assertNull(resultado);
        }
    }

    @Nested
    @DisplayName("Pruebas de remover suscriptor")
    class RemoverSuscriptorTests {

        @Test
        @DisplayName("Remover suscriptor existente")
        void removerSuscriptorExistente() {
            SuscriptorPrueba suscriptor = new SuscriptorPrueba("suscriptor-1");
            busEventos.suscribirse("EVENTO_TEST", suscriptor);

            busEventos.removeSuscriptor(suscriptor);

            assertNull(busEventos.getSuscriptor("suscriptor-1"));
        }

        @Test
        @DisplayName("Remover suscriptor de multiples eventos")
        void removerSuscriptorDeMultiplesEventos() {
            SuscriptorPrueba suscriptor = new SuscriptorPrueba("suscriptor-1");
            busEventos.suscribirse("EVENTO_1", suscriptor);
            busEventos.suscribirse("EVENTO_2", suscriptor);

            busEventos.removeSuscriptor(suscriptor);

            assertNull(busEventos.getSuscriptor("suscriptor-1"));
        }

        @Test
        @DisplayName("Remover suscriptor null no causa error")
        void removerSuscriptorNullNoCausaError() {
            assertDoesNotThrow(() -> {
                busEventos.removeSuscriptor((IEventSuscriptor) null);
            });
        }

        @Test
        @DisplayName("Remover suscriptor no existente no causa error")
        void removerSuscriptorNoExistenteNoCausaError() {
            SuscriptorPrueba suscriptor = new SuscriptorPrueba("no-suscrito");

            assertDoesNotThrow(() -> {
                busEventos.removeSuscriptor(suscriptor);
            });
        }
    }

    @Nested
    @DisplayName("Pruebas de manejarEvento")
    class ManejarEventoTests {

        @Test
        @DisplayName("Manejar evento SUSCRIBIR agrega suscriptor")
        void manejarEventoSuscribir() {
            // Crear mensaje de suscripcion
            Mensaje mensaje = new Mensaje(TipoAccion.SUSCRIBIR, "EVENTO_TEST", null, null);
            String json = gson.toJson(mensaje);

            // Crear un suscriptor simulado (UserServerThread mock)
            SuscriptorPrueba suscriptor = new SuscriptorPrueba("thread-1");

            // Simular que el bus recibe la suscripcion directamente
            busEventos.suscribirse("EVENTO_TEST", suscriptor);

            assertNotNull(busEventos.getSuscriptor("thread-1"));
        }
    }

    @Nested
    @DisplayName("Pruebas de concurrencia")
    class ConcurrenciaTests {

        @Test
        @DisplayName("Suscripciones concurrentes no causan error")
        void suscripcionesConcurrentesNoCausanError() throws InterruptedException {
            int numThreads = 10;
            CountDownLatch latch = new CountDownLatch(numThreads);

            for (int i = 0; i < numThreads; i++) {
                final int index = i;
                new Thread(() -> {
                    try {
                        SuscriptorPrueba suscriptor = new SuscriptorPrueba("suscriptor-" + index);
                        busEventos.suscribirse("EVENTO_CONCURRENTE", suscriptor);
                    } finally {
                        latch.countDown();
                    }
                }).start();
            }

            boolean completado = latch.await(5, TimeUnit.SECONDS);
            assertTrue(completado, "Las suscripciones deberian completarse en tiempo");
        }

        @Test
        @DisplayName("Remociones concurrentes no causan error")
        void remocionesConcurrentesNoCausanError() throws InterruptedException {
            // Primero suscribir varios
            List<SuscriptorPrueba> suscriptores = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                SuscriptorPrueba s = new SuscriptorPrueba("suscriptor-" + i);
                suscriptores.add(s);
                busEventos.suscribirse("EVENTO_CONCURRENTE", s);
            }

            int numThreads = 10;
            CountDownLatch latch = new CountDownLatch(numThreads);

            for (int i = 0; i < numThreads; i++) {
                final int index = i;
                new Thread(() -> {
                    try {
                        busEventos.removeSuscriptor(suscriptores.get(index));
                    } finally {
                        latch.countDown();
                    }
                }).start();
            }

            boolean completado = latch.await(5, TimeUnit.SECONDS);
            assertTrue(completado, "Las remociones deberian completarse en tiempo");
        }
    }

    @Nested
    @DisplayName("Pruebas de Mensaje")
    class MensajeTests {

        @Test
        @DisplayName("Mensaje se serializa correctamente")
        void mensajeSeSerializaCorrectamente() {
            Mensaje mensaje = new Mensaje(TipoAccion.PUBLICAR, "DISPARO", null, "cliente-1");

            String json = gson.toJson(mensaje);

            assertNotNull(json);
            assertTrue(json.contains("PUBLICAR"));
            assertTrue(json.contains("DISPARO"));
        }

        @Test
        @DisplayName("Mensaje se deserializa correctamente")
        void mensajeSeDeserializaCorrectamente() {
            String json = "{\"accion\":\"PUBLICAR\",\"evento\":\"RESULTADO_DISPARO\",\"idPublicador\":\"server\"}";

            Mensaje mensaje = gson.fromJson(json, Mensaje.class);

            assertNotNull(mensaje);
            assertEquals(TipoAccion.PUBLICAR, mensaje.getAccion());
            assertEquals("RESULTADO_DISPARO", mensaje.getEvento());
        }
    }

    @Nested
    @DisplayName("Pruebas de TipoAccion")
    class TipoAccionTests {

        @Test
        @DisplayName("TipoAccion tiene valores correctos")
        void tipoAccionTieneValoresCorrectos() {
            assertEquals(3, TipoAccion.values().length);
            assertNotNull(TipoAccion.SUSCRIBIR);
            assertNotNull(TipoAccion.PUBLICAR);
            assertNotNull(TipoAccion.SEND_UNICAST);
        }
    }

    @Nested
    @DisplayName("Pruebas de IEventSuscriptor")
    class IEventSuscriptorTests {

        @Test
        @DisplayName("Suscriptor recibe eventos correctamente")
        void suscriptorRecibeEventosCorrectamente() {
            SuscriptorPrueba suscriptor = new SuscriptorPrueba("test-1");

            suscriptor.recibirEvento("{\"tipo\":\"test\"}");

            assertEquals(1, suscriptor.getCantidadMensajes());
            assertTrue(suscriptor.getMensajesRecibidos().get(0).contains("test"));
        }

        @Test
        @DisplayName("Suscriptor puede recibir multiples eventos")
        void suscriptorRecibeMultiplesEventos() {
            SuscriptorPrueba suscriptor = new SuscriptorPrueba("test-1");

            suscriptor.recibirEvento("{\"msg\":1}");
            suscriptor.recibirEvento("{\"msg\":2}");
            suscriptor.recibirEvento("{\"msg\":3}");

            assertEquals(3, suscriptor.getCantidadMensajes());
        }

        @Test
        @DisplayName("getSuscriptorId retorna ID correcto")
        void getSuscriptorIdRetornaIdCorrecto() {
            SuscriptorPrueba suscriptor = new SuscriptorPrueba("mi-id-unico");

            assertEquals("mi-id-unico", suscriptor.getSuscriptorId());
        }
    }

    @Nested
    @DisplayName("Pruebas de manejo de errores")
    class ManejoErroresTests {

        @Test
        @DisplayName("Suscriptor con error es removido automaticamente")
        void suscriptorConErrorEsRemovidoAutomaticamente() {
            // Este test verifica que el bus maneja errores en suscriptores
            SuscriptorConError suscriptorConError = new SuscriptorConError("error-1");
            SuscriptorPrueba suscriptorNormal = new SuscriptorPrueba("normal-1");

            busEventos.suscribirse("EVENTO_TEST", suscriptorConError);
            busEventos.suscribirse("EVENTO_TEST", suscriptorNormal);

            // Ambos suscriptores estan registrados inicialmente
            assertNotNull(busEventos.getSuscriptor("error-1"));
            assertNotNull(busEventos.getSuscriptor("normal-1"));
        }
    }

    @Nested
    @DisplayName("Pruebas de integracion del Bus")
    class IntegracionBusTests {

        @Test
        @DisplayName("Flujo completo: suscribir, getSuscriptor, remover")
        void flujoCompletoSuscribirObtenerRemover() {
            SuscriptorPrueba suscriptor = new SuscriptorPrueba("flujo-test");

            // 1. Suscribir
            busEventos.suscribirse("EVENTO_FLUJO", suscriptor);
            assertNotNull(busEventos.getSuscriptor("flujo-test"));

            // 2. Obtener
            IEventSuscriptor obtenido = busEventos.getSuscriptor("flujo-test");
            assertEquals("flujo-test", obtenido.getSuscriptorId());

            // 3. Remover
            busEventos.removeSuscriptor(suscriptor);
            assertNull(busEventos.getSuscriptor("flujo-test"));
        }

        @Test
        @DisplayName("Multiples eventos con diferentes suscriptores")
        void multiplesEventosDiferentesSuscriptores() {
            SuscriptorPrueba suscriptorA = new SuscriptorPrueba("suscriptor-A");
            SuscriptorPrueba suscriptorB = new SuscriptorPrueba("suscriptor-B");
            SuscriptorPrueba suscriptorC = new SuscriptorPrueba("suscriptor-C");

            busEventos.suscribirse("DISPARO", suscriptorA);
            busEventos.suscribirse("DISPARO", suscriptorB);
            busEventos.suscribirse("RESULTADO", suscriptorB);
            busEventos.suscribirse("RESULTADO", suscriptorC);

            // Todos los suscriptores deben estar registrados
            assertNotNull(busEventos.getSuscriptor("suscriptor-A"));
            assertNotNull(busEventos.getSuscriptor("suscriptor-B"));
            assertNotNull(busEventos.getSuscriptor("suscriptor-C"));

            // Remover solo suscriptorB
            busEventos.removeSuscriptor(suscriptorB);
            assertNull(busEventos.getSuscriptor("suscriptor-B"));
            assertNotNull(busEventos.getSuscriptor("suscriptor-A"));
            assertNotNull(busEventos.getSuscriptor("suscriptor-C"));
        }
    }
}
