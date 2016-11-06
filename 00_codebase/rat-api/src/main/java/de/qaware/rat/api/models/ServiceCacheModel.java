package de.qaware.rat.api.models;

/**
 * The {@code ServuceCacheModel} class represents a data structure that is used
 * to cache services in the {@code ServiceLocator} class.
 * 
 * @author Matthias
 *
 */
public class ServiceCacheModel {
    private Object serviceImpementation;
    private Object serviceInterface;

    /**
     * Creates a {@code ServiceCacheModel} object.
     * 
     * @param serviceImpementation
     *            the serviceImplementation object
     * @param serviceInterface
     *            the serviceInterface object.
     */
    public ServiceCacheModel(Object serviceImpementation, Object serviceInterface) {
        this.serviceImpementation = serviceImpementation;
        this.serviceInterface = serviceInterface;
    }

    public Object getServiceImpementation() {
        return serviceImpementation;
    }

    public void setServiceImpementation(Object serviceImpementation) {
        this.serviceImpementation = serviceImpementation;
    }

    public Object getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(Object serviceInterface) {
        this.serviceInterface = serviceInterface;
    }
}