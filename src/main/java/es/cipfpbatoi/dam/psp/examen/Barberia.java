package es.cipfpbatoi.dam.psp.examen;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Barberia {
    private final int huecosDisponibles = 5;
    private final Queue<Cliente> sillas;
    private final Lock lock;
    private final Condition barberoDurmiendo;
    private final Condition clientePreparado;

    public Barberia() {
        sillas = new LinkedList<>();
        lock = new ReentrantLock();
        barberoDurmiendo = lock.newCondition();
        clientePreparado = lock.newCondition();
    }

    public void anyadirCliente(Cliente cliente) {
        lock.lock();
        try {
            if (sillas.size() == huecosDisponibles) {
                System.out.println("La barbería esta llena, el cliente " + cliente.getId() + " se va enfadado.");
                Thread.currentThread().interrupt();
                return;
            }
            sillas.add(cliente);
            System.out.println("El cliente " + cliente.getId() + " entra en la barbería y se sienta en una de las sillas disponibles.");
            barberoDurmiendo.signal();
            clientePreparado.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void cortarBarba() throws InterruptedException {
        lock.lock();
        try {
            while (sillas.isEmpty()) {
                System.out.println("El barbero se echa una cabezadita en una silla.");
                barberoDurmiendo.await();
            }

            Cliente cliente = sillas.element();
            clientePreparado.signal();
            System.out.println("El barbero le corta la barba al cliente " + cliente.getId());
            lock.unlock();
            Thread.sleep(new Random().nextInt(4000) + 6000);
            lock.lock();
            sillas.poll();
            System.out.println("El barbero ha terminado con el cliente " + cliente.getId());
        } finally {
            lock.unlock();
        }
    }

    public void abrirBarberia() {
        Thread hiloBarbero = new Thread(new Barbero(this));
        hiloBarbero.start();

        Thread[] clientes = new Thread[10];

        for (int i = 0; i < 10; i++) {
            Cliente cliente = new Cliente(this, i + 1);
            try {
                Thread.sleep(new Random().nextInt(1000) + 2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            clientes[i] = new Thread(cliente);
            clientes[i].start();
        }
        for (Thread t : clientes) {
            if (t != null) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (estaVacia()) {
            System.out.println("Es la hora del cierre. Good job!");
            hiloBarbero.interrupt();
        }
    }

    public boolean estaVacia() {
        return (sillas.isEmpty() || sillas == null);
    }
}
