package org.biovars.bioinformaticsportal.sequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record BatchSequenceSearchFormat(
    List<String> sampleIds,
    List<String> runIds,
    List<String> lanes,
    List<String> types,
    boolean fuzzySearch
) {
    public BatchSequenceSearchFormat sortDescending() {
        List<String> sortedSampleIds = sampleIds
                .stream()
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());
        List<String> sortedRunIds = runIds
                .stream()
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());
        List<String> sortedLanes = lanes
                .stream()
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());
        List<String> sortedTypes = types
                .stream()
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());

        return new BatchSequenceSearchFormat(
                sortedSampleIds,
                sortedRunIds,
                sortedLanes,
                sortedTypes,
                fuzzySearch
        );
    }
    private List<String> removeNegateSymbolFromList(List<String> values) {
         return values
                .stream()
                .map(x -> (!x.isEmpty() && (x.charAt(0) == '-')) ? x.substring(1, x.length()) : x)
                .collect(Collectors.toList());
    }
    private List<String> encloseInModulusFromList(List<String> values) {
        return values
                .stream()
                .map(x -> (!x.isEmpty()  ? "%"+x+"%" : "%"))
                .collect(Collectors.toList());
    }
    public BatchSequenceSearchFormat removeNegateSymbol() {
        return new BatchSequenceSearchFormat(
                removeNegateSymbolFromList(this.sampleIds),
                removeNegateSymbolFromList(this.runIds),
                removeNegateSymbolFromList(this.lanes),
                removeNegateSymbolFromList(this.types),
                fuzzySearch
        );
    }
    public BatchSequenceSearchFormat encloseInModulus() {
        return new BatchSequenceSearchFormat(
                encloseInModulusFromList(this.sampleIds),
                encloseInModulusFromList(this.runIds),
                encloseInModulusFromList(this.lanes),
                encloseInModulusFromList(this.types),
                fuzzySearch
        );
    }

    public BatchSequenceSearchFormat removeEmptyValues() {
        return new BatchSequenceSearchFormat(
                this.sampleIds.stream().filter(x -> !x.isEmpty()).collect(Collectors.toList()),
                this.runIds.stream().filter(x -> !x.isEmpty()).collect(Collectors.toList()),
                this.lanes.stream().filter(x -> !x.isEmpty()).collect(Collectors.toList()),
                this.types.stream().filter(x -> !x.isEmpty()).collect(Collectors.toList()),
                this.fuzzySearch
        );
    }
    public boolean isEmpty() {
        return this.sampleIds.isEmpty() && this.runIds.isEmpty() && this.lanes.isEmpty();
    }
    public Object[] toArray() {
        List<String> result = new ArrayList<>();
        result.addAll(this.sampleIds);
        result.addAll(this.runIds);
        result.addAll(this.lanes);
        result.addAll(this.types);
        return result.toArray();
    }
}
