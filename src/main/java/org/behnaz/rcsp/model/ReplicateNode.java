package org.behnaz.rcsp.model;

import org.javatuples.Pair;

import java.util.Set;

public class ReplicateNode extends Node {
    public ReplicateNode(final Pair<String, Pair<Set<String>, Set<String>>> pair) {
        if (pair.getValue1().getValue0().size() != 1) {
            throw new RuntimeException(pair.getValue0() + " Replicate has wrong source number");
        }

        if (pair.getValue1().getValue0().isEmpty()) {
            throw new RuntimeException(pair.getValue0() + " Replicate has wrong sink number");
        }

        this.pair = pair;
    }

    public String getSourceEnd(){
        return pair.getValue1().getValue0().iterator().next();
    }

    public Set<String> getSinkEnds(){
        return pair.getValue1().getValue1();
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
        throw new RuntimeException("Not implemented");
    }
}
