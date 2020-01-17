package org.behnaz.rcsp.input;

import javafx.util.Pair;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class JSONNetworkReader {
    private final List<String> readers = new ArrayList<>();
    private final List<String> writers = new ArrayList<>();
    private Map<String, String> connections = new HashMap<>();
    private JSONObject jsonObject;
    @Getter
    private final List<Pair<String, Pair<Set<String>, Set<String>>>> replicates = new ArrayList<>();
    @Getter
    private final List<Pair<String, Pair<Set<String>, Set<String>>>> routes = new ArrayList<>();
    @Getter
    private final List<Pair<String, Pair<Set<String>, Set<String>>>> merges = new ArrayList<>();
    @Getter
    private final List<Pair<String, String>> syncs = new ArrayList<>();
    @Getter
    private final List<Pair<String, String>> fifos = new ArrayList<>();
    @Getter
    private final List<Pair<String, String>> lossys = new ArrayList<>();
    @Getter
    private final List<Pair<String, String>> syncdrains = new ArrayList<>();
    @Getter
    private final List<Pair<String, String>> twoPrioritySyncs = new ArrayList<>();
    @Getter
    private final List<Pair<String, String>> onePrioritySyncs = new ArrayList<>();

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
                throw new RuntimeException("bad type " + obj.getJSONArray("type") + " for " +  obj.getString("name"));
            }
        }
        return new Pair<>(sources, sinks);
    }

    public void handle(final JSONObject node) {
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
                assert(false);
        }
    }

    private void readWriters() {
        final JSONArray writers = jsonObject.getJSONArray("writers");

        for (int i=0; i < writers.length(); i++) {
            handleReaderWriter(writers.getJSONObject(i));
        }
    }

    private void handleReaderWriter(final JSONObject node) {
        final String t = node.keys().next();
        if ("Source".equals(node.getJSONArray("ends").getJSONObject(0).getString("type"))) {
            writers.add(node.getJSONArray("ends").getJSONObject(0).getString("name"));
        } else {
            readers.add(node.getJSONArray("ends").getJSONObject(0).getString("name"));
        }
    }

    private void readReaders() {
        final JSONArray readers = jsonObject.getJSONArray("readers");

        for (int i=0; i < readers.length(); i++) {
            handleReaderWriter(readers.getJSONObject(i));
        }
    }

    private void readConnections() {
        final JSONArray temp = jsonObject.getJSONArray("connections");
        for (int i=0; i < temp.length(); i++) {
            JSONObject connection = temp.getJSONObject(i);
            String from = connection.getString("one");
            String to = connection.getString("two");
            connections.put(from, to);
            connections.put(to, from);
        }
    }

    public void read(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        readWriters();
        readReaders();
        readConnections();
    }

    public List<String> getWriters() {
        return writers;
    }
    public List<String> getReaders() {
        return readers;
    }

    public Map<String, String> getConnections() {
        return connections;
    }
}
