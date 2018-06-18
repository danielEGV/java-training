package co.com.s4n.training.java;

import io.vavr.collection.List;
import io.vavr.concurrent.Future;

import java.util.function.BiFunction;

public class ClassTestFold<A> {

    private Future<A> myFold(List<Future<A>> list, A zero, BiFunction<A, A, A> bo) {
        A[] resultado = (A[]) new Object[1];
        resultado[0] = zero;
        List<A> strings = list.flatMap(a -> {
            resultado[0] = bo.apply(resultado[0], a.get());
            return Future.of(() -> resultado[0]);
        });
        return Future.of(() -> resultado[0]);
    }
}
