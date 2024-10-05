package example.demo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UtilTest {
    Util util;

    @BeforeEach
    public void setUp() {
        this.util = new Util();
    }

    @Test
    @DisplayName("When first number is less than second")
    public void comapre1() {
        int result = this.util.compare(1, 20);
        Assertions.assertEquals(-1, result);
    }

    @Test
    @DisplayName("When first number is greater than second")
    public void comapre2() {
        int result = this.util.compare(100, 20);
        Assertions.assertEquals(1, result);
    }

    @Test
    @DisplayName("When both are equal")
    public void comapre3() {
        int result = this.util.compare(20, 20);
        Assertions.assertEquals(0, result);
    }
}
