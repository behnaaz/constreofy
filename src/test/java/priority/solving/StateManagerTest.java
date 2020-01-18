package priority.solving;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.behnaz.rcsp.*;
import org.junit.Before;
import org.junit.Test;


public class StateManagerTest {
	private final StateManager mgr = new StateManager();
	private List<IOAwareSolution> solutions;
	private Solver solver;
	
	@Before
	public void setUp() throws Exception {
		final IOComponent ioComponent = new IOComponent("a", 1);
		StateValue stateValue = StateValue.builder().build();
		IOAwareStateValue currentStatesValue = new IOAwareStateValue(stateValue, ioComponent);
		ConstraintConnector cc = new ExampleMaker(1).getExample(currentStatesValue);
		solver = Solver.builder().connectorConstraint(cc).initState(currentStatesValue).build();
		solutions = solver.doSolve(currentStatesValue, cc.getConstraint());
	}

	@Test
	public void testNextStates() {
		List<IOAwareStateValue> next = mgr.findNextStates(solutions, new ArrayList<>(), new ArrayList<>());
		assertEquals(5, next.size());
	}
}
