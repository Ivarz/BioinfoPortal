package org.biovars.bioinformaticsportal.sequence;

/**
 * Represents a sequencing data record containing metadata for a sequencing sample.
 *
 * @param id       the unique identifier of the sequence record
 * @param sampleId the identifier of the sample associated with this sequence
 * @param runId    the identifier of the sequencing run
 * @param lane     the sequencing lane where the sample was processed
 * @param barcode  the barcode used to identify the sample
 * @param type     the type of sequencing (e.g., "Metagenomic", "Metatranscriptomic", etc.)
 * @param mate1    the file path or identifier for the first mate in paired-end sequencing
 * @param mate2    the file path or identifier for the second mate in paired-end sequencing (if applicable)
 */

public record Sequence(
    long id,
    String sampleId,
    String runId,
    String lane,
    String barcode,
    String type,
    String mate1,
    String mate2
) {}
