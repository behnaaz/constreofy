package test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import priority.init.ExampleMaker;
import priority.solving.IOAwareSolution;
import priority.solving.IOAwareStateValue;
import priority.solving.IOComponent;
import priority.solving.Solver;
import priority.states.StateManager;
import priority.states.StateValue;

public class StateManagerTest {
	StateManager mgr = new StateManager();
	List<IOAwareSolution> solutions;
	Solver solver;
	
	@Before
	public void setUp() throws Exception {
		solver = new Solver();
		solutions = solver.findSolutions(new IOAwareStateValue(new StateValue(), new IOComponent("a", 1)), new ExampleMaker(3));
	}

	@Test
	public void testNextStates() {
		List<IOAwareStateValue> next = mgr.findNextStates(solutions, null, null);//???
		assertEquals(4, next.size());
	}
}
