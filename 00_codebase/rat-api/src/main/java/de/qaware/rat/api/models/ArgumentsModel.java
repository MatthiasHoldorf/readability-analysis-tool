package de.qaware.rat.api.models;

import java.util.List;

/**
 * The ArgumentsModel class represents an object that stores the parsed
 * arguments.
 * 
 * @author Matthias
 *
 */
public class ArgumentsModel {
    private String configurationPath = null;
    private String outputDirectory = null;
    private List<String> filePaths = null;

    /**
     * Create a new ArgumentsModel object.
     * 
     * @param configurationPath
     *            the path of the configuration file.
     * @param outputDirectory
     *            the output directory path.
     * @param filePaths
     *            the file paths of the documents that should be analysed.
     */
    public ArgumentsModel(String configurationPath, String outputDirectory, List<String> filePaths) {
        this.configurationPath = configurationPath;
        this.outputDirectory = outputDirectory;
        this.filePaths = filePaths;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public String getConfigurationPath() {
        return configurationPath;
    }

    public void setConfigurationPath(String configurationPath) {
        this.configurationPath = configurationPath;
    }

    public List<String> getFilePaths() {
        return filePaths;
    }

    public void setFilePaths(List<String> filePaths) {
        this.filePaths = filePaths;
    }
}