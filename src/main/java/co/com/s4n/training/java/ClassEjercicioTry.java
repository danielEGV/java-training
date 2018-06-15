package co.com.s4n.training.java;

import com.sun.net.httpserver.Authenticator;
import io.vavr.control.Try;

import static io.vavr.API.Failure;

public class ClassEjercicioTry {
    public static Try<Double> dividir(Double numero1, Double numero2) {
        if (numero2 != 0) {
            return Try.of(() -> numero1 / numero2);
        } else {
            return Failure(new Exception("/ Exception."));
        }
    }

    public static Try<Double> elevar(Double numero1, Double numero2) {
        if (numero1 < 0 && numero2 < 1)
        {
            return Failure(new Exception("^ Exception."));
        }
        return Try.of(() -> Math.pow(numero1, numero2));
    }

    public static Try<Double> multiplicar(Double numero1, Double numero2) {
        return Try.of(() -> numero1 * numero2);
    }

    public static Try<String> concatenar(String valor1, String valor2) {
        return Try.of(() -> String.valueOf(valor1) + " - " + String.valueOf(valor2));
        }

}
