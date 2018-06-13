package co.com.s4n.training.java.jdk;

import static org.junit.Assert.*;

import co.com.s4n.training.java.ClaseLambda;
import org.junit.Assert;
import org.junit.Test;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;
import java.util.function.Supplier;

public class LambdaSuite {

    @FunctionalInterface
    interface InterfaceDeEjemplo{
        int metodoDeEjemplo(int x, int y);
    }

    class ClaseDeEjemplo{
        public int metodoDeEjemplo1(int z, InterfaceDeEjemplo i){
            return z + i.metodoDeEjemplo(1,2);
        }

        public int metodoDeEjemplo2(int z, BiFunction<Integer, Integer, Integer> fn){
            return z + fn.apply(1,2);
        }
    }

    @Test
    public void smokeTest() {
        assertTrue(true);
    }

    @Test
    public void usarUnaInterfaceFuncional1(){

        InterfaceDeEjemplo i = (x,y)->x+y;

        ClaseDeEjemplo instancia = new ClaseDeEjemplo();

        int resultado = instancia.metodoDeEjemplo1(1,i);

        assertTrue(resultado==4);
    }

    @Test
    public void usarUnaInterfaceFuncional1_1(){

        InterfaceDeEjemplo i = (x,y)->(x*y)-1;

        ClaseDeEjemplo instancia = new ClaseDeEjemplo();

        int resultado = instancia.metodoDeEjemplo1(1,i);
        System.out.print(resultado);

        assertTrue(resultado==2);
    }

    @Test
    public void usarUnaInterfaceFuncional2(){

        BiFunction<Integer, Integer, Integer> f = (x, y) -> new Integer(x.intValue()+y.intValue());

        ClaseDeEjemplo instancia = new ClaseDeEjemplo();

        int resultado = instancia.metodoDeEjemplo2(1,f);

        assertTrue(resultado==4);
    }

    @Test
    public void usarUnaInterfaceFuncional2_1(){

        BiFunction<Integer, Integer, Integer> f = (x, y) ->
                new Integer((x.intValue()+y.intValue())/(y.intValue()-x.intValue()));

        ClaseDeEjemplo instancia = new ClaseDeEjemplo();

        int resultado = instancia.metodoDeEjemplo2(1,f);

        assertTrue(resultado==4);
    }

    class ClaseDeEjemplo2{

        public int metodoDeEjemplo2(int x, int y, IntBinaryOperator fn){
            return fn.applyAsInt(x,y);
        }
    }
    @Test
    public void usarUnaFuncionConTiposPrimitivos(){
        IntBinaryOperator f = (x, y) -> x + y;

        ClaseDeEjemplo2 instancia = new ClaseDeEjemplo2();

        int resultado = instancia.metodoDeEjemplo2(1,2,f);

        assertEquals(3,resultado);
    }

    @Test
    public void usarUnaFuncionConTiposPrimitivos_1(){
        IntBinaryOperator f = (x, y) -> x + y;

        ClaseDeEjemplo2 instancia = new ClaseDeEjemplo2();

        double n = 1.0;

        //int resultado = instancia.metodoDeEjemplo2(n,2,f);

        int resultado = instancia.metodoDeEjemplo2(1, 2, f);

        assertEquals(3,resultado);
    }

    class ClaseDeEjemplo3{

        public String operarConSupplier(Supplier<Integer> s){
            return "El int que me han entregado es: " + s.get();
        }
    }

    @Test
    public void usarUnaFuncionConSupplier(){
        Supplier s1 = () -> {
            System.out.println("Cuándo se evalúa esto? (1)");
            return 4;
        };

        Supplier s2 = () -> {
            System.out.println("Cuándo se evalúa esto? (2)");
            return 4;
        };

        ClaseDeEjemplo3 instancia = new ClaseDeEjemplo3();

        String resultado = instancia.operarConSupplier(s2);

        assertEquals("El int que me han entregado es: 4",resultado);
    }

    class ClaseDeEjemplo4{

        private int i = 0;

        public void operarConConsumer(Consumer<Integer> c){
            c.accept(i);
        }
    }

    @Test
    public void usarUnaFuncionConConsumer(){
        Consumer<Integer> c1 = x -> {
            System.out.println("Me han entregado este valor: "+x);
        };

        ClaseDeEjemplo4 instancia = new ClaseDeEjemplo4();

        instancia.operarConConsumer(c1);


    }

    class ClaseDeEjemplo5{
        public void operarConConsumer(int i, Consumer<Integer> c){
            c.accept(i);
        }
    }

    @Test
    public void usarUnaFuncionConConsumer_1(){
        Consumer<Integer> c1 = x -> {
            System.out.println("Me han entregado este valor: "+x);
        };

        ClaseDeEjemplo5 instancia = new ClaseDeEjemplo5();

        instancia.operarConConsumer(3, c1);


    }

