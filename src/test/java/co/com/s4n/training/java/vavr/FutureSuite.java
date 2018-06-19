package co.com.s4n.training.java.vavr;

import co.com.s4n.training.java.ClassTestFold;
import io.vavr.Function1;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vavr.concurrent.Promise;
import org.junit.Test;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static io.vavr.Predicates.instanceOf;
import static io.vavr.Patterns.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.util.function.BiFunction;

import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import static io.vavr.API.*;
import static org.junit.Assert.assertNotEquals;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.*;
import java.util.function.Supplier;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;


public class FutureSuite {
    // Max wait time for results = WAIT_MILLIS * WAIT_COUNT (however, most probably it will take only WAIT_MILLIS * 1)
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

    /**
     * Se prueba que pasa cuando se crea un futuro con error.
     */
    @Test(expected = Error.class)
    public void testFutureWithError() {
        Future<String> future = Future.of(() -> {throw new Error("Failure");});
        future.get();
    }

    /**
     * El resultado de un futuro se puede esperar con onComplete
     */
    @Test
    public void testOnCompleteSuccess() {
        // Toma un futuro de un arreglo de String con tres elementos Text, To, Split
        Future<String[]> futureSplit = Future.of(() -> "TEXT_TO_SPLIT".split("_"));

        //Si hubo un suceso entonces toma el tamaño del resultado e itera sobre el,
        //toma cada uno y hace un lowercase
        futureSplit.onComplete(res -> {
            if (res.isSuccess()) {
                for (int i = 0; i < res.get().length; i++) {
                    res.get()[i] = res.get()[i].toLowerCase();
                }
            }
            System.out.println("OnComplete " + Thread.currentThread().getName());
        });
        futureSplit.await();
        String[] expected = {"text", "to", "split"};
        //Wait until we are sure that the second thread (onComplete) is done.
        // Espera 50 milisegundos, ya que en la posición 2 esta la palabra split.
        waitUntil(() -> {
            System.out.println("OnComplete2 " + Thread.currentThread().getName());
            return futureSplit.get()[2].equals("split");
        });

        assertArrayEquals("The arrays are different", expected, futureSplit.get());
    }

    @Test
    public void testOnCompleteSuccess_1() {
        // Toma un futuro de un arreglo de String con tres elementos Text, To, Split
        Future<String[]> futureSplit = Future.of(() -> "TEXT_TO_SPLIT".split("_"));

        //Si hubo un suceso entonces toma el tamaño del resultado e itera sobre el,
        //toma cada uno y hace un lowercase
        futureSplit.onComplete(res -> {
            if (res.isSuccess()) {
                for (int i = 0; i < res.get().length; i++) {
                    res.get()[i] = res.get()[i].toLowerCase();
                }
            }
        });
        //futureSplit.await();
        String[] expected = {"text", "to", "split"};
        //Wait until we are sure that the second thread (onComplete) is done.
        // Espera 50 milisegundos, ya que en la posición 2 esta la palabra split.
        waitUntil(() -> futureSplit.get()[2].equals("split"));

        assertArrayEquals("The arrays are different", expected, futureSplit.get());
    }

    @Test
    public void testOnCompleteSuccess_2() {
        // Toma un futuro de un arreglo de String con tres elementos Text, To, Split
        Future<String[]> futureSplit = Future.of(() -> "TEXT_TO_SPLIT".split("_"));

        //Si hubo un suceso entonces toma el tamaño del resultado e itera sobre el,
        //toma cada uno y hace un lowercase
        futureSplit.onComplete(res -> {
            if (res.isSuccess()) {
                for (int i = 0; i < res.get().length; i++) {
                    res.get()[i] = res.get()[i].toLowerCase();
                }
            }
        });
        futureSplit.await();
        String[] expected = {"text", "to", "split"};
        //Wait until we are sure that the second thread (onComplete) is done.
        // Espera 50 milisegundos, ya que en la posición 2 esta la palabra split.
        //waitUntil(() -> futureSplit.get()[2].equals("split"));

        assertArrayEquals("The arrays are different", expected, futureSplit.get());
    }



