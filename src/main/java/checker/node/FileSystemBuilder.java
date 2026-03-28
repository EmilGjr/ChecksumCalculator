package checker.node;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

public class FileSystemBuilder {
    private final boolean followLinks;
    private final Set<Path> visitedPaths = new HashSet<>();

    public FileSystemBuilder(boolean followLinks) {
        this.followLinks = followLinks;
    }

    public FileSystemNode build(Path path) throws IOException {
        // Изчистваме посетените пътища при нов старт на build
        visitedPaths.clear();
        return buildRecursive(path.toAbsolutePath().normalize());
    }

    private FileSystemNode buildRecursive(Path path) throws IOException {
        // Проверка за цикли (Cycle Detection)
        Path realPath = path.toRealPath();
        if (visitedPaths.contains(realPath)) {
            throw new FileSystemException("Цикъл открит при: " + path + ". Прекратяване на обхождането.");
        }
        visitedPaths.add(realPath);

        // Логика за символни връзки
        if (Files.isSymbolicLink(path) && !followLinks) {
            return new FileNode(path.getFileName().toString(), Files.size(path));
        }

        if (Files.isDirectory(path)) {
            DirectoryNode directory = new DirectoryNode(path.getFileName().toString());
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path entry : stream) {
                    directory.addChild(buildRecursive(entry));
                }
            }
            return directory;
        } else {
            return new FileNode(path.getFileName().toString(), Files.size(path));
        }
    }
}