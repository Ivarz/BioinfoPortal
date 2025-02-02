package org.biovars.bioinformaticsportal.analysis.request.details.metatranscriptomics;

import org.biovars.bioinformaticsportal.analysis.request.details.AnalysisParameters;


/**
 * Represents the parameters required for a metatranscriptomic analysis.
 * This record defines the options and configurations used to perform
 * metatranscriptomics workflows, including the tools and resources to be applied.
 *
 * @param host                     the host organism associated with the analysis
 * @param type                     the type of analysis being performed (e.g., metatranscriptomics)
 * @param kraken2Uhgg              whether to use the Kraken2 UHGG database
 * @param kraken2Pluspf            whether to use the Kraken2 PlusPF database
 * @param kraken2PluspfSpecific    whether to perform specific analyses with the Kraken2 PlusPF database
 * @param metaphlan4               whether to include MetaPhlAn4 in the analysis workflow
 * @param humann4                  whether to include HUMAnN4 in the analysis workflow
 */
public record MetatranscriptomicAnalysisParameters(
    String host,
    String type,
    boolean kraken2Uhgg,
    boolean kraken2Pluspf,
    boolean kraken2PluspfSpecific,
    boolean metaphlan4,
    boolean humann4
) implements AnalysisParameters { }
