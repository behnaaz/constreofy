package priority.init;

import java.util.List;
import java.util.Optional;

import priority.common.Constants;
import priority.connector.ConstraintConnector;
import priority.draw.Drawer;
import priority.solving.IOAwareSolution;
import priority.solving.IOAwareStateValue;
import priority.solving.Solver;
import priority.states.StateValue;
import priority.states.StateVariableValue;

public class Starter {
	private Starter() {
	}
	
	public static void main(String[] args) throws Exception {
		StateValue initStateValue = makeInitState();
		IOAwareStateValue initState = new IOAwareStateValue(initStateValue, null/*new IOComponent("w1", 10000000)*/);
		ExampleMaker exampleMaker = new ExampleMaker(-80);
		ConstraintConnector cc = exampleMaker.getExample(initState);
		List<IOAwareSolution> solutions = new Solver(cc, initState).solve(-1);
		System.out.println("Use new method?" + Constants.USE_EQUAL_SET_ON);
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