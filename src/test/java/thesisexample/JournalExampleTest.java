package thesisexample;

import javafx.util.Pair;
import lombok.NonNull;
import org.behnaz.rcsp.ConstraintConnector;
import org.behnaz.rcsp.EqualBasedConnectorFactory;
import org.behnaz.rcsp.FIFO;
import org.behnaz.rcsp.IOAwareSolution;
import org.behnaz.rcsp.IOAwareStateValue;
import org.behnaz.rcsp.IOComponent;
import org.behnaz.rcsp.StateValue;
import org.behnaz.rcsp.StateVariableValue;
import org.behnaz.rcsp.input.JSONNetworkReader;
import org.behnaz.rcsp.model.MergerNode;
import org.behnaz.rcsp.model.Node;
import org.behnaz.rcsp.model.ReplicateNode;
import org.behnaz.rcsp.model.RouteNode;
import org.behnaz.rcsp.model.util.SolverHelper;
import org.behnaz.rcsp.output.Drawer;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.behnaz.rcsp.model.util.SolverHelper.equalize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class JournalExampleTest implements ExampleData {
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
        //nodeEnds.addAll(replicates);
        //nodeEnds.addAll(routes);
       // nodeEnds.addAll(merges);

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
        testSol(connector, 2, 6, "b2cbc1ring,b3ebe1ring");

        testSol(connector, 1, 2);
        testSol(connector, 2, 8, "q5pqp1ring");
        testSol(connector, 2, 8);
        testSol(connector, 3, 8);

        List<IOAwareSolution> solutions = testSol(connector, 4, 12);
        for (IOAwareSolution s : solutions) {
            if (s.getSolution().getFromVariables().size()  > 1) {
                System.out.println("FROM STATE WITH MORE THAN ONE" + s.getSolution().readable());
            }
            System.out.println(s.getSolution().readable());
        }
        new Drawer("/tmp/out").draw(solutions);
        //assertEquals("", connector.getConstraint());
        assertEquals(3, solutions.stream().map(e -> e.getSolution().getFromVariables()).distinct().filter(e -> e.size() > 1).count());
        assertEquals(8, solutions.stream().map(e -> e.getSolution().getToVariables()).distinct().filter(e -> e.size() > 1).count());
        assertEquals(0, solutions.stream().map(e -> e.getSolution().getFromVariables()).distinct().filter(e -> e.size() == 0).count());
        assertEquals(0, solutions.stream().map(e -> e.getSolution().getToVariables()).distinct().filter(e -> e.size() == 0).count());
    }

    private  List<IOAwareSolution>  testSol(final ConstraintConnector connector, final int rounds, final int expected, final String init) {
        List<IOAwareSolution> solutions = null;
        try {
            solutions = new ArrayList<>(solve(connector, rounds, init));
        } catch (IOException e) {
            e.printStackTrace();
            fail("Failed to solve");
        }
        new Drawer("/tmp/out-"+rounds+"-"+init.replaceAll(",","_")).draw(solutions);
        assertEquals(expected, solutions.size());
        return solutions;
    }

    private List<IOAwareSolution> testSol(final ConstraintConnector connector, final int rounds, final int expected) {
        return testSol(connector, rounds, expected, "");
    }

    private Set<IOAwareSolution> solve(final ConstraintConnector connector, final int numberOfRounds, @NonNull final String givenInit) throws IOException {
        final Set<StateVariableValue> fifos = this.fifos.stream().map(e -> new FIFO(e.getKey(), e.getValue()).getMemory().toLowerCase())
                .map(e -> StateVariableValue.builder().stateName(e).value(givenInit.contains(e)).build())
                .collect(Collectors.toSet());

        IOAwareStateValue initState = new IOAwareStateValue(StateValue.builder().variableValues(fifos).build(), new IOComponent("W11", 1), new IOComponent("W31", 1));
        return SolverHelper.solve(connector.getConstraint(), numberOfRounds, initState);
   }

    private List<HashSet<String>> createEquals() {
        final List<HashSet<String>> result = new ArrayList<>();
        for (Map.Entry<String, String> s : connections.entrySet()) {
            equalize(result, s.getKey(), s.getValue());
        }

        equalize(result, "C1", "BC1");
        equalize(result, "BC1", "C1");
        equalize(result, onePrioritySyncs.get(0).getKey(), onePrioritySyncs.get(0).getValue());
        equalize(result, "I1", connections.get("I1"));
        equalize(result, "I1", "I2");
        equalize(result, "I2", connections.get("I2"));
        equalize(result, "IJ1", connections.get("IJ1"));
        equalize(result, "J1", "J2");equalize(result, "J1", "J3");equalize(result, "J1", "J4");equalize(result, "J1", "J5");equalize(result, "J1", "J6");//(factory.replicator("J1", "J2", "J3", "J4", "J5", "J6"), "J1", connections.get("J1"));
        equalize(result, "J1", connections.get("J1"));equalize(result, "J2", connections.get("J2"));
        equalize(result, "J3", connections.get("J3"));equalize(result, "J4", connections.get("J4"));
        equalize(result, "J5", connections.get("J5"));equalize(result, "J6", connections.get("J6"));
        equalize(result, "K1", connections.get("K1"));
        equalize(result, "J4L", connections.get("J4L"));
        equalize(result, "N2", connections.get("N2"));
        equalize(result, "M1", connections.get("M2"));
        equalize(result, "M1", connections.get("CM1"));
        equalize(result, "C3M", connections.get("C3M"));
        equalize(result, "JM2", connections.get("M2"));

        equalize(result, "B1", "B2");
        equalize(result, "B1", "B3");
        equalize(result, "B1", "AB1");
        equalize(result, "A2B", "AB1");
        equalize(result, "A1", "A2");
        equalize(result, "A2B", "A2");
        equalize(result, "A1", "W21");
        equalize(result, "D3U", "D3");
        equalize(result, "JU2", "U2");
        equalize(result, "U1", "U2");
        equalize(result, "U1", "DU1");
        equalize(result, "D3U", "DU1");
        equalize(result, "JN1", "N1");
        equalize(result, "N2", "N1");
        equalize(result, "K1", "K2");
        equalize(result, "R12", "K2");
        equalize(result, "B2C", "B2");
        equalize(result, "D2F", "DF1");//sync
        equalize(result, "D2F", "D2");
        equalize(result, "DF1", "F1");
        equalize(result, "M2", "M1");
        equalize(result, "F3G", "F3");
        equalize(result, "FG1", "G1");
        equalize(result, "G2", "G1");
        equalize(result, "G2", "R32");
        equalize(result, "E2F", "EF2");
        equalize(result, "F2", "EF2");
        equalize(result, "E2", "EF2");
        equalize(result, "E3L", "E3");
        equalize(result, "E3L", "EL1");
        equalize(result, "L1", "EL1");
        equalize(result, "L1", "L2");
        equalize(result, "F1", "F2");
        equalize(result, "F1", "F3");
        equalize(result, "B3E", "B3");
        equalize(result, "BE1", "E1");
        equalize(result, "J4L", "JL2");
        equalize(result, "L2", "JL2");
        equalize(result, "E2F", "EF2");
        equalize(result, "F2", "EF2");
        equalize(result, "E4T", "E4");
        equalize(result, "ET2", "T2");
        equalize(result, "T2", "T1");
        equalize(result, "T2", "T1");
        equalize(result, "Q2T", "QT1");//sync
        equalize(result, "Q2T", "Q2");
        equalize(result, "QT1", "T1");
        equalize(result, "Q1", "Q2");
        equalize(result, "Q1", "Q3");
        equalize(result, "Q1", "Q4");
        equalize(result, "Q1", "Q5");
        equalize(result, "SQ1", "S2Q");//priosync
        equalize(result, "Q1", "SQ1");
        equalize(result, "S2", "S2Q");
        equalize(result, "S2", "S1");
        equalize(result, "W31", "S1");
        equalize(result, "Q5P", "Q5");
        equalize(result, "QP1", "P1");
        equalize(result, "P2", "P1");
        equalize(result, "P2", "R42");
        equalize(result, "O1", "O2");
        equalize(result, "H1", "H2");
        equalize(result, "ET2", "E4T");


        //TODO TEMP taghalllob
        equalize(result, "CM1", "C3M");


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

    private ConstraintConnector network() {
        final EqualBasedConnectorFactory factory = new EqualBasedConnectorFactory(createEquals());//Arrays.asList("C2D", "CD1", "B2C", "BC1",
        ConstraintConnector connector = factory.writer("W11", 1);
//factory.prioritySync(onePrioritySyncs.get(0).getKey(), onePrioritySyncs.get(0).getValue());
        connector.add(factory.fifo("J2k", "JK1"), "J2K", connections.get("J2K"));
        connector.add(factory.writer("R12", 1), "R12", connections.get("R12"));//TODO reader
        connector.add(factory.lossySync("J3M", "JM2"), "J3", connections.get("J3"));
        connector.add(factory.lossySync("JU2", "J6U"), "J6", connections.get("J6"));
        connector.add(factory.fifo("J5N", "JN1"), "J5N", connections.get("J5N"));
        connector.add(factory.writer("R22", 1), "R22", connections.get("R22"));//TODO reader

        connector.add(factory.syncDrain("CM1", "C3M"), "M1", "CM1");
        connector.add(factory.router("C1", "C2", "C3", "C4"), "C3", "C3M");
        connector.add(factory.fifo("B2C", "BC1"), "BC1", connections.get("BC1"));
        connector.add(factory.writer("W21", 1), "W21", connections.get("W21"));//TODO reader
        connector.add(factory.syncDrain("D3U", "DU1"), "D3U", connections.get("D3U"));

        connector.add(factory.fifo("C2D", "CD1"), "C2D", connections.get("C2D"));
        connector.add(factory.router("D1", "D2", "D3", "D4"), "D1", connections.get("D1"));

        connector.add(factory.merger("F1", "F2", "F3"), "F3", connections.get("F3"));
        connector.add(factory.fifo("F3G", "FG11"), "F3G", connections.get("F3G"));
        connector.add(factory.router("E1", "E2", "E3", "E4"), "E2", connections.get("E2"));
        connector.add(factory.syncDrain("E3L", "EL1"), "E3L", connections.get("E3L"));
        connector.add(factory.fifo("B3E", "BE1"), "BE1", connections.get("BE1"));

        connector.add(factory.syncDrain("E4T", "ET2"), "E4T", connections.get("ET2"));
        connector.add(factory.writer("W31", 1), "W31", connections.get("W31"));

        connector.add(factory.fifo("Q5P", "QP1"), "F3G", connections.get("F3G"));
        connector.add(factory.writer("R42", 1), "R42", connections.get("R42"));//TODO reader

        connector.add(factory.lossySync("Q3O", "QO2"), "Q3O", connections.get("Q3O"));
        connector.add(factory.lossySync("Q4H", "QH2"), "Q4H", connections.get("Q4H"));

        connector.add(factory.syncDrain("O1D", "OD4"), "O1D", connections.get("O1D"));
        connector.add(factory.syncDrain("HC4", "H1C"), "E3L", connections.get("E3L"));

        return connector;
    }
}
