/**
 * This class provides a simple command-line interface for user interaction.
 * It demonstrates parsing commands and handling file system output.
 */
package checker.cli;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import checker.filesystem.DirectoryNode;
import checker.filesystem.FileSystemBuilder;
import checker.filesystem.FileSystemNode;

public class CommandLineInterface {

    public static void main(String[] args) {
        // Example: args can be processed here to select a path
        Path path = Paths.get("."); // default to current folder

        FileSystemBuilder builder = new FileSystemBuilder(false);
        try {
            FileSystemNode root = builder.build(path);
            printTree(root, 0);
        } catch (IOException e) {
            System.err.println("Error building file system: " + e.getMessage());
        }
    }

    private static void printTree(FileSystemNode node, int indent) {
        // Print indentation
        for (int i = 0; i < indent; i++) {
            System.out.print("    ");
        }
        System.out.println(node.getName() + " (" + node.getSize() + " bytes)");

        // If this is a directory, recursively print its children
        if (node instanceof DirectoryNode) {
            DirectoryNode dir = (DirectoryNode) node;
            for (FileSystemNode child : dir.getChildren()) {
                printTree(child, indent + 1);
            }
        }
    }
}
