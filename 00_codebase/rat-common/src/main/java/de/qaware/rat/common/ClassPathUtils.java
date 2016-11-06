package de.qaware.rat.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * The ClasspathUtils class allows loading of resources from class path.
 * 
 * @author Matthias
 *
 */
public final class ClassPathUtils {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private ClassPathUtils() {
    }

    /**
     * This function loads content from the class path.
     * 
     * @param location
     *            the location of the class path.
     * @return content as byte array.
     */
    public static byte[] loadAsByte(String location) {
        ClassLoader classLoader = getContextClassLoader();

        try {
            InputStream inputStream = classLoader.getResourceAsStream(location);
            if (inputStream == null) {
                throw new IllegalArgumentException("The location parameter is invalid");
            }
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to load classpath resource", e);
        }
    }

    /**
     * Load a classpath resource as a string.
     *
     * @param location
     *            classpath location
     * @return resource contents as string
     */
    public static String loadAsString(String location) {
        return loadAsString(location, DEFAULT_CHARSET);
    }

    /**
     * Load a classpath resource as a string.
     *
     * @param location
     *            classpath location
     * @param charset
     *            charset the resource is encoded with
     * @return resource contents as string
     */
    public static String loadAsString(String location, Charset charset) {
        ClassLoader classLoader = getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(location)) {
            return IOUtils.toString(inputStream, charset);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Uable to load classpath resource \"%s\"", location), e);
        }
    }

    /**
     * This function returns the absolute path of a class path resource.
     * 
     * @param resoureLocation
     *            the location of the resource.
     * @return the {@code Path} of the class pat resource.
     * @throws URISyntaxException
     *             if this URL is not formatted strictly according to to RFC2396
     *             and cannot be converted to a URI.
     */
    public static String getPath(String resoureLocation) throws URISyntaxException {
        return getContextClassLoader().getResource(resoureLocation).toURI().getPath();
    }

    /**
     * This function retrieves all file names from a given directory in the
     * class path.
     * 
     * @param directory
     *            the location of the directory in the class path.
     * @return a list of all file names in that directory.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public static List<String> getFileNamesFromDirectory(String directory) throws IOException {
        return IOUtils.readLines(getContextClassLoader().getResourceAsStream(directory), DEFAULT_CHARSET);
    }

    private static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}