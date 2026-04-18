/**
 * This class builds the file system tree from a given path.
 * It recursively walks directories and creates nodes for files and folders.
 */
package checker.filesystem;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class FileSystemBuilder {
    private final boolean followLinks;
    private final Set<Path> visitedPaths = new HashSet<>();

    public FileSystemBuilder(boolean followLinks) {
        this.followLinks = followLinks;
    }

    public FileSystemNode build(Path path) throws IOException {
        // Clear visited paths for a fresh build
        visitedPaths.clear();
        return buildRecursive(path.toAbsolutePath().normalize());
    }

    private FileSystemNode buildRecursive(Path path) throws IOException {

        // Symbolic link handling
        if (Files.isSymbolicLink(path) && !followLinks) {
            return new FileNode(path, Files.size(path));

        }// Cycle detection
        Path realPath = path.toRealPath();
        if (visitedPaths.contains(realPath)) {
            throw new FileSystemException("Cycle detected at: " + path + ". Aborting traversal.");
        }
        visitedPaths.add(realPath);

        
        if (Files.isDirectory(path)) {
            DirectoryNode directory = new DirectoryNode(path);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path entry : stream) {
                    directory.addChild(buildRecursive(entry));
                }
            }
            return directory;
        } else {
            return new FileNode(path, Files.size(path));
        }
    }
}
