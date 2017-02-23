package priority.states;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import priority.primitives.FIFO;
import priority.solving.Containable;
import priority.solving.IOAwareSolution;
import priority.solving.IOAwareStateValue;

/**
 * 
 * @author behnaz.changizi
 *
 */
public class StateManager implements Containable {
	/**
	 * Returns the current state
	 * @param fifos
	 * @return
	 */
	public Map<String, Boolean> getCurrentStates(final List<FIFO> fifos) {
		final Map<String, Boolean> currents = new ConcurrentHashMap<>();
		for (final FIFO fifo : fifos) {
			if (fifo.isFull().isPresent()) {
				boolean full = fifo.isFull().get();
				currents.put(fifo.getMemory(), full);
			}
		}
		return currents;
	}

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
			if (!contains(nexts, temp) && !contains(visitedStates, temp) && !contains(explorableStates, temp)) {
				System.out.println("OOOO Adding  " + temp.toString());
				nexts.add(temp);
			}
		});
		return nexts;
	}
}
