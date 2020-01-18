package org.behnaz.rcsp;

import org.behnaz.rcsp.model.util.VariableNamer;

public class Primitive {
	public String flow(String node) {
		return VariableNamer.flow(node);
	}

	public String memory(String p1, String p2) {
		return VariableNamer.memory(p1, p2);
	}

	public String nextMemory(String p1, String p2) {
		return VariableNamer.nextMemory(p1, p2);
	}
}
