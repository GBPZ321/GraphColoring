package datastructures;

import lombok.Data;

@Data
public class Triple<X, Y, Z> {
    public X vertex;
    public Y color;
    public Z delta;
    private boolean isEmpty;
    public Triple() {
        this.isEmpty = true;
    }
}