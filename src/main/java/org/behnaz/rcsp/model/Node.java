package org.behnaz.rcsp.model;

import javafx.util.Pair;

import java.util.Set;

public class Node {
    Pair<String, Pair<Set<String>, Set<String>>> pair = null;

    public String getName() {
        return pair.getKey();
    }

    public boolean ownsEnd(final String name) {
        throw new RuntimeException("Not implemented");
    }

    public String getConstraint() {
        throw new RuntimeException("Not implemented");
    }
}
