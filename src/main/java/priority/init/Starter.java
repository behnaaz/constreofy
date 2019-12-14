package priority.init;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import priority.connector.ConstraintConnector;
import priority.draw.Drawer;
import priority.solving.IOAwareSolution;
import priority.solving.IOAwareStateValue;
import priority.solving.Solver;
import priority.states.StateValue;
import priority.states.StateVariableValue;

@Slf4j
public class Starter {
	private Starter() {
	}
	
	public static void main(String[] args) {
	//	if (args.length == 0) {
	//		log.error("Please provide the path to reduce program i.e. /mydrive/reduce/trunk/bin/redpsl");
	//	}
		final StateValue initStateValue = makeInitState("a1b1ring");
		final IOAwareStateValue initState = new IOAwareStateValue(initStateValue, null/*new IOComponent("w1", 10000000)*/);
		final ExampleMaker exampleMaker = new ExampleMaker(-80);
		final ConstraintConnector cc = exampleMaker.getExample(initState);
		List<IOAwareSolution> solutions = null;
		try {
			solutions = Solver.builder()
					.connectorConstraint(cc)
					.initState(initState)
					.reduceProgram("/media/c/projects/reotool/reduce-algebra-code-r5207-trunk/bin")
					.build()
					.solve(-1);
		} catch (IOException e) {
			log.error("Failed to load Reduce {0} \r\n {1}", "/media/c/projects/reotool/reduce-algebra-code-r5207-trunk/bin", e.getMessage());
			return;
		}
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