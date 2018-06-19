package co.com.s4n.training.java;

import io.vavr.control.Option;

import static io.vavr.API.None;

public class ClassEjercicioOption {

    public static Option<Double> dividir(Double numero1, Double numero2) {
        if (numero2 != 0) {
            return Option.of(numero1 / numero2);
        } else {
            return None();
        }
    }

    public static Option<Double> elevar(Double numero1, Double numero2) {
        if (numero2 < 1 && numero1 < 0) {
            return None();
        }
        return Option.of(Math.pow(numero1, numero2));
    }

    public static Option<Double> multiplicar(Double numero1, Double numero2) {
        return Option.of(numero1 * numero2);
    }

    public static Option<String> concatenar(String valor1, String valor2) {
        return Option.of(String.valueOf(valor1) + " - " + String.valueOf(valor2));
    }
}
