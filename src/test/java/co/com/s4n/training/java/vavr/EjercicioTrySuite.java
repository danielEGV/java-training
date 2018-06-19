package co.com.s4n.training.java.vavr;

import co.com.s4n.training.java.ClassEjercicioTry;
import io.vavr.CheckedFunction1;
import io.vavr.CheckedFunction2;
import io.vavr.Function1;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Assert;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.junit.Assert.assertTrue;

@RunWith(JUnitPlatform.class)
public class EjercicioTrySuite {

    @Test
    public void ejercicioTryFlatMap() {
        Try<Double> resultado = ClassEjercicioTry.elevar(2D, 3D)
                .flatMap(a -> ClassEjercicioTry.multiplicar(a, 2D)
                .flatMap(b -> ClassEjercicioTry.dividir(b, 4D)
                .flatMap(c -> ClassEjercicioTry.multiplicar(c, a))));

        assertEquals(Success(32D), resultado);

    }

    @Test
    public void ejercicioTryFor() {
        Try<Double> resultado =
                For(ClassEjercicioTry.elevar(2D, 3D), a ->
                        For(ClassEjercicioTry.multiplicar(a, 2D), b ->
                                For(ClassEjercicioTry.dividir(b, 4D), c ->
                                        ClassEjercicioTry.multiplicar(c, a)))).toTry();
        
        assertEquals(Success(32D), resultado);
    }

    @Test
    public void ejercicioTryFlatMap_1() {
        Try<String> resultado = ClassEjercicioTry.elevar(2D, 3D)
                .flatMap(a -> ClassEjercicioTry.dividir(a, 2D)
                        .flatMap(b -> ClassEjercicioTry.multiplicar(b, 2D)
                                .flatMap(c -> ClassEjercicioTry.concatenar(String.valueOf(a), String.valueOf(b))
                                        .flatMap(d -> ClassEjercicioTry.concatenar(d, String.valueOf(c))))));
        
        assertEquals(Success("8.0 - 4.0 - 8.0"), resultado);
    }

    @Test
    public void ejercicioTryFor_1() {
        Try<String> resultado =
                For(ClassEjercicioTry.elevar(2D, 3D), a ->
                        For(ClassEjercicioTry.dividir(a, 2D), b ->
                                For(ClassEjercicioTry.multiplicar(b, 2D), c ->
                                        For(ClassEjercicioTry.concatenar(String.valueOf(a), String.valueOf(b)), d ->
                                                ClassEjercicioTry.concatenar(d, String.valueOf(c)))))).toTry();

        assertEquals(Success("8.0 - 4.0 - 8.0"), resultado);
    }

    @Test
    public void ejercicioTryFlatMap_2() {
        Try<String> resultado = ClassEjercicioTry.elevar(2D, 3D)
                .flatMap(a -> ClassEjercicioTry.multiplicar(a, 2D)
                        .flatMap(b -> ClassEjercicioTry.dividir(a, 0D)
                                .flatMap(c -> ClassEjercicioTry.concatenar(String.valueOf(a), String.valueOf(b))
                                        .flatMap(d -> ClassEjercicioTry.concatenar(d, String.valueOf(c))))));

        assertTrue(resultado.isFailure());
        assertEquals(Try.failure(new Exception("/ Exception.")).toString(), resultado.toString());
    }

    @Test
    public void ejercicioTryFor_2() {
        Try<String> resultado =
                For(ClassEjercicioTry.elevar(2D, 3D), a ->
                        For(ClassEjercicioTry.multiplicar(a, 2D), b ->
                                For(ClassEjercicioTry.dividir(a, 0D), c ->
                                        For(ClassEjercicioTry.concatenar(String.valueOf(a), String.valueOf(b)), d ->
                                                ClassEjercicioTry.concatenar(d, String.valueOf(c)))))).toTry();

        Try<Double> resultado1 =
                For(ClassEjercicioTry.elevar(2D, 3D), a ->
                        For(ClassEjercicioTry.multiplicar(a, 2D), b ->
                                ClassEjercicioTry.dividir(a, 0D))).toTry();

        assertTrue(resultado.isFailure());
        //assertEquals(Try.failure(new Exception("/ Exception.")).toString(), resultado.toString());

        assertTrue(resultado1.isFailure());
        //assertEquals(Try.failure(new Exception("/ Exception.")), resultado1.toString());
    }

