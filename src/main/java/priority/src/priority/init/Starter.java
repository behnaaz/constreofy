package priority.src.priority.init;

import java.util.List;
import java.util.Optional;

import priority.src.priority.connector.ConstraintConnector;
import priority.src.priority.draw.Drawer;
import priority.src.priority.solving.IOAwareSolution;
import priority.src.priority.solving.IOAwareStateValue;
import priority.src.priority.solving.Solver;
import priority.src.priority.states.StateValue;
import priority.src.priority.states.StateVariableValue;

public class Starter {
	private Starter() {
	}
	
	public static void main(String[] args) throws Exception {
		StateValue initStateValue = makeInitState();
		IOAwareStateValue initState = new IOAwareStateValue(initStateValue, null/*new IOComponent("w1", 10000000)*/);
		ExampleMaker exampleMaker = new ExampleMaker(-80);
		ConstraintConnector cc = exampleMaker.getExample(initState);
		List<IOAwareSolution> solutions = new Solver(cc, initState).solve(-1);
		System.out.println("Use new method?" + Solver.USE_EQUAL_SET_ON);
        Drawer d = new Drawer(solutions);
        d.draw();
        d.toGoJS();
	}

	private static StateValue makeInitState() {
		StateVariableValue svv = new StateVariableValue("a1b1ring", Optional.of(Boolean.TRUE));
		StateValue sv = new StateValue();
		sv.add(svv);
		return sv;
	}
}