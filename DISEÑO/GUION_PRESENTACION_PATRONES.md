# GUION DE PRESENTACION - PATRONES Y ESTILOS ARQUITECTONICOS
## Proyecto Batalla Naval - Arquitectura de Software

**Tiempo sugerido:** 5 minutos para código (según PDF de entrega)

---

## 1. PATRON MVC (Model-View-Controller)
**Tiempo: ~1 minuto**

### Mostrar diagrama primero
> Abrir: `DISEÑO/Diagramas/Equipo/Patrones y Estilos Arquitectonicos/DiagramaPatronMVC.jpg`

### MODEL - El Modelo
> Abrir: `battleship/src/main/java/cliente/negocio/ControlModelo.java`

**Lineas a mostrar: 34-66**
```
"Aquí está nuestro MODELO. La clase ControlModelo implementa IModeloCliente
y contiene todo el ESTADO del juego:"
- Línea 37: jugador (JugadorDTO)
- Línea 40: tablero (TableroDTO)
- Línea 43: naves (List<NaveDTO>)
- Línea 46: turno (boolean)
- Línea 49: tableroConfirmado
- Línea 52: suscriptores (para Observer)
```

### VIEW - Las Vistas
> Abrir carpeta: `battleship/src/main/java/cliente/presentacion/frames/`

**Mostrar lista de archivos:**
```
"Estas son nuestras VISTAS, todos los JFrames:"
- FrmMenuPrincipal.java
- FrmCrearPartida.java
- FrmUnirsePartida.java
- FrmLobby.java
- FrmColocarNaves.java
- FrmPartidaEnCurso.java
- FrmFinPartida.java
```

### CONTROLLER - El Controlador
> Abrir: `battleship/src/main/java/cliente/controlador/Controlador.java`

**Lineas a mostrar: 40-66**
```
"El CONTROLADOR conecta la Vista con el Modelo:"
- Línea 43: private IModeloCliente partida (referencia al modelo)
- Línea 46: private IClienteSocket cliente (comunicación)
- Línea 49: Map<String, Consumer<Mensaje>> manejadorEventos

"Recibe acciones del usuario y las manda al modelo, por ejemplo:"
```

**Mostrar método líneas 196-202:**
```java
public void realizarDisparo(CoordenadasDTO coordenadas) {
    DisparoDTO disparo = partida.realizarDisparo(coordenadas); // Delega al modelo
    if (disparo != null) {
        enviarMensaje("DISPARO", disparo); // Envía al servidor
    }
}
```

---

## 2. ARQUITECTURA EN CAPAS
**Tiempo: ~45 segundos**

### Mostrar diagrama primero
> Abrir: `DISEÑO/Diagramas/Equipo/Patrones y Estilos Arquitectonicos/DiagramaArquitecturaEnCapas.jpg`

### Mostrar estructura de paquetes en NetBeans
> En el panel de proyectos, expandir `src/main/java`

```
"Nuestra arquitectura tiene 5 CAPAS bien definidas:"

CAPA 1 - PRESENTACION
└── cliente.presentacion
    ├── frames/      → Los JFrames (UI)
    └── componentes/ → CasillaButton, TimerPanel, etc.

CAPA 2 - CONTROL
├── cliente.controlador
│   ├── Controlador.java
│   └── ControlVista.java
└── servidor.controlador
    └── ControladorServidor.java

CAPA 3 - COMUNICACION
├── compartido.comunicacion
│   ├── socket/  → ClienteSocket, ServidorSocket
│   └── dto/     → Objetos de transferencia
└── servidor.bus
    └── BusEventos.java

CAPA 4 - NEGOCIO/DOMINIO
├── cliente.negocio
│   ├── ControlModelo.java
│   └── builder/
└── servidor.negocio
    ├── GestorPartida.java
    └── Partida.java

CAPA 5 - COMPARTIDO (Entidades)
└── compartido
    ├── entidades/  → Jugador, Tablero, Nave, etc.
    ├── enums/      → Estados, colores, resultados
    └── observador/ → ISuscriptor

"Las dependencias van de ARRIBA hacia ABAJO, nunca al revés."
```

---

## 3. BUS DE EVENTOS + SRP
**Tiempo: ~1 minuto**

### Mostrar diagrama primero
> Abrir: `DISEÑO/Diagramas/Equipo/Patrones y Estilos Arquitectonicos/DiagramaBusDeEventos.jpg`

### El Bus de Eventos
> Abrir: `battleship/src/main/java/servidor/bus/BusEventos.java`

