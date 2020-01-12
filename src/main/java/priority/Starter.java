package priority;

import java.io.IOException;
import java.util.Collections;
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
	
	public static void main(String[] args) {
		final StateValue initStateValue = makeInitState("a1b1ring");
		final IOAwareStateValue initState = new IOAwareStateValue(initStateValue, null/*new IOComponent("w1", 10000000)*/);
		final ExampleMaker exampleMaker = new ExampleMaker(1);
		final ConstraintConnector cc = exampleMaker.getExample(initState);
		List<IOAwareSolution> solutions = null;
		try {
			solutions = Solver.builder()
					.connectorConstraint(cc)
					.initState(initState)
					.build()
					.solve(-1);
		} catch (IOException e) {
			log("Solving constraints with reduce failed " +e.getMessage());
			return;
		}
		System.out.println("Use new method?" + Solver.USE_EQUAL_SET_ON);
      	//  final Drawer d = new Drawer(solutions);
		//  d.draw();
        //	d.toGoJS();
	}

	public static void log(String s) {
		if (PropertyReader.debug()) {
			System.out.println(s);
		}
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