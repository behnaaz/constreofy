package org.behnaz.rcsp.model;

import javafx.util.Pair;
import org.behnaz.rcsp.model.util.VariableNamer;

import static org.behnaz.rcsp.AbstractConnector.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class RouteNode extends Node {
    public RouteNode(final String name, final String source, final Set<String> sinks) {
        this(new Pair<>(name, new Pair<>(new HashSet<>(Collections.singleton(source)), sinks)));
    }

    public RouteNode(final Pair<String, Pair<Set<String>, Set<String>>> pair) {
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
        return "(" + VariableNamer.flow(getSourceEnd()) + RIGHTLEFTARROW  + "(" + getSinkEnds().stream().map(VariableNamer::flow).collect(Collectors.joining(OR)) + "))" +
                    AND +
                "(" + NOT + "(" + getSinkEnds().stream().map(VariableNamer::flow).collect(Collectors.joining(AND)) + "))";
    }
}