    /**
     *Valida la funcion de find aplicando un predicado que viene de una implementacion de la clase Iterable que contenga Futuros
     * Tener encuenta el primero que cumpla con el predicado y sea Oncomplete es el que entrega
     */
    @Test
    public void testFutureToFind() {
        List<Future<Integer>> myLista = List.of( Future.of(() -> 5+4), Future.of(() -> 6+9), Future.of(() -> 31+1),Future.of(() -> 20+9));

        Future<Option<Integer>> futureSome = Future.find(myLista, v -> v < 10);
        Future<Option<Integer>> futureSomeM = Future.find(myLista, v -> v > 31);
        Future<Option<Integer>> futureNone = Future.find(myLista, v -> v > 40);
        assertEquals("Valide find in the List with Future", Some(9), futureSome.get());
        assertEquals("Valide find in the List with Future", Some(32), futureSomeM.get());
        assertEquals("Valide find in the List with Future", None(), futureNone.get());
    }

    /**
     *Valida la funcion de find aplicando un predicado que viene de una implementacion de la clase Iterable que contenga Futuros
     */
    @Test
    public void testFutureToTransform() {
        Integer futuretransform = Future.of( () -> 9).transform(v -> v.getOrElse(12) + 80);
        Future<Integer> myResult= Future.of(() -> 9).transformValue(v -> Try.of(()-> v.get()+12));
        assertEquals("Valide transform in a Future",new Integer(89) ,futuretransform);
        assertEquals("Valide transform in a Future",new Integer (21) ,myResult.get());
    }

    /**
     *Valida la funcion de find aplicando un predicado que viene de una implementacion de la clase Iterable que contenga Futuros
     */
    @Test
    public void testFutureToOnFails() {
        final String[] valor = {"default","pedro"};
        String[] valor1 = {"hola", "hola"};

        Consumer<Object> funcion = element -> {
            valor[1] = "fallo";
        };
        Future<Object> myFuture = Future.of(() -> {throw new Error("No implemented");});

        myFuture.onFailure(funcion);

        assertEquals("Validete Onfailure in Future", "pedro",valor[1]);

        System.out.println("Fail1: " + valor[1].toString());

        myFuture.await();

        System.out.println("Fail2: " + valor[1].toString());

        assertTrue("Validete Onfailure in Future",myFuture.isFailure());

        System.out.println("Fail3: " + valor[1].toString());

        waitUntil(() -> valor[1].toString()=="fallo");

        assertEquals("Validete Onfailure in Future", "fallo",valor[1]);
    }

    /**
     *Se valida el uso de Map obteniendo la longitu de un String
     * Se valida el uso Flatmap obteniendo el resultado apartir de una suma
     */
    @Test
    public void testFutureToMap() {
        Future<Integer> myMap = Future.of( () -> "pedro").map(v -> v.length());
        Future<Integer> myFlatMap = Future.of( () ->Future.of(() -> 5+9))
                .flatMap(v -> Future.of(()->v.await()
                        .getOrElse(15)));
        Future<Integer> myFlatMap1 = Future.of( () ->Future.of(() ->
                new Integer(null))).flatMap(v -> Future.of(()->v.await().getOrElse(15)));
        assertEquals("validate map with future",new Integer(5),myMap.get());
        assertEquals("validate map with future",new Integer(14),myFlatMap.get());
        assertEquals("validate map with future",new Integer(15),myFlatMap1.get());
    }



    @Test
    public void testFutureToMap_1() {
           Future<Integer> myMap = Future.of( () -> {
                       System.out.println("Map0 " + Thread.currentThread().getName());
                        return "pedro";
                   }
           ).map(v -> {
              System.out.println("Map1 " + Thread.currentThread().getName());
               return v.length();
           });
                   assertEquals("validate map with future",new Integer(5),myMap.get());
    }

    @Test
    public void testFutureToMap_2() {
        Future<Integer> myFlatMap = Future.of( () ->Future.of(() -> {return 5+9;}))
                .flatMap(v -> Future.of(()->v.await()
                        .getOrElse(15)));
        Future<Integer> myFlatMap1 = Future.of( () ->Future.of(() ->
                new Integer(null))).flatMap(v -> Future.of(()->v.await().getOrElse(15)));
        assertEquals("validate map with future",new Integer(14),myFlatMap.get());
        assertEquals("validate map with future",new Integer(15),myFlatMap1.get());
    }

