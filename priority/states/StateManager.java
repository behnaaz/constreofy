package priority.states;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import priority.primitives.FIFO;
import priority.solving.Containable;
import priority.solving.IOAwareSolution;
import priority.solving.IOAwareStateValue;

public class StateManager implements Containable {
	public Map<String, Boolean> currentStates(List<FIFO> fifos) {
		Map<String, Boolean> currentStatesValues = new HashMap<>();
		for (FIFO fifo : fifos)
			currentStatesValues.put(fifo.memory(), fifo.full());
		return currentStatesValues;
	}

	public List<IOAwareStateValue> findNextStates(List<IOAwareSolution> solutions, List<IOAwareStateValue> visitedStates,
			List<IOAwareStateValue> explorableStates) {
		List<IOAwareStateValue> nexts = new ArrayList<>();
		solutions.forEach(sol -> {
			System.out.println("find next states for solution " + sol);
			StateValue aNextState = sol.getSolution().getNextStateValue();//TODO??
			System.out.println("a next state is " + aNextState.toString());
			IOAwareStateValue temp = new IOAwareStateValue(aNextState, sol.getIOs());
			if (!contains(nexts, temp) && !contains(visitedStates, temp) && !contains(explorableStates, temp)) {
				System.out.println("OOOO Adding  " + temp.toString());
				nexts.add(temp);
			}
		});
		return nexts;
	}
}
