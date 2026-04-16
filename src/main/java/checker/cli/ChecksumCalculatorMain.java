/**
 * Този клас е основната входна точка на приложението за изчисляване на контролни суми.
 * Парсира аргументите от командния ред, управлява изчисляване или проверка
 * и предоставя интерактивен интерфейс за командите pause, resume и quit.
 */
package checker.cli;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;

import checker.control.ScanController;
import checker.core.ChecksumCalculator;
import checker.core.ChecksumProcessor;
import checker.filesystem.FileSystemBuilder;
import checker.filesystem.FileSystemNode;
import checker.reporting.ReportWriter;
import checker.reporting.VerificationResult;
import checker.reporting.VerificationStatus;
import checker.storage.ChecksumFileParser;
import checker.storage.ScanMemento;
import checker.storage.StateStorage;
import progress.ConsoleObserver;
import progress.ProgressReporter;

public class ChecksumCalculatorMain {

    public static void main(String[] args) {
        CommandLineOptions options;
        try {
            options = CommandLineOptions.parse(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            printUsage();
            System.exit(1);
            return;
        }

        System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new java.io.PrintStream(System.err, true, StandardCharsets.UTF_8));

        if (options.isHelp()) {
            printUsage();
            return;
        }

        // Ако пътят не е зададен, поискайте го от потребителя
        try (Scanner scanner = new Scanner(System.in)) {
            if (options.getPath().toString().equals(".")) {
                System.out.println("    CHECKSUM CALCULATOR - Checksum calculator for files");
                System.out.print("Enter path to file or folder: ");
                String path = scanner.nextLine().trim();
                if (path.isEmpty()) {
                    System.err.println(" Error: path cannot be empty");
                    System.exit(1);
                }
                // Презапишете опциите с новия път
                options = new CommandLineOptions(
                    options.getMode(),
                    java.nio.file.Paths.get(path),
                    options.getAlgorithm(),
                    options.getChecksumsFile(),
                    options.getVerificationOutput(),
                    options.getReportFormat(),
                    options.getStateFile(),
                    options.isFollowLinks(),
                    false
                );
            }

            run(options, scanner);
        } catch (IOException e) {
            System.err.println(" Error: " + e.getMessage());
            System.exit(2);
        }
    }

