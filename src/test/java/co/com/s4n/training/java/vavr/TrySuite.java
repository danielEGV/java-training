package co.com.s4n.training.java.vavr;

import io.vavr.CheckedFunction1;
import io.vavr.CheckedFunction2;
import io.vavr.Function1;
import io.vavr.control.Try;
//import org.junit.Test;
import static io.vavr.API.*;
import static io.vavr.Predicates.*;
import static io.vavr.Patterns.*;
//import static junit.framework.TestCase.assertEquals;
import io.vavr.PartialFunction;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import java.util.List;
import java.util.function.Consumer;
import static io.vavr.control.Try.failure;
import static org.junit.jupiter.api.Assertions.*;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotEquals;
//import static org.junit.Assert.assertTrue;

@RunWith(JUnitPlatform.class)
public class TrySuite {

    /**
     *En este test se validara cuando un Try por medio de la ejecucion de una funcion
     * devuelve Success en caso de exito o failure con la encapsulacion del error
     */
    @Test
    public void testTrytoSuccesAndFailure(){
        Try<Integer> myTrySuccess = Try.of(() -> 15 / 5 );
        Try<Integer> myTryFailure = Try.of(() -> 15 / 0 );

        assertEquals(
                Success(3),
                myTrySuccess);


        assertNotEquals(3, myTrySuccess);

        assertTrue(
                myTryFailure.isFailure());

    }

    private String patternMyTry(Try<Integer> myTry) {
        return Match(myTry).of(
                Case($Success($()),"Este Try es exitoso"),
                Case($Failure($()),"Este Try es fallido"));
    }

    /**
     * Validar pattern Matching a un Try validado entre Succes y failure.
     */
    @Test
    public void testTryToPatternMatching() {

        Try<Integer> myTrySuccess = Try.of(() -> 15 / 5 );
        Try<Integer> myTryFailure = Try.of(() -> 15 / 0 );

        assertEquals(
                "Este Try es exitoso",
                patternMyTry(myTrySuccess));

        assertEquals(
                "Este Try es fallido",
                patternMyTry(myTryFailure));
    }

    private Try<Integer> recoverMyTry(Integer a, Integer b) {
        return Try.of(() -> a / b).recover(x -> Match(x).of(
                Case($(instanceOf(Exception.class)), -1)));
    }

    /**
     * Validar el uso de recover para retornar un Integer por defecto en caso de error o el valor transformado acorde a la funcion
     */
    @Test
    public void testTryToRecover() {

        Try<Integer> myRecoverSuccess = recoverMyTry(15, 5);
        Try<Integer> myRecoverFailure = recoverMyTry(15, 0);

        assertEquals(
                Success(3),
                myRecoverSuccess);

        assertEquals(
                Success(-1),
                myRecoverFailure);
    }



    /**
     * La funcionalidad AndThen usa el parametro de salida de la anterior funcion cómo
     * parametro de entrada de la siguiente función.
     */
    @Test
    public void testSuccessAndThen() {
        Try<Integer> actual = Try.of(() -> new ArrayList<Integer>())
                .andThen(arr -> arr.add(10))
                .andThen(arr -> arr.add(30))
                .andThen(arr -> arr.add(20))
                .map(arr -> arr.get(1));


        assertEquals(
                Try.success(30).toString(),
                actual.toString());
    }


    /**
     * La funcionalidad transform permite aplicar una modificación
     * sobre la salida de la función.
     */
    @Test
    public void testSuccessTransform() {
        Try<Integer> number = Try.of(() -> 5);
        String transform = number.transform(self -> self.get() + " example of text");

        assertEquals(
                "5 example of text",
                transform);
    }


    public boolean foo(Try<Integer> t) {
        return false;
    }

    @Test
    public void testSuccessTransform2() {
        Try<Integer> number = Try.of(() -> 5);
        Try<Integer> transform = number.transform(self -> self);

        Boolean transform1 = number.transform(self -> foo(self));

        assertEquals(
                Success(5),
                transform);

        assertFalse(transform1);
    }


    @Test
    public void testingMap() {
        Try<Integer> tamano = Try.of(() -> "Daniel")
                .map(String::length);

        assertEquals(Success(6), tamano);
    }


