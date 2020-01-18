package org.behnaz.rcsp.model;

import javafx.util.Pair;

import java.util.Set;

public class RouteNode extends Node {
    public RouteNode(final Pair<String, Pair<Set<String>, Set<String>>> p) {
        if (p.getValue().getKey().size() != 1) {
            throw new RuntimeException(p.getKey() + " Replicate has wrong source number");
        }

        if (p.getValue().getKey().isEmpty()) {
            throw new RuntimeException(p.getKey() + " Replicate has wrong sink number");
        }
        pair = p;
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
