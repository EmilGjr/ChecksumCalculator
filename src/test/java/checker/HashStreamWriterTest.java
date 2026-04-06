package checker;

import checker.node.FileNode;
import checker.visitor.HashStreamWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HashStreamWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void testVisitorOutputFormat() throws IOException {
        String fileName = "test.txt";
        Path file = tempDir.resolve(fileName);
        Files.writeString(file, "Hello Clean Code");

        // подготовка на Visitor и Calculator
        Md5Calculator md5Calc = new Md5Calculator();
        StringWriter output = new StringWriter();

        // подаваме tempDir като basePath, за да може Visitor-ът да намери файла
        HashStreamWriter visitor = new HashStreamWriter(md5Calc, output, tempDir);

        //създаване на възел и приемане на посетителя
        FileNode node = new FileNode(fileName, Files.size(file));
        node.accept(visitor);

        // проверка на резултата
        String result = output.toString();

        // коригирано извикване в теста: отваряме поток за очаквания хеш
        String expectedHash;
        try (InputStream is = Files.newInputStream(file)) {
            expectedHash = md5Calc.calculate(is);
        }

        assertTrue(result.contains(expectedHash), "Резултатът трябва да съдържа правилния MD5 хеш.");
        assertTrue(result.contains("*" + fileName), "Резултатът трябва да съдържа името на файла.");
    }
}