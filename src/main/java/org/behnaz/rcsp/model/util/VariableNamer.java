package org.behnaz.rcsp.model.util;

import static priority.Variable.CURRENT_MEMORY;
import static priority.Variable.NEXT_MEMORY;
import static priority.Variable.TILDE;

public interface VariableNamer {
    static String flow(final String node) {
        return new StringBuilder().append(node).append(TILDE).toString();
    }

    static String memory(final String p1, final String p2) {
        return new StringBuilder().append(p1).append(p2).append(CURRENT_MEMORY).toString();
    }

    static String nextMemory(final String p1, final String p2) {
        return new StringBuilder().append(p1).append(p2).append(NEXT_MEMORY).toString();
    }
}
