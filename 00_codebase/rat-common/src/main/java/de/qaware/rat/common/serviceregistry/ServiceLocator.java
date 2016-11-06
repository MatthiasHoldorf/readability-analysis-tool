package de.qaware.rat.common.serviceregistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qaware.rat.api.interfaces.ServiceProvider;

/**
 * The ServiceLocator class loads concrete implementations (service provider)
 * for a given interfaces.
 * 
 * @author Matthias
 *
 */
public final class ServiceLocator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLocator.class);
    
    private static Map<String, ServiceCacheModel> serviceCache = new HashMap<String, ServiceCacheModel>();
    
    private ServiceLocator() {
    }

    /**
     * Search for service provider classes.
     * 
     * @param clazz
     *            the interface class which is implemented by the service
     *            provider classes.
     * @param capability
     *            the capability to look for.
     * @return the first service that matches the capability or null if no
     *         service is found for the given capability.
     */
    public static <T> T getService(Class<T> clazz, String capability) {
        T serviceImplementation = cacheLookup(clazz, capability);
        
        List<T> services = (List<T>) GenericServiceLoader.locateAll(clazz);

        for (T service : services) {
            if (((ServiceProvider) service).getCapability().equals(capability)) {
                serviceImplementation = service;
                serviceCache.put(capability, new ServiceCacheModel(serviceImplementation, clazz));
            }
        }

        return serviceImplementation;
    }

    /**
     * Get all capabilities of a given service interface.
     * 
     * @param clazz
     *            the interface class which is implemented by the service
     *            provider classes.
     * @return a list of capabilities as strings.
     */
    public static <T> List<String> getCapabilities(Class<T> clazz) {
        List<String> capabilities = new ArrayList<String>();

        List<T> executorService = (List<T>) GenericServiceLoader.locateAll(clazz);

        for (T service : executorService) {
            capabilities.add(((ServiceProvider) service).getCapability());
        }

        return capabilities;
    }

    /**
     * Returns all implementations of a given service interface.
     * 
     * @param clazz
     *            the interface class which is implemented by the service
     *            provider classes.
     * @return a list of implementations of the service interface.
     */
    public static <T> List<T> getImplementations(Class<T> clazz) {
        return (List<T>) GenericServiceLoader.locateAll(clazz);
    }
    
    @SuppressWarnings("unchecked")
    private static <T> T cacheLookup(Class<T> clazz, String capability) {
        T service = null;
        
        for (Map.Entry<String, ServiceCacheModel> entry : serviceCache.entrySet()) {
            String key = entry.getKey();
            ServiceCacheModel value = entry.getValue();           
            if (key.equals(capability) && clazz.toString().equals(value.getServiceInterface().toString())) {
                LOGGER.debug("Cache lookup successfull for: " + value.getServiceImpementation().getClass().toString());
                service = (T) value;
            }
        }
        
        return service;
    }
}