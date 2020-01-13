package thesisexample;

import javafx.util.Pair;
import org.behnaz.rcsp.ConnectorFactory;
import org.behnaz.rcsp.ConstraintConnector;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
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
    private final Set<String> checkedEnds = new HashSet<>();
    private final List<String> replicates = new ArrayList<>();
    private final List<String> routes = new ArrayList<>();
    private final List<String> merges = new ArrayList<>();
    private final List<Pair<String, String>> syncs = new ArrayList<>();//Primitive
    private final List<Pair<String, String>>  fifos = new ArrayList<>();
    private final List<Pair<String, String>> lossys = new ArrayList<>();
    private final List<Pair<String, String>> syncdrains = new ArrayList<>();
    private final List<Pair<String, String>> twoPrioritySyncs = new ArrayList<>();
    private final List<Pair<String, String>> onePrioritySyncs = new ArrayList<>();
    private final Map<String, String> connections = new HashMap<>();

    private JSONObject jsonObject;
    @Before
    public void init() {
       jsonObject  = new JSONObject(CONTENT);
    }

    private ConstraintConnector network() {
        ConnectorFactory factory = new ConnectorFactory();
        ConstraintConnector connector = factory.prioritySync("a", "b");
        connector.add(factory.merger("c", "d", "e"), "c", "b");
        connector.add(factory.sync("f", "g"), "f", "a");
        connector.add(factory.merger("h", "i", "j"), "h", "g");
        return connector;
    }

    @Test
    public void example() {
        assertEquals(4, jsonObject.getJSONArray("readers").length());
        assertEquals(3, jsonObject.getJSONArray("writers").length());
    }

    @Test
    public void checkChannels() {
        readChannels();
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

    private void readChannels() {
        final JSONArray channels = jsonObject.getJSONArray("channels");
        assertEquals(24, channels.length());

        for (int i=0; i < channels.length(); i++) {
            JSONObject node = channels.getJSONObject(i);
            handle(node);
        }
    }

    @Test
    public void checkConnections() {
        final JSONArray temp = jsonObject.getJSONArray("connections");
        assertEquals(49, temp.length());
        for (int i=0; i < temp.length(); i++) {
            JSONObject connection = temp.getJSONObject(i);
            String from = connection.getString("one");
            String to = connection.getString("two");
            connections.put(from, to);
            connections.put(to, from);
        }
        assertEquals(92, connections.size());

        readChannels();;
        for (Pair<String, String> p : syncs) {
            reportErrors(p);
            assertTrue(connections.containsKey(p.getKey()) || connections.containsKey(p.getValue()));
        }
        for (Pair<String, String> p : syncdrains) {
            reportErrors(p);
            assertTrue(connections.containsKey(p.getKey()) || connections.containsKey(p.getValue()));
        }

        for (Pair<String, String> p : lossys) {
            reportErrors(p);
            assertTrue(connections.containsKey(p.getKey()) || connections.containsKey(p.getValue()));
        }

        for (Pair<String, String> p : fifos) {
            reportErrors(p);
            assertTrue(connections.containsKey(p.getKey()) || connections.containsKey(p.getValue()));
        }

        for (Pair<String, String> p : onePrioritySyncs) {
            reportErrors(p);
            assertTrue(connections.containsKey(p.getKey()) || connections.containsKey(p.getValue()));
        }

        for (Pair<String, String> p : twoPrioritySyncs) {
            reportErrors(p);
            assertTrue(connections.containsKey(p.getKey()) || connections.containsKey(p.getValue()));
        }

        int cnt = 0;
        final StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = checkedEnds.iterator();
        while (iterator.hasNext()) {
            String t = iterator.next();
            sb.append(t).append(", ");
            cnt++;
        }
        System.out.println(cnt + " Missing or double connections: " + sb.toString());//TODO figure out why missing or double
    }

    private void reportErrors(final Pair<String, String> p) {
        if (! connections.containsKey(p.getKey()) || checkedEnds.contains(p.getKey())) {
            System.out.println(p.getKey());
        } else {
            checkedEnds.add(p.getKey());
        }
        if (! connections.containsKey(p.getValue()) || checkedEnds.contains(p.getValue())) {
            System.out.println(p.getValue());
        } else {
            checkedEnds.add(p.getValue());
        }
    }

    @Test
    public void checkNodes() {
        final JSONArray nodes = jsonObject.getJSONArray("nodes");
        assertEquals(20, nodes.length());

        for (int i=0; i < nodes.length(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            handle(node);
        }
        assertEquals(13, replicates.size());
        assertEquals("A, B, G, H, I, J, K, L, M, N, O, P, Q", replicates.stream().collect(Collectors.joining(", ")));

        assertEquals(3, routes.size());
        assertEquals("C, D, E", routes.stream().collect(Collectors.joining(", ")));

        assertEquals(1, merges.size());
        assertEquals("F", merges.stream().collect(Collectors.joining(", ")));
    }

    private void handle(final JSONObject node) {
        String t = node.keys().next();

        switch (t) {
            case "Replicate":
                replicates.add(node.getJSONObject(t).getString("name"));
                break;
            case "Route":
                routes.add(node.getJSONObject(t).getString("name"));
                break;
            case "Merge":
                merges.add(node.getJSONObject(t).getString("name"));
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
            case "ends":
                //TODO bad data
                break;
            default:
                fail();
        }
    }
}
