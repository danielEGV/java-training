package co.com.s4n.training.java;

import lombok.Getter;

public class ClassPersonLombok {
    @Getter private String name;
    @Getter private int edad;

    public ClassPersonLombok(String name, int edad) {
        this.name = name;
        this.edad = edad;
    }
}
