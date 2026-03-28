package checker;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import static org.junit.jupiter.api.Assertions.assertEquals;

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