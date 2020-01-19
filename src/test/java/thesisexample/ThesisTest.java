package thesisexample;

import javafx.util.Pair;
import org.behnaz.rcsp.ConstraintConnector;
import org.behnaz.rcsp.FIFO;
import org.behnaz.rcsp.IOAwareSolution;
import org.behnaz.rcsp.IOAwareStateValue;
import org.behnaz.rcsp.IOComponent;
import org.behnaz.rcsp.Solver;
import org.behnaz.rcsp.StateValue;
import org.behnaz.rcsp.StateVariableValue;
import org.behnaz.rcsp.input.JSONNetworkReader;
import org.behnaz.rcsp.model.MergerNode;
import org.behnaz.rcsp.model.Node;
import org.behnaz.rcsp.model.ReplicateNode;
import org.behnaz.rcsp.model.RouteNode;
import org.behnaz.rcsp.output.Drawer;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.behnaz.rcsp.model.util.SolverHelper.equalize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ThesisTest implements ExampleData {
    private final JSONNetworkReader networkReader = new JSONNetworkReader();
    private List<ReplicateNode> replicates = null;
    private List<RouteNode> routes = null;
    private List<MergerNode> merges = null;
    private List<Pair<String, String>> syncs = null;
    private List<Pair<String, String>> fifos = null;
    private List<Pair<String, String>> lossys = null;
    private List<Pair<String, String>> syncdrains = null;
    private List<Pair<String, String>> twoPrioritySyncs = null;
    private List<Pair<String, String>> onePrioritySyncs = null;
    private Map<String, String> connections = null;
    private JSONObject jsonObject;

    @Before
    public void init() {
        jsonObject  = new JSONObject(CONTENT);
        networkReader.read(jsonObject);
        connections = networkReader.getConnections();
        fifos = networkReader.getFifos();
        syncs = networkReader.getSyncs();
        lossys = networkReader.getLossys();
        syncdrains = networkReader.getSyncdrains();
        onePrioritySyncs = networkReader.getOnePrioritySyncs();
        twoPrioritySyncs = networkReader.getTwoPrioritySyncs();
        replicates = networkReader.getReplicates();
        routes = networkReader.getRoutes();
        merges= networkReader.getMerges();
    }

    private List<HashSet<String>> findEquals() {
        final List<HashSet<String>> result = new ArrayList<>();
        for (Map.Entry<String, String> s : connections.entrySet()){
            equalize(result, s.getKey(), s.getValue());
        }
        return result;
    }

    private ConstraintConnector network() {
        List<HashSet<String>> equals = findEquals();
        String source = networkReader.getFifos().get(0).getKey();
        String sink = networkReader.getFifos().get(0).getValue();

        ConstraintConnector connector = new ConstraintConnector(new FIFO(source, sink).generateConstraint().getConstraint());
        Node bk = replicates.stream().filter(e -> e.ownsEnd("B2")).findAny().get();
        Node tmp = routes.stream().filter(e -> e.ownsEnd("C1")).findAny().get();

        assertEquals("B", bk.getName());
        assertEquals("B2C", source);
        assertEquals("BC1", sink);
        assertEquals("B2", connections.get(source));
        assertEquals("C1", connections.get(sink));
        assertTrue(replicates.stream().anyMatch(e -> e.ownsEnd("B2")));
        assertTrue(routes.stream().anyMatch(e -> e.ownsEnd("C1")));
        assertTrue(replicates.stream().filter(e -> e.ownsEnd("B2")).findAny().isPresent());
        assertFalse(replicates.stream().filter(e -> e.ownsEnd("C1")).findAny().isPresent());
        assertEquals("C", tmp.getName());
        assertEquals("(C1tilde equiv (C3tilde or C4tilde or C2tilde)) and ( not (C3tilde and C4tilde and C2tilde))", tmp.getConstraint());
        return connector;
    }

    @Test
    public void example() {
        assertEquals(4, networkReader.getReaders().size());
        assertEquals(3, networkReader.getWriters().size());
//        assertEquals(20, nodes.length());
        //        assertEquals(24, channels.length());
//        assertEquals("ends", t);
        //      assertEquals(1, node.getJSONArray("ends").length()); // name
        //    assertTrue(node.getJSONArray("ends").getJSONObject(0).has("name"));
        //  assertTrue(node.getJSONArray("ends").getJSONObject(0).has("type"));
        //assertTrue(node.getJSONArray("ends").getJSONObject(0).getString("type"), "Source".equals(node.getJSONArray("ends").getJSONObject(0).getString("type")) || "Sink".equals(node.getJSONArray("ends").getJSONObject(0).getString("type")));


        assertEquals(110, connections.size());
        assertEquals("I1", connections.get("W11"));
        assertEquals("W11", connections.get("I1"));
        assertEquals("I2J", connections.get("I2"));
        assertEquals("I2", connections.get("I2J"));
        assertEquals("J2", connections.get("J2K"));
        assertEquals("JK1", connections.get("K1"));

        assertEquals(4, jsonObject.getJSONArray("readers").length());
        assertEquals(3, jsonObject.getJSONArray("writers").length());
        checkChannels();
        checkNodes();
        checkConnections();
    }

    private void checkChannels() {
        assertEquals(7, fifos.size());
        assertEquals(5, syncs.size());
        assertEquals(6, syncdrains.size());
        assertEquals(4, lossys.size());
        assertEquals(1, onePrioritySyncs.size());
        assertEquals(1, twoPrioritySyncs.size());

        assertEquals(24,  fifos.size() + twoPrioritySyncs.size() + onePrioritySyncs.size() + lossys.size() + syncdrains.size() + syncs.size());
        assertEquals("A2B->AB1, D2F->DF1, E2F->EF2, Q2T->QT1, J4L->JL2", syncs.stream().map(e -> e.getKey() + "->" + e.getValue()).collect(Collectors.joining(", ")));
        assertEquals("E4T<->ET2, O1D<->OD4, H1C<->HC4, C3M<->CM1, D3U<->DU1, E3L<->EL1", syncdrains.stream().map(e -> e.getKey() + "<->" + e.getValue()).collect(Collectors.joining(", ")));
        assertEquals("Q3O.>QO2, Q4H.>QH2, J3M.>JM2, J6U.>JU2", lossys.stream().map(e -> e.getKey() + ".>" + e.getValue()).collect(Collectors.joining(", ")));
        assertEquals("I2J!>IJ1", onePrioritySyncs.stream().map(e -> e.getKey() + "!>" + e.getValue()).collect(Collectors.joining(", ")));
        assertEquals("S2Q!!>SQ1", twoPrioritySyncs.stream().map(e -> e.getKey() + "!!>" + e.getValue()).collect(Collectors.joining(", ")));
        assertEquals("B2C[]>BC1, C2D[]>CD1, F3G[]>FG1, B3E[]>BE1, Q5P[]>QP1, J2K[]>JK1, J5N[]>JN1", fifos.stream().map(e -> e.getKey() + "[]>" + e.getValue()).collect(Collectors.joining(", ")));
    }

    private void checkConnections() {
        final List<Pair<String, String>> channelEnds = new ArrayList<>();
        channelEnds.addAll(syncs);
        channelEnds.addAll(syncdrains);
        channelEnds.addAll(lossys);
        channelEnds.addAll(fifos);
        channelEnds.addAll(onePrioritySyncs);
        channelEnds.addAll(twoPrioritySyncs);

        for (Pair<String, String> p : channelEnds) {
            assertTrue(connections.containsKey(p.getKey()) || connections.containsKey(p.getValue()));
        }
    }

    private void checkNodes() {
        assertEquals(16, replicates.size());
        assertEquals("A, B, G, H, I, J, K, L, M, N, O, P, Q, S, T, U", replicates.stream().map(Node::getName).collect(Collectors.joining(", ")));

        assertEquals(3, routes.size());
        assertEquals("C, D, E", routes.stream().map(Node::getName).collect(Collectors.joining(", ")));

        assertEquals(1, merges.size());
        assertEquals("F", merges.stream().map(Node::getName).collect(Collectors.joining(", ")));
    }

    @After
    public void reportErrors() {
        final Set<String> bads = new HashSet<>();
        final Set<String> checkedEnds = new HashSet<>();
        List<Pair<String, String>> ps = new ArrayList<>();
        ps.addAll(syncs);
        ps.addAll(lossys);
        ps.addAll(fifos);
        ps.addAll(syncdrains);
        ps.addAll(onePrioritySyncs);
        ps.addAll(twoPrioritySyncs);

        for (Pair<String, String> p : ps) {
            if (!connections.containsKey(p.getKey()) || checkedEnds.contains(p.getKey())) {
                bads.add(p.getKey());
            } else {
                checkedEnds.add(p.getKey());
            }
            if (!connections.containsKey(p.getValue()) || checkedEnds.contains(p.getValue())) {
                bads.add(p.getValue());
            } else {
                checkedEnds.add(p.getValue());
            }
        }

        List<Pair<String, Pair<Set<String>, Set<String>>>> nodeEnds = new ArrayList<>();
       //TODO nodeEnds.addAll(replicates);
      //  nodeEnds.addAll(routes);
        //nodeEnds.addAll(merges);

        for (Pair<String, Pair<Set<String>, Set<String>>> p : nodeEnds) {
            final Pair<Set<String>, Set<String>> sourceSinks = p.getValue();
            final Set<String> ends = sourceSinks.getKey();
            ends.addAll(sourceSinks.getValue());

            for (String s : ends) {
                if (!connections.containsKey(s) || checkedEnds.contains(s)) {
                    bads.add(s);
                } else {
                    checkedEnds.add(s);
                }
            }
        }

        assertEquals(3, networkReader.getWriters().size());
        assertEquals(4, networkReader.getReaders().size());

        final List<String> components = new ArrayList<>();
        //   components.addAll(nereaders);
        //   components.addAll(writers);

        for (String p : components) {
            if (!connections.containsKey(p)) {
                bads.add(p);
            } else {
                checkedEnds.add(p);
            }
        }

        int cnt = 0;
        final StringBuilder sb = new StringBuilder();
        for (String t : bads) {
            sb.append(t).append(", ");
            cnt++;
        }
        assertEquals( cnt + " Missing or double connections: " + sb.toString(), 0, cnt);

        final ConstraintConnector connector = network();

        List<IOAwareSolution> solutions = null;
        try {
            solutions = new ArrayList<>(checkSolutions(connector.getConstraint(), Collections.emptyList()));//TODO
        } catch (IOException e) {
            e.printStackTrace();
            fail("Failed to solve");
        }

        assertEquals(4, solutions.size());
        for (IOAwareSolution s : solutions) {
            if (s.getSolution().getFromVariables().size()  > 1) {
                System.out.println("FROM STATE WITH MORE THAN ONE" + s.getSolution().readable());
            }
            System.out.println(s.getSolution().readable());
        }
        new Drawer("/tmp/out").draw(solutions);
        //assertEquals("", connector.getConstraint());
        assertEquals(0, solutions.stream().map(e -> e.getSolution().getFromVariables()).distinct().filter(e -> e.size() > 1).count());
        assertEquals(0, solutions.stream().map(e -> e.getSolution().getToVariables()).distinct().filter(e -> e.size() > 1).count());
        assertEquals(0, solutions.stream().map(e -> e.getSolution().getFromVariables()).distinct().filter(e -> e.size() == 0).count());
        assertEquals(0, solutions.stream().map(e -> e.getSolution().getToVariables()).distinct().filter(e -> e.size() == 0).count());
        assertEquals(2, solutions.stream().map(e -> e.getSolution().getFromVariables()).distinct().filter(e -> e.size() == 1).count());
        assertEquals(2, solutions.stream().map(e -> e.getSolution().getToVariables()).distinct().filter(e -> e.size() == 1).count());
    }

    private Set<IOAwareSolution> checkSolutions(final String constraint, final List<String> variables) throws IOException {
        final Set<StateVariableValue> fifos = new HashSet<>();
        fifos.add(StateVariableValue.builder().stateName("j2kjk1ring").value(Boolean.FALSE).build());
        fifos.add(StateVariableValue.builder().stateName("j5njn1ring").value(Boolean.FALSE).build());
        IOAwareStateValue initState = new IOAwareStateValue(StateValue.builder().variableValues(fifos).build(), new IOComponent("W11", 1), new IOComponent("W31", 1));
        return new HashSet<>(Solver.builder()
                .initState(initState)
                .build()
                .solve(constraint,4));
    }

    private List<HashSet<String>> createEquals() {
        final List<HashSet<String>> result = new ArrayList<>();
        for (Map.Entry<String, String> s : connections.entrySet()) {
    //        equalize(result, s.getKey(), s.getValue());
        }

        //TODO TEMP taghalllob
      //  equalize(result, "CM1", "C3M");

        assertEquals(18, result.size());
        validateEnds(result);
        //assertEquals("", result);
        return result;
    }

    private void validateEnds(final List<HashSet<String>> result) {
        for (HashSet<String> set : result) {
            for (String s : set) {
                assertTrue("Invalid end " + s , connections.containsKey(s) | connections.containsValue(s));
            }
        }
    }
}
