package co.com.s4n.training.java;

import io.vavr.collection.List;

import java.util.function.Consumer;

public class ClaseLambda {
    String a;

    public Consumer<String> setA() {
        Consumer<String> c = b -> a = b;
        return c;
    }



    public static void main(String[] args) {
        ClaseLambda claseLambda = new ClaseLambda();
        Consumer<String> c = claseLambda.setA();
        c.accept("Hola");
        System.out.println(claseLambda.a);
    }


}
