package co.com.s4n.training.java;

import io.vavr.control.Either;

import java.util.function.Consumer;

public class EitherTest<A, B> {
    public Either<A, B> myBiPeek(Either<A, B> either, Consumer<A> left, Consumer<B> rigth) {
        return (either.isLeft()) ? either.peekLeft(left) : either.peek(rigth);
    }
}
