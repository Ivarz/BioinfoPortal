package org.biovars.bioinformaticsportal.analysis.request.details;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.biovars.bioinformaticsportal.analysis.request.details.metagenomics.MetagenomicAnalysisParameters;
import org.biovars.bioinformaticsportal.analysis.request.details.metatranscriptomics.MetatranscriptomicAnalysisParameters;
import org.biovars.bioinformaticsportal.analysis.request.details.transcriptomics.TranscriptomicAnalysisParameters;

/**
 * Interface representing the analysis parameters used in various types of bioinformatics analyses.
 * This interface serves as a polymorphic base for different specific types of analysis configurations,
 * such as metagenomics, transcriptomics, and metatranscriptomics. The subtype of the analysis is
 * determined by the "type" property in the JSON serialization.
 *
 * Implementations of this interface provide specific configurations and parameters required
 * for running their respective analysis workflows.
 *
 * Annotations:
 * - @JsonTypeInfo: Used to include type information in JSON serialization and deserialization.
 *   The "type" property distinguishes between subtypes.
 * - @JsonSubTypes: Declares the specific subtypes implementing this interface along with their
 *   corresponding type name for JSON mapping.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, // Use the "type" property to determine the subtype
        include = JsonTypeInfo.As.PROPERTY, // Add the "type" property in JSON
        property = "type", // Name of the property that contains the type information
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MetagenomicAnalysisParameters.class, name = "metagenomics"),
        @JsonSubTypes.Type(value = TranscriptomicAnalysisParameters.class, name = "transcriptomics"),
        @JsonSubTypes.Type(value = MetatranscriptomicAnalysisParameters.class, name = "metatranscriptomics")
})
public interface AnalysisParameters { }