    /**
     * La funcionalidad transform va a generar error sobre un try con error.
     */
    @Test
    public void testFailTransformWhen() {

        Try<Integer> error = Try.of(() -> {throw new Error("Error 1"); });
        assertThrows(Error.class, () -> { error.transform(self -> self.get() + " example of text");});
    }

    /**
     * flatMap permite mappear un try y anidar varios mapeos sin crear multiples Try encadenados
     * , es decir, busca evitar la creacion de variables tipo Try[Try[Ty[...]]] al encadenar varios mapeos sobre success
     */
    @Test
    public void testFlatMapOnSuccess() {
        CheckedFunction2<Integer, Integer, Integer> divide = (dividend, divisor) -> dividend / divisor;

        Try<Integer> result = Try.of(() -> divide.apply(3, 1));

        Function1<Try<Integer>, Try<Integer>> mapper = try_var -> try_var
                .flatMap(i -> Try.of(() -> i * 10))
                .flatMap(i_10 -> Try.of(() -> i_10 * 10));

        Try<Integer> success_example = mapper.apply(result);

        assertEquals(
                Try.of(() -> 300),
                success_example);
    }

    /**
     * flatMap permite encadenar Try aunque alguno de ellos falle
     */
    @Test
    public void testFlatMap() {
        CheckedFunction2<Integer, Integer, Integer> divide = (dividend, divisor) -> dividend / divisor;
        Function1<Try<Integer>,Try<Integer>> mapper = try_var -> try_var.flatMap(i ->Try.of(() -> i * 10))
                .flatMap(i_10 -> Try.of(() -> i_10 * 10));
        Try<Integer> exception = Try.of(() -> divide.apply(3,0));
        Try<Integer> fail_example = mapper.apply(exception);
        assertEquals(
                failure(new ArithmeticException("/ by zero")).toString(),
                fail_example.toString());
    }

    /**
     * Un try se puede encadenar con funciones que lancen excepciones con AndThenTry
     */
    @Test
    public void testAndThenTry() {
        CheckedFunction2<Integer, Integer, Integer> divide = (a, b) -> a / b;
        CheckedFunction2<Integer, Integer, Integer> multiply = (a, b) -> a * b;
        Try<Integer> tryToDivide = Try.of(() -> divide.apply(70, 2));
        Try<Integer> tryToMultiply = tryToDivide.andThenTry(i -> multiply.apply(i, 2));
        assertTrue( tryToMultiply.isSuccess());
        tryToDivide = Try.of(() -> divide.apply(70, 0));
        tryToMultiply = tryToDivide.andThenTry(i -> multiply.apply(i, 2));
        assertTrue( tryToMultiply.isFailure());
    }

