package priority.states;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import priority.primitives.FIFO;
import priority.primitives.Primitive;
import priority.solving.Containable;
import priority.solving.IOAwareSolution;
import priority.solving.IOAwareStateValue;
import priority.solving.IOComponent;

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
			IOComponent[] updatedIOs = updateRequests(sol);
			IOAwareStateValue temp = new IOAwareStateValue(aNextState, updatedIOs);
			if (!contains(nexts, temp) && !contains(visitedStates, temp) && !contains(explorableStates, temp)) {
				nexts.add(temp);
			}
		});
		return nexts;
	}
private IOComponent[] updateRequests(IOAwareSolution sol) {
	IOComponent[] result = sol.getIOs();
	Set<String> flowVariables = sol.getSolution().getFlowVariables();
	if (flowVariables == null || flowVariables.isEmpty())
		return result;

	for (IOComponent io : result) {
		Primitive p = new Primitive();
		String flowNode = p.flow(io.getNodeName());
		if (flowVariables.contains(flowNode)) {
			io.consume();//TODO
		}
	}
	return result;
	}
}
