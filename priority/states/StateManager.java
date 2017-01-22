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

	public Map<String, Boolean> makeItCurrent(Map<String, Boolean> list) {
		Map<String, Boolean> temp = new HashMap<String, Boolean>();
		for (String s : list.keySet()) {
			temp.put(s.toLowerCase().replace("xring", "ring"), list.get(s));
		}
		return temp;
	}

	public List<Map<String, Boolean>> stateValues(Set<Solution> solutions) {
		List<Map<String, Boolean>> nexts = new ArrayList<Map<String, Boolean>>();
		for (Solution sol : solutions) {
			System.out.println(sol.nextStateValuess().toString());
			Map<String, Boolean> t = makeItCurrent(sol.nextStateValuess());
			if (!nexts.contains(t))
				nexts.add(t);
		}
		return nexts;
	}
}
