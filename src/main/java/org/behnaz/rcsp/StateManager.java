package org.behnaz.rcsp;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

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
	public List<IOAwareStateValue> findNextStates(@NonNull final List<IOAwareSolution> solutions,
												  @NonNull final List<IOAwareStateValue> visitedStates,
												  @NonNull final List<IOAwareStateValue> explorableStates) {
		final List<IOAwareStateValue> nexts = new ArrayList<>();
		solutions.forEach(sol -> {
			Starter.log("find next states for solution " + sol);
			final StateValue aNextState = sol.getSolution().getNextStateValue();//TODO??
			Starter.log("a next state is " + aNextState.toString());
			final IOAwareStateValue temp = new IOAwareStateValue(aNextState, sol.getPostIOs());//Pre or post rename properly TODO

			if (!nexts.contains(temp) && !visitedStates.contains(temp) && !explorableStates.contains(temp)) {
				nexts.add(temp);
			}
		});
		return nexts;
	}
}
