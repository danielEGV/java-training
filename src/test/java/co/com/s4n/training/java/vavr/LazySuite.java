package co.com.s4n.training.java.vavr;

import io.vavr.Lazy;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Test;

import java.util.function.Supplier;

import static io.vavr.API.Some;
import static org.junit.Assert.*;

public class LazySuite {

    private static final long WAIT_MILLIS = 50;
    private static final int WAIT_COUNT = 100;
    private static void waitUntil(Supplier<Boolean> condition) {
        int count = 0;
        while (!condition.get()) {
            if (++count > WAIT_COUNT) {
                fail("Condition not met.");
            } else {
                Try.run(() -> Thread.sleep(WAIT_MILLIS));
            }
        }
    }

    private void sleep(int milliseconds){
        try{
            Thread.sleep(milliseconds);
        }catch(Exception e){
            System.out.println("Problemas durmiendo hilo");
        }
    }

    @Test
    public void testLazy() {
        Lazy<Double> lazy = Lazy.of(Math::random);
        assertFalse(lazy.isEvaluated());
        lazy.get();
        assertTrue(lazy.isEvaluated());
    }


    @Test
    public void testFutureLazy() {
        Lazy<Future<String>> l1 = Lazy.of(() -> {
            sleep(500);
            return Future.of(() -> "Hola");
        });

        Lazy<Future<String>> l2 = Lazy.of(() -> {
            sleep(800);
            return  Future.of(() -> " mundo");
        });

        Lazy<Future<String>> l3 = Lazy.of(() -> {
            sleep(300);
            return Future.of(() -> "!");
        });

        long inicio = System.nanoTime();

        Future<String> future = l1.get()
                .flatMap(a -> l2.get()
                        .flatMap(b -> l3.get()
                                .flatMap(c -> Future.of(() -> a + b + c))));
        future.await();
        waitUntil(() -> future.get().equals("Hola mundo!"));

        long fin = System.nanoTime();

        double elapsed = (fin - inicio) * Math.pow(10, -6);

        System.out.println("Elapsed: " + elapsed);

        assertTrue(elapsed >= 1600D);
    }

    @Test
    public void testFutureLazy_entrando_de_nuevo() {
        Lazy<Future<String>> l1 = Lazy.of(() -> {
            sleep(500);
            return Future.of(() -> "Hola");
        });

        Lazy<Future<String>> l2 = Lazy.of(() -> {
            sleep(800);
            return  Future.of(() -> " mundo");
        });

        Lazy<Future<String>> l3 = Lazy.of(() -> {
            sleep(300);
            return Future.of(() -> "!");
        });

        long inicio = System.nanoTime();

        Future<String> future = l1.get()
                .flatMap(a -> l2.get()
                        .flatMap(b -> l3.get()
                                .flatMap(c -> Future.of(() -> a + b + c))));
        future.await();
        waitUntil(() -> future.get().equals("Hola mundo!"));

        long fin = System.nanoTime();

        double elapsed = (fin - inicio) * Math.pow(10, -6);

        System.out.println("Elapsed 1 1: " + elapsed);

        assertTrue(elapsed >= 1600D);

        long inicio1 = System.nanoTime();

        Future<String> future1 = l1.get()
                .flatMap(a -> l2.get()
                        .flatMap(b -> l3.get()
                                .flatMap(c -> Future.of(() -> a + b + c))));
        future1.await();
        waitUntil(() -> future.get().equals("Hola mundo!"));

        long fin1 = System.nanoTime();

        double elapsed1 = (fin1 - inicio1) * Math.pow(10, -6);

        System.out.println("Elapsed 1 2: " + elapsed1);

        assertTrue(elapsed1 <= 1600D);
    }

    @Test
    public void testSupplier() {
        Supplier<String> s1 = () -> {
            sleep(500);
            return "Hola";
        };

        long inicio = System.nanoTime();
        String s = s1.get();
        long fin = System.nanoTime();

        double lapsed = (fin - inicio) * Math.pow(10, -6);

        long inicio1 = System.nanoTime();
        String ss = s1.get();
        long fin1 = System.nanoTime();

        double lapsed1 = (fin1 - inicio1) * Math.pow(10, -6);

        assertTrue(lapsed >= 500);
        assertFalse(lapsed1 <= 500);

    }

    @Test
    public void testSupplierLazy() {
        Lazy<String> s1 = Lazy.of(() -> {
            sleep(500);
            return "Hola";
        });

        long inicio = System.nanoTime();
        String s = s1.get();
        long fin = System.nanoTime();

        double lapsed = (fin - inicio) * Math.pow(10, -6);

        long inicio1 = System.nanoTime();
        String ss = s1.get();
        long fin1 = System.nanoTime();

        double lapsed1 = (fin1 - inicio1) * Math.pow(10, -6);

        assertTrue(lapsed >= 500);
        assertTrue(lapsed1 <= 500);

    }
}
