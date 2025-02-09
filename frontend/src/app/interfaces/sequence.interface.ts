
export interface Sequence {
  recordId: number;
  sampleId: string;
  runId: string;
  lane: string;
  barcode: string;
  type: string;
  mate1: string;
  mate2: string;
}

export interface SequenceDbMaintenance {
  maintenanceModeOn: boolean;
}
