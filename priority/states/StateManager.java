package priority.states;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import priority.primitives.FIFO;
import priority.solving.Containable;
import priority.solving.Solution;

public class StateManager implements Containable {
	public Map<String, Boolean> currentStates(List<FIFO> fifos) {
		Map<String, Boolean> currentStatesValues = new HashMap<>();
		for (FIFO fifo : fifos)
			currentStatesValues.put(fifo.memory(), fifo.full());
		return currentStatesValues;
	}

	public List<StateValue> findNextStates(List<Solution> solutions) {
		List<StateValue> nexts = new ArrayList<>();
		solutions.forEach(sol -> {
			System.out.println("find next states for solution " + sol);
			StateValue aNextState = sol.getNextStateValue();
			System.out.println("a next state is " + aNextState.toString());
			//boolean add = true;
			//for (StateValue s : nexts) {
			//	if (!s.getVariableValues().equals(sol.getVariableValues()))//??
				//	add = false;
			//}
			if (!contains(nexts, aNextState))
				nexts.add(aNextState);
		});
		return nexts;
	}
}
