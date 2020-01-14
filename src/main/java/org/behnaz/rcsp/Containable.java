package org.behnaz.rcsp;

import java.util.Arrays;
import java.util.List;

public interface Containable {

	default boolean contains(List<IOAwareStateValue> states, IOAwareStateValue state) {
		if (state == null || states == null)
			return false;
		for (IOAwareStateValue t : states) {
			if (Arrays.equals(t.getIOs(), state.getIOs()) &&
					t.getStateValue().getVariableValues().toString().trim().equals(state.getStateValue().getVariableValues().toString().trim())) {
				Starter.log("Visited b4: " + state.toString() + " in " + states.toString());
				return true;
			}
		}
		return false;
	}

	default boolean contains(List<IOAwareSolution> sols, IOAwareSolution s2) {
		for (IOAwareSolution sol : sols) {
			Solution t = sol.getSolution();
			Solution s = s2.getSolution();
			if (t.getFlowVariables().equals(s.getFlowVariables()) && t.getFromVariables().equals(s.getFromVariables()) && t.getToVariables().equals(s.getToVariables()))
				return Arrays.equals(sol.getPreIOs(), s2.getPreIOs()) &&
						Arrays.equals(sol.getPostIOs(), s2.getPostIOs());
		}
		return false;
	}
}
