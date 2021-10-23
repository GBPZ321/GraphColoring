package utility;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Satisfies {
    private int errorCount = 0;
    private boolean satisfies;
}