    @Test
    public void testFutureToMap_3() {
        Future<Integer> myFlatMap = Future.of( () ->Future.of(() -> {return 5+9;}))
                .flatMap(v -> Future.of(()->v.await()
                        .getOrElse(15)));
        Future<Integer> myFlatMap1 = Future.of( () ->Future.of(() ->
                new Integer(null))).flatMap(v -> Future.of(()->v.getOrElse(15)));
        assertEquals("validate map with future",new Integer(14),myFlatMap.get());
        assertEquals("validate map with future",new Integer(15),myFlatMap1.get());
    }

    private Future<Double> sumar(Double x, Double y) {
        return Future.of(() -> x + y);
    }

    private Future<Double> dividir(Double x, Double y) {
        System.out.println("x: "+ x + "Y: " + y);
        return (y != 0) ? Future.of(() -> x / y) : Future.failed(new Exception("/ error"));
    }


    @Test
    public void testFlatMap_1() {
        Future<Double> future = sumar(1D, 1D)
                .flatMap(a -> sumar(a, 2D)
                        .flatMap(b -> dividir(b, 2D)
                                .flatMap(c -> sumar(a, 2D))));
        future.await();
        assertEquals(Future.of(() -> 4D).get(), future.get());
    }

    @Test
    public void testFlatMap_2() {
        Future<Double> future = sumar(1D, 1D)
                .flatMap(a -> sumar(a, 2D)
                        .flatMap(b -> dividir(b, 0D)
                                .flatMap(c -> sumar(a, 2D))));
        future.await();
        assertTrue(future.isFailure());
    }

    /**
     *Se valida el uso de foreach para el encademaient de futuros
     */
    @Test
    public void testFutureToForEach() {
        java.util.List<Integer> results = new ArrayList<>();
        java.util.List<Integer> compare = Arrays.asList(9,15,32,29);
        List<Future<Integer>> myLista = List.of(
                Future.of(() -> 5 + 4), Future.of(() -> 6 + 9), Future.of(() -> 31 + 1), Future.of(() -> 20 + 9));
        myLista.forEach(v -> {
            results.add(v.get());
        });
        assertEquals("Validate Foreach in Future", compare, results);
    }

    @Test
    public void forEachInFuture() {
        final String[] result = {"666"};
        Future<String> f1 = Future.of(() -> "1");
        f1.forEach(i -> result[0] = i);
        f1.await();
        assertEquals(f1.get(), "1");
        waitUntil(() -> "1".equals(result[0]));
        assertEquals("1", result[0]);
    }

    @Test
    public void forEachInFuture_1() {
        final String[] result = {"666"};
        Future<String> f1 = Future.of(() -> "1");
        f1.map(i -> result[0] = i);
        f1.await();
        assertEquals(f1.get(), "1");
    }

    @Test
    public void forEachInFuture_2() {
        final String[] result = {"666"};
        Future<String> f1 = Future.of(() -> "1");
        Future<String> f2 = Future.of(() -> "1");
        Future<String> strings = f1.onComplete(i -> result[0] = i.get());
        f1.await();
        assertEquals(f1.get(), "1");
        System.out.println("OnComplete2: " + strings.get());
        assertEquals(strings.get(), "1");
        assertEquals(strings, f1);
        assertNotEquals(f1, f2);
    }

    @Test
    public void forEachInFuture_3() {
        final String[] result = {"666"};
        Future<Integer> f1 = Future.of(() -> 1);
        Future<Integer> fold = Future.fold(List.of(f1),
                0, (acc, el) -> acc + el);

        assertEquals(new Integer(1), fold.get());

        Future<String> f3 = Future.of(() -> "1");
        Future<String> f4 = Future.of(() -> "2");
        Future<String> f5 = Future.of(() -> "3");
        Future<String> f6 = Future.fold(List.of(f3, f4, f5), "", (acc, el) -> acc + el);
        Future<String> f7 = f6.await();
        assertEquals("123", f7.get());
        assertEquals(f6, f7);

    }

    @Test
    public void forEachInFuture_4() {
        Future<String> f3 = Future.of(() -> "1");
        Future<String> f4 = Future.of(() -> "2");
        Future<String> f5 = Future.failed(new Exception());
        Future<String> f6 = Future.fold(List.of(f3, f4, f5), "", (acc, el) ->
                {
                    System.out.println(acc + el);
                    return acc + el;});
        Future<String> f7 = f6.await();
        assertTrue(f7.isFailure());

    }

