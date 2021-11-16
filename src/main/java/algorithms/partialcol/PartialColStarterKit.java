package algorithms.partialcol;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
class PartialColStarterKit {
    private Map<Integer, Integer> startingColorings;
    private Set<Integer> U;
}
