package priority.states;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import priority.ExampleMaker;
import priority.connector.ConstraintConnector;
import priority.solving.IOAwareSolution;
import priority.solving.IOAwareStateValue;
import priority.solving.IOComponent;
import priority.solving.Solver;


public class StateManagerTest {
	private final StateManager mgr = new StateManager();
	private List<IOAwareSolution> solutions;
	private Solver solver;
	
	@Before
	public void setUp() throws Exception {
		final IOComponent ioComponent = new IOComponent("a", 1);
		StateValue stateValue = StateValue.builder().build();
		IOAwareStateValue currentStatesValue = new IOAwareStateValue(stateValue, ioComponent);
		ConstraintConnector cc = new ExampleMaker(2).getExample(currentStatesValue);
		solver = Solver.builder().connectorConstraint(cc).initState(currentStatesValue).build();
		solutions = solver.doSolve(currentStatesValue, cc);
	}

	@Test
	public void testNextStates() {
		List<IOAwareStateValue> next = mgr.findNextStates(solutions, new ArrayList<>(), new ArrayList<>());
		assertEquals(4, next.size());
	}
}
