package co.com.s4n.training.java.enumtest;

import co.com.s4n.training.java.ClassEnumColor;
import co.com.s4n.training.java.ClassEnumEquipo;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
//import org.junit.Test;
//import static org.junit.Assert.*;
@RunWith(JUnitPlatform.class)
public class EnumSuite {

    @Test
    public void testClassEnumColor() {
        ClassEnumColor rojo = ClassEnumColor.ROJO;
        assertEquals("ROJO", rojo.name());
    }

    @Test
    public void testClassEnumColor_1() {
        ClassEnumColor negro = ClassEnumColor.NEGRO;
        assertEquals(0, negro.ordinal());
    }

    @Test
    public void testClassEnumEquipo() {
        ClassEnumEquipo realMadrid = ClassEnumEquipo.realMadrid;
        assertEquals("CF Real Madrid", realMadrid.getName());
        assertEquals(2, realMadrid.getPosicion());
    }
}
