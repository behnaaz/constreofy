package thesisexample;

import org.javatuples.Pair;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.behnaz.rcsp.ConstraintConnector;
import org.behnaz.rcsp.EqualBasedConnectorFactory;
import org.behnaz.rcsp.FIFO;
import org.behnaz.rcsp.GraphViz;
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
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
        jsonObject = new JSONObject(CONTENT);
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
        merges = networkReader.getMerges();
    }

    @Test
    @Ignore
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
        reportErrors();
    }

    private void checkChannels() {
        assertEquals(7, fifos.size());
        assertEquals(5, syncs.size());
        assertEquals(6, syncdrains.size());
        assertEquals(4, lossys.size());
        assertEquals(1, onePrioritySyncs.size());
        assertEquals(1, twoPrioritySyncs.size());

        assertEquals(24, fifos.size() + twoPrioritySyncs.size() + onePrioritySyncs.size() + lossys.size() + syncdrains.size() + syncs.size());
        assertEquals("A2B->AB1, D2F->DF1, E2F->EF2, Q2T->QT1, J4L->JL2", syncs.stream().map(e -> e.getValue0() + "->" + e.getValue1()).collect(Collectors.joining(", ")));
        assertEquals("E4T<->ET2, O1D<->OD4, H1C<->HC4, C3M<->CM1, D3U<->DU1, E3L<->EL1", syncdrains.stream().map(e -> e.getValue0() + "<->" + e.getValue1()).collect(Collectors.joining(", ")));
        assertEquals("Q3O.>QO2, Q4H.>QH2, J3M.>JM2, J6U.>JU2", lossys.stream().map(e -> e.getValue0() + ".>" + e.getValue1()).collect(Collectors.joining(", ")));
        assertEquals("I2J!>IJ1", onePrioritySyncs.stream().map(e -> e.getValue0() + "!>" + e.getValue1()).collect(Collectors.joining(", ")));
        assertEquals("S2Q!!>SQ1", twoPrioritySyncs.stream().map(e -> e.getValue0() + "!!>" + e.getValue1()).collect(Collectors.joining(", ")));
        assertEquals("B2C[]>BC1, C2D[]>CD1, F3G[]>FG1, B3E[]>BE1, Q5P[]>QP1, J2K[]>JK1, J5N[]>JN1", fifos.stream().map(e -> e.getValue0() + "[]>" + e.getValue1()).collect(Collectors.joining(", ")));
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
            assertTrue(connections.containsKey(p.getValue0()) || connections.containsKey(p.getValue1()));
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
            if (!connections.containsKey(p.getValue0()) || checkedEnds.contains(p.getValue0())) {
                bads.add(p.getValue0());
            } else {
                checkedEnds.add(p.getValue0());
            }
            if (!connections.containsKey(p.getValue1()) || checkedEnds.contains(p.getValue1())) {
                bads.add(p.getValue1());
            } else {
                checkedEnds.add(p.getValue1());
            }
        }

        List<Pair<String, Pair<Set<String>, Set<String>>>> nodeEnds = new ArrayList<>();
        //nodeEnds.addAll(replicates);
        //nodeEnds.addAll(routes);
        // nodeEnds.addAll(merges);

        for (Pair<String, Pair<Set<String>, Set<String>>> p : nodeEnds) {
            final Pair<Set<String>, Set<String>> sourceSinks = p.getValue1();
            final Set<String> ends = sourceSinks.getValue0();
            ends.addAll(sourceSinks.getValue1());

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
        assertEquals(cnt + " Missing or double connections: " + sb.toString(), 0, cnt);

        final ConstraintConnector connector = network();
        // testSol(connector, 1, 32, "b3ebe1ring,c2dcd1ring");

        testSol(connector, 2, 7, "googogoli");

        testSol(connector, 2, 5, "b2cbc1ring,b3ebe1ring");
        testSol(connector, 3, 9, "b2cbc1ring,b3ebe1ring");
        // testSol(connector, 4, 9, "b2cbc1ring,b3ebe1ring");

        testSol(connector, 1, 2);

        testSol(connector, 1, 4, "j2kjk1ring");
        testSol(connector, 2, 14, "j2kjk1ring");
        testSol(connector, 3, 19, "j2kjk1ring");
        testSol(connector, 4, 19, "j2kjk1ring");
        //  testSol(connector, -1, 19, "j2kjk1ring");

        testSol(connector, 1, 4, "j5njn1ring");
        testSol(connector, 2, 14, "j5njn1ring");
        testSol(connector, 3, 19, "j5njn1ring");
        testSol(connector, 4, 19, "j5njn1ring");


        testSol(connector, 1, 8, "j2kjk1ring,j5njn1ring");
        testSol(connector, 2, 28, "j2kjk1ring,j5njn1ring");
        testSol(connector, 3, 38, "j2kjk1ring,j5njn1ring");
        testSol(connector, 4, 48, "j2kjk1ring,j5njn1ring");
        testSol(connector, 5, 53, "j2kjk1ring,j5njn1ring");
        testSol(connector, 6, 57, "j2kjk1ring,j5njn1ring");
        testSol(connector, 7, 61, "j2kjk1ring,j5njn1ring");
        testSol(connector, 8, 63, "j2kjk1ring,j5njn1ring");
        testSol(connector, 9, 79, "j2kjk1ring,j5njn1ring");
        testSol(connector, 10, 87, "j2kjk1ring,j5njn1ring");
        testSol(connector, 15, 139, "j2kjk1ring,j5njn1ring");

        testSol(connector, 2, 8, "q5pqp1ring");
        testSol(connector, 1, 4, "q5pqp1ring");
        testSol(connector, 3, 13, "q5pqp1ring");
        testSol(connector, 4, 15, "q5pqp1ring");
        testSol(connector, 5, 21, "q5pqp1ring");//TODO ali

        testSol(connector, 2, 7);
        testSol(connector, 3, 11);
        testSol(connector, 1, 4, "f3gfg1ring");
        testSol(connector, 2, 14, "f3gfg1ring");
        testSol(connector, 3, 19, "f3gfg1ring");
        testSol(connector, 5, 29, "f3gfg1ring");
        testSol(connector, 6, 33, "f3gfg1ring");
        //   testSol(connector, 7, 33, "f3gfg1ring");
    }

    private List<IOAwareSolution> testSol(final ConstraintConnector connector, final int rounds, final int expected, final String init) {
        List<IOAwareSolution> solutions = null;
        try {
            solutions = new ArrayList<>(solve(connector, rounds, init));
        } catch (IOException e) {
            e.printStackTrace();
            fail("Failed to solve");
        }
        new Drawer("/tmp/out-" + rounds + "-" + init.replaceAll(",", "_")).draw(solutions);
        assertEquals(expected, solutions.size());
        return solutions;
    }

    private List<IOAwareSolution> testSol(final ConstraintConnector connector, final int rounds, final int expected) {
        return testSol(connector, rounds, expected, "");
    }

    private Set<IOAwareSolution> solve(final ConstraintConnector connector, final int numberOfRounds, @NonNull final String givenInit) throws IOException {
        final Set<StateVariableValue> fifos = this.fifos.stream().map(e -> new FIFO(e.getValue0(), e.getValue1()).getMemory().toLowerCase())
                .map(e -> StateVariableValue.builder().stateName(e).value(givenInit.contains(e)).build())
                .collect(Collectors.toSet());

        IOAwareStateValue initState = new IOAwareStateValue(
                StateValue.builder().variableValues(fifos).build(),
                new IOComponent("W11", 1), new IOComponent("W21", 1), new IOComponent("W31", 1),
                new IOComponent("R12", 1), new IOComponent("R22", 1), new IOComponent("R32", 1), new IOComponent("R42", 1));
        return SolverHelper.solve(connector.getConstraint(), numberOfRounds, initState);
    }

    private List<HashSet<String>> createEquals() {
        final List<HashSet<String>> result = new ArrayList<>();
        for (Map.Entry<String, String> s : connections.entrySet()) {
            equalize(result, s.getKey(), s.getValue());
        }

        equalize(result, "C4", "H1");
        equalize(result, "C1", "BC1");
        equalize(result, "BC1", "C1");
        equalize(result, onePrioritySyncs.get(0).getValue0(), onePrioritySyncs.get(0).getValue1());
        equalize(result, "I1", connections.get("I1"));
        equalize(result, "I1", "I2");
        equalize(result, "I2", connections.get("I2"));
        equalize(result, "IJ1", connections.get("IJ1"));
        equalize(result, "J1", "J2");
        equalize(result, "J1", "J3");
        equalize(result, "J1", "J4");
        equalize(result, "J1", "J5");
        equalize(result, "J1", "J6");//(factory.replicator("J1", "J2", "J3", "J4", "J5", "J6"), "J1", connections.get("J1"));
        equalize(result, "J1", connections.get("J1"));
        equalize(result, "J2", connections.get("J2"));
        equalize(result, "J3", connections.get("J3"));
        equalize(result, "J4", connections.get("J4"));
        equalize(result, "J5", connections.get("J5"));
        equalize(result, "J6", connections.get("J6"));
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
        //TODO TEMP taghalllob
        equalize(result, "OD4", "O1D");


        assertEquals(16, result.size());
        validateEnds(result);
        assertEquals("[[Q1, Q2, ET2, Q3, Q4, Q4H, Q5, E4T, Q3O, Q5P, QT1, E4, SQ1, Q2T, S2Q, W31, T1, T2, S1, S2], [C3, M1, M2, CM1, C3M, JM2], [I2J, J2K, J5N, J4L, J3M, L1, L2, J1, I1, J2, J6U, I2, J3, J4, J5, EL1, J6, E3, IJ1, JL2, W11, E3L], [A1, AB1, B2, A2, A2B, B3, B2C, B3E, W21, B1], [BC1, C1], [JK1, R12, K1, K2], [R22, N1, N2, JN1], [R32, FG1, G1, G2], [C2D, C2], [E1, BE1], [C4, HC4, H1, H2, QH2, H1C], [R42, P1, P2, QP1], [CD1, D1], [EF2, E2F, D2F, F1, F3G, F2, E2, F3, D2, DF1], [DU1, JU2, D3U, U1, U2, D3], [D4, O1, O1D, O2, QO2, OD4]]", result.toString());
        return result;
    }

    private void validateEnds(final List<HashSet<String>> result) {
        for (HashSet<String> set : result) {
            for (String s : set) {
                assertTrue("Invalid end " + s, connections.containsKey(s) | connections.containsValue(s));
            }
        }
    }

    private ConstraintConnector network() {
        final EqualBasedConnectorFactory factory = new EqualBasedConnectorFactory(createEquals());//Arrays.asList("C2D", "CD1", "B2C", "BC1",
        ConstraintConnector connector = factory.writer("W11", 1);
//factory.prioritySync(onePrioritySyncs.get(0).getValue0(), onePrioritySyncs.get(0).getValue1());
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
        connector.add(factory.fifo("F3G", "FG1"), "F3G", connections.get("F3G"));
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

    @Test
    public void a() {
        String s =
                "empty -> bc_be [ label=\"L1:{[a1]}\"];\n" +
                        "bc_be -> qp [ label=\"L2:{[q1],[e1],[c4]]}\"];\n" +
                        "cd_be -> qp [ label=\"L2:{[q1],[e1],[d4]]}\"];\n" +
                        "empty -> empty [ label=\"L3:{}\"];\n" +
                        "cd_be -> fg [ label=\"L5:{[f3],[d1],[e1]}\"];\n" +
                        "bc_be -> bc_be [ label=\"L6:{}\"];\n" +
                        "bc_be -> cd_be [ label=\"L7:{[c2]}\"];\n" +
                        "fg -> empty [ label=\"L12:{[r32]}\"];\n" +
                        "cd_be -> jk_jn [ label=\"L14:{[w11],[d3]}\"];\n" +
                        "bc_be -> jk_jn [ label=\"L15:{[w11],}\"];\n" +
                        "fg -> fg [ label=\"L17:{}\"];\n" +
                        "empty -> empty [ label=\"L18:{}\"];\n" +
                        "jk -> empty [ label=\"L19:{[r12]}\"];\n" +
                        "jn -> empty [ label=\"L20:{[r22]}\"];\n" +
                        "jk_jn -> jn [ label=\"L21:{[r12]}\"];\n" +
                        "jk_jn -> jk [ label=\"L22:{[r22]}\"];\n" +
                        "jk_jn -> jk_jn [ label=\"L23:{}\"];\n" +
                        "jk_jn -> empty [ label=\"L24:{[r12],[r22]}\"];\n" +
                        "qp -> qp [ label=\"L25:{}\"];\n" +
                        "qp -> empty [ label=\"L26:{[r42]}\"];\n";

        final String refined = s.replaceAll("\"L\\d+:", "\"");
        final Set<String> transitions = Arrays.asList(refined.split("\n")).stream()//map(e ->
                //(e.split(":")[0].substring(0, e.split(":")[0].lastIndexOf("\"")+1))+ e.split(":")[1])
                .collect(Collectors.toSet());
        String reachableTrans = "";

        Map<String, Set<String>> reachables = new HashMap<>();
        Set<String> targets;
        LinkedHashSet<String> toExplore = new LinkedHashSet<>(Arrays.asList("empty"));
        Set<String> visited = new HashSet<>();
        String old = "empty";
        while (!toExplore.isEmpty()) {
            String state = toExplore.iterator().next();
            reachableTrans += transitions.stream().filter(e -> e.startsWith(state + " -> ")).collect(Collectors.joining("\n"));
            visited.add(state);
            toExplore.remove(toExplore.iterator().next());
            targets = transitions.stream().filter(e -> e.startsWith(state + " -> "))
                    .map(e -> e.substring(e.indexOf(">") + 1, e.indexOf("[")).trim())
                    .collect(Collectors.toSet());
            toExplore.addAll(targets.stream().filter(e -> !visited.contains(e)).collect(Collectors.toSet()));
            Set<String> temp = reachables.getOrDefault(old, new HashSet<>());
            temp.add(state);
            reachables.put(old, temp);
            old = state;
        }

        writeToFile(reachableTrans, "svg");
        writeToFile(reachableTrans, "txt");
    }

    @SneakyThrows
    private void writeToFile(final String content, final String fileType) {
        File out = new File("/tmp/outbehnaz" + "." + fileType);   // Linux
        if ("svg".equals(fileType)) {
            GraphViz gv = new GraphViz();
            gv.addln(gv.start_graph());
            gv.add(("labelloc=\"b\";\n" +
                    "aHtmlTable [\n" +
                    "   shape=plaintext\n" +
                    "   color=blue      // The color of the border of the table\n" +
                    "   label=<\n" +
                    "\n" +
                    "     <table border='1' cellborder='0'>\n" +
                    "       <tr><td>a1</td><td>{A1, AB1, B2, A2, A2B, B3, B2C, B3E, W21, B1}</td></tr>\n" +
                    "       <tr><td>w11</td><td>{I2J, J2K, J5N, J4L, J3M, L1, L2, J1, I1, J2, J6U, I2, J3, J4, J5, EL1, J6, E3, IJ1, JL2, W11, E3L}</td></tr>\n" +
                    "       <tr><td>C3</td><td>{C3, M1, M2, CM1, C3M, JM2}</td></tr>\n" +
                    "       <tr><td>Q1</td><td>{Q1, Q2, ET2, Q3, Q4, Q4H, Q5, E4T, Q3O, Q5P, QT1, E4, SQ1, Q2T, S2Q, W31, T1, T2, S1, S2}</td></tr>\n" +
                    "       <tr><td>C1</td><td>{BC1, C1}</td></tr>\n" +
                    "       <tr><td>r12</td><td>{JK1, R12, K1, K2}</td></tr>\n" +
                    "       <tr><td>r22</td><td>{R22, N1, N2, JN1}</td></tr>\n" +
                    "       <tr><td>r32</td><td>{R32, FG1, G1, G2}</td></tr>\n" +
                    "       <tr><td>C2</td><td>{C2D, C2}</td></tr>\n" +
                    "       <tr><td>E1</td><td>{E1, BE1}</td></tr>\n" +
                    "       <tr><td>C4</td><td>{C4, HC4, H1, H2, QH2, H1C}</td></tr>\n" +
                    "       <tr><td>R42</td><td>{R42, P1, P2, QP1}</td></tr>\n" +
                    "       <tr><td>d1</td><td>{CD1, D1}</td></tr>\n" +
                    "       <tr><td>f3</td><td>{EF2, E2F, D2F, F1, F3G, F2, E2, F3, D2, DF1}</td></tr>\n" +
                    "       <tr><td>D3</td><td>{DU1, JU2, D3U, U1, U2, D3}</td></tr>\n" +
                    "       <tr><td>D4</td><td>{D4, O1, O1D, O2, QO2, OD4}</td></tr>\n" +
                    "     </table>\n" +
                    "\n" +
                    "  >];" + content).toLowerCase());
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

            gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type, repesentationType), out);
        } else {
            FileOutputStream fos = new FileOutputStream(out);
            fos.write(content.getBytes());
        }
    }
}
