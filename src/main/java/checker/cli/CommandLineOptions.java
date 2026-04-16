/**
 * This class processes and validates command-line options.
 * It parses arguments such as mode, path, algorithm, checksums file, and other settings.
 */
package checker.cli;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import checker.reporting.ReportFormat;

public class CommandLineOptions {
    private final String mode;
    private final Path path;
    private final String algorithm;
    private final Path checksumsFile;
    private final Path verificationOutput;
    private final ReportFormat reportFormat;
    private final Path stateFile;
    private final boolean followLinks;
    private final boolean help;

    public CommandLineOptions(String mode,
                              Path path,
                              String algorithm,
                              Path checksumsFile,
                              Path verificationOutput,
                              ReportFormat reportFormat,
                              Path stateFile,
                              boolean followLinks,
                              boolean help) {
        this.mode = mode;
        this.path = path;
        this.algorithm = algorithm;
        this.checksumsFile = checksumsFile;
        this.verificationOutput = verificationOutput;
        this.reportFormat = reportFormat;
        this.stateFile = stateFile;
        this.followLinks = followLinks;
        this.help = help;
    }

    public String getMode() {
        return mode;
    }

    public Path getPath() {
        return path;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public Path getChecksumsFile() {
        return checksumsFile;
    }

    public Path getVerificationOutput() {
        return verificationOutput;
    }

    public ReportFormat getReportFormat() {
        return reportFormat;
    }

    public Path getStateFile() {
        return stateFile;
    }

    public boolean isFollowLinks() {
        return followLinks;
    }

    public boolean isHelp() {
        return help;
    }

    public boolean isCalculateMode() {
        return "calculate".equals(mode);
    }

    public boolean isVerifyMode() {
        return "verify".equals(mode);
    }

    public boolean isPauseMode() {
        return "pause".equals(mode);
    }

    public boolean isResumeMode() {
        return "resume".equals(mode);
    }

    public static CommandLineOptions parse(String[] args) {
        String mode = "calculate";
        Path path = Paths.get(".");
        String algorithm = "sha256";
        Path checksumsFile = Paths.get("checksums.txt");
        Path verificationOutput = null;
        ReportFormat reportFormat = ReportFormat.TEXT;
        Path stateFile = Paths.get("scan.state");
        boolean followLinks = true;
        boolean help = false;

        List<String> arguments = new ArrayList<>(Arrays.asList(args));
        for (String arg : arguments) {
            if (arg.equals("--help") || arg.equals("-h")) {
                help = true;
                break;
            }
            if (arg.startsWith("-m=")) {
                mode = arg.substring("-m=".length()).toLowerCase();
            } else if (arg.startsWith("-p=")) {
                path = Paths.get(arg.substring("-p=".length()));
            } else if (arg.startsWith("-a=")) {
                algorithm = arg.substring("-a=".length()).toLowerCase();
            } else if (arg.startsWith("-c=")) {
                checksumsFile = Paths.get(arg.substring("-c=".length()));
            } else if (arg.startsWith("-t=")) {
                verificationOutput = Paths.get(arg.substring("-t=".length()));
            } else if (arg.startsWith("-f=")) {
                reportFormat = ReportFormat.fromString(arg.substring("-f=".length()));
            } else if (arg.startsWith("-s=")) {
                stateFile = Paths.get(arg.substring("-s=".length()));
            } else if (arg.equals("-nfl")) {
                followLinks = false;
            } else {
                throw new IllegalArgumentException("Unknown option: " + arg);
            }
        }

        if (!mode.equals("calculate") && !mode.equals("verify") && !mode.equals("pause") && !mode.equals("resume")) {
            throw new IllegalArgumentException("Unknown mode: " + mode);
        }

        if (!algorithm.equals("md5") && !algorithm.equals("sha256") && !algorithm.equals("sha-256")) {
            throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }

        return new CommandLineOptions(mode, path, algorithm, checksumsFile, verificationOutput, reportFormat, stateFile, followLinks, help);
    }
}
