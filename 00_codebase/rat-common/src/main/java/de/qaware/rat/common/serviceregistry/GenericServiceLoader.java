package de.qaware.rat.common.serviceregistry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The GenericServiceLoader class loads service implementations provided by
 * different modules.
 * 
 * <p>
 * An implementation is exposed through a config file located in
 * META-INF/services. The file is named by the
 * fully-qualified-class-name-of-the-service-interface e.g.,
 * {@link de.qaware.rat.api.interfaces.ImporterService} the module implements.
 * 
 * <p>
 * The config file consists of single lines of the fully qualified class name of
 * the concrete implementation by the module, e.g.
 * {@link de.qaware.rat.codec.docx.Docx4jImporter}.
 * 
 * <p>
 * For further explanation of the Service Provider Pattern (SPI) see:
 * https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html
 * 
 * @author Matthias
 *
 */
public final class GenericServiceLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericServiceLoader.class);

    private GenericServiceLoader() {
    }

    /**
     * Locates a single service.
     * 
     * @param clazz
     *            the interface class which is implemented by the service
     *            provider classes.
     * @return the service class or null if no service is found.
     */
    public static <T> T locate(Class<T> clazz) {
        final List<T> services = locateAll(clazz);
        return services.isEmpty() ? (T) null : services.get(0);
    }

    /**
     * Locates multiple services.
     * 
     * @param clazz
     *            the interface class which is implemented by the service
     *            provider classes.
     * @return a list of service class or null if no service is found.
     */
    public static <T> List<T> locateAll(Class<T> clazz) {
        final Iterator<T> iterator = ServiceLoader.load(clazz, ClassLoader.getSystemClassLoader()).iterator();
        final List<T> services = new ArrayList<T>();

        while (iterator.hasNext()) {
            try {
                services.add(iterator.next());
            } catch (ServiceConfigurationError serviceError) {
                LOGGER.error(serviceError.getMessage());
            }
        }

        return services;
    }
}