    @Test
    public void forEachInFuture_7() {
        Future<String> f3 = Future.of(() -> "1");
        Future<String> f4 = Future.of(() -> "2");
        Future<String> f5 = Future.failed(new Exception());
        Future<String> f6 = f3.flatMap(a -> f4.flatMap(b -> f5.flatMap(c -> Future.of(() -> a + b + c))));
        Future<String> f7 = f6.await();
        assertTrue(f7.isFailure());

    }

    @Test
    public void forEachInFuture_8() {
        Future<String> f3 = Future.of(() -> "1");
        Future<String> f4 = Future.of(() -> "2");
        Future<String> f5 = Future.of(() -> "3");
        Future<String> f6 = f3.flatMap(a -> f4.flatMap(b -> f5.flatMap(c -> Future.of(() -> a + b + c))));
        Future<String> f7 = f6.await();
        assertEquals("123", f7.get());

    }

    /*
    private Future<String> myFold1(List<Future<String>> list, String zero, BiFunction<String, String, String> bo) {
        Future<String> ofZero = Future.of(() -> zero);
        Future<String> future = ofZero
                .flatMap(z -> {
                    Future<String> a = list.fold(z, (acc, el) -> Future.of(() -> bo.apply(acc.get(), el.get())));
                    return
                });
        return Future.of(() -> "");
    }*/


    private Future<String> myFold(List<Future<String>> list, String zero, BiFunction<String, String, String> bo) {
        String[] resultado = {zero};
        List<String> strings = list.flatMap(a -> {
            resultado[0] = bo.apply(resultado[0], a.get());
            return Future.of(() -> resultado[0]);
        });
        return Future.of(() -> resultado[0]);
    }

    @Test
    public void testMyFold() {
        List<Future<String>> list = List.of(Future.of(() -> "A"), Future.of(() -> "B"), Future.of(() -> "C"), Future.of(() -> "D"));
        BiFunction<String, String, String> bo = (a, b) -> a + b;
        Future<String> future = myFold(list, "", bo);
        future.await();
        System.out.println(future.get());
    }

    /**
     * Se puede crear un future utilizando funciones lambda
     */
    @Test
    public void testFromLambda(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<String> future = Future.ofSupplier(service, ()-> Thread.currentThread().getName());
        String future_thread = future.get();
        String main_thread = Thread.currentThread().getName();
        assertNotEquals("Failure - the future must to run in another thread", main_thread, future_thread);
        assertTrue("Failure - the future must be completed after call get()", future.isCompleted());
    }

    /**
     * Se puede crear un future utilizando referencias a metodos
     */
    @Test
    public void testFromMethodRef(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Double> future = Future.ofSupplier(service, Math::random);
        future.get();
        assertTrue("Failure - the future must be completed after call get()", future.isCompleted());
    }


    /**
     * Este metodo me permite coger el primero futuro que termine su trabajo, la coleccion de futuros debe
     * extender de la interfaz iterable
     */
    @Test
    public void testFutureFirstCompleteOf() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        ExecutorService service2 = Executors.newSingleThreadExecutor();

        Future<String> future2 = Future.ofSupplier(service, () -> {
            try {
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return ("Hello this is the Future 2");
        });
        Future<String> future = Future.ofSupplier(service2, () -> "Hello this is the Future 1");
        List<Future<String>> futureList = List.of(future,future2);
        Future<String> future3 = Future.firstCompletedOf(service,futureList);

        assertEquals("Failure - the future 2 complete his job first",
                "Hello this is the Future 1",future3.get());
    }

    /**
     * Se puede cambiar el valor de un Future.Failure por otro Future utilizando el metodo fallBackTo
     */
    @Test
    public void testFailureFallBackTo(){
        Future<String> failure = Future.of(() -> {throw new Error("No implemented");});
        String rescue_msg = "Everything is Ok!";
        Future<String> rescue_future = Future.of(() -> rescue_msg);
        Future<String> final_future = failure.fallbackTo(rescue_future);
        assertEquals("Failure - The failure must be mapped to the rescue message", rescue_msg, final_future.get());
    }