    /**
     * En caso de que se use andThen, se debe manejar la excepción con try-catch
     * AndThen no maneja checked exceptions
     */
    @Test
    public void testAndThenWithChecked() {
        CheckedFunction2<Integer, Integer, Integer> divide = (a, b) -> a / b;
        CheckedFunction2<Integer, Integer, Integer> multiply = (a, b) -> a * b;
        Try<Integer> tryToDivide = Try.of(() -> divide.apply(70, 2));
        Try<Integer> tryToMultiply = tryToDivide.andThen(i -> {
            try {
                multiply.apply(i, 2);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        assertTrue( tryToMultiply.isSuccess());
    }

    /**
     * Collect permite aplicar una funcion parcial a un Try
     */
    @Test
    public void testCollect(){
        PartialFunction<Double, Double> square_root =  new PartialFunction<Double, Double>() {
            @Override
            public Double apply(Double i) {
                return Math.sqrt(i);
            }

            @Override
            public boolean isDefinedAt(Double i) {
                return i >= 0;
            }
        };
        Try<Double> valid = Try.of(() -> 25.0);
        assertEquals(
                Try.of(() -> 5.0),
                valid.collect(square_root));

        Try<Double> invalid = Try.of(() -> -25.0);
        assertTrue(
                invalid.collect(square_root).isFailure());
    }

    /**
     * withResource es el metodo seguro para crear un try en base a una instancia de una clase que implemente la interfaz Autocloseable
     */
    @Test
    public void testTryWithResources(){

        Stream<Integer> stream = Stream.of(1,2,3,4,5,6,7,8,9,10);
        Try<String> try_stream = Try.withResources(() -> stream).of(s -> s.toString());
        assertTrue(try_stream.isSuccess());
        assertThrows(IllegalStateException.class, () -> {stream.count();});
    }

    /**
     * La funcionalidad peek permite realizar una acción dependiendo de
     * una condición.
     */
    @Test
    public void testErrorPeek() {
        final List<String> tmp = new ArrayList<>();
        Consumer<Object> addIfTrue = element -> {
            if (element.toString().contains("add")) {
                tmp.add("element");
            }
        };
        Try.of(() -> {throw new Error("Error 1");})
                .peek(addIfTrue);
        assertEquals(
                true,
                tmp.isEmpty());
    }

    /**
     * La funcionalidad peek permite realizar una acción dependiendo de
     * una condición.
     */
    @Test
    public void testSuccessPeek() {
        final List<String> tmp = new ArrayList<>();
        Consumer<Object> addIfTrue = element -> {
            if (element.toString().contains("add")) {
                tmp.add("element");
            }
        };
        Try.of(() -> "add")
                .peek(addIfTrue);
        assertEquals(
                "element",
                tmp.get(0));
    }

    /**
     * Validar el uso de Map para transformar un Try de String en otro String con mas informacion
     */

    @Test
    public void testMapToTrySuccess() {
        Try<String> myRecoverSuccess =  Try.of(()-> ", Cool" ).map(x -> "This Try is good" + x);
        assertEquals(Success("This Try is good, Cool"), myRecoverSuccess);
    }

    /**
     * Validar el uso de Map para transformar un Try de String en otro String con mas informacion
     */

    @Test
    public void testMapToTryFailure() {
        Try<Integer> myRecoverSuccessOne =  Try.of(()-> 3 ).map(x -> x/0);
        assertTrue(myRecoverSuccessOne.isFailure());
    }

    /**
     * Filtrar un Try de tipo entero con filter y filterTry devolviendo Success si es multiplo de 3 o Failure si No lo es
     */
    @Test
    public void testFilterToTry() {
        Try<Integer> myFilterSuccess =  Try.of(()-> 12 ).filter(x -> x%3==0);
        Try<Integer> myFilterFailure =  Try.of(()-> 12 ).filter(x -> x%3/0==0);
        assertTrue(myFilterFailure.isFailure());
        assertEquals(Success(12), myFilterSuccess);
    }

    /**
     * Filtrar un Try de tipo entero con filter y filterTry devolviendo Success si es multiplo de 3 o Failure si No lo es
     */
    @Test
    public void testFilterTryToTry() {
        CheckedFunction2<Integer,Integer,Integer> my = ((a,b) -> a /b);
        Try<Integer> myFilterTrySuccess =  Try.of(()-> 15 ).filterTry(x -> (x + my.apply(6,2))%3==0);
        Try<Integer> myFilterTryFailure =  Try.of(()-> 15 ).filterTry(x -> (x + my.apply(6,0))%3==0);
        assertTrue(myFilterTryFailure.isFailure());
        assertEquals(Success(15), myFilterTrySuccess);
    }




    /**
     *  El recover with debe retornar un Try de el error que recupere.
     */
    @Test
    public void testTryAndRecoverWith() {
        Try<Integer> aTry = Try.of(() -> 2/0).recoverWith(ArithmeticException.class,Try.of(() ->  2));
        Try<Integer> aTry2 = Try.of(() -> 2/0).recoverWith(ArithmeticException.class,Try.of(() ->  2/0));
        assertEquals(Try.of(() -> 2), aTry);
        assertEquals(Try.failure(new ArithmeticException("/ by zero")).toString() ,
                aTry2.toString());
    }

    @Test
    public void testTryAndRecover_1() {
        //Try.of(() -> 2/0).recover(Exception.class, Try.of(() -> 2));
    }

    /**
     *  El Recover retorna el valor a recuperar, pero sin Try, permitiendo que lance un Exception
     *  si, falla
     */
    @Test
    public void testTryAndRecover() {
        assertThrows(ArithmeticException.class, () -> {Try<Integer> aTry = Try.of(() -> 2 / 0).recover(ArithmeticException.class, 2/0);});

    }
    /**
     *  Uso de MapTry
     */
    @Test
    public void testTryWithMapTry() {
        CheckedFunction1<Integer,Integer> checkedFunction1 = (CheckedFunction1<Integer, Integer>) s -> {
            Integer result = 0;
            try {
                result = s/0;
            } catch (ArithmeticException e) {
                result = 1;
                //throw e;
            }
            return result;
        };
        Try<Integer> aTry = Try.of(() -> 2).mapTry(checkedFunction1);
        assertEquals(Success(1),aTry);
    }

    private Try<Integer> sumar(Integer a, Integer b) {
        return Try.of(() -> a + b);
    }

    private Try<Integer> divide(Integer a, Integer b) {
        return Try.of(() -> a / b);
    }

    private Try<Integer> divideWithRecoder(Integer a, Integer b) {
        return Try.of(() -> a / b).recoverWith(Exception.class, Try.of(() -> 0));
    }

    @Test
    public void testMonadicCompuestoConFlatMap() {
        Try<Integer> resultado = sumar(1, 2)
                .flatMap(r0 -> sumar(r0, r0)
                        .flatMap(r1 -> sumar(r1, -6)
                                .flatMap(r2 -> divide(r2, r2))));

        assertTrue(resultado.isFailure());
    }

    @Test
    public void testMonadicCompuestoConFor() {
        Try<Integer> resultado =
                For(sumar(1, 2), r0 ->
                For(sumar(r0, r0), r1 ->
                For(sumar(r1, -6), r2 ->
                divide(r2, r2)))).toTry();

        assertTrue(resultado.isFailure());
    }

    @Test
    public void testMonadicCompuestoConFlatMapRecover() {
        Try<Integer> resultado = sumar(1, 2)
                .flatMap(r0 -> sumar(r0, r0)
                        .flatMap(r1 -> sumar(r1, -6)
                                .flatMap(r2 -> divide(r2, r2).recover(Exception.class, 0))));

        assertEquals(Success(0), resultado);
    }

    @Test
    public void testMonadicCompuestoConForRecover() {
        Try<Integer> resultado =
                For(sumar(1, 2), r0 ->
                        For(sumar(r0, r0), r1 ->
                                For(sumar(r1, -6), r2 ->
                                        divide(r2, r2).recover(Exception.class, 0)))).toTry();

        assertEquals(Success(0), resultado);
    }

    @Test
    public void testMonadicCompuestoConForRecover_1() {
        Try<Integer> resultado =
                For(sumar(1, 2), r0 ->
                        For(sumar(r0, r0), r1 ->
                                For(sumar(r1, -6), r2 ->
                                        divide(r2, r2).recover(Exception.class, e-> 0)))).toTry();

        assertEquals(Success(0), resultado);
    }

    @Test
    public void testMonadicCompuestoConFlatMapRecover_1() {
        Try<Integer> resultado = sumar(1, 2)
                .flatMap(r0 -> sumar(r0, r0)
                        .flatMap(r1 -> sumar(r1, -6)
                                .flatMap(r2 -> divideWithRecoder(r2, r2))));

        assertEquals(Success(0), resultado);
    }

    @Test
    public void testMonadicCompuestoConForRecover_2() {
        Try<Integer> resultado =
                For(sumar(1, 2), r0 ->
                        For(sumar(r0, r0), r1 ->
                                For(sumar(r1, -6), r2 ->
                                        divideWithRecoder(r2, r2)))).toTry();

        assertEquals(Success(0), resultado);
    }

    @Test
    public void testMonadicCompuestoConFlatMapRecover_2() {
        Try<Integer> resultado = sumar(1, 2)
                .flatMap(r0 -> sumar(r0, r0)
                        .flatMap(r1 -> sumar(r1, -6)
                                .flatMap(r2 -> divide(r2, r2).recoverWith(Exception.class, Try.of(() -> 0)))));

        assertEquals(Success(0), resultado);
    }

    @Test
    public void testMonadicCompuestoConForRecover_3() {
        Try<Integer> resultado =
                For(sumar(1, 2), r0 ->
                        For(sumar(r0, r0), r1 ->
                                For(sumar(r1, -6), r2 ->
                                        divide(r2, r2).recoverWith(Exception.class, Try.of(() -> 0))))).toTry();

        assertEquals(Success(0), resultado);
    }

}
