package org.biovars.bioinformaticsportal;

import org.biovars.bioinformaticsportal.sequence.BatchSequenceSearchFormat;
import org.biovars.bioinformaticsportal.sequence.Sequence;
import org.biovars.bioinformaticsportal.sequence.SequenceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = {
        org.biovars.bioinformaticsportal.sequence.SequenceRepository.class,
        org.biovars.bioinformaticsportal.datasource.SequenceDataSourceConfiguration.class
})
@ActiveProfiles("test")
@JdbcTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class SequenceRepositoryTests {

    @Autowired
    SequenceRepository sequenceRepository;

    @DisplayName("findLikeSequence_whenQueryingExistingSampleId_shouldReturnListMatchingSampleId")
    @Test
    void findLikeSequence_whenQueryingExistingSampleId_shouldReturnListMatchingSampleId() {
        List<Sequence> seqs = sequenceRepository.findLikeSequence(
        "PK8899", "", "", ""
        );
        assertEquals(1, seqs.size(), "Expected one sequence to match the sample ID");
        assertEquals("PK8899", seqs.get(0).sampleId());
    }

    @DisplayName("fuzzyFindSequences_whenEmptyQuery_shouldReturnEmptyList")
    @Test
    void fuzzyFindSequences_whenEmptyQuery_shouldReturnEmptyList() {
        BatchSequenceSearchFormat search = new BatchSequenceSearchFormat(
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                true
        );
        var result = sequenceRepository.fuzzyFindSequences(search);
        assertEquals(0, result.size());
    }

    @DisplayName("fuzzyFindSequences_whenJunkQuery_shouldReturnEmptyList")
    @Test
    void fuzzyFindSequences_whenJunkQuery_shouldReturnEmptyList() {
        BatchSequenceSearchFormat search = new BatchSequenceSearchFormat(
                List.of("'--"),
                List.of("'--"),
                List.of("'--"),
                List.of("'--"),
                true
        );
        var result = sequenceRepository.fuzzyFindSequences(search);
        assertEquals(0, result.size());
    }

    @DisplayName("fuzzyFindSequences_whenQueryingSampleIdsBySinglePrefix_shouldReturnRunIdsMatchingPrefix")
    @Test
    void fuzzyFindSequences_whenQueryingSampleIdsBySinglePrefix_shouldReturnRunIdsMatchingPrefix() {
        BatchSequenceSearchFormat search = new BatchSequenceSearchFormat(
                List.of("DU6"),
                List.of(),
                List.of(),
                List.of(),
                true
        );
        var result = sequenceRepository.fuzzyFindSequences(search);
        assertEquals(result.size(), 2);
        var ids = result.stream().map(c -> c.sampleId()).collect(Collectors.toList());
        Collections.sort(ids);
        assertEquals(ids.get(0), "DU6074");
        assertEquals(ids.get(1), "DU6075");
    }

    @DisplayName("fuzzyFindSequences_whenQueryingSampleIdsWithMultiplePrefixes_shouldReturnSampleIdsMatchingAnyOfPrefixes")
    @Test
    void fuzzyFindSequences_whenQueryingSampleIdsWithMultiplePrefixes_shouldReturnSampleIdsMatchingAnyOfPrefixes() {
        BatchSequenceSearchFormat search = new BatchSequenceSearchFormat(
                List.of("DU6", "HH"),
                List.of(),
                List.of(),
                List.of(),
                true
        );
        var result = sequenceRepository.fuzzyFindSequences(search);
        assertEquals(result.size(), 4);
        var ids = result.stream().map(c -> c.sampleId()).collect(Collectors.toList());
        Collections.sort(ids);
        var expectedIds = List.of("DU6074", "DU6075", "HH3804", "HH3805");
        assertEquals(expectedIds, ids);
    }

    @DisplayName("fuzzyFindSequences_whenQueryingSampleIdsWithNegatingPrefixes_shouldReturnRunIdsMatchingAnyOfPrefixesExceptNegatedPrefixes")
    @Test
    void fuzzyFindSequences_whenQueryingSampleIdsWithNegatingPrefixes_shouldReturnRunIdsMatchingAnyOfPrefixesExceptNegatedPrefixes() {
        BatchSequenceSearchFormat search = new BatchSequenceSearchFormat(
                List.of("DU6", "HH", "-DU6075"),
                List.of(),
                List.of(),
                List.of(),
                true
        );
        var result = sequenceRepository.fuzzyFindSequences(search);
        assertEquals(3, result.size() );
        var ids = result.stream().map(c -> c.sampleId()).collect(Collectors.toList());
        Collections.sort(ids);
        var expectedIds = List.of("DU6074", "HH3804", "HH3805");
        assertEquals(expectedIds, ids);
    }

    @DisplayName("fuzzyFindSequences_whenQueryingRunIdsBySinglePrefix_shouldReturnRunIdsMatchingPrefix")
    @Test
    void fuzzyFindSequences_whenQueryingRunIdsByPrefix_shouldReturnRunIdsMatchingPrefix() {
        BatchSequenceSearchFormat search = new BatchSequenceSearchFormat(
                List.of(),
                List.of("V1"),
                List.of(),
                List.of(),
                true
        );
        var result = sequenceRepository.fuzzyFindSequences(search);
        assertEquals(result.size(), 2);
        var ids = result.stream().map(c -> c.runId()).collect(Collectors.toList());
        Collections.sort(ids);
        assertEquals("V10087472", ids.get(0));
        assertEquals("V10087473", ids.get(1));
    }
    @DisplayName("fuzzyFindSequences_whenQueryingRunIdsByNegatingPrefixes_shouldReturnRunIdsMatchingAnyOfPrefixesExceptNegatedPrefixes")
    @Test
    void fuzzyFindSequences_whenQueryingRunIdsByNegatingPrefixes_shouldReturnRunIdsMatchingAnyOfPrefixesExceptNegatedPrefixes() {
        BatchSequenceSearchFormat search = new BatchSequenceSearchFormat(
                List.of(),
                List.of("V1", "-V10087472"),
                List.of(),
                List.of(),
                true
        );
        var result = sequenceRepository.fuzzyFindSequences(search);
        assertEquals(result.size(), 1);
        var ids = result.stream().map(c -> c.runId()).collect(Collectors.toList());
        Collections.sort(ids);
        assertEquals("V10087473", ids.get(0));
    }
    @DisplayName("fuzzyFindSequences_whenQueryingMultipleAttributes_shouldReturnRunIdsMatchingAnyOfPrefixesExceptNegatedPrefixes")
    @Test
    void fuzzyFindSequences_whenQueryingMultipleAttributes_shouldReturnRunIdsMatchingAnyOfPrefixesExceptNegatedPrefixes() {
        BatchSequenceSearchFormat search = new BatchSequenceSearchFormat(
                List.of("RV0001"),
                List.of("V000000", "-V00000003"),
                List.of("1","2","3","-4"),
                List.of("-Teal"),
                true
        );
        var result = sequenceRepository.fuzzyFindSequences(search);
        assertEquals(result.size(), 2);
        var ids = result.stream().map(c -> c.barcode()).collect(Collectors.toList());
        Collections.sort(ids);
        assertEquals("02", ids.get(0));
        assertEquals("68", ids.get(1));
    }

    @DisplayName("batchLeftJoinSequencesFull_whenEmptyQuery_shouldReturnEmptyList")
    @Test
    void batchLeftJoinSequencesFull_whenEmptyQuery_shouldReturnEmptyList() {
        BatchSequenceSearchFormat search = new BatchSequenceSearchFormat(
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                false
        );
        var result = sequenceRepository.batchLeftJoinSequencesFull(search);
        assertEquals(0, result.size());
    }

    @DisplayName("batchLeftJoinSequencesFull_whenJunkQuery_shouldReturnEmptyAttributes")
    @Test
    void batchLeftJoinSequencesFull_whenJunkQuery_shouldReturnEmptyAttributes() {
        BatchSequenceSearchFormat search = new BatchSequenceSearchFormat(
                List.of("'--"),
                List.of("'--"),
                List.of("'--"),
                List.of("'--"),
                false
        );
        var result = sequenceRepository.batchLeftJoinSequencesFull(search);
        assertEquals(1, result.size());
        var rec = result.get(0);
        var expectedAttributes = Arrays.asList("'--", null, null, null, null, null, null);
        var actualAttributes = Arrays.asList(rec.sampleId(),
                rec.runId(),
                rec.lane(),
                rec.barcode(),
                rec.type(),
                rec.mate1(),
                rec.mate2());
        assertEquals(expectedAttributes, actualAttributes);
    }

    @DisplayName("batchLeftJoinSequencesFull_whenQueryExistingSample_shouldReturnSamplesAttributes")
    @Test
    void batchLeftJoinSequencesFull_whenQueryExistingSample_shouldReturnSamplesAttributes() {
        BatchSequenceSearchFormat search = new BatchSequenceSearchFormat(
                List.of("WY4579"),
                List.of(""),
                List.of(""),
                List.of(""),
                false
        );
        var result = sequenceRepository.batchLeftJoinSequencesFull(search);
        assertEquals(1, result.size());
        var rec = result.get(0);
        var expectedAttributes = Arrays.asList(
                "WY4579",
                "V00070609",
                "6",
                "61",
                "Orange",
                "V00070609/L06/{run_id}_L0{lane}_61_1.fq.gz",
                "V00070609/L06/{run_id}_L0{lane}_61_2.fq.gz"
        );

        var actualAttributes = Arrays.asList(rec.sampleId(),
                rec.runId(),
                rec.lane(),
                rec.barcode(),
                rec.type(),
                rec.mate1(),
                rec.mate2());
        assertEquals(expectedAttributes, actualAttributes);
    }

    @DisplayName("batchLeftJoinSequencesFull_whenQueryMultipleSamples_shouldReturnSamplesAttributes")
    @Test
    void batchLeftJoinSequencesFull_whenQueryMultipleSamples_shouldReturnSamplesAttributes() {
        BatchSequenceSearchFormat search = new BatchSequenceSearchFormat(
                List.of("WY4579", "PK8899", "'--"),
                List.of(""),
                List.of(""),
                List.of(""),
                false
        );
        var result = sequenceRepository.batchLeftJoinSequencesFull(search);
        assertEquals(3, result.size());
        result.sort(Comparator.comparing(Sequence::sampleId));
        var expectedAttributes = List.of(
                Arrays.asList(
                        "'--",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null),
                Arrays.asList(
                    "PK8899",
                    "V00092048",
                    "9",
                    "61",
                    "Green",
                    "V00092048/L09/{run_id}_L0{lane}_61_1.fq.gz",
                    "V00092048/L09/{run_id}_L0{lane}_61_2.fq.gz"),
                Arrays.asList(
                        "WY4579",
                        "V00070609",
                        "6",
                        "61",
                        "Orange",
                        "V00070609/L06/{run_id}_L0{lane}_61_1.fq.gz",
                        "V00070609/L06/{run_id}_L0{lane}_61_2.fq.gz")
        );

        for (var i=0; i < result.size(); i++) {
            var currRec = result.get(i);
            var actualAttributes = Arrays.asList(currRec.sampleId(),
                    currRec.runId(),
                    currRec.lane(),
                    currRec.barcode(),
                    currRec.type(),
                    currRec.mate1(),
                    currRec.mate2());
            assertEquals(expectedAttributes.get(i), actualAttributes);
        }
    }
    @DisplayName("batchLeftJoinSequencesFull_whenQueryMultipleAttributes_shouldReturnSamplesMatchingQueriesAttributes")
    @Test
    void batchLeftJoinSequencesFull_whenQueryMultipleAttributes_shouldReturnSamplesMatchingQueriesAttributes() {
        BatchSequenceSearchFormat search = new BatchSequenceSearchFormat(
                List.of("PK8899", "RV0001"),
                List.of("V00000002", "V00092048"),
                List.of(""),
                List.of(""),
                false
        );
        var result = sequenceRepository.batchLeftJoinSequencesFull(search);
        assertEquals(3, result.size());
        result.sort(Comparator.comparing(Sequence::barcode));
        var expectedAttributes = List.of(
                Arrays.asList(
                        "RV0001",
                        "V00000002",
                        "3",
                        "38",
                        "Teal",
                        "V00000002/L03/{run_id}_L0{lane}_38_1.fq.gz",
                        "V00000002/L03/{run_id}_L0{lane}_38_2.fq.gz"),
                Arrays.asList(
                        "PK8899",
                        "V00092048",
                        "9",
                        "61",
                        "Green",
                        "V00092048/L09/{run_id}_L0{lane}_61_1.fq.gz",
                        "V00092048/L09/{run_id}_L0{lane}_61_2.fq.gz"),
                Arrays.asList(
                        "RV0001",
                        "V00000002",
                        "4",
                        "74",
                        "Orange",
                        "V00000002/L04/{run_id}_L0{lane}_74_1.fq.gz",
                        "V00000002/L04/{run_id}_L0{lane}_74_2.fq.gz")
        );

        for (var i=0; i < result.size(); i++) {
            var currRec = result.get(i);
            var actualAttributes = Arrays.asList(currRec.sampleId(),
                    currRec.runId(),
                    currRec.lane(),
                    currRec.barcode(),
                    currRec.type(),
                    currRec.mate1(),
                    currRec.mate2());
            assertEquals(expectedAttributes.get(i), actualAttributes);
        }
    }

}

