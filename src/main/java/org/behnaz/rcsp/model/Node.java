package org.behnaz.rcsp.model;

import javafx.util.Pair;

import java.util.Set;

public class Node {
    Pair<String, Pair<Set<String>, Set<String>>> pair = null;

    public String getName() {
        return pair.getKey();
    }
}
