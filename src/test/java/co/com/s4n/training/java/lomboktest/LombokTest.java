package co.com.s4n.training.java.lomboktest;

import co.com.s4n.training.java.ClassPersonLombok;
import org.junit.Test;
import static org.junit.Assert.*;

public class LombokTest {
    @Test
    public void testLombok() {
        ClassPersonLombok personLombok = new ClassPersonLombok("Daniel", 23);
        assertEquals("Daniel", personLombok.getName());
    }
}
