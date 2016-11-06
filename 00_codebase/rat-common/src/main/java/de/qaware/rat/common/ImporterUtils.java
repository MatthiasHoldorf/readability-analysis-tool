package de.qaware.rat.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ImporterUtils class provides utility functions which are used across
 * multiple concrete importer classes, regardless of the underlying file format
 * the importer class is written for.
 * 
 * @author Matthias
 *
 */
public final class ImporterUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImporterUtils.class);

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private ImporterUtils() {
    }

    /**
     * This functions retrieves a file from its file path and returns its
     * content as a byte array.
     * 
     * @param filePath
     *            a file path to a file to read.
     * @return a byte array of the file's content.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public static byte[] readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        InputStream inputStream = Files.newInputStream(path);
        return IOUtils.toByteArray(inputStream);
    }

    /**
     * This functions retrieves a file from its file path and returns its
     * content as a String.
     * 
     * @param filePath
     *            a file path to a file to read.
     * @return a String of the file's content.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public static String readFileAsString(String filePath) throws IOException {
        return readFileAsString(filePath, DEFAULT_CHARSET);
    }

    /**
     * This function reads a file as string and returns an entry for each new
     * line.
     * 
     * @param filePath
     *            the file path to read the file from.
     * @return a String array {@code String[]} containing a string for each new
     *         line.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public static String[] readWordlist(String filePath) throws IOException {
        String wordList = ClassPathUtils.loadAsString(filePath);
        return wordList.split("\\r\\n|\\n|\\r");
    }

    /**
     * This functions retrieves a file from its file path and returns its
     * content as a String.
     * 
     * @param filePath
     *            a file path to a file to read.
     * @param charset
     *            the charset the file is encoded with.
     * @return a String of the file's content.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public static String readFileAsString(String filePath, Charset charset) throws IOException {
        Path path = Paths.get(filePath);
        InputStream inputStream = Files.newInputStream(path);
        return IOUtils.toString(inputStream, DEFAULT_CHARSET);
    }

    /**
     * Retrieves the current version of this maven project.
     * 
     * <p>
     * The content of "/version.properties" is the reference to the maven
     * project's version: {@code version=${project.version}}.
     * 
     * <p>
     * In order to work, filtering of resources must be enabled for the
     * {@code version.properties} file.
     *
     * @return the version of the project as String.
     */
    public static String getVersion() {
        Properties properties = new Properties();

        try {
            InputStream inputStream = ImporterUtils.class.getClass().getResourceAsStream("/version.properties");
            properties.load(inputStream);
            inputStream.close();
            return (String) properties.get("version");
        } catch (IOException e) {
            throw new IllegalArgumentException("The version could not be read from the resource stream.", e);
        }
    }

    /**
     * This functions scans a directory for files and returns a list containing
     * the file paths of all files within the given directory.
     * 
     * @param directoryPath
     *            the path of the directory.
     * @return a list of file paths of all files in the given directory.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public static List<String> getFilePathsFromDirectory(String directoryPath) throws IOException {
        List<String> locations = new ArrayList<String>();

        if (Files.isDirectory(Paths.get(directoryPath))) {
            Files.walk(Paths.get(directoryPath)).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    locations.add(filePath.toString());
                }
            });
        } else {
            LOGGER.warn(String.format("The directory path: \"%s\" does not point to an existing directory.",
                    directoryPath));
        }

        return locations;
    }

    /**
     * This function detects the file type of a file at the given location.
     * 
     * @param location
     *            the location of the file to detect the file type for.
     * @return a string indicating the file type of the file.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public static String detectFileType(String location) throws IOException {
        String sanitizedLocation = location.replaceFirst("^/(.:/)", "$1");
        Path filePath = Paths.get(sanitizedLocation);
        return Files.probeContentType(filePath);
    }

    /**
     * This function detects the file extension of a file at the given location.
     * 
     * @param location
     *            the location of the file to detect the file extension for.
     * @return a string indicating the file type of the file.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public static String detectFileExtension(String location) {
        String sanitizedLocation = location.replaceFirst("^/(.:/)", "$1");
        Path filePath = Paths.get(sanitizedLocation);
        return FilenameUtils.getExtension(filePath.toString());
    }

    /**
     * This function gets the file name of a given path (as String) to a file.
     * 
     * @param path
     *            the location of the file to detect the file name from.
     * @return the file name as a {@code String}
     */
    public static String getFileNameFromPath(String path) {
        return FilenameUtils.getBaseName(path);
    }

    /**
     * This function gets the directory name of a given path (as String) to a
     * file.
     * 
     * @param path
     *            the location of the file to detect the directory name from.
     * @return the directory name as a {@code String}
     */
    public static String getDirectoryPathFromFilePath(String path, String delimiter) {
        int index = path.lastIndexOf(delimiter);
        return path.substring(0, index + 1);
    }

    /**
     * This function gets the directory name of a given path (as String) to a
     * file.
     * 
     * @param path
     *            the location of the file to detect the directory name from.
     * @return the directory name as a {@code String}
     */
    public static String getDirectoryPathFromFilePath(String path) {
        String delimiter = "/";

        if (path.indexOf('\\') != -1) {
            delimiter = "\\";
        }

        int index = path.lastIndexOf(delimiter);
        return path.substring(0, index + 1);
    }

    /**
     * This function detects sentences in a text.
     * 
     * <p>
     * It uses a {@code Local} object for localization.
     * 
     * @param text
     *            A text to detect the sentence from.
     * @param locale
     *            A <code>Locale</code> object represents a specific
     *            geographical, political, or cultural region.
     * @return a list of Strings of detected sentences.
     */
    public static List<String> detectSentences(String text, Locale locale) {
        List<String> sentences = new ArrayList<String>();
        BreakIterator iterator = BreakIterator.getSentenceInstance(locale);

        iterator.setText(text);
        int start = iterator.first();

        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            sentences.add(text.substring(start, end));
        }

        return sentences;
    }
}