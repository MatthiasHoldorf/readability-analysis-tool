package de.qaware.rat.executor.cmd;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qaware.rat.api.exceptions.ParserException;
import de.qaware.rat.api.models.ArgumentsModel;
import de.qaware.rat.common.ImporterUtils;

/**
 * The CommandLineParserAllArguments class parses arguments from the command
 * line with the {@code org.apache.commons.cli} library.
 * 
 * @author Matthias
 *
 */
public final class CommandLineParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandLineParser.class);

    private static Options options = configureParserOptions();

    private CommandLineParser() {
    }

    /**
     * Parses the arguments.
     * 
     * <p>
     * The configurationPath is optional.
     * 
     * <p>
     * The files to be analysed is a free argument after the optional
     * argument(s).
     * 
     * <pre>
     * usage: Rat v{project.version}
     *  -c,--configurationPath <arg>   the file path of the configuration
     *  -h,--help                      display help menu
     * </pre>
     * 
     * @param args
     *            the arguments.
     * @return an {@code ArgumentsModel} representing a container object of the
     *         parsed arguments.
     * @throws ParserException
     *             if a parse error occurs or no files were detected.
     */
    public static ArgumentsModel parse(String[] args) throws ParserException {
        if (Arrays.asList(args).contains("-h") || Arrays.asList(args).contains("--help") || args.length == 0) {
            printHelp();
            return null;
        }

        ArgumentsModel arguments = parseArguments(args);

        return arguments;
    }

    private static Options configureParserOptions() {
        Options configurationOptions = new Options();

        // Options
        Option configurationPathOption = new Option("c", "configurationPath", true,
                "the file path of the configuration");
        Option outputDirectoryOption = new Option("o", "outputDirectory", true,
                "the output directory for the document and statistic report");
        Option help = new Option("h", "help", false, "display help menu");

        // Add options
        configurationOptions.addOption(configurationPathOption);
        configurationOptions.addOption(outputDirectoryOption);
        configurationOptions.addOption(help);

        return configurationOptions;
    }

    private static ArgumentsModel parseArguments(String[] args) throws ParserException {
        CommandLine cmd;

        try {
            cmd = new DefaultParser().parse(options, args, true);

            // Configuration path
            String configurationPath = cmd.getOptionValue("configurationPath");

            if (configurationPath != null) {
                configurationPath = configurationPath.trim();

                if (!Files.isRegularFile(Paths.get(configurationPath))) {
                    LOGGER.warn("The configurationPath does not point to a regular file.");
                }
            }

            // Output directory
            String outputDirectory = cmd.getOptionValue("outputDirectory");

            if (outputDirectory != null) {
                outputDirectory = outputDirectory.trim();

                if (!Files.isDirectory(Paths.get(outputDirectory))) {
                    LOGGER.warn("The outputDirectory does not point to a regular folder.");
                }
            }

            List<String> filePaths = new ArrayList<String>();

            for (String filePath : cmd.getArgs()) {
                filePath = filePath.trim();

                if (!Files.isRegularFile(Paths.get(filePath))) {
                    LOGGER.warn(String.format("The filePath: %s does not point to a regular file.", filePath));
                } else {
                    filePaths.add(filePath);
                }
            }

            if (filePaths.size() == 0) {
                LOGGER.warn("No files were detected.");
            } else if (filePaths.size() == 1) {
                LOGGER.info(String.format("Found %s potential file for an analysis.", filePaths.size()));
            } else {
                LOGGER.info(String.format("Found %s potential files for an analysis.", filePaths.size()));
            }

            return new ArgumentsModel(configurationPath, outputDirectory, filePaths);

        } catch (ParseException e) {
            printHelp(e.getMessage());
            return null;
        } catch (InvalidPathException e) {
            LOGGER.error("The file path is invalid. " + e);
            return null;
        }
    }

    private static void printHelp() {
        new HelpFormatter().printHelp("Rat v" + ImporterUtils.getVersion(), options);
    }

    private static void printHelp(String errorMessage) {
        new HelpFormatter().printHelp("Rat v" + ImporterUtils.getVersion(), "\n", options, "\nError: " + errorMessage);
    }
}