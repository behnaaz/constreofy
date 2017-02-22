package priority.init;

import java.util.List;

import priority.connector.ConstraintConnector;
import priority.draw.Drawer;
import priority.solving.IOAwareSolution;
import priority.solving.IOAwareStateValue;
import priority.solving.IOComponent;
import priority.solving.Solver;
import priority.states.StateValue;

public class Starter {
	private Starter() {
	}
	
	public static void main(String[] args) throws Exception {
		IOAwareStateValue initState = new IOAwareStateValue(new StateValue(), new IOComponent("w1", 1));
		ExampleMaker exampleMaker = new ExampleMaker(4);
		ConstraintConnector cc = exampleMaker.getExample(initState);
		List<IOAwareSolution> solutions = new Solver(cc, initState).solve(-1);
        Drawer d = new Drawer(solutions);
        d.draw();
        d.toGoJS();
	}
}