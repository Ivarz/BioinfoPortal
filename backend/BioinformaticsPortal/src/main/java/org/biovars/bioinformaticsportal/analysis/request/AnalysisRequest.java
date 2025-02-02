package org.biovars.bioinformaticsportal.analysis.request;

import org.biovars.bioinformaticsportal.analysis.request.details.AnalysisRequestDetails;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("ANALYSIS_REQUESTS")
public record AnalysisRequest(
    @Id String id,
    String userId,
    long statusId,
    boolean fulfilled,
    boolean failed,
    AnalysisRequestDetails details
)
{}