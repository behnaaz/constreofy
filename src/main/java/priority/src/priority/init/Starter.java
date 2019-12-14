package priority.src.priority.init;

import java.util.Collections;
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
		final StateValue initStateValue = makeInitState("a1b1ring");
		final IOAwareStateValue initState = new IOAwareStateValue(initStateValue, null/*new IOComponent("w1", 10000000)*/);
		final ExampleMaker exampleMaker = new ExampleMaker(-80);
		final ConstraintConnector cc = exampleMaker.getExample(initState);
		final List<IOAwareSolution> solutions = new Solver(cc, initState).solve(-1);
		System.out.println("Use new method?" + Solver.USE_EQUAL_SET_ON);
        final Drawer d = new Drawer(solutions);
        d.draw();
        d.toGoJS();
	}

	private static StateValue makeInitState(final String stateName) {
		return StateValue.builder()
				.variableValues(
						Collections.singleton(
								StateVariableValue.builder()
										.stateName(stateName)
										.value(Optional.of(Boolean.TRUE))
										.build()
						)
				)
				.build();
	}
}