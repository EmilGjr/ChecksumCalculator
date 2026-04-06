package checker.visitor;

import checker.node.DirectoryNode;
import checker.node.FileNode;

public interface FileSystemVisitor {
    void visit(FileNode file);
    void visit(DirectoryNode dir);
}