package co.com.s4n.training.java.jdk;

import static org.junit.Assert.*;

//import com.sun.java.util.jar.pack.Package;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class CompletableFutureSuite {

    private void sleep(int milliseconds){
        try{
            Thread.sleep(milliseconds);
        }catch(Exception e){
            System.out.println("Problemas durmiendo hilo");
        }
    }

    void imprimirMensaje(String mensaje) {
        LocalTime timeNow = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        String time = timeNow.format(formatter);
        System.out.println(mensaje + " " + time);
        //return mensaje + " " + time;
    }

    @Test
    public void mensajeConFecha() {
        String mensaje = "Hola";

        LocalTime timeNow = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        String time = timeNow.format(formatter);
        System.out.println(mensaje + " time: " + time);

        /*
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        completableFuture
                .thenApply(s -> "Hola " + timeNow.format(formatter));

        try {
            String resultado = completableFuture.get();
        } catch (InterruptedException e) {
            assertTrue(false);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        */

    }

    @Test
    public void t1() {

        CompletableFuture<String> completableFuture
                = new CompletableFuture<>();


        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.submit(() -> {
            Thread.sleep(300);

            completableFuture.complete("Hello");
            return null;
        });
            System.out.println(Thread.currentThread().getName());

        try {
            String s = completableFuture.get(500, TimeUnit.MILLISECONDS);
            assertEquals(s, "Hello");
        }catch(Exception e){
            assertTrue(false);
        }finally{
            executorService.shutdown();

        }

    }

    @Test
    public void t2(){
        CompletableFuture<String> completableFuture
                = new CompletableFuture<>();

        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.submit(() -> {
            Thread.sleep(300);

            completableFuture.complete("Hello");
            return null;
        });

        try {
            String s = completableFuture.get(500, TimeUnit.MILLISECONDS);
            assertEquals(s, "Hello");
        }catch(Exception e){
            assertTrue(false);
        }finally{
            executorService.shutdown();
        }
    }

    @Test
    public void t3(){
        // Se puede construir un CompletableFuture a partir de una lambda Supplier (que no recibe parámetros pero sí tiene retorno)
        // con supplyAsync
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            sleep(300);
            return "Hello";
        });

        try {
            String s = future.get(500, TimeUnit.MILLISECONDS);
            //String s = future.get(30, TimeUnit.MILLISECONDS);
            assertEquals(s, "Hello");
        }catch(Exception e){

            assertTrue(false);
        }
    }

    @Test
    public void t4(){

        int i = 0;
        // Se puede construir un CompletableFuture a partir de una lambda (Supplier)
        // con runAsync
        Runnable r = () -> {
            sleep(300);
            System.out.println("Soy impuro y no merezco existir");
        };

        // Note el tipo de retorno de runAsync. Siempre es un CompletableFuture<Void> asi que
        // no tenemos manera de determinar el retorno al completar el computo
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(r);

        try {
            voidCompletableFuture.get(500, TimeUnit.MILLISECONDS);
        }catch(Exception e){
            assertTrue(false);
        }
    }

    @Test
    public void t5(){

        String testName = "t5";

        System.out.println(testName + " - El test (hilo ppal) esta corriendo en: "+Thread.currentThread().getName());

        CompletableFuture<String> completableFuture
                = CompletableFuture.supplyAsync(() -> {
            System.out.println(testName + " - completbleFuture corriendo en el thread: "+Thread.currentThread().getName());
            return "Hello";
        });

        //thenApply acepta lambdas de aridad 1 con retorno
        CompletableFuture<String> future = completableFuture
                .thenApply(s -> {
                    //System.out.println(testName + " - future corriendo en el thread: "+Thread.currentThread().getName());
                    imprimirMensaje(testName + " - future corriendo en el thread: "+Thread.currentThread().getName());
                    return s + " World";
                })
                .thenApply(s -> {
                    //System.out.println(testName + " - future corriendo en el thread: "+Thread.currentThread().getName());
                    imprimirMensaje(testName + " - future corriendo en el thread: "+Thread.currentThread().getName());
                    return s + "!";
                });

        try {
            assertEquals("Hello World!", future.get());
        }catch(Exception e){
            assertTrue(false);
        }
    }


    @Test
    public void t6(){

        String testName = "t6";

        CompletableFuture<String> completableFuture
                = CompletableFuture.supplyAsync(() -> {
            System.out.println(testName + " - completbleFuture corriendo en el thread: "+Thread.currentThread().getName());
            return "Hello";
        });

        // thenAccept solo acepta Consumer (lambdas de aridad 1 que no tienen retorno)
        // analice el segundo thenAccept ¿Tiene sentido?
        CompletableFuture<Void> future = completableFuture
                .thenAccept(s -> {
                    //System.out.println(testName + " - future corriendo en el thread: " + Thread.currentThread().getName() + " lo que viene del futuro es: "+s);
                    imprimirMensaje(testName + " - future corriendo en el thread: " + Thread.currentThread().getName() + " lo que viene del futuro es: "+s);
                })
                .thenAccept(s -> {
                    System.out.println(testName + " - future corriendo en el thread: " + Thread.currentThread().getName() + " lo que viene del futuro es: "+s);
                    imprimirMensaje(testName + " - future corriendo en el thread: " + Thread.currentThread().getName() + " lo que viene del futuro es: "+s);
                });

    }

    @Test
    public void t6_1(){

        String testName = "t6_1";

        CompletableFuture<String> completableFuture
                = CompletableFuture.supplyAsync(() -> {
            System.out.println(testName + " - completbleFuture corriendo en el thread: "+Thread.currentThread().getName());
            return "Hello";
        });

        // thenAccept solo acepta Consumer (lambdas de aridad 1 que no tienen retorno)
        // analice el segundo thenAccept ¿Tiene sentido?
        CompletableFuture<Void> future = completableFuture
                .thenAccept(s -> {
                    //System.out.println(testName + " - future corriendo en el thread: " + Thread.currentThread().getName() + " lo que viene del futuro es: "+s);
                    imprimirMensaje(testName + " - future corriendo en el thread: " + Thread.currentThread().getName() + " lo que viene del futuro es: "+s);
                });

        CompletableFuture<Void> future1 = completableFuture
                .thenAccept(s -> {
                    //System.out.println(testName + " - future corriendo en el thread: " + Thread.currentThread().getName() + " lo que viene del futuro es: "+s);
                    imprimirMensaje(testName + " - future corriendo en el thread: " + Thread.currentThread().getName() + " lo que viene del futuro es: "+s);
                });

    }

    @Test
    public void t7(){

        String testName = "t7";

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(testName + " - completbleFuture corriendo en el thread: "+Thread.currentThread().getName());
            return "Hello";
        });

        //thenAccept solo acepta Consumer (lambdas de aridad 1 que no tienen retorno)
        CompletableFuture<Void> future = completableFuture
                .thenRun(() -> {
                    System.out.println(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());
                    imprimirMensaje(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());
                    /*try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                })
                .thenRun(() -> {
                    System.out.println(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());
                    imprimirMensaje(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());
                });

    }

    @Test
    public void t8(){

        String testName = "t8";

        CompletableFuture<String> completableFuture = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());
                    return "Hello";
                })
                .thenCompose(s -> {
                    System.out.println(testName + " - compose corriendo en el thread: " + Thread.currentThread().getName());
                    return CompletableFuture.supplyAsync(() ->{
                        System.out.println(testName + " - CompletableFuture interno corriendo en el thread: " + Thread.currentThread().getName());
                        return s + " World"  ;
                    } );
                });

        try {
            assertEquals("Hello World", completableFuture.get());
        }catch(Exception e){
            assertTrue(false);
        }
    }

    public class Persona {
        String name;
        int edad;

        public Persona(String name, int edad) {
            this.name = name;
            this.edad = edad;
        }
    }

    @Test
    public void t8_1(){


        String testName = "t8_1";

        CompletableFuture<Persona> completableFuture = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());
                    return "Esteban.23";
                })
                .thenCompose(s -> {
                    System.out.println(testName + " - compose corriendo en el thread: " + Thread.currentThread().getName());
                    List<String> persona = Arrays.asList(s.split("\\."));
                    String nombre = persona.get(0);
                    int edad = Integer.parseInt(persona.get(1));
                    return CompletableFuture.supplyAsync(() ->{
                        System.out.println(testName + " - CompletableFuture interno corriendo en el thread: " + Thread.currentThread().getName());
                        return new Persona(nombre, edad);
                    } );
                });

        try {
            //System.out.println("Persona");
            //System.out.println("Nombre: " + completableFuture.get().name);
            //System.out.println("Edad: " + completableFuture.get().edad);
            Persona persona = completableFuture.get();

            assertEquals("Esteban", persona.name);
            assertEquals(23, persona.edad);
        } catch(Exception e){
            assertTrue(false);
        }
    }

    @Test
    public void t9(){

        String testName = "t9";


        // El segundo parametro de thenCombina es un BiFunction la cual sí tiene que tener retorno.
        CompletableFuture<String> completableFuture = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println(testName + " - Futuro supplyAsync corriendo en el thread " + Thread.currentThread().getName());
                    return "Hello";
                })
                .thenCombine(
                        CompletableFuture.supplyAsync(() -> {
                            System.out.println(testName + " - Futuro thenCombine (1) corriendo en el thread " + Thread.currentThread().getName());
                            return " World";
                        }),
                        (s1, s2) -> {
                            System.out.println(testName + " - Futuro thenCombine (2) corriendo en el thread " + Thread.currentThread().getName());
                            return s1 + s2;
                        }
                );

        try {
            assertEquals("Hello World", completableFuture.get());
        }catch(Exception e){
            assertTrue(false);
        }
    }

    @Test
    public void t10(){

        String testName = "t10";

        // El segundo parametro de thenAcceptBoth debe ser un BiConsumer. No puede tener retorno.
        CompletableFuture future = CompletableFuture.supplyAsync(() -> "Hello")
                .thenAcceptBoth(
                        CompletableFuture.supplyAsync(() -> " World"),
                        (s1, s2) -> System.out.println(testName + " corriendo en thread: "+Thread.currentThread().getName()+ " : " +s1 + s2));

        try{
            Object o = future.get();
        }catch(Exception e){
            assertTrue(false);

        }
    }

    @Test
    public void testEnlanceConSupplyAsync() {
        ExecutorService es = Executors.newFixedThreadPool(1);
        CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> "Hello", es);

        CompletableFuture<String> f2 = f.supplyAsync(()->{
            imprimirMensaje("t11 Ejecutando a");
            sleep(500);
            return "a";
        }).supplyAsync(() -> {
            imprimirMensaje("t11 Ejecuntando b");
            return "b";
        });

        try {
            assertEquals(f2.get(), "b");
        } catch (Exception e) {
            assertFalse(true);
        }
    }

    @Test
    public void testEnlanceConSupplyAsync_1() {
        ExecutorService es = Executors.newFixedThreadPool(1);
        CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> "Hello", es);

        CompletableFuture<String> f2 = f.supplyAsync(()->{
            imprimirMensaje("t11_1 Ejecutando a");
            sleep(500);
            return "a";
        }, es).supplyAsync(() -> {
            imprimirMensaje("t11_1 Ejecuntando b");
            return "b";
        }, es);

        try {
            assertEquals(f2.get(), "b");
        } catch (Exception e) {
            assertFalse(true);
        }
    }

    @Test
    public void t11(){

        String testName = "t11";

        ExecutorService es = Executors.newFixedThreadPool(1);
        CompletableFuture f = CompletableFuture.supplyAsync(()->"Hello",es);

        f.supplyAsync(() -> "Hello")
                .thenCombineAsync(
                    CompletableFuture.supplyAsync(() -> {
                        System.out.println(testName + " thenCombineAsync en Thread (1): " + Thread.currentThread().getName());
                        return " World";
                    }),
                    (s1, s2) -> {
                        System.out.println(testName + " thenCombineAsync en Thread (2): " + Thread.currentThread().getName());
                        return s1 + s2;
                    },
                    es
                );
    }

    @Test
    public void testEjercicio1() {
        String testName = "Ex 1";
        ExecutorService es = Executors.newFixedThreadPool(1);

        CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
                //System.out.println(testName + " - Futuro completable supplyAsync con Thread: " + Thread.currentThread().getName());
                imprimirMensaje(testName + " - Futuro completable supplyAsync con Thread: " + Thread.currentThread().getName());
                return "Daniel ";
            }, es);

        CompletableFuture<String> f2 = f
                .thenApplyAsync(s -> {

                    //System.out.println(testName + " - Futuro completable thenApply (1) con Thread: " + Thread.currentThread().getName());
                    imprimirMensaje(testName + " - Futuro completable thenApply (1) con Thread: " + Thread.currentThread().getName());
                    sleep(500);
                    return s + "Esteban ";
                })
                .thenApplyAsync(s -> {
                    //System.out.println(testName + " - Futuro completable thenApply (2) con Thread: "+ Thread.currentThread().getName());
                    imprimirMensaje(testName + " - Futuro completable thenApply (2) con Thread: "+ Thread.currentThread().getName());
                    return s + "Guevara";
                });

        try {
            System.out.println(f2.get());
            assertEquals("Daniel Esteban Guevara", f2.get());
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test
    public void testEjercicio1_1() {
        String testName = "Ex 1_1";
        ExecutorService es = Executors.newFixedThreadPool(1);
        ExecutorService es1 = Executors.newFixedThreadPool(2);

        CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
            //System.out.println(testName + " - Futuro completable supplyAsync con Thread: " + Thread.currentThread().getName());
            imprimirMensaje(testName + " - Futuro completable supplyAsync con Thread: " + Thread.currentThread().getName());
            return "Daniel ";
        }, es);

        CompletableFuture<String> f2 = f
                .thenApplyAsync(s -> {
                    //System.out.println(testName + " - Futuro completable thenApply (1) con Thread: " + Thread.currentThread().getName());
                    imprimirMensaje(testName + " - Futuro completable thenApply (1) con Thread: " + Thread.currentThread().getName());
                    sleep(500);
                    return s + "Esteban ";
                }, es1)
                .thenApplyAsync(s -> {
                    //System.out.println(testName + " - Futuro completable thenApply (2) con Thread: "+ Thread.currentThread().getName());
                    imprimirMensaje(testName + " - Futuro completable thenApply (2) con Thread: "+ Thread.currentThread().getName());
                    return s + "Guevara";
                }, es1);

        try {
            System.out.println(f2.get());
            assertEquals("Daniel Esteban Guevara", f2.get());
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test
    public void testEjercicio1_2() {
        String testName = "Ex 1_2";
        ExecutorService es = Executors.newFixedThreadPool(1);
        ExecutorService es1 = Executors.newFixedThreadPool(2);

        CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
            //System.out.println(testName + " - Futuro completable supplyAsync con Thread: " + Thread.currentThread().getName());
            imprimirMensaje(testName + " - Futuro completable supplyAsync con Thread: " + Thread.currentThread().getName());
            return "Daniel ";
        }, es);

        CompletableFuture<String> f2 = f
                .thenApply(s -> {
                    //System.out.println(testName + " - Futuro completable thenApply (1) con Thread: " + Thread.currentThread().getName());
                    imprimirMensaje(testName + " - Futuro completable thenApply (1) con Thread: " + Thread.currentThread().getName());
                    sleep(500);
                    return s + "Esteban ";
                })
                .thenApplyAsync(s -> {
                    //System.out.println(testName + " - Futuro completable thenApply (2) con Thread: "+ Thread.currentThread().getName());
                    imprimirMensaje(testName + " - Futuro completable thenApply (2) con Thread: "+ Thread.currentThread().getName());
                    return s + "Guevara";
                }, es1);

        try {
            System.out.println(f2.get());
            assertEquals("Daniel Esteban Guevara", f2.get());
        } catch (Exception e) {
            assertTrue(false);
        }
    }

}