**Lineas a mostrar: 32-59**
```
"El BusEventos es como un CARTERO que reparte mensajes:"
- Línea 35: ConcurrentHashMap<String, Set<UserServerThread>> eventos
  → Guarda qué clientes están suscritos a qué eventos
- Línea 38: ConcurrentHashMap<String, UserServerThread> clientesPorId
- Línea 44: GestorPartida gestorPartida
  → Aquí está la SEPARACION DE RESPONSABILIDADES (SRP)
```

**Mostrar método publicar() líneas 76-91:**
```java
public void publicar(String evento, Mensaje mensaje) {
    String jsonMensaje = gson.toJson(mensaje);
    Set<UserServerThread> suscriptores = eventos.get(evento);
    if (suscriptores != null) {
        suscriptores.forEach(s -> s.sendMessage(jsonMensaje));
    }
}
```

### SRP - Separación de Responsabilidades
**Mostrar líneas 185-195:**
```java
private void procesarPublicacion(Mensaje mensaje) {
    String evento = mensaje.getEvento();

    // DELEGA la lógica al GestorPartida (SRP)
    boolean debePublicar = gestorPartida.procesarEvento(evento, mensaje);

    // El Bus SOLO se encarga de publicar
    if (debePublicar) {
        publicar(evento, mensaje);
    }
}
```

```
"IMPORTANTE: El Bus NO tiene lógica del juego. Solo enruta mensajes.
Toda la lógica está en GestorPartida. Así cumplimos con SRP."
```

### GestorPartida - La Lógica
> Abrir: `battleship/src/main/java/servidor/negocio/GestorPartida.java`

**Mostrar líneas 136-176 (switch de eventos):**
```
"GestorPartida tiene TODA la lógica del juego:"
- procesarCrearPartida()
- procesarUnirsePartida()
- procesarDisparo()
- procesarConfirmarTablero()
- etc.
```

---

## 4. PATRON SINGLETON
**Tiempo: ~30 segundos**

### Mostrar diagrama primero
> Abrir: `DISEÑO/Diagramas/Equipo/Patrones y Estilos Arquitectonicos/DiagramaPatronSingleton.jpg`

### Implementación
> Abrir: `battleship/src/main/java/cliente/controlador/ControlVista.java`

**Mostrar líneas 53-54 y 172-177:**
```java
// Línea 54: Instancia única
private static ControlVista controlVista;

// Líneas 172-177: Método getInstance thread-safe
public static synchronized ControlVista getInstancia() {
    if (controlVista == null) {
        controlVista = new ControlVista();
    }
    return controlVista;
}
```

**Mostrar línea 99 - Constructor privado:**
```java
private ControlVista() {
    manejadoresNoti = new HashMap<>();
    // ...
}
```

### Uso del Singleton
> Abrir: `battleship/src/main/java/cliente/MainCliente.java`

**Mostrar líneas 64-65:**
```java
ControlVista controlVista = ControlVista.getInstancia();
controlVista.setControl(controlador);
```

```
"Todas las vistas obtienen la MISMA instancia con getInstancia().
Es synchronized para que sea thread-safe."
```

---

## 5. PATRON BUILDER
**Tiempo: ~45 segundos**

### Mostrar diagrama primero
> Abrir: `DISEÑO/Diagramas/Equipo/Patrones y Estilos Arquitectonicos/DiagramaPatronBuilder.jpg`

### El Director
> Abrir: `battleship/src/main/java/cliente/negocio/builder/Director.java`

**Mostrar líneas 26-42:**
```java
public class Director {

    public IModeloCliente makePartida(IPartidaBuilder builder) {
        builder.setCantBarcos(0);
        builder.setCantSubmarinos(0);
        builder.setCantCruceros(0);
        builder.setCantPortaAviones(0);
        builder.setTotalNaves(0);
        builder.setEstado(EstadoPartida.EN_CURSO);
        builder.setJugadores(new ArrayList<>());
        builder.setSuscriptores(new ArrayList<>());

        return builder.getResult();
    }
}
```

### El Builder Concreto
> Abrir: `battleship/src/main/java/cliente/negocio/builder/PartidaBuilder.java`

**Mostrar líneas 92-110:**
```java
public IModeloCliente getResult() {
    TableroDTO tableroDTO = new TableroDTO(10, 10);
    List<NaveDTO> naves = new ArrayList<>();

    ControlModelo modelo = new ControlModelo(
        null,      // jugador se asigna después
        tableroDTO,
        naves,
        false,     // turno inicial
        suscriptores
    );

    return modelo;
}
```

