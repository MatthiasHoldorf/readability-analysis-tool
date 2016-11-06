package de.qaware.rat.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qaware.rat.api.exceptions.ParserException;
import de.qaware.rat.api.models.ConfigurationModel;
import de.qaware.rat.api.models.QualityGateConfigModel;

public class ConfigurationUtilsTest {    
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationUtilsTest.class);
    
    @Test
    public void testParseConfiguration() throws IOException, ParserException {
        // Arrange
        String config = ImporterUtils.readFileAsString("src/test/resources/config/rat-config.xml");

        // Act
        ConfigurationModel configurationModel = ConfigurationUtils.parseConfiguration(config);

        // Assert

        // Keywords
        assertEquals(2, configurationModel.getKeywords().size());

        // Anomalies
        assertEquals(21, configurationModel.getAnomalyRuleModels().size());
        assertEquals("AdjectiveStyle", configurationModel.getAnomalyRuleModels().get(0).getName());
        assertEquals("Critical", configurationModel.getAnomalyRuleModels().get(0).getSeverity());
        assertEquals(3, configurationModel.getAnomalyRuleModels().get(0).getThreshold());
        assertEquals(true, configurationModel.getAnomalyRuleModels().get(0).isEnabled());

        // Quality gate
        Map<String, QualityGateConfigModel> qualityGateInformation = configurationModel.getQualityGateInformation();

        // Anomalies
        assertEquals(50, qualityGateInformation.get("minor").getHardWarningThreshold(), 0.0);
        assertEquals(9999, qualityGateInformation.get("minor").getHardErrorThreshold(), 0.0);
        assertEquals(1, qualityGateInformation.get("major").getHardWarningThreshold(), 0.0);
        assertEquals(30, qualityGateInformation.get("major").getHardErrorThreshold(), 0.0);
        assertEquals(1, qualityGateInformation.get("critical").getHardWarningThreshold(), 0.0);
        assertEquals(1, qualityGateInformation.get("critical").getHardErrorThreshold(), 0.0);

        // Formulas
        assertNotNull(qualityGateInformation.get("flesch-reading-ease-amstad"));
        assertNotNull(qualityGateInformation.get("wiener-sachtextformel"));

        // Statistics
        assertNotNull(qualityGateInformation.get("average-number-of-words-per-sentence"));
        assertNotNull(qualityGateInformation.get("average-number-of-syllables-per-word"));
    }

    @Test
    public void testLoadDefaultAndInternalConfiguration() throws IOException, ParserException {
        // Act
        LOGGER.info("Assert configPath");
        ConfigurationUtils.loadConfiguration("src/test/resources/config/rat-config.xml", null, null);
        LOGGER.info("Assert directoryPath");
        ConfigurationUtils.loadConfiguration(null, "src/test/resources/config/", null);
        LOGGER.info("Assert directoryPath");
        ConfigurationUtils.loadConfiguration(null, "src\\test\\resources\\config\\", null);
        LOGGER.info("Assert internal");
        ConfigurationUtils.loadConfiguration(null, "src/test/resources/config", null);
        LOGGER.info("Assert internal");
        ConfigurationUtils.loadConfiguration(null, null, null);
        LOGGER.info("Assert from defaultPath");
        ConfigurationUtils.loadConfiguration(null, null, "src/test/resources/config/rat-config.xml");
    }
    
    @Test(expected=ParserException.class)
    public void testParseConfigurationForMissingFormula() throws IOException, ParserException {
        // Arrange
        String config = ConfigurationUtils.loadConfiguration(null, null, "src/test/resources/config/missing-formula.xml");
        
        // Act
        ConfigurationUtils.parseConfiguration(config);
    }
    
    @Test(expected=ParserException.class)
    public void testParseConfigurationForCorruptFormula() throws IOException, ParserException {
        // Arrange
        String config = ConfigurationUtils.loadConfiguration(null, null, "src/test/resources/config/corrupt-formula.xml");
        
        // Act
        ConfigurationUtils.parseConfiguration(config);
    }
    
    @Test(expected=ParserException.class)
    public void testParseConfigurationForInvalidEntry() throws IOException, ParserException {
        // Arrange
        String config = ConfigurationUtils.loadConfiguration(null, null, "src/test/resources/config/invalid-entry.xml");
        
        // Act
        ConfigurationUtils.parseConfiguration(config);
    }
    
    @Test(expected=ParserException.class)
    public void testParseConfigurationForInvalidTag() throws IOException, ParserException {
        // Arrange
        String config = ConfigurationUtils.loadConfiguration(null, null, "src/test/resources/config/invalid-tag.xml");
        
        // Act
        ConfigurationUtils.parseConfiguration(config);
    }
    
    @Test(expected=ParserException.class)
    public void testParseConfigurationForInvalidTagStatistic() throws IOException, ParserException {
        // Arrange
        String config = ConfigurationUtils.loadConfiguration(null, null, "src/test/resources/config/invalid-tag-statistic.xml");
        
        // Act
        ConfigurationUtils.parseConfiguration(config);
    }
}