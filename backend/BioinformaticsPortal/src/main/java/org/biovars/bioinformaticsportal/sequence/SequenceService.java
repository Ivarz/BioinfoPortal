package org.biovars.bioinformaticsportal.sequence;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SequenceService {

    private SequenceRepository sequenceRepository;

    public SequenceService(SequenceRepository sequenceRepository) {
        this.sequenceRepository = sequenceRepository;
    }

    List<Sequence> findLikeSequence(String sampleId,
                                String runId,
                                String lane,
                                String type) {
        return sequenceRepository.findLikeSequence(sampleId, runId, lane, type);
    }
    List<Sequence> fuzzyFindSequences(BatchSequenceSearchFormat batchSearchInput) {
        return sequenceRepository.fuzzyFindSequences(batchSearchInput);
    }

    List<Sequence> findBySampleIdRunIdLane(String sampleId,
                                    String runId,
                                    String lane) {
        return sequenceRepository.findBySampleIdRunIdLane(sampleId, runId, lane);
    }

    SequenceDbMaintenance isMaintenanceOn() {
        return sequenceRepository.isMaintenanceModeOn();
    }

    List<Pair<Sequence, Boolean>> validateSequences(List<Sequence> sequences) {
        List<Pair<Sequence, Boolean>> result = new ArrayList<>();
        for (Sequence seq: sequences) {
            var res = findBySampleIdRunIdLane(seq.sampleId(), seq.runId(), seq.lane());
            for (Sequence resSeq: res) {
                result.add(Pair.of(resSeq, true));
            }
            if (res.isEmpty()) {
                result.add(Pair.of(seq, false));
            }
        }
        return result;
    }

    public List<Sequence> batchFindSequenceFull(BatchSequenceSearchFormat batchSequenceSearchFormat) {
        return sequenceRepository.batchLeftJoinSequencesFull(batchSequenceSearchFormat);
    }
}
