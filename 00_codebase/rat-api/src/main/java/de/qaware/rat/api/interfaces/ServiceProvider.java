package de.qaware.rat.api.interfaces;

/**
 * The ServiceProvider interface has to be extended by every service interface.
 * E.g., see {@link de.qaware.rat.api.exporter.exporter.ExporterService} and
 * {@link de.qaware.rat.api.importer.importer.ImporterService}.
 * 
 * @author Matthias
 *
 */
public interface ServiceProvider {
	/**
	 * This function returns the capability of a service.
	 * 
	 * <p>
	 * By that, the {@link de.qaware.rat.common.serviceregistry.ServiceLocator} class
	 * can decide which service is made available to the application.
	 * 
	 * @return the capability of the service
	 */
	String getCapability();
}