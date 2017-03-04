package priority.init;

import java.util.List;
import java.util.Optional;

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
		Optional<Boolean> optTrue = Optional.of(Boolean.TRUE);
		StateVariableValue svv = new StateVariableValue("a1b1ring", optTrue);
		StateValue sv = new StateValue();
		sv.add(svv);
		IOAwareStateValue initState = new IOAwareStateValue(sv, null/*new IOComponent("w1", 10000000)*/);
		ExampleMaker exampleMaker = new ExampleMaker(-80);
		ConstraintConnector cc = exampleMaker.getExample(initState);
		List<IOAwareSolution> solutions = new Solver(cc, initState).solve(-1);
        Drawer d = new Drawer(solutions);
        d.draw();
        d.toGoJS();
	}
}