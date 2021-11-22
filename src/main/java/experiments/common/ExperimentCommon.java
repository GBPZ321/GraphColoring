package experiments.common;

import experiments.MetropolisExperiment;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class ExperimentCommon {
    private static final String graphsDir = "graphs/";
    public static void printLog(String experimentLabel, String header) {
        System.out.println(experimentLabel);
        System.out.println(header);
    }

    public static List<String> getListOfGraphs() throws IOException {
        return IOUtils.readLines(Objects.requireNonNull(ExperimentCommon.class.getClassLoader().getResourceAsStream(graphsDir)), StandardCharsets.UTF_8);
    }

    public static InputStream getStream(String filename) {
        return ExperimentCommon.class.getClassLoader().getResourceAsStream(graphsDir + filename);
    }


}
