/**
 * Този клас тества функционалността на Sha256Calculator.
 * Проверява правилността на изчисляване на SHA-256 хешове.
 */
package checker;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import checker.core.Sha256Calculator;

class Sha256CalculatorTest {

    @Test
    void testCalculateAbc() {
        // Проверяваме, че SHA-256 за "abc" съвпада с еталонния
        String input = "abc";
        InputStream is = new ByteArrayInputStream(input.getBytes());
        Sha256Calculator calculator = new Sha256Calculator();

        String hash = calculator.calculate(is);

        // Еталонен SHA-256 за "abc"
        String expected = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad";

        assertEquals(expected, hash, "SHA-256 за 'abc' е изчислен неправилно");
    }

    @Test
    void testCalculateEmptyString() {
        // Проверяваме SHA-256 за празен низ
        String input = "";
        InputStream is = new ByteArrayInputStream(input.getBytes());
        Sha256Calculator calculator = new Sha256Calculator();

        String hash = calculator.calculate(is);

        String expected = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

        assertEquals(expected, hash, "SHA-256 за празен низ е изчислен неправилно");
    }

    @Test
    void testCalculateLongString() {
        // Проверка за по-дълъг вход
        String input = "The quick brown fox jumps over the lazy dog";
        InputStream is = new ByteArrayInputStream(input.getBytes());
        Sha256Calculator calculator = new Sha256Calculator();

        String hash = calculator.calculate(is);

        String expected = "d7a8fbb307d7809469ca9abcb0082e4f8d5651e46d3cdb762d02d0bf37c9e592";

        assertEquals(expected, hash, "SHA-256 за дълъг низ е изчислен неправилно");
    }
}