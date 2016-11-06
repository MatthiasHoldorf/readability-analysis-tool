package de.qaware.rat.integrationtest;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.qaware.rat.api.exceptions.ImportException;
import de.qaware.rat.api.interfaces.ImporterService;
import de.qaware.rat.api.models.DocumentModel;
import de.qaware.rat.common.ImporterUtils;
import de.qaware.rat.common.serviceregistry.ServiceLocator;

@RunWith(Parameterized.class)
public class ImporterServiceTest {
    private ImporterService service;

    public ImporterServiceTest(ImporterService service) {
        this.service = service;
    }

    @Parameters
    public static List<ImporterService> instancesToTest() {
        return ServiceLocator.getImplementations(ImporterService.class);
    }

    @Before
    public void setUp() throws Exception {
        this.service = this.service.getClass().newInstance();
    }

    @Test
    public void testGetDocument() throws ImportException, IOException {
        // Arrange
        byte[] data = ImporterUtils.readFile("src/test/resources/documents/importer-test/small-document.docx");

        // Act
        DocumentModel documentModel = service.getDocumentModel(data);

        // Assert
        assertEquals(239, documentModel.getText().length());
    }
}