    /**
     * El metodo fallBackTo no tiene efecto si el future inicial es exitoso
     */
    @Test
    public void testSuccessFallBackTo(){
        String initial_msg = "Hello!";
        Future<String> success = Future.of(() -> initial_msg);
        Future<String> rescue_future = Future.of(() -> "Everything is Ok!");
        Future<String> final_future = success.fallbackTo(rescue_future);
        assertEquals("Failure - The success future must contain the initial value", initial_msg, final_future.get());
    }

    /**
     * al usar el metodo fallBackTo si los dos futures fallan el failure final debe contener el error del futuro inicial
     */
    @Test
    public void testFailureFallBackToFailure(){
        String initial_error = "I failed first!";
        Future<String> initial_future = Future.of(() -> {throw new Error(initial_error);});
        Future<String> rescue_future = Future.of(() -> {TimeUnit.SECONDS.sleep(1);throw new Error("Second failure");});
        Future<String> final_future = initial_future.fallbackTo(rescue_future);
        final_future.await();
        assertEquals("Failure - the result must be the first failure",
                initial_error,
                final_future.getCause().get().getMessage()); //Future -> Some -> Error -> String
    }

    /**
     * Se puede cancelar un futuro si este no ha sido completado aún
     */
    @Test
    public void testCancelFuture(){
        Future<String> future = Future.of(() -> {
            TimeUnit.SECONDS.sleep(2);
            return "End";});
        assertTrue("Failure - The future was not canceled", future.cancel());
        assertTrue("Failure - The future must be completed after cancel it", future.isCompleted());
        assertTrue("Failure - A canceled future must be a Failure",future.isFailure());
    }

    /**
     * No se puede cancelar un futuro completado
     */
    @Test
    public void testCancelAfterComplete(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<String> future = Future.of(service,() -> "Hello!");
        future.await();
        assertTrue("Failure - the future was not completed", future.isCompleted());
        assertFalse("Failure - the future was canceled after its ends", future.cancel());
    }

    /**
     * onFail, onSuccess y onComplete devuelven el mismo futuro que invoca los metodos
     */
    @Test
    public void testTriggersReturn() {
        Future<String> futureSplit = Future.of(() -> "Hello!");

        Future<String> onComplete = futureSplit.onComplete(res -> {/*do some side effect*/});
        Future<String> onSuccess = futureSplit.onSuccess(res ->{/*do some side effect*/});
        Future<String> onFail = futureSplit.onFailure(res -> {/*do some side effect*/});
        futureSplit.await();
        assertSame("Failure - onComplete did not return the same future", futureSplit, onComplete);
        assertSame("Failure - onSuccess did not return the same future", futureSplit, onSuccess);
        assertSame("Failure - onFail did not return the same future", futureSplit, onFail);
    }

    /**
     * Se prueba el poder realizar una acción luego de que un futuro finaliza.
     */
    @Test
    public void testOnSuccess() {
        String[] holder = {"Don't take my"};
        Future<String> future = Future.of(() -> "Ghost");
        future.onSuccess(s -> {
            assertTrue("Future is not completed", future.isCompleted());
            holder[0] += " hate personal";
        });
        waitUntil(() -> holder[0].length() > 14);
        assertEquals("Failure - The message wasn't change after success.", "Don't take my hate personal",holder[0]);
    }

    /**
     * Se puede crear un futuro como resultado de aplicar un fold a un objeto iterable compuesto de futuros
     */
    @Test
    public void testFoldOperation(){
        List<Future<Integer>> futureList = List.of(
                Future.of(()->0),
                Future.of(()->1),
                Future.of(()->2),
                Future.of(()->3));
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<String> futureResult = Future.fold(
                service, // Optional executor service
                futureList, // <Iterable>
                "Numbers on the list: ", // Seed
                (acumulator, element) -> acumulator + element); // Fold operation
        assertEquals("Failure - the result of the fold operation is incorrect",
                "Numbers on the list: 0123",
                futureResult.get());
    }

    /**
     * Un futuro se puede filtrar dado un predicado
     * filter retorna una nueva referencia
     */
    @Test
    public void testFilter() {
        Future<String> future = Future.successful("this_is_a_text");
        Future<String> some = future.filter(s -> s.contains("a_text"));
        Future<String> none = future.filter(s -> s.contains("invalid"));
        assertNotSame("Failure - The futures shouldn't be the same",future,some);
        assertNotSame("Failure - The futures shouldn't be the same",future,none);
        assertEquals("Failure - The filter was not successful", "this_is_a_text", some.get());
        assertTrue("Failure - The filter was successful", none.isEmpty());
    }

