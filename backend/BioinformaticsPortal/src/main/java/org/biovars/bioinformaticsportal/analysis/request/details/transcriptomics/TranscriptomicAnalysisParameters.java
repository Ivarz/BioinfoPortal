package org.biovars.bioinformaticsportal.analysis.request.details.transcriptomics;

import org.biovars.bioinformaticsportal.analysis.request.details.AnalysisParameters;


/**
 * Represents the parameters required for a transcriptomic analysis.
 * This record defines the type of analysis being performed and the reference
 * dataset to be used for transcriptomics workflows.
 *
 * @param type          the type of analysis being performed (e.g., transcriptomics)
 * @param starReference the STAR alignment reference to be used for the analysis
 */
public record TranscriptomicAnalysisParameters(
    String type,
    String starReference,
    boolean rRnaEstimation
)  implements AnalysisParameters { }