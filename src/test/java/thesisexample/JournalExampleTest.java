package thesisexample;

import javafx.util.Pair;
import org.behnaz.rcsp.ConnectorFactory;
import org.behnaz.rcsp.ConstraintConnector;
import org.behnaz.rcsp.IOAwareSolution;
import org.behnaz.rcsp.IOAwareStateValue;
import org.behnaz.rcsp.IOComponent;
import org.behnaz.rcsp.Solver;
import org.behnaz.rcsp.StateValue;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class JournalExampleTest implements ExampleData {
    private final List<Pair<String, Pair<Set<String>, Set<String>>>> replicates = new ArrayList<>();
    private final List<Pair<String, Pair<Set<String>, Set<String>>>> routes = new ArrayList<>();
    private final List<Pair<String, Pair<Set<String>, Set<String>>>> merges = new ArrayList<>();
    private final List<Pair<String, String>> syncs = new ArrayList<>();
    private final List<Pair<String, String>> fifos = new ArrayList<>();
    private final List<Pair<String, String>> lossys = new ArrayList<>();
    private final List<Pair<String, String>> syncdrains = new ArrayList<>();
    private final List<Pair<String, String>> twoPrioritySyncs = new ArrayList<>();
    private final List<Pair<String, String>> onePrioritySyncs = new ArrayList<>();
    private final List<String> readers = new ArrayList<>();
    private final List<String> writers = new ArrayList<>();
    private final Map<String, String> connections = new HashMap<>();

    private JSONObject jsonObject;

    private void readConnections() {
        final JSONArray temp = jsonObject.getJSONArray("connections");
        assertEquals(57, temp.length());
        for (int i=0; i < temp.length(); i++) {
            JSONObject connection = temp.getJSONObject(i);
            String from = connection.getString("one");
            String to = connection.getString("two");
            connections.put(from, to);
            connections.put(to, from);
        }

        assertEquals(110, connections.size());
    }

    private void readNodes() {
        final JSONArray nodes = jsonObject.getJSONArray("nodes");
        assertEquals(20, nodes.length());

        for (int i=0; i < nodes.length(); i++) {
            handle(nodes.getJSONObject(i));
        }
    }

    private void readChannels() {
        final JSONArray channels = jsonObject.getJSONArray("channels");
        assertEquals(24, channels.length());

        for (int i=0; i < channels.length(); i++) {
            handle(channels.getJSONObject(i));
        }
    }

    private void readWriters() {
        final JSONArray writers = jsonObject.getJSONArray("writers");
        assertEquals(3, writers.length());

        for (int i=0; i < writers.length(); i++) {
            handleReaderWriter(writers.getJSONObject(i));
        }
    }

    private void handleReaderWriter(final JSONObject node) {
        final String t = node.keys().next();
        assertEquals("ends", t);
        assertEquals(1, node.getJSONArray("ends").length()); // name
        assertTrue(node.getJSONArray("ends").getJSONObject(0).has("name"));
        assertTrue(node.getJSONArray("ends").getJSONObject(0).has("type"));
        assertTrue(node.getJSONArray("ends").getJSONObject(0).getString("type"), "Source".equals(node.getJSONArray("ends").getJSONObject(0).getString("type")) || "Sink".equals(node.getJSONArray("ends").getJSONObject(0).getString("type")));
        if ("Source".equals(node.getJSONArray("ends").getJSONObject(0).getString("type"))) {
            writers.add(node.getJSONArray("ends").getJSONObject(0).getString("name"));
        } else {
            readers.add(node.getJSONArray("ends").getJSONObject(0).getString("name"));
        }
    }

    private void readReaders() {
        final JSONArray readers = jsonObject.getJSONArray("readers");
        assertEquals(4, readers.length());

        for (int i=0; i < readers.length(); i++) {
            handleReaderWriter(readers.getJSONObject(i));
        }
    }

    private void handle(final JSONObject node) {
        final String t = node.keys().next();

        switch (t) {
            case "Replicate":
                replicates.add(new Pair<>(node.getJSONObject(t).getString("name"), extractEnds(node, t)));
                break;
            case "Route":
                routes.add(new Pair<>(node.getJSONObject(t).getString("name"), extractEnds(node, t)));
                break;
            case "Merge":
                merges.add(new Pair<>(node.getJSONObject(t).getString("name"), extractEnds(node, t)));
                break;
            case "Sync":
                syncs.add(new Pair(node.getJSONArray(t).getJSONObject(0).getString("Source"), node.getJSONArray(t).getJSONObject(1).getString("Sink")));
                break;
            case "FIFO":
                fifos.add(new Pair(node.getJSONArray(t).getJSONObject(0).getString("Source"), node.getJSONArray(t).getJSONObject(1).getString("Sink")));
                break;
            case "SyncDrain":
                syncdrains.add(new Pair(node.getJSONArray(t).getJSONObject(0).getString("Source"), node.getJSONArray(t).getJSONObject(1).getString("Source")));
                break;
            case "PrioritySync2":
                twoPrioritySyncs.add(new Pair(node.getJSONArray(t).getJSONObject(0).getString("Source"), node.getJSONArray(t).getJSONObject(1).getString("Sink")));
                break;
            case "PrioritySync1":
                onePrioritySyncs.add(new Pair(node.getJSONArray(t).getJSONObject(0).getString("Source"), node.getJSONArray(t).getJSONObject(1).getString("Sink")));
                break;
            case "Lossy":
                lossys.add(new Pair(node.getJSONArray(t).getJSONObject(0).getString("Source"), node.getJSONArray(t).getJSONObject(1).getString("Sink")));
                break;
            default:
                fail();
        }
    }

    private Pair<Set<String>, Set<String>> extractEnds(JSONObject node, String t) {
        final JSONArray array = node.getJSONObject(t).getJSONArray("ends");
        final Set<String> sources = new HashSet<>();
        final Set<String> sinks = new HashSet<>();
        for (int i = 0; i < array.length(); i++) {
            final JSONObject obj = array.getJSONObject(i);
            if (obj.getString("type").equals("Source")) {
                sources.add(obj.getString("name"));
            } else if (obj.getString("type").equals("Sink")) {
                sinks.add(obj.getString("name"));
            } else {
                fail("bad type " + obj.getJSONArray("type") + " for " +  obj.getString("name"));
            }
        }
        return new Pair<>(sources, sinks);
    }

    @Before
    public void init() {
        jsonObject  = new JSONObject(CONTENT);
        readChannels();
        readWriters();
        readReaders();
        readNodes();
        readConnections();
    }

    @Test
    public void example() {
        assertEquals(4, jsonObject.getJSONArray("readers").length());
        assertEquals(3, jsonObject.getJSONArray("writers").length());
    }

    @Test
    public void checkChannels() {
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

    @Test
    public void checkConnections() {
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

    @Test
    public void checkNodes() {
        assertEquals(16, replicates.size());
        assertEquals("A, B, G, H, I, J, K, L, M, N, O, P, Q, S, T, U", replicates.stream().map(e -> e.getKey()).collect(Collectors.joining(", ")));

        assertEquals(3, routes.size());
        assertEquals("C, D, E", routes.stream().map(e -> e.getKey()).collect(Collectors.joining(", ")));

        assertEquals(1, merges.size());
        assertEquals("F", merges.stream().map(e -> e.getKey()).collect(Collectors.joining(", ")));
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
        nodeEnds.addAll(replicates);
        nodeEnds.addAll(routes);
        nodeEnds.addAll(merges);

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

        assertEquals(3, writers.size());
        assertEquals(4, readers.size());

        final List<String> components = new ArrayList<>();
        components.addAll(readers);
        components.addAll(writers);

        for (String p : components) {
            if (!connections.containsKey(p)) {
                bads.add(p);
            } else {
                checkedEnds.add(p);
            }
        }

        int cnt = 0;
        final StringBuilder sb = new StringBuilder();
        final Iterator<String> iterator = bads.iterator();
        while (iterator.hasNext()) {
            String t = iterator.next();
            sb.append(t).append(", ");
            cnt++;
        }
        assertEquals( cnt + " Missing or double connections: " + sb.toString(), 0, cnt);

        //final ConstraintConnector connector = network();
        //assertEquals(new HashSet<>(Arrays.asList("I2JTILDE", "I2JC",  "I2TILDE", "W11TILDE", "I1TILDE", "IJ1K", "IJ1TILDE", "I2JBULLET", "IJ1BULLET")), connector.getVariables());
        //assertEquals("(I2JTILDE equiv IJ1TILDE) and  not (I2JC and IJ1K) and I2JBULLET and IJ1BULLET  and  (W11TILDE  or   not  W11TILDE)", connector.getConstraint());

        List<IOAwareSolution> solutions = null;
        try {
            solutions = checkSolutions(network());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Failed to solve");
        }

        assertEquals(4, solutions.size());
       // assertEquals(1, solutions.get(9));
      //  assertEquals("() ----{ ij1tilde, i2tilde, i1tilde, i2jtilde, } ----> ()", solutions.get(0).getSolution().readable());
        //() ----{ i2tilde, i1tilde, } ----> () assertEquals("() ----{ } ----> ()", solutions.get(1).getSolution().readable());
       // [] ------ { ij1tilde ij1PRIORITY i2jtilde i2jPRIORITY} -------> ()   assertEquals("() ----{ } ----> ()", solutions.get(2).getSolution().readable());
       // assertEquals("() ----{ } ----> ()", solutions.get(3).getSolution().readable());
    }

    private List<IOAwareSolution> checkSolutions(final ConstraintConnector connector) throws IOException {
                IOAwareStateValue initState = new IOAwareStateValue(StateValue.builder().build(), new IOComponent("W31", 1));
                return Solver.builder()
                                .connectorConstraint(connector)
                                .initState(initState)
                                .build()
                                .solve(-1);
   }

    private ConstraintConnector network() {
        final ConnectorFactory factory = new ConnectorFactory();
        ConstraintConnector connector = factory.prioritySync(onePrioritySyncs.get(0).getKey(), onePrioritySyncs.get(0).getValue());
        connector.add(factory.writer("W11", 1), "W11", connections.get("W11"));
        connector.add(factory.sync("I1", "I2"), "I2", connections.get("I2"));
        assertEquals("I1", connections.get("W11"));
        assertEquals("I2J", connections.get("I2"));
        //  connector.add(factory.merger("c", "d", "e"), "c", "b");
        //connector.add(factory.sync("f", "g"), "f", "a");
        //connector.add(factory.merger("h", "i", "j"), "h", "g");
        return connector;
    }

}
