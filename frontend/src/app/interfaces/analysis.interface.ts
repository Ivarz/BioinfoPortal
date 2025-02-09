import { Sequence } from './sequence.interface'

export interface AnalysisRequest {
    id: String;
    userId: String;
    statusId: number;
    fulfilled: boolean;
    failed: boolean;
    details: AnalysisRequestDetails;
}

export interface AnalysisResultObjectDTO {
    analysisId: String;
    userId: String;
    fullName: string;
    fileName: string;
    fulfilled: boolean;
    failed: boolean;
}

export type AnalysisType = "metagenomics" | "metatranscriptomics" | "transcriptomics"
export interface AnalysisParameters { }

export interface AnalysisRequestDetails {
    id: number;
    hpcAccount: string;
    title: string;
    analysisType: AnalysisType;
    keepHpcFiles: boolean;
    analysisParameters: AnalysisParameters;
    //metagenomicAnalysisParameters: MetagenomicAnalysisParameters;
    //transcriptomicAnalysisParameters: TranscriptomicAnalysisParameters;
    //metatranscriptomicAnalysisParameters: MetatranscriptomicAnalysisParameters;
    sequences: Sequence[];
}


export interface MetagenomicAnalysisParameters extends AnalysisParameters {
    type: string;
    host: string;
    kraken2Uhgg: boolean;
    kraken2Pluspf: boolean;
    kraken2PluspfSpecific: boolean;
    metaphlan4: boolean;
    humann4: boolean;
}

export interface MetatranscriptomicAnalysisParameters extends AnalysisParameters {
    type: string;
    host: string;
    kraken2Uhgg: boolean;
    kraken2Pluspf: boolean;
    kraken2PluspfSpecific: boolean;
    metaphlan4: boolean;
    humann4: boolean;
}

export interface TranscriptomicAnalysisParameters extends AnalysisParameters {
    type: string;
    starReference: string;
    rRnaEstimation: boolean;
}
