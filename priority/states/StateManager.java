package priority.states;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import priority.primitives.FIFO;
import priority.solving.Solution;

public class StateManager {
	public Map<String, Boolean> currentStates(List<FIFO> fifos) {
		Map<String, Boolean> currentStatesValues = new HashMap<>();
		for (FIFO fifo : fifos) {
			currentStatesValues.put(fifo.memory(), fifo.full());
		}
		return currentStatesValues;
	}

	public Set<StateValue> makeItCurrent(Set<StateValue> list) {
		Set<StateValue> temp = new TreeSet<>();//TODO compare
		list.forEach(s -> temp.add(new StateValue(makeNextStateCurrent(s), s.value())));
		return temp;
	}

	private String makeNextStateCurrent(StateValue s) {
		return s.stateName().toLowerCase().replace("xring", "ring");//TODO constant
	}
	

	public List<Set<StateValue>> extractStateValues(Set<Solution> solutions) {
		List<Set<StateValue>> nexts = new ArrayList<>();
		for (Solution sol : solutions) {
			System.out.println(sol.nextStateValuess().toString());
			Set<StateValue> t = makeItCurrent(sol.nextStateValuess());
			if (!nexts.contains(t))//??
				nexts.add(t);
		}
		return nexts;
	}
}
