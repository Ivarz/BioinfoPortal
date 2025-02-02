package org.biovars.bioinformaticsportal.sequence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SequenceController {

    private final SequenceService sequenceService;
    private static final Logger logger = LoggerFactory.getLogger(SequenceController.class);

    SequenceController(SequenceService sequenceService) {
        this.sequenceService = sequenceService;
    }

    @GetMapping("/sequences/search")
    List<Sequence> findLikeSequence(
            @RequestParam(value = "sample_id", required = false) String sampleId,
            @RequestParam(value = "run_id", required = false) String runId,
            @RequestParam(value = "lane", required = false) String lane,
            @RequestParam(value = "type", required = false) String type) {

        return sequenceService.findLikeSequence(sampleId, runId, lane, type);
    }

    @PostMapping("/sequences/search")
    List<Sequence> batchfindsequencefull(@RequestBody BatchSequenceSearchFormat searchFormat) {
        if (searchFormat.fuzzySearch()) {
            return sequenceService.fuzzyFindSequences(searchFormat);

        } else {
            return sequenceService.batchFindSequenceFull(searchFormat);
        }
    }

    @PostMapping("/sequences/validate")
    List<Pair<Sequence, Boolean>> validateSequences(@RequestBody List<Sequence> samples) {
        return sequenceService.validateSequences(samples);
    }

    @GetMapping("sequences/maintenance")
    SequenceDbMaintenance isMaintenanceOn() {
        return sequenceService.isMaintenanceOn();
    }
}
