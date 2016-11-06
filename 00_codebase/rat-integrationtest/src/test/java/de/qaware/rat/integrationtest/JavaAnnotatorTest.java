package de.qaware.rat.integrationtest;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

import de.qaware.rat.api.enums.LinguisticEngineSteps;
import de.qaware.rat.api.exceptions.ImportException;
import de.qaware.rat.api.exceptions.PipelineException;
import de.qaware.rat.api.models.DocumentModel;
import de.qaware.rat.codec.docx.Docx4jImporter;
import de.qaware.rat.common.CollectionUtils;
import de.qaware.rat.common.ImporterUtils;
import de.qaware.rat.pipeline.de.Pipeline;
import de.qaware.rat.pipeline.de.rules.AdjectiveStyleAnnotator;
import de.qaware.rat.pipeline.de.rules.AmbiguousAdjectivesAndAdverbsAnnotator;
import de.qaware.rat.pipeline.de.rules.ConsecutiveFillersAnnotator;
import de.qaware.rat.pipeline.de.rules.ConsecutivePrepositionsAnnotator;
import de.qaware.rat.pipeline.de.rules.DoubleNegativeAnnotator;
import de.qaware.rat.pipeline.de.rules.FillerAnnotator;
import de.qaware.rat.pipeline.de.rules.FillerSentenceAnnotator;
import de.qaware.rat.pipeline.de.rules.IndirectSpeechAnnotator;
import de.qaware.rat.pipeline.de.rules.LeadingAttributesAnnotator;
import de.qaware.rat.pipeline.de.rules.LongSentenceAnnotator;
import de.qaware.rat.pipeline.de.rules.LongWordAnnotator;
import de.qaware.rat.pipeline.de.rules.ModalVerbAnnotator;
import de.qaware.rat.pipeline.de.rules.ModalVerbSentenceAnnotator;
import de.qaware.rat.pipeline.de.rules.NestedSentenceAnnotator;
import de.qaware.rat.pipeline.de.rules.NestedSentenceConjunctionAnnotator;
import de.qaware.rat.pipeline.de.rules.NestedSentenceDelimiterAnnotator;
import de.qaware.rat.pipeline.de.rules.NominalStyleAnnotator;
import de.qaware.rat.pipeline.de.rules.PassiveVoiceAnnotator;
import de.qaware.rat.pipeline.de.rules.SentencesStartWithSameWordAnnotator;
import de.qaware.rat.pipeline.de.rules.SubjectiveLanguageAnnotator;
import de.qaware.rat.pipeline.de.rules.SuperlativeAnnotator;
import de.qaware.rat.pipeline.de.rules.UnnecessarySyllablesAnnotator;
import de.qaware.rat.type.RatReadabilityAnomaly;