### Uso en MainCliente
> Abrir: `battleship/src/main/java/cliente/MainCliente.java`

**Mostrar líneas 39-40:**
```java
Director director = new Director();
IModeloCliente modelo = director.makePartida(new PartidaBuilder());
```

```
"El Director orquesta la construcción paso a paso.
El Builder encapsula la creación del objeto complejo."
```

---

## 6. PATRON OBSERVER
**Tiempo: ~45 segundos**

### Mostrar diagrama primero
> Abrir: `DISEÑO/Diagramas/Equipo/Patrones y Estilos Arquitectonicos/DiagramaPatronObserver.jpg`

### La Interfaz Observer
> Abrir: `battleship/src/main/java/compartido/observador/ISuscriptor.java`

**Mostrar todo el archivo (líneas 1-21):**
```java
public interface ISuscriptor {
    public void notificar(String contexto, Object datos);
}
```

### El Observable (Subject)
> Abrir: `battleship/src/main/java/cliente/negocio/ControlModelo.java`

**Mostrar líneas 52 y 237-249:**
```java
// Línea 52: Lista de observadores
private List<ISuscriptor> suscriptores;

// Líneas 237-244: Métodos para suscribirse
public void suscribirAPartida(ISuscriptor suscriptor) {
    suscriptores.add(suscriptor);
}

public void desuscribirDePartida(ISuscriptor suscriptor) {
    suscriptores.remove(suscriptor);
}

// Líneas 247-249: Notificar a todos
public void notificarAllSuscriptores(String contexto, Object datos) {
    suscriptores.forEach(s -> s.notificar(contexto, datos));
}
```

### El Observer Concreto
> Abrir: `battleship/src/main/java/cliente/controlador/ControlVista.java`

**Mostrar línea 51 y 269-282:**
```java
// Línea 51: Implementa ISuscriptor
public class ControlVista implements ISuscriptor {

// Líneas 269-282: Método notificar
@Override
public void notificar(String contexto, Object datos) {
    Consumer<Object> manejador = manejadoresNoti.get(contexto);
    if (manejador != null) {
        manejador.accept(datos);
    }
}
```

### Ejemplo de uso
**Mostrar línea 118 en ControlModelo.java:**
```java
// Cuando hay un disparo, notifica a todas las vistas
notificarAllSuscriptores("RESULTADO_DISPARO", disparo);
```

```
"Cuando el modelo cambia, notifica automáticamente a todos los
observadores suscritos. Así las vistas se actualizan sin acoplamiento."
```

---

## 7. DIAGRAMA INTEGRADO (Cierre)
**Tiempo: ~15 segundos**

> Abrir: `DISEÑO/Diagramas/Equipo/Patrones y Estilos Arquitectonicos/DiagramaPatronesIntegrados.jpg`

```
"Este diagrama muestra cómo TODOS los patrones trabajan juntos:

CLIENTE:
- Builder crea el ControlModelo
- MVC estructura la aplicación
- Observer notifica cambios a ControlVista
- Singleton garantiza una única instancia de ControlVista

SERVIDOR:
- Bus de Eventos enruta mensajes
- GestorPartida tiene la lógica (SRP)

La comunicación es via Sockets TCP/IP."
```

---

## RESUMEN RAPIDO (Por si te preguntan)

| Patrón | Clase Principal | Línea Clave |
|--------|-----------------|-------------|
| **MVC-Model** | `ControlModelo` | 34 |
| **MVC-View** | `Frm*.java` | carpeta frames/ |
| **MVC-Controller** | `Controlador` | 40 |
| **Capas** | Estructura paquetes | 5 capas |
| **Bus Eventos** | `BusEventos` | 32, 76 |
| **SRP** | `BusEventos` → `GestorPartida` | 185-195 |
| **Singleton** | `ControlVista` | 172-177 |
| **Builder** | `Director` + `PartidaBuilder` | 31-42, 92-110 |
| **Observer** | `ISuscriptor` + `ControlModelo` | 14, 247-249 |

---

## TIPS PARA LA PRESENTACION

1. **Abre los archivos ANTES** de la presentación para no perder tiempo buscando
2. **Usa Ctrl+G** en NetBeans para ir directamente a una línea
3. **Resalta el código** con el cursor mientras explicas
4. **No leas el código**, explica QUÉ hace y POR QUÉ está ahí
5. **Si te preguntan algo** que no sabes, di "Lo revisamos en el código" y búscalo

---

**Creado para presentación de Arquitectura de Software - Batalla Naval**
