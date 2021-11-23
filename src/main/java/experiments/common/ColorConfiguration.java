package experiments.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ColorConfiguration {
    boolean cooperative;
    boolean withMatrix;
    AdditionalConfiguration configuration;
}
