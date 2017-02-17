package priority.solving;

import java.util.Arrays;
import java.util.List;

public interface Containable {

	default boolean contains(List<IOAwareStateValue> states, IOAwareStateValue state) {
		if (state.toString().trim().contains("de1de2ringtrue,ij1ij2ringtrue,jk1jk2ringtrue"))
			System.out.println("here" + state);
		for (IOAwareStateValue t : states) {
			if (t.getIOs().equals(state.getIOs()) &&
					t.getStateValue().getVariableValues().toString().trim().equals(state.getStateValue().getVariableValues().toString().trim())) {
				System.out.println("Visited b4: " + state.toString() + " in " + state.toString());
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
				return Arrays.equals(sol.getIOs(), s2.getIOs());
		}
		return false;
	}
}
