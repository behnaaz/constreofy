package org.behnaz.rcsp.model;

import javafx.util.Pair;

import java.util.Set;

public class MergerNode extends Node {
    public MergerNode(final Pair<String, Pair<Set<String>, Set<String>>> pair) {
        if (pair.getValue().getValue().size() != 1) {
            throw new RuntimeException(pair.getKey() + " Replicate has wrong sink number");
        }

        if (pair.getValue().getKey().isEmpty()) {
            throw new RuntimeException(pair.getKey() + " Replicate has wrong sink number");
        }

        this.pair = pair;
    }

    public String getName() {
        return pair.getKey();
    }

    public String getSinkEnd(){
        return pair.getValue().getValue().iterator().next();
    }

    public Set<String> getSourceEnds(){
        return pair.getValue().getKey();
    }

    public boolean ownsEnd(final String name) {
        if (getSinkEnd().equals(name)) {
            return true;
        }

        return getSourceEnds().stream().filter(e -> e.equals(name)).findAny().isPresent();
    }
}
