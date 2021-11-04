package formatter;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Builder
@Getter
@Setter
public class Schema {
    private Integer edgeCount;
    private Integer vertexCount;
    private List<String> edges;
    private Map<Integer, Integer> colors;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder
                .append(vertexCount).append('\n')
                .append(edgeCount).append('\n');
        Set<Integer> keys = new LinkedHashSet<>(colors.keySet());
        for(Integer key : keys) {
            builder.append(colors.get(key)).append('\n');
        }
        for(String e : edges) {
            builder.append(e).append('\n');
        }
        builder.setLength(builder.length() - 1); // remove last '\n'
        return builder.toString();
    }
}
