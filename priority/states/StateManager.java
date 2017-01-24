package priority.states;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	public List<StateValue> extractStateValues(Set<Solution> solutions) {
		List<StateValue> nexts = new ArrayList<>();
		solutions.forEach(sol -> {
			System.out.println(sol.nextStateValuess().toString());
			StateValue t = sol.nextStateValuess().makeItCurrent();//????
			if (!nexts.contains(t))//??
				nexts.add(t);
		});
		return nexts;
	}
}
