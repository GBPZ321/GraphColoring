package graph;

public class ColorableVertex {
    private String vertexName;
    private Integer color;

    public ColorableVertex(String vertexName, Integer color) {
        this.vertexName = vertexName;
        this.color = color;
    }

    public ColorableVertex(String vertexName) {
        this.vertexName = vertexName;
    }

    public String getVertexName() {
        return vertexName;
    }

    public void setVertexName(String vertexName) {
        this.vertexName = vertexName;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }
}
