package org.biovars.bioinformaticsportal.analysis.request.details;

import org.biovars.bioinformaticsportal.analysis.request.AnalysisRequest;
import org.biovars.bioinformaticsportal.analysis.request.details.metagenomics.MetagenomicAnalysisParameters;
import org.biovars.bioinformaticsportal.analysis.request.details.metatranscriptomics.MetatranscriptomicAnalysisParameters;
import org.biovars.bioinformaticsportal.analysis.request.details.transcriptomics.TranscriptomicAnalysisParameters;
import org.biovars.bioinformaticsportal.sequence.Sequence;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

/**
 * Represents the details of an analysis request for bioinformatics workflows.
 * This record holds metadata and configurations for an analysis submission including
 * the associated HPC account, title of the analysis, type of analysis, analysis-specific
 * parameters, and sequencing data used for the analysis.
 *
 * Annotations:
 * - @Table: Maps this record to the "ANALYSIS_DETAILS" table in the database.
 * - @Id: Marks the unique identifier field of the analysis request details.
 *
 * Fields:
 * - id: The unique identifier for the analysis request details.
 * - hpcAccount: The HPC account associated with this analysis request.
 * - title: A human-readable title describing the analysis.
 * - analysisType: The type of analysis conducted (e.g., metagenomics, transcriptomics).
 * - analysisParameters: The parameters specific to the type of analysis being performed.
 * - sequences: A list of sequencing data records associated with the analysis request.
 */
@Table("ANALYSIS_DETAILS")
public record AnalysisRequestDetails(
    @Id long id,
    String hpcAccount,
    String title,
    String analysisType,
    boolean keepHpcFiles,
    AnalysisParameters analysisParameters,
    List<Sequence> sequences
){ }
