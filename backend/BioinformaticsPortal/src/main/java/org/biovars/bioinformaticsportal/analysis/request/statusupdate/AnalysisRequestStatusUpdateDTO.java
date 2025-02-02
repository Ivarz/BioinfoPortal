package org.biovars.bioinformaticsportal.analysis.request.statusupdate;

/**
 * A data transfer object representing the status update of an analysis request.
 * This DTO is used to communicate the current status of an analysis request between
 * different components of the system.
 *
 * This record consists of:
 * - An identifier for the analysis request.
 * - An identifier for the user who initiated the request.
 * - A numerical status code representing the current state of the request.
 * - A boolean indicating whether the request has been successfully fulfilled.
 * - A boolean indicating whether the request processing has failed.
 */
public record AnalysisRequestStatusUpdateDTO(
    String id,
    String userId,
    long statusId,
    boolean fulfilled,
    boolean failed
){}
