package co.com.s4n.training.java.lomboktest;

import co.com.s4n.training.java.ClassPersonLombok;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
//import org.junit.Test;
//import static org.junit.Assert.*;

@RunWith(JUnitPlatform.class)
public class LombokTest {
    @Test
    public void testLombok() {
        ClassPersonLombok personLombok = new ClassPersonLombok("Daniel", 23);
        //assertEquals("Daniel", personLombok.getName());
    }
}