    /**
     *  Sequence permite cambiar una lista de futuros<T> a un futuro de una lista <T>,
     *  este devuelve por defecto un Futuro<stream>
     */
    @Test
    public void testFutureWithSequence() {
        List<Future<String>> listOfFutures = List.of(
                Future.of(() -> "1 mensaje"),
                Future.of(() -> "2 mensaje")
        );

        Future<Seq<String>> futureList = Future.sequence(listOfFutures);
        assertFalse("The future is already completed",futureList.isCompleted());
        assertTrue("Failure - futureList is not instance of Future",futureList instanceof Future);

        Stream<String> stream = (Stream<String>) futureList.get();
        assertEquals("Stream does not a List",List.of("1 mensaje","2 mensaje").asJava(),stream.asJava());
    }

    /**
     *  El Recover me sirve para recuperar futuros que hayan fallado, y se recupera el resultado con otro
     *  y se crea un futuro nuevo
     */
    @Test
    public void testFutureRecover() {
        final String[] thread1 = {""};
        final String[] thread2 = {""};
        Future<Integer> aFuture = Future.of(
                () -> {
                    Thread.sleep(1000);
                    thread1[0] = Thread.currentThread().getName().toString();
                    return 2/0;
                }
        );
        Future<Integer> aRecover = aFuture.recover(it -> Match(it).of(
                Case($(),() -> {
                    thread2[0] = Thread.currentThread().getName().toString();
                    return 2;
                })
        ));
        aRecover.await();
        System.out.println("Recover 1 " + thread1[0]);
        System.out.println("Recover 2 " + thread2[0]);
        assertTrue("Failure - The future wasn't a success",aRecover.isSuccess());
        assertFalse("Failure - The threads should be different",thread1[0].equals(thread2[0]));
        assertEquals("Failure - It's not two",new Integer(2),aRecover.get());
    }

    @Test
    public void testFutureRecover_2() {
        final String[] thread1 = {""};
        final String[] thread2 = {""};
        ExecutorService es = Executors.newFixedThreadPool(1);
        Future<Integer> aFuture = Future.of(es,
                () -> {
                    Thread.sleep(1000);
                    thread1[0] = Thread.currentThread().getName().toString();
                    return 2/0;
                }
        );

        Future<Integer> aRecover = aFuture.recover(it -> Match(it).of(
                Case($(),() -> {
                    thread2[0] = Thread.currentThread().getName().toString();
                    return 2;
                })
        ));
        aRecover.await();
        System.out.println("Recover 1 " + thread1[0]);
        System.out.println("Recover 2 " + thread2[0]);
        assertTrue("Failure - The future wasn't a success",aRecover.isSuccess());
        assertTrue("Failure - The threads should be different",thread1[0].equals(thread2[0]));
        assertEquals("Failure - It's not two",new Integer(2),aRecover.get());
    }

    /**
     *  El Recover me sirve para recuperar futuros que hayan fallado, y se recupera el futuro con otro
     *  y se crea un futuro nuevo
     */
    @Test
    public void testFutureRecoverWith() {
        final String[] thread1 = {""};
        final String[] thread2 = {""};
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> aFuture = Future.of(service,() -> {
            thread1[0] = Thread.currentThread().getName().toString();
            return 2 / 0;
        });
        Future<Integer> aRecover = aFuture.recoverWith(it -> Match(it).of(
                Case($(), () -> Future.of(() -> {
                    thread2[0] = Thread.currentThread().getName().toString();
                    return 1;
                }))
        ));
        aRecover.await();
        assertTrue("Failure - The future wasn't a success",aRecover.isSuccess());
        assertFalse("Failure - The threads should be different",thread1[0].equals(thread2[0]));
        assertEquals("Failure - It's not one",new Integer(1),aRecover.get());
    }

    @Test
    public void testFutureRecover_1() {
        final String[] thread1 = {""};
        final String[] thread2 = {""};
        Future<Integer> aFuture = Future.of(
                () -> {
                    Thread.sleep(1000);
                    thread1[0] = Thread.currentThread().getName().toString();
                    return 2/0;
                }
        );
        Future<Integer> aRecover = aFuture.recover(it -> Match(it).of(
                Case($(),() -> {
                    thread2[0] = Thread.currentThread().getName().toString();
                    return 2/0;
                })
        ));
        aRecover.await();
        System.out.println("Recover 1 r " + thread1[0]);
        System.out.println("Recover 2 r " + thread2[0]);
        assertTrue("Failure - The future wasn't a success",aRecover.isFailure());
        assertFalse("Failure - The threads should be different",thread1[0].equals(thread2[0]));
    }

