# Actividad: El Problema del Barbero Dormilón en Java

* [1. Objetivo](#1-objetivo)
* [2. Contexto](#2-contexto)
* [3. Entrega](#3-entrega)
* [4. Puntos Clave para la Reflexión](#4-puntos-clave-para-la-reflexión)
* [5. Referencias](#5-referencias)

![El Barbero Durmiente](md_media/Barbero.webp)

## 1. Objetivo

Implementar una solución al clásico problema de concurrencia del barbero dormilón utilizando mecanismos de concurrencia en Java, como hilos ([`Thread`][Thread]), bloqueos reentrantes ([`ReentrantLock`][ReentrantLock]), variables de condición ([`Condition`][Condition]), y semáforos ([`Semaphore`][Semaphore]).

## 2. Contexto

En una barbería hay un barbero, una silla de barbero, y N (Para este ejemplo 5) sillas para los clientes esperar si el barbero está ocupado. Si no hay clientes, el barbero se sienta en la silla y se duerme. Cuando llega un cliente, este tiene que despertar al barbero si está dormido o, si el barbero está atendiendo a otro cliente, esperar en una de las sillas disponibles. Si todas las sillas están ocupadas, el cliente se va.

## 3. Entrega

Tu programa debe compilar y ejecutarse sin errores, mostrando en la consola el flujo de eventos en la barbería (por ejemplo, llegada de clientes, el barbero cortando el cabello, clientes esperando o yéndose).

## 4. Puntos Clave para la Reflexión
### 4.1. Decisión del Cliente:
Considera cómo un cliente decide si esperará o se irá cuando llegue a la barbería. ¿Qué factores influyen en esta decisión y cómo se puede implementar esta lógica de manera que sea coherente con el comportamiento esperado?

Para que un cliente decida si esperar o no cuando llega a la barbería, puede depender de factores que estén ligados al cliente, como el tiempo de espera para que se quede una silla libre. En este caso, si el cliente ve todas las sillas ocupadas, toma la decisión de irse. Esta lógica está en anyadirCliente(). Esta "decisión obligada" se toma en el código comprobando si hay sillas disponibles.
### 4.2. Manejo de la Cola de Espera:
Reflexiona sobre la estructura de datos que podría representar mejor la cola de espera. ¿Cómo garantizarías que los clientes sean atendidos en el orden correcto, especialmente cuando el barbero se desocupa y está listo para atender al siguiente cliente?

Una estructura de datos que representa la cola de espera es Queue que implementa de LinkedList. Usar Queue hace que los clientes se atiendan por orden de llegada a la barbería ya que element() obtiene el primer cliente de la cola, el que llegó en primer lugar evitando que se cuele nadie y no se enfaden nuestros clientes.
### 4.3. Concurrencia y Sincronización:
Concurrencia y Sincronización: Piensa en cómo gestionarías la concurrencia en este escenario. ¿Cómo asegurarías que el barbero no sea despertado por un cliente cuando ya está atendiendo a otro? ¿Cómo manejarías las situaciones en las que múltiples clientes llegan al mismo tiempo cuando solo queda una silla de espera disponible?

En este ejercicio he usado locks y conditiosn, para gestionar el acceso a la cola. Me aseguro de que el barbero no se despierte utilizando await() con la condicion barberodurmiente de manera que, mientras haya un cliente en la barbería, el barbero estará despierto. Lo contrario estaría muy feo por parte del barbero, la verdad.
El lock de anyadirCliente, hacen que si llegaran varios clientes simultáneamente, solo un cliente puede entrar y sentarse en la silla que quede disponible y hasta que se ejecute el unlock, el siguiente no puede comprobar si hay silla o no. La segunda condición, clientePreparado, la utilizo para que, mientras el barbero está atendiendo a otro cliente, el cliente de la condición se queda "bloqueado" a la espera. Cuando el barbero termina con el cliente, llama a signal() de cliente preparado, para hacerle saber al cliente que el barbero va a atenderlo y que el hilo puede seguir su ejecución (Se pondrá a hacerle su tupé).
### 4.4. Justicia y Eficiencia:
Considera el equilibrio entre la justicia (asegurando que todos los clientes tengan una oportunidad justa de ser atendidos) y la eficiencia (minimizando el tiempo de espera para los clientes y el tiempo inactivo para el barbero). ¿Cómo impactan tus decisiones de diseño en este equilibrio?

El equilibro de justicia y eficiencia, creo que se marca adecuadamente priorizando que los clientes que han llegado primero, sean los primeros en ser atendidos haciendo que el tiempo de espera de cada cliente sea el mínimo posible. Además el barbero es eficiente ya que en el mismo momento que entra un cliente en su barbería se pone manos a la obra.
Mis decisiones de diseño influyen en la justicia y el equilibro. Por ejemplo, si no usara un Queue, puede que el barbero peinara antes a otros clientes en lugar de a los que más rato llevan esperando y hacer que estos se enfaden.

## 5. Referencias

* [Thread]
* [ReentrantLock]
* [Condition]
* [Queue]

[Thread]: https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html
[ReentrantLock]: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/ReentrantLock.html
[Condition]: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/Condition.html
[Queue]: https://docs.oracle.com/javase/8/docs/api/java/util/Queue.html
