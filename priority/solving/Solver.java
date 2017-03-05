package priority.solving;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.Lists;

import priority.common.Constants;
import priority.connector.ConstraintConnector;
import priority.semantics.DNF;
import priority.states.StateManager;
import priority.states.StateValue;

public class Solver implements Constants, Containable {
	private static final String REDUCE_PROGRAM = "/Users/behnaz.changizi/Desktop/reduce/trunk/bin/redpsl";
	private ConstraintConnector connectorConstraint;
	private IOAwareStateValue initState;

	public Solver(ConstraintConnector cc, IOAwareStateValue initState) throws IOException {
		this.connectorConstraint = cc;
		this.initState = initState;
	}

	public List<IOAwareSolution> solve(int maxLimit) throws Exception {
		List<IOAwareStateValue> visitedStates = new ArrayList<>();
		List<IOAwareStateValue> explorableStates = new ArrayList<>();
		//TODO convert to treemap and fix contains ad delete issues
		int n = 0;
		IOAwareStateValue currentStatesValue = initState;
		StateManager stateManager = new StateManager();
		List<IOAwareSolution> solutions = new ArrayList<>();

		do {
			visitedStates = visit(visitedStates, currentStatesValue);
			// Get solutions from current state
			List<IOAwareSolution> foundSolutions = doSolve(currentStatesValue, connectorConstraint);
			solutions = addToSolutions(solutions, foundSolutions);

			explorableStates = addToExplorableStates(visitedStates, explorableStates, stateManager, solutions);
			currentStatesValue = getNextUnexploredState(visitedStates, explorableStates);
			if (currentStatesValue != null)
				System.out.println("Step " + ++n + " from " + currentStatesValue.toString());
		} while (currentStatesValue != null && (maxLimit < 0 || n < maxLimit));
		System.out.println(".....done in step " + n);
		return solutions;
	}

	public List<IOAwareStateValue> addToExplorableStates(List<IOAwareStateValue> visitedStates, List<IOAwareStateValue> explorableStates,
			StateManager stateManager, List<IOAwareSolution> solutions) {
		//if (debug)
//	System.out.println("B4 Updated explorable states: " + explorableStates.size() + " " + explorableStates.toString());
		List<IOAwareStateValue> nexts = stateManager.findNextStates(solutions, visitedStates, explorableStates);
		for (IOAwareStateValue state : nexts) {
	//		System.out.println("  " + state.toString() + " exporable  ");
			explorableStates.add(state);
		}
		System.out.println("Updated explorable states: " + explorableStates.size() + " " + explorableStates.toString());
		return explorableStates;
	}

	private List<IOAwareStateValue> visit(final List<IOAwareStateValue> visitedStates, final IOAwareStateValue currentStatesValues) {
		//System.out.println("B4 visit states: " + visitedStates.size() + " " + visitedStates.toString());
		if (!contains(visitedStates, currentStatesValues))
			visitedStates.add(new IOAwareStateValue(currentStatesValues.getStateValue(), currentStatesValues.getIOs()));
		//System.out.println("After visit states: " + visitedStates.size() + " " + visitedStates.toString());
		return visitedStates;
	}

	private List<IOAwareSolution> addToSolutions(final List<IOAwareSolution> solutions, final List<IOAwareSolution> stepSolutions) {
		for (IOAwareSolution s : stepSolutions)
		{
		//	IOAwareSolution temp = new IOAwareSolution(s.getSolution(), /*updateRequests(s.getSolution(),*/ s.getPreIOs());
			//if (!contains(solutions, temp)) {
				//System.out.println("Solution added "+temp.toString());
				solutions.add(s);//temp);
			//}
		}
		return solutions;
	}

	private IOAwareStateValue getNextUnexploredState(List<IOAwareStateValue> visitedStates, List<IOAwareStateValue> explorableStates) {
		IOAwareStateValue currentStatesValues = null;
		if (explorableStates.isEmpty())
			return currentStatesValues;
		do{
		if (!explorableStates.isEmpty()) {
			currentStatesValues = explorableStates.remove(0);
			//???
		//////	currentStatesValues.setIOs(writer1);
			if (contains(visitedStates, currentStatesValues))
				currentStatesValues = null;
		}}while(currentStatesValues == null && !explorableStates.isEmpty());
		
		return currentStatesValues;
	}

	public List<IOAwareSolution> doSolve(IOAwareStateValue currentStatesValue, ConstraintConnector cc) throws Exception {
		List<String> reduceOutput = executeReduce(cc, currentStatesValue.getStateValue());
		String strReduceOutput = getOnlyAnswer(reduceOutput);
		DNF dnf = new DNF(Lists.newArrayList(cc.getVariables()));
		List<Solution> solutions = dnf.extractSolutions(strReduceOutput);
		return ioAwarify(solutions, currentStatesValue.getIOs());
	}

	private String getOnlyAnswer(List<String> reduceOutput) {
		int formulaStart = -1;
		int resultStart = -1;
		int resultEnd = -1;
		for (int i = 0; i < reduceOutput.size() && resultStart == -1; i++) {
			if (reduceOutput.get(i).contains(FORMULA_NAME_EQUAL))//TODO
				formulaStart = i;
			if (formulaStart > -1 && 
				i + 2 < reduceOutput.size() &&
					isEmpty(reduceOutput.get(i)) && isEmpty(reduceOutput.get(i+1))) {
						resultStart = i + 2;
			}
		}

		for (int j = reduceOutput.size() - 1; j > resultStart && resultEnd == -1; j--) {
			resultEnd = isEndOfResult(reduceOutput.get(j)) ? j : -1;
		}
		
		StringBuilder sb = new StringBuilder();
		if (resultStart > -1 && resultEnd > -1) {
			for (int i = resultStart; i < resultEnd; i++)
				sb.append(reduceOutput.get(i));
		}

		return sb.toString();
	}

	private boolean isEndOfResult(String s) {
		return SHUT.equals(s.trim());
	}
	private boolean isEmpty(String s) {
		String nothing = "";//???
		return nothing.equals(s.trim());
	}

	private List<IOAwareSolution> ioAwarify(List<Solution> solutions, IOComponent[] iOs) {
		List<IOAwareSolution> temp = new ArrayList<>();
		solutions.forEach(s ->
			temp.add(new IOAwareSolution(s, iOs))////???? updateRequests(s, iOs)));
		);
		return temp;
	}

	private List<String> executeReduce(ConstraintConnector cc, StateValue stateValue) throws IOException {
		Process process = Runtime.getRuntime().exec(REDUCE_PROGRAM);
		OutputStream stdin = process.getOutputStream();
		stdin.write(cc.buildConstraint(stateValue).getBytes());
		stdin.flush();
		stdin.close();

		List<String> output = new ArrayList<>();

		BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		try {
			while ((line = out.readLine()) != null) { 
				//if (debug)
					//System.out.println("....solution line " + line);
				output.add(line);
			}
		}
		finally{ 
			out.close();
		}
		return output;
	}
}