    @Test
    public void testFutureRecoverWith_1() {
        final String[] thread1 = {""};
        final String[] thread2 = {""};
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> aFuture = Future.of(service,() -> {
            thread1[0] = Thread.currentThread().getName().toString();
            return 2 / 0;
        });
        Future<Integer> aRecover = aFuture.recoverWith(it -> Match(it).of(
                Case($(), () -> Future.of(() -> {
                    thread2[0] = Thread.currentThread().getName().toString();
                    return 2 / 0;
                }))
        ));
        aRecover.await();
        assertTrue("Failure - The future wasn't a success",aRecover.isFailure());
        System.out.println("Recover 1 w " + thread1[0]);
        System.out.println("Recover 2 w " + thread2[0]);
    }

    /**
     * Validar pattern Matching a un future correcto.
     */
    @Test
    public void testFuturePatternMatchingSuccess() {
        Future<String> future = Future.of(() -> "Glad to help");
        String result = Match(future).of(
                Case($Future($(instanceOf(Error.class))), "Failure!"),
                Case($Future($()), "Success!"),
                Case($(), "Double failure"));
        assertEquals("Failure - The future should be a success", "Success!", result);
    }

    /**
     * Validar pattern Matching a un future correcto.
     */
    @Test
    public void testFuturePatternMatchingError() {

        Future<String> future = Future.of(() -> {
            throw new Error("Failure");
        });

        // Este test algunas veces tiene exito y algunas otras fracasa
        // Por que sera?

        String result = Match(future).of(
                Case($Future($Some($Failure($()))), "Failure!"),
                Case($Future($()), "Success!"),
                Case($(), "Double failure"));

        assertEquals("Failure - The future should be a success",
                "Failure!",
                result);
    }

    /**
     * Crear un futuro a partir de un Try fallido
     */
    @Test
    public void testFromFailedTry(){
        Try<String> tryValue = Try.of(() -> {throw new Error("Try again!");});
        Future<String> future = Future.fromTry(tryValue);
        future.await();
        assertTrue("Failure - A future from a failed Try must be Failure", future.isFailure());
        assertEquals("Failure - The cause of the failure future must be the same of the tryValue",
                tryValue.getCause(),
                future.getCause().get()); //Future -> Option -> Throwable
    }

    /**
     * Crear un futuro a partir de un Try exitoso
     */
    @Test
    public void testFromSuccessTry(){
        Try<String> tryValue = Try.of(() -> "Hi!");
        Future<String> future = Future.fromTry(tryValue);
        future.await();
        assertTrue("Failure - A future from a success Try must be success", future.isSuccess());
        assertEquals("Failure - A future from a success Try must be contain the value", "Hi!",future.get());
    }