public class JavaAnnotatorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaAnnotatorTest.class);

    @Test
    public void testNominalStyleDetection() throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils
                .readFile("src/test/resources/documents/java-annotator/NominalStyle.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = createEngineDescription(NominalStyleAnnotator.class);
        ;
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(Iterables.get(annotations, 0).toString());

        assertEquals(1, annotations.size());
    }

    @Test
    public void testLeadingAttributesDetection() throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils
                .readFile("src/test/resources/documents/java-annotator/LeadingAttributes.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = createEngineDescription(LeadingAttributesAnnotator.class);
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(annotations.toString());

        assertEquals(1, annotations.size());
    }

    @Test
    public void testLongSentenceDetection() throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils
                .readFile("src/test/resources/documents/java-annotator/LongSentence.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = createEngineDescription(LongSentenceAnnotator.class);
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(annotations.toString());

        assertEquals(2, annotations.size());
    }

    @Test
    public void testAdjectiveStyleDetection() throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils
                .readFile("src/test/resources/documents/java-annotator/AdjectiveStyle.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = createEngineDescription(AdjectiveStyleAnnotator.class);
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(annotations.toString());

        assertEquals(1, annotations.size());
    }

    @Test
    public void testNestedSentenceConjunctionDetection()
            throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils
                .readFile("src/test/resources/documents/java-annotator/NestedSentence.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = createEngineDescription(NestedSentenceConjunctionAnnotator.class);
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(annotations.toString());

        assertEquals(1, annotations.size());
    }

    @Test
    public void testNestedSentenceDelimiterDetection()
            throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils
                .readFile("src/test/resources/documents/java-annotator/NestedSentence.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = createEngineDescription(NestedSentenceDelimiterAnnotator.class);
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(annotations.toString());

        assertEquals(1, annotations.size());
    }

    @Test
    public void testNestedSentenceDetection() throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils
                .readFile("src/test/resources/documents/java-annotator/NestedSentence.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = createEngineDescription(NestedSentenceAnnotator.class);
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(annotations.toString());

        assertEquals(1, annotations.size());
    }

    @Test
    public void testConsecutiveFillersDetection()
            throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils
                .readFile("src/test/resources/documents/java-annotator/Filler.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = createEngineDescription(ConsecutiveFillersAnnotator.class);
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(annotations.toString());

        assertEquals(1, annotations.size());
    }

    @Test
    public void testConsecutivePrepositionsDetection()
            throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils.readFile(
                "src/test/resources/documents/java-annotator/ConsecutivePrepositions.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = createEngineDescription(ConsecutivePrepositionsAnnotator.class);
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(annotations.toString());

        assertEquals(1, annotations.size());
    }

    @Test
    public void testModalVerbSentenceDetection() throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils
                .readFile("src/test/resources/documents/java-annotator/ModalVerb.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = createEngineDescription(ModalVerbSentenceAnnotator.class);
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(annotations.toString());

        assertEquals(1, annotations.size());
    }

    @Test
    public void testFillerDetection() throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils
                .readFile("src/test/resources/documents/java-annotator/Filler.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = createEngineDescription(FillerAnnotator.class);
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(annotations.toString());

        assertEquals(3, annotations.size());
    }

    @Test
    public void testModalVerbDetection() throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils
                .readFile("src/test/resources/documents/java-annotator/ModalVerb.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = createEngineDescription(ModalVerbAnnotator.class);
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(annotations.toString());

        assertEquals(2, annotations.size());
    }

    @Test
    public void testSentencesStartWithSameWordDetection()
            throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils.readFile(
                "src/test/resources/documents/java-annotator/SentencesStartWithSameWords.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = createEngineDescription(SentencesStartWithSameWordAnnotator.class);
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(annotations.toString());

        assertEquals(3, annotations.size());
    }

    @Test
    public void testPassiveVoiceDetection() throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils
                .readFile("src/test/resources/documents/java-annotator/PassiveVoice.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = createEngineDescription(PassiveVoiceAnnotator.class);
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(annotations.toString());

        assertEquals(1, annotations.size());
    }

    @Test
    public void testSubjectiveLanguage() throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils
                .readFile("src/test/resources/documents/java-annotator/SubjectiveLanguage.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = createEngineDescription(SubjectiveLanguageAnnotator.class);
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(annotations.toString());

        assertEquals(2, annotations.size());
    }
    
    @Test
    public void testSuperlativeAnnotator() throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils
                .readFile("src/test/resources/documents/java-annotator/Superlatives.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = createEngineDescription(SuperlativeAnnotator.class);
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(annotations.toString());

        assertEquals(1, annotations.size());
    }

    @Test
    public void testDoubleNegativeDetection() throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils
                .readFile("src/test/resources/documents/java-annotator/DoubleNegative.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = createEngineDescription(DoubleNegativeAnnotator.class);
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(annotations.toString());

        assertEquals(1, annotations.size());
    }

    @Test
    public void testIndirectSpeechDetection() throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils
                .readFile("src/test/resources/documents/java-annotator/IndirectSpeech.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = createEngineDescription(IndirectSpeechAnnotator.class);
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(annotations.toString());

        assertEquals(2, annotations.size());
    }

    @Test
    public void testUnnecessarySyllableDetection()
            throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils.readFile(
                "src/test/resources/documents/java-annotator/UnnecessarySyllableWords.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = createEngineDescription(UnnecessarySyllablesAnnotator.class);
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(annotations.toString());

        assertEquals(2, annotations.size());
    }

    @Test
    public void testLongWordDetection() throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils
                .readFile("src/test/resources/documents/java-annotator/LongWord.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.TOKENIZING);
        AnalysisEngineDescription ruleEngine = createEngineDescription(LongWordAnnotator.class);
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(annotations.toString());

        assertEquals(1, annotations.size());
    }

    @Test
    public void testAmbiguousAdjectivesAndAdverbsDetection()
            throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils.readFile(
                "src/test/resources/documents/java-annotator/AmbiguousAdjectivesAndAdverbs.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.TOKENIZING);
        AnalysisEngineDescription ruleEngine = createEngineDescription(AmbiguousAdjectivesAndAdverbsAnnotator.class);
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(annotations.toString());

        assertEquals(2, annotations.size());
    }

    @Test
    public void testFillerSentenceDetection() throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils
                .readFile("src/test/resources/documents/java-annotator/Filler.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.TOKENIZING);
        AnalysisEngineDescription ruleEngine = createEngineDescription(FillerSentenceAnnotator.class);
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(annotations.toString());

        assertEquals(1, annotations.size());
    }

    @Test
    public void testIfWordListsAreDistinc() throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        List<String> wordLists = new ArrayList<String>();
        wordLists.addAll(Arrays.asList(ImporterUtils.readWordlist("word-lists/AmbiguousAdjectivesAndAdverbs.txt")));
        wordLists.addAll(Arrays.asList(ImporterUtils.readWordlist("word-lists/Fillers.txt")));
        wordLists.addAll(Arrays.asList(ImporterUtils.readWordlist("word-lists/IndirectSpeech.txt")));
        wordLists.addAll(Arrays.asList(ImporterUtils.readWordlist("word-lists/ModalVerbs.txt")));
        wordLists.addAll(Arrays.asList(ImporterUtils.readWordlist("word-lists/Negation.txt")));
        wordLists.addAll(Arrays.asList(ImporterUtils.readWordlist("word-lists/NominalStyle.txt")));
        wordLists.addAll(Arrays.asList(ImporterUtils.readWordlist("word-lists/PassiveVoice.txt")));
        wordLists.addAll(Arrays.asList(ImporterUtils.readWordlist("word-lists/Prepositions.txt")));
        wordLists.addAll(Arrays.asList(ImporterUtils.readWordlist("word-lists/SubjectiveLanguage.txt")));
        wordLists.addAll(Arrays.asList(ImporterUtils.readWordlist("word-lists/Superlatives.txt")));
        wordLists.addAll(Arrays.asList(ImporterUtils.readWordlist("word-lists/UnnecessarySyllableWords.txt")));
        
        // Assert
        Set<String> duplicates = CollectionUtils.findDuplicates(wordLists);

        LOGGER.info(duplicates.toString());

        assertEquals(12, duplicates.size());
    }
}