    @Test
    public void ejercicioTryFlatMap_3() {
        Try<String> resultado = ClassEjercicioTry.elevar(2D, 3D)
                .flatMap(a -> ClassEjercicioTry.multiplicar(a, 2D)
                        .flatMap(b -> ClassEjercicioTry.dividir(a, 0D).recoverWith(Exception.class, Try.of(() -> 0D))
                                .flatMap(c -> ClassEjercicioTry.concatenar(String.valueOf(a), String.valueOf(b))
                                        .flatMap(d -> ClassEjercicioTry.concatenar(d, String.valueOf(c))))));

        assertEquals(Success("8.0 - 16.0 - 0.0"), resultado);
    }

    @Test
    public void ejercicioTryFor_3() {
        Try<String> resultado =
                For(ClassEjercicioTry.elevar(2D, 3D), a ->
                        For(ClassEjercicioTry.multiplicar(a, 2D), b ->
                                For(ClassEjercicioTry.dividir(a, 0D).recoverWith(Exception.class, Try.of(() -> 0D)), c ->
                                        For(ClassEjercicioTry.concatenar(String.valueOf(a), String.valueOf(b)), d ->
                                                ClassEjercicioTry.concatenar(d, String.valueOf(c)))))).toTry();

        assertEquals(Success("8.0 - 16.0 - 0.0"), resultado);
    }


    @Test
    public void ejercicioTryFlatMap_4() {
        Try<String> resultado = ClassEjercicioTry.elevar(-2D, 0.5)
                .flatMap(a -> ClassEjercicioTry.multiplicar(a, 2D)
                        .flatMap(b -> ClassEjercicioTry.dividir(b, 4D)
                                .flatMap(c -> ClassEjercicioTry.concatenar(String.valueOf(c), String.valueOf(8D))
                                )));
        
        assertTrue(resultado.isFailure());
        assertEquals(Try.failure(new Exception("^ Exception.")).toString(), resultado.toString());
    }

    @Test
    public void ejercicioOptionFor_4() {
        Try<String> resultado =
                For(ClassEjercicioTry.elevar(-2D, 0.5), a ->
                        For(ClassEjercicioTry.multiplicar(a, 2D), b ->
                                For(ClassEjercicioTry.dividir(b, 4D), c ->
                                        ClassEjercicioTry.concatenar(String.valueOf(c), String.valueOf(8D))))).toTry();
        
        assertTrue(resultado.isFailure());
        //assertEquals(Try.failure(new Exception("^ Exception.")).toString(), resultado.toString());
    }


    @Test
    public void ejercicioTryFlatMap_5() {
        Try<String> resultado = ClassEjercicioTry.elevar(-2D, 0.5).recoverWith(Exception.class, Try.of(() -> 2D))
                .flatMap(a -> ClassEjercicioTry.multiplicar(a, 2D)
                        .flatMap(b -> ClassEjercicioTry.dividir(b, 4D)
                                .flatMap(c -> ClassEjercicioTry.concatenar(String.valueOf(c), String.valueOf(8D))
                                )));

        assertEquals(Success("1.0 - 8.0"), resultado);
    }

    @Test
    public void ejercicioOptionFor_5() {
        Try<String> resultado =
                For(ClassEjercicioTry.elevar(-2D, 0.5).recoverWith(Exception.class, Try.of(() -> 2D)), a ->
                        For(ClassEjercicioTry.multiplicar(a, 2D), b ->
                                For(ClassEjercicioTry.dividir(b, 4D), c ->
                                        ClassEjercicioTry.concatenar(String.valueOf(c), String.valueOf(8D))))).toTry();

        assertEquals(Success("1.0 - 8.0"), resultado);
    }
}