    /**
     * Crear un futuro de la libreria vavr a partir de un futuro de java8
     */
    @Test
    public void testFromJavaFuture() {
        Callable<String> task = () -> Thread.currentThread().getName();
        ExecutorService service = Executors.newSingleThreadExecutor();
        java.util.concurrent.Future<String> javaFuture = service.submit(task);
        ExecutorService service2 = Executors.newSingleThreadExecutor();
        Future<String> future = Future.fromJavaFuture(service2, javaFuture);
        try {
            assertEquals("Failure - vavr Future and java Future had different results", javaFuture.get(), future.get());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Un futuro se puede crear a partir de una promesa
     */
    @Test
    public void testFutureFromPromise() {
        Promise<String> promise = Promise.successful("success!");
        //Future can be created from a promise
        Future<String> future = promise.future();
        future.await();
        assertTrue("The future did not complete", future.isCompleted());
        assertTrue("The promise did not complete", promise.isCompleted());
        assertEquals("The future does not have the value from the promise", "success!", future.get());
    }

    /**
     *Se valida la comunicacion de Futuros mediante promesas
     */
    @Test
    public void testComunicateFuturesWithPromise() {
        Promise<Integer> mypromise = Promise.make();
        Future<Object> myFuture = Future.of(()-> {
            mypromise.success(15);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "algo";
        });
        Future<Integer> myFutureOne = mypromise.future();
        myFutureOne.await();
        assertEquals("Failure - Validate Future with Promise",new Integer(15),myFutureOne.get());
        assertFalse("Failure - Validate myFuture is not complete",myFuture.isCompleted());
    }

    private Future<String> myFold1(List<Future<String>> list, String zero, BiFunction<String, String, String> bo) {
        String[] resultado = {zero};
        List<String> strings = list.flatMap(a -> {
            resultado[0] = bo.apply(resultado[0], a.get());
            return Future.of(() -> resultado[0]);
        });
        return Future.of(() -> resultado[0]);
    }

    @Test
    public void myFoldTest() {
        List<Future<String>> myLista = List.of(Future.of(() -> "5 + 4"), Future.of(() -> "6 + 9"), Future.of(() -> "31 + 1"), Future.of(() -> "20 + 9"));
        //Future<String> future = myFold(myLista);
    }

    private void sleep(int milliseconds){
        try{
            Thread.sleep(milliseconds);
        }catch(Exception e){
            System.out.println("Problemas durmiendo hilo");
        }
    }

    @Test
    public void testFutureLazy() {
        Future<String> f1 = Future.of(() -> {
            sleep(500);
            return "Hola";
        });

        Future<String> f2 = Future.of(() -> {
            sleep(800);
            return " mundo";
        });

        Future<String> f3 = Future.of(() -> {
            sleep(300);
            return "!";
        });

        long inicio = System.nanoTime();

        Future<String> fn = f1
                .flatMap(a-> f2
                        .flatMap(b -> f3
                                .flatMap(c -> Future.of(() -> a + b + c))));

        fn.await();

        long fin = System.nanoTime();

        double elapsed = (fin - inicio) * Math.pow(10, -6);

        System.out.println("Elapsed: " + elapsed);

        assertTrue(elapsed >= 800D);
        assertEquals("Hola mundo!", fn.get());
    }

    @Test
    public void testMyFoldGenerico() {
        List<Future<String>> list = List.of(Future.of(() -> "A"), Future.of(() -> "B"), Future.of(() -> "C"), Future.of(() -> "D"));
        BiFunction<String, String, String> bo = (a, b) -> a + b;
        ClassTestFold<String> classTestFold = new ClassTestFold<>();

        Future<String> future = classTestFold.myFold(list, "", bo);
        future.await();
        System.out.println("Generica: " + future.get());
        assertEquals("ABCD", future.get());
    }

    @Test
    public void testMyFoldGenerico_1() {
        List<Future<String>> list = List.of(Future.of(() -> "A"), Future.of(() -> "B"), Future.of(() -> "C"), Future.of(() -> "D"));
        BiFunction<String, String, String> bo = (b, a) -> a + b;
        ClassTestFold<String> classTestFold = new ClassTestFold<>();
        Future<String> future = classTestFold.myFold(list, "", bo);
        future.await();
        System.out.println("Generica: " + future.get());
        assertEquals("DCBA", future.get());
    }

    @Test
    public void testMyFoldGenerico_2() {
        List<Future<Integer>> list = List.of(Future.of(() -> 1 + 2), Future.of(() -> 2 + 3), Future.of(() -> 3 + 4), Future.of(() -> 4 + 5));
        BiFunction<Integer, Integer, Integer> bo = (a, b) -> a + b;
        ClassTestFold<Integer> classTestFold = new ClassTestFold<>();
        Future<Integer> future = classTestFold.myFold(list, 0, bo);
        future.await();
        System.out.println("Generica: " + future.get());
        assertEquals(new Integer(24), future.get());
    }

    @Test
    public void testMyFoldGenerico_3() {
        List<Future<Integer>> list = List.of(Future.of(() -> 1 + 2), Future.of(() -> 2 + 3), Future.of(() -> 3 + 4), Future.of(() -> 4 + 5));
        BiFunction<Integer, Integer, Integer> bo = (a, b) -> a * b;
        ClassTestFold<Integer> classTestFold = new ClassTestFold<>();
        Future<Integer> future = classTestFold.myFold(list, 1, bo);
        future.await();
        System.out.println("Generica: " + future.get());
        assertEquals(new Integer(945), future.get());
    }

}
