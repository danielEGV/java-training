package co.com.s4n.training.java;

public enum  ClassEnumEquipo {
    barca("Barcelona FC", 1), realMadrid("CF Real Madrid", 2);

    private String name;
    private int posicion;

    ClassEnumEquipo(String name, int posicion) {
        this.name = name;
        this.posicion = posicion;
    }

    public String getName() {
        return name;
    }

    public int getPosicion() {
        return posicion;
    }
}
