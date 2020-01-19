package org.behnaz.rcsp.output;

import lombok.AllArgsConstructor;
import org.behnaz.rcsp.GraphViz;
import org.behnaz.rcsp.IOAwareSolution;
import static org.behnaz.rcsp.Solution.NEG;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class Drawer {
    public static final String STATE_VARIABLE_DELIMITER = "_";
    private final String path;

    final Map<String, String> labels = new HashMap<>();

    public void draw(final List<IOAwareSolution> solutions) {
        GraphViz gv = new GraphViz();
        gv.addln(gv.start_graph());
        for (IOAwareSolution sol : solutions) {
            gv.addln(makeLine(sol));
        }
        gv.addln(gv.end_graph());
        System.out.println(gv.getDotSource());

        gv.increaseDpi();   // 106 dpi

        String type = "svg";
        //      String type = "dot";
        //      String type = "fig";    // open with xfig
        //      String type = "pdf";
        //      String type = "ps";
        //      String type = "svg";    // open with inkscape
        //      String type = "png";
        //      String type = "plain";

        String repesentationType = "dot";
        //		String repesentationType= "neato";
        //		String repesentationType= "fdp";
        //		String repesentationType= "sfdp";
        // 		String repesentationType= "twopi";
        // 		String repesentationType= "circo";

        File out = new File( path  + gv.getImageDpi() + "." + type);   // Linux
        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type, repesentationType), out);

        final File desc = new File(path + "labels.txt");   // Linux
        try {
            FileOutputStream fos = new FileOutputStream(desc);
            fos.write(labels.entrySet().stream().map(e -> e.getValue() + " = " + e.getKey()).collect(Collectors.joining("\n")).getBytes());
            fos.close();
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private String flow(final Set<String> flowVariables) {
        return " [ label=\"" + cache(flowVariables) + "\" " + "]";
    }

    private String makeLine(final IOAwareSolution sol) {
        return handleState(sol.getSolution().getFromVariables()).stream().map(e -> e.replaceAll("ring", "")).collect(Collectors.joining(STATE_VARIABLE_DELIMITER)) + " -> " + handleState(sol.getSolution().getToVariables()).stream().collect(Collectors.joining(STATE_VARIABLE_DELIMITER)).replaceAll("xring", "") + flow(sol.getSolution().getFlowVariables()) + ";";
    }

    private String cache(final Set<String> flowVariables) {
        final String lbl = flowVariables.isEmpty() ? "{}" : flowVariables.stream().filter(e -> ! e.startsWith(NEG)).collect(Collectors.joining(","));
        if (labels.containsKey(lbl)) {
            return labels.get(lbl);
        }

        String tmp = "L" + (labels.size() + 1);
        labels.put(lbl, tmp);
        return tmp;
    }

    private Set<String> handleState(final Set<String> set) {
        if (set.isEmpty()) {
            return new HashSet<>(Arrays.asList("empty"));
        }

        final Set<String> temp = set.stream()
                .filter(e -> !e.startsWith(NEG))
                .map(e -> e.replaceAll("\\d", "").substring(0, 2))
                .collect(Collectors.toSet());
        return temp.isEmpty() ? new HashSet<>(Arrays.asList("empty")) : temp;
    }
}
