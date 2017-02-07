package test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import priority.init.ExampleMaker;
import priority.solving.Solution;
import priority.solving.Solver;
import priority.states.StateManager;
import priority.states.StateValue;

public class StateManagerTest {
	StateManager mgr = new StateManager();
	List<Solution> solutions;
	Solver solver;
	
	@Before
	public void setUp() throws Exception {
		solver = new Solver();
		solutions = solver.findSolutions(1, new StateValue(), new ExampleMaker(3));
	}

	@Test
	public void testNextStates() {
		List<StateValue> next = mgr.findNextStates(solutions);
		assertEquals(4, next.size());
	}
}
