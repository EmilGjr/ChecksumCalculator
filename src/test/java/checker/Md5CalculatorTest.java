/**
 * Този клас тества функционалността на Md5Calculator.
 * Проверява правилността на изчисляване на MD5 хешове.
 */
package checker;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import checker.core.Md5Calculator;

class Md5CalculatorTest {
    @Test
    void testCalculateAbc() {
        Md5Calculator calculator = new Md5Calculator();
        String input = "abc";
        ByteArrayInputStream is = new ByteArrayInputStream(input.getBytes());

        String result = calculator.calculate(is);

        // Очакваната стойност е взета от материалите
        assertEquals("900150983cd24fb0d6963f7d28e17f72", result, "MD5 for 'abc' is incorrect");
    }
}