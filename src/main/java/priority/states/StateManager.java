package priority.states;

import java.util.ArrayList;
import java.util.List;

import priority.solving.IOAwareSolution;
import priority.solving.IOAwareStateValue;

/**
 * 
 * @author behnaz.changizi
 *
 */
public class StateManager {
	/***
	 * Return next states reachable from the current state
	 * @param solutions
	 * @param visitedStates
	 * @param explorableStates
	 * @return
	 */
	public List<IOAwareStateValue> findNextStates(final List<IOAwareSolution> solutions,
												  final List<IOAwareStateValue> visitedStates,
												  final List<IOAwareStateValue> explorableStates) {
		final List<IOAwareStateValue> nexts = new ArrayList<>();
		solutions.forEach(sol -> {
			System.out.println("find next states for solution " + sol);
			final StateValue aNextState = sol.getSolution().getNextStateValue();//TODO??
			System.out.println("a next state is " + aNextState.toString());
			final IOAwareStateValue temp = new IOAwareStateValue(aNextState, sol.getPostIOs());//Pre or post rename properly TODO
			if (!nexts.contains(temp) && !visitedStates.contains(temp) && !explorableStates.contains(temp)) {
				nexts.add(temp);
			}
		});
		return nexts;
	}
}
