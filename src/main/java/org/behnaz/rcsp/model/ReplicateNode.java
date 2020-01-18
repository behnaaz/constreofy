package org.behnaz.rcsp.model;

import javafx.util.Pair;

import java.util.Set;

public class ReplicateNode extends Node {
    public ReplicateNode(final Pair<String, Pair<Set<String>, Set<String>>> pair) {
        if (pair.getValue().getKey().size() != 1) {
            throw new RuntimeException(pair.getKey() + " Replicate has wrong source number");
        }

        if (pair.getValue().getKey().isEmpty()) {
            throw new RuntimeException(pair.getKey() + " Replicate has wrong sink number");
        }

        this.pair = pair;
    }

    public String getSourceEnd(){
        return pair.getValue().getKey().iterator().next();
    }

    public Set<String> getSinkEnds(){
        return pair.getValue().getValue();
    }

    @Override
    public boolean ownsEnd(final String name) {
        if (getSourceEnd().equals(name)) {
            return true;
        }

        return getSinkEnds().stream().filter(e -> e.equals(name)).findAny().isPresent();
    }

    @Override
    public String getConstraint() {
        return "";
    }
}
