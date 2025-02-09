import { Sequence } from  '../interfaces/sequence.interface';

export function sequencesToTsv(seqs: Sequence[]): string {
  let result = [];
  result.push(`sample_id\tfc_id\tlane\tbarcode\ttype\tmate1\tmate2`);
  for (let seq of seqs) {
    result.push(`${seq.sampleId}\t${seq.runId}\t${seq.lane}\t${seq.barcode}\t${seq.type}\t${seq.mate1}\t${seq.mate2}`);
  }
  return result.join('\n');
}
