package priority.states;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import priority.ExampleMaker;
import priority.connector.ConstraintConnector;
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
		IOComponent ioComponent = new IOComponent("a", 1);
		StateValue stateValue = new StateValue();
		IOAwareStateValue currentStatesValue = new IOAwareStateValue(stateValue, ioComponent);
		ConstraintConnector cc = new ExampleMaker(3).getExample(currentStatesValue);
		solver = new Solver(cc, currentStatesValue);
		solutions = solver.doSolve(currentStatesValue, cc);
	}

	@Test
	public void testNextStates() {
		List<IOAwareStateValue> next = mgr.findNextStates(solutions, null, null);//???
		assertEquals(4, next.size());
	}
}
