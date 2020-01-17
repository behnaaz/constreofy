package org.behnaz.rcsp.input;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JSONNetworkReader {
    private final List<String> readers = new ArrayList<>();
    private final List<String> writers = new ArrayList<>();
    private Map<String, String> connections = new HashMap<>();
    private JSONObject jsonObject;

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