    @FunctionalInterface
    interface Ejercicio1_B {
        void funLambda(Supplier<Integer> a, Supplier<Integer> b, Supplier<Integer> c, int d, Consumer<Integer> c1);
    }

    @FunctionalInterface
    interface Ejercicio1{
        Consumer<Integer> funcLambda(Supplier<Integer> a, Supplier<Integer> b, Supplier<Integer> c);
    }


    class ClaseEjercicio1{
        public void operar(int i, Consumer<Integer> c){
            c.accept(i);
        }
    }

    @Test
    public void usarUnaFuncionConLambdaSuplierConsumer_1(){
        Ejercicio1 e = (a, b, c) ->{
            Consumer<Integer> c1 = d -> System.out.println(a.get() + b.get() + c.get() + d);
            return c1;
        };

        Supplier<Integer> s1 = () -> 3;
        Supplier<Integer> s2 = () -> 2;
        Supplier<Integer> s3 = () -> 1;

        Consumer<Integer> c1 = e.funcLambda(s1, s2, s3);
        System.out.println();
        c1.accept(1);

        /*
        Supplier<Integer> s1 = () -> 3;
        Supplier<Integer> s2 = () -> 2;
        Supplier<Integer> s3 = () -> 1;

        Consumer<Integer> c2 = d -> {
            Integer s = s1.get() + s2.get() + s3.get() + d;
            System.out.println("1. Valor ejercicio1: " + s);
        };

        ClaseEjercicio1 claseEjercicio1 = new ClaseEjercicio1();
        claseEjercicio1.operar(1, c2);

        // Otra forma.

        Ejercicio1_B e = (a, b, c, d, c1) -> {
            int s = a.get() + b.get() + c.get() + d;
            c1.accept(s);
        };

        Consumer<Integer> c1 = t -> System.out.println("2. Valor ejercicio1: " + t);

        e.funLambda(s1, s2, s3, 1, c1);
        */

    }

    @FunctionalInterface
    interface Ejercicio2Lambda {
        String caracterCompuesto(char a, char b);
    }

    class ClaseEjercicio2Lambda {
        String metodoCaracterCompuesto (char c, Ejercicio2Lambda e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(c);
            stringBuilder.append(e.caracterCompuesto('c', 'a'));
            return stringBuilder.toString();
        }
        String metodoCaracterCompuesto2(char c, BiFunction<Character, Character, String> b) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(c);
            stringBuilder.append(b.apply('c', 'a'));
            return stringBuilder.toString();
        }
    }

    @Test
    public void testEjercicio2_1() {
        Ejercicio2Lambda e = (a, b) -> {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(a);
            stringBuilder.append(b);
            return stringBuilder.toString();
        };

        ClaseEjercicio2Lambda c = new ClaseEjercicio2Lambda();
        String resultado = c.metodoCaracterCompuesto('b', e);

        Assert.assertEquals("bca", resultado);
    }

    @Test
    public void ejercicio2_2() {
        ClaseEjercicio2Lambda c = new ClaseEjercicio2Lambda();

        BiFunction<Character, Character, String> caracter = (a, b) -> {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(a);
            stringBuilder.append(b);
            return stringBuilder.toString();
        };

        String resultado1 = c.metodoCaracterCompuesto2('b', caracter);

        Assert.assertEquals("bca", resultado1);
    }

    @FunctionalInterface
    interface Ejercicio3Lambda {
        IntBinaryOperator metodoEjercicio3(Supplier<Integer> a, Supplier<Integer> b);
    }

    @Test
    public void testEjercicio3 (){
        Ejercicio3Lambda e = (a, b) -> {
            IntBinaryOperator bO = (x, y) -> x + y + a.get() + b.get();
            return bO;
        };
        Supplier<Integer> a = () -> 1;
        Supplier<Integer> b = () -> 2;

        IntBinaryOperator bOp = e.metodoEjercicio3(a, b);

        int result = bOp.applyAsInt(3, 4);

        Assert.assertEquals(10, result);
    }

    @Test
    public void testEjercicio4() {
        Consumer<String> imprimirEnMayuscula = s -> System.out.println(s.toUpperCase());
        System.out.println();
        imprimirEnMayuscula.accept("Hola");
    }

    public class ClaseLambda {
        String a;

        public Consumer<String> setA() {
            Consumer<String> c = b -> a = b;
            return c;
        }
    }

    @Test
    public void testEjercicio5() {
        ClaseLambda claseLambda = new ClaseLambda();
        Consumer<String> c = claseLambda.setA();
        c.accept("Hola");

        Assert.assertEquals("Hola", claseLambda.a);

    }



}
/*
class ClaseEjercicio4 {
    static String a;

    public static void main(String args[]) {
        Consumer<String> c = b -> a = b.toString();
        c.accept("A");

        System.out.println(a);
    }

}
*/