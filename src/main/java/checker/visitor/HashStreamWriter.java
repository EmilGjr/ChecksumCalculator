package checker.visitor;

import checker.ChecksumCalculator;
import checker.node.DirectoryNode;
import checker.node.FileNode;
import checker.node.FileSystemNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class HashStreamWriter implements FileSystemVisitor {
    private final ChecksumCalculator calculator;
    private final Writer writer;
    private final Path basePath;

    public HashStreamWriter(ChecksumCalculator calculator, Writer writer, Path basePath) {
        this.calculator = calculator;
        this.writer = writer;
        this.basePath = basePath;
    }

    @Override
    public void visit(FileNode file) {
        Path filePath = basePath.resolve(file.getName());

        // Отваряме потока тук и го предаваме на калкулатора
        try (InputStream is = Files.newInputStream(filePath)) {
            String hash = calculator.calculate(is); // Увери се, че калкулаторът приема InputStream
            writer.write(String.format("%s *%s%n", hash, file.getName()));
        } catch (IOException e) {
            throw new RuntimeException("Грешка при обработка на файл: " + filePath, e);
        }
    }

    @Override
    public void visit(DirectoryNode dir) {
        for (FileSystemNode child : dir.getChildren()) {
            child.accept(this);
        }
    }
}