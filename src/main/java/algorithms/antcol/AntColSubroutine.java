package algorithms.antcol;

import algorithms.interfaces.Subroutine;
import datastructures.SolutionMatrix;
import datastructures.pojo.SolutionWithStatus;
import graph.definition.GraphDefinition;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;

public class AntColSubroutine implements Subroutine {

//    private final SolutionMatrix solutionMatrix;
    private final GraphDefinition graphDefinition;
    private final Integer constraintChecks;
    private final Integer tabuIterations;
    private final Integer randomSeed;
    private final Integer targetColors;

    private Integer numberOfConstraintChecks = 0;
    private ArrayList<Integer> degrees;
    private Graph<Integer, DefaultEdge> localPheremoneDelta = new SimpleGraph<>(DefaultEdge.class);

    public AntColSubroutine(GraphDefinition graphDefinition, Integer constraintChecks, Integer tabuIterations, Integer randomSeed, Integer targetColors) {
        this.graphDefinition = graphDefinition;
        this.constraintChecks = constraintChecks;
        this.tabuIterations = tabuIterations;
        this.randomSeed = randomSeed;
        this.targetColors = targetColors;
        this.degrees = new ArrayList<>();
    }

    @Override
    public SolutionWithStatus findSolution() {
        prepare();

        System.out.println("error");
//        while (numberOfConstraintChecks < constraintChecks) {
//
//        }

        return null;
    }

    private void prepare() {
        createDegreesVector();
    }

    private void createDegreesVector() {
        for (Integer vertex : graphDefinition.getGraphWrapper().getVertices()) {
            Integer neighbors = graphDefinition.getGraphWrapper().getNeighborsOfV(vertex).size();
            this.degrees.add(neighbors);
        }
    }
}
