package priority.solving;

import java.util.List;

import priority.states.StateValue;

public interface Containable {

	default boolean contains(List<StateValue> states, StateValue state) {
		if (state.toString().trim().contains("de1de2ringtrue,ij1ij2ringtrue,jk1jk2ringtrue"))
			System.out.println("here" + state);
		for (StateValue t : states) {
			if (t.getVariableValues().toString().trim().equals(state.getVariableValues().toString().trim())) {
				System.out.println("Visited b4: " + state.toString() + " in " + state.toString());
				return true;
			}
		}
		return false;
	}

	default boolean contains(List<Solution> sols, Solution s) {
		for (Solution t : sols)
			if (t.getFlowVariables().equals(s.getFlowVariables()) && t.getFromVariables().equals(s.getFromVariables()) && t.getToVariables().equals(s.getToVariables()))
				return true;
		return false;
	}
}