    private static void run(CommandLineOptions options, Scanner scanner) throws IOException {
        if (options.isPauseMode()) {
            System.out.println("Pause mode is available during a running scan.");
            System.out.println("Start a checksum operation with -m=calculate or -m=verify and use the pause command while the scan is running.");
            if (options.getStateFile() != null) {
                System.out.println("Use -s=<state_file> to save or load scan progress.");
            }
            return;
        }

        ScanMemento resumeState = null;
        if (options.isResumeMode()) {
            if (!Files.exists(options.getStateFile())) {
                throw new IOException("No saved scan state found: " + options.getStateFile());
            }
            resumeState = StateStorage.load(options.getStateFile());
            Path savedRoot = java.nio.file.Paths.get(resumeState.getRootPath());
            if (!Files.exists(savedRoot)) {
                throw new IOException("Saved root path does not exist: " + savedRoot);
            }
            if (!options.getPath().equals(savedRoot) && !options.getPath().toString().equals(".")) {
                System.out.println("Warning: using saved scan root path from state instead of provided path.");
            }
            if (!options.getAlgorithm().equalsIgnoreCase(resumeState.getAlgorithm())) {
                System.out.println("Warning: using algorithm from saved state instead of provided algorithm.");
            }
            if (options.isFollowLinks() != resumeState.isFollowLinks()) {
                System.out.println("Warning: using follow-links setting from saved state instead of provided -nfl option.");
            }
            options = new CommandLineOptions(
                options.getMode(),
                savedRoot,
                resumeState.getAlgorithm(),
                options.getChecksumsFile(),
                options.getVerificationOutput(),
                options.getReportFormat(),
                options.getStateFile(),
                resumeState.isFollowLinks(),
                options.isHelp()
            );
        }

        CommandLineOptions runtimeOptions = options;
        Path target = runtimeOptions.getPath();
        if (!Files.exists(target)) {
            throw new IOException("Target does not exist: " + target);
        }

        System.out.println(" Scanning: " + target.toAbsolutePath());
        System.out.println(" Algorithm: " + options.getAlgorithm().toUpperCase());
        System.out.println(" Starting...");
        System.out.println("Input available: pause, resume, quit (type while scan is running)");

        FileSystemBuilder builder = new FileSystemBuilder(options.isFollowLinks());
        FileSystemNode root = builder.build(target);

        ProgressReporter reporter = new ProgressReporter();
        reporter.addObserver(new ConsoleObserver());

        ScanController controller = new ScanController();
        ChecksumCalculator calculator = createCalculator(runtimeOptions.getAlgorithm());
        ChecksumProcessor processor = new ChecksumProcessor(calculator, reporter, controller);

        Thread commandThread = startCommandListener(controller, scanner, () -> {
            if (runtimeOptions.getStateFile() != null) {
                try {
                    StateStorage.save(processor.createMemento(runtimeOptions.getAlgorithm(), runtimeOptions.isFollowLinks()), runtimeOptions.getStateFile());
                    System.out.println("Scan state saved: " + runtimeOptions.getStateFile());
                } catch (IOException e) {
                    System.err.println("Failed to save scan state: " + e.getMessage());
                }
            }
        }, () -> {
            if (runtimeOptions.getStateFile() != null) {
                try {
                    StateStorage.save(processor.createMemento(runtimeOptions.getAlgorithm(), runtimeOptions.isFollowLinks()), runtimeOptions.getStateFile());
                    System.out.println("Scan state saved: " + runtimeOptions.getStateFile());
                } catch (IOException e) {
                    System.err.println("Failed to save scan state: " + e.getMessage());
                }
            }
        });

        try {
            if (options.isVerifyMode()) {
                runVerification(options, root, processor, resumeState);
            } else {
                runCalculation(options, root, processor, resumeState);
            }
        } finally {
            try {
                commandThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void runCalculation(CommandLineOptions options,
                                       FileSystemNode root,
                                       ChecksumProcessor processor,
                                       ScanMemento resumeState) throws IOException {
        Map<String, String> results = processor.process(root, resumeState);
        System.out.println();

        Path checksumFile = options.getChecksumsFile();
        if (checksumFile.getParent() != null) {
            Files.createDirectories(checksumFile.getParent());
        }

        ReportWriter.saveChecksumReport(results, checksumFile, options.getReportFormat());
        System.out.println(" Checksums saved: " + checksumFile.toAbsolutePath());
    }

    private static void runVerification(CommandLineOptions options,
                                        FileSystemNode root,
                                        ChecksumProcessor processor,
                                        ScanMemento resumeState) throws IOException {
        if (!Files.exists(options.getChecksumsFile())) {
            throw new IOException("Checksums file does not exist: " + options.getChecksumsFile());
        }
        Map<String, String> expectedChecksums = ChecksumFileParser.parse(options.getChecksumsFile());
        Map<String, String> actualChecksums = processor.process(root, resumeState);

        List<VerificationStatus> statuses = new ArrayList<>();
        TreeSet<String> allPaths = new TreeSet<>();
        allPaths.addAll(expectedChecksums.keySet());
        allPaths.addAll(actualChecksums.keySet());

        for (String path : allPaths) {
            String expected = expectedChecksums.get(path);
            String actual = actualChecksums.get(path);
            VerificationResult result;
            if (expected == null) {
                result = VerificationResult.NEW;
            } else if (actual == null) {
                result = VerificationResult.REMOVED;
            } else if (expected.equalsIgnoreCase(actual)) {
                result = VerificationResult.OK;
            } else {
                result = VerificationResult.MODIFIED;
            }
            statuses.add(new VerificationStatus(path, result, expected, actual));
        }

        if (options.getVerificationOutput() != null) {
            if (options.getVerificationOutput().getParent() != null) {
                Files.createDirectories(options.getVerificationOutput().getParent());
            }
            ReportWriter.saveVerificationReport(statuses, options.getVerificationOutput(), options.getReportFormat());
            System.out.println("Verification results saved: " + options.getVerificationOutput());
        } else {
            ReportWriter.printVerificationReport(statuses, options.getReportFormat());
        }
    }

    private static Thread startCommandListener(ScanController controller,
                                               Scanner scanner,
                                               Runnable onPause,
                                               Runnable onQuit) {
        Thread commands = new Thread(() -> {
            while (!controller.isStopRequested()) {
                if (!scanner.hasNextLine()) {
                    break;
                }
                String line = scanner.nextLine().trim().toLowerCase();
                switch (line) {
                    case "pause":
                        if (!controller.isPaused()) {
                            controller.pause();
                            System.out.println("Scan paused.");
                            if (onPause != null) {
                                onPause.run();
                            }
                        } else {
                            System.out.println("Scan is already paused.");
                        }
                        break;
                    case "resume":
                        controller.resume();
                        System.out.println("Scan resumed.");
                        break;
                    case "quit":
                    case "exit":
                        controller.requestStop();
                        System.out.println("Scan stopping.");
                        if (onQuit != null) {
                            onQuit.run();
                        }
                        return;
                    default:
                        if (!line.isBlank()) {
                            System.out.println("Commands: pause, resume, quit");
                        }
                        break;
                }
            }
        });
        commands.setDaemon(true);
        commands.start();
        return commands;
    }

    private static ChecksumCalculator createCalculator(String algorithm) {
        switch (algorithm.toLowerCase()) {
            case "md5":
                return new checker.core.Md5Calculator();
            case "sha256":
            case "sha-256":
                return new checker.core.Sha256Calculator();
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }

    private static void printUsage() {
        System.out.println("    CHECKSUM CALCULATOR - File checksum utility");
        System.out.println("USAGE:");
        System.out.println("   java -cp target/classes checker.cli.ChecksumCalculatorMain [options]\n");
        System.out.println("OPTIONS:");
        System.out.println("   -p=<path>                  Path to file or folder to scan");
        System.out.println("   -m=<mode>                  Mode: calculate, verify");
        System.out.println("   -a=<algorithm>             Algorithm: md5, sha256 (default: sha256)");
        System.out.println("   -c=<checksums_path>        Path to checksums file");
        System.out.println("   -t=<output_path>           Path to save verification results");
        System.out.println("   -f=<text/json>             Report format for saved or printed results");
        System.out.println("   -s=<state_file>            Path to save or restore scan progress");
        System.out.println("   -nfl                       Do not follow symbolic links");
        System.out.println("   --help                     Show this help screen\n");
        System.out.println("INTERACTIVE COMMANDS (during scan):");
        System.out.println("   pause                      Pause scanning");
        System.out.println("   resume                     Resume scanning");
        System.out.println("   quit / exit                Terminate the program\n");
        System.out.println("EXAMPLES:");
        System.out.println("   java -cp target/classes checker.cli.ChecksumCalculatorMain");
        System.out.println("   java -cp target/classes checker.cli.ChecksumCalculatorMain -p=/path/to/folder");
        System.out.println("   java -cp target/classes checker.cli.ChecksumCalculatorMain -m=verify -p=. -c=checksums/folder_sha256.txt");
    }
}
