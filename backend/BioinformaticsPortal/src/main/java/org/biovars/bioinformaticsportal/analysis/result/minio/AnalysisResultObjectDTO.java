package org.biovars.bioinformaticsportal.analysis.result.minio;

public record AnalysisResultObjectDTO(
    String analysisId,
    String userId,
    String fullName,
    String fileName
){}
