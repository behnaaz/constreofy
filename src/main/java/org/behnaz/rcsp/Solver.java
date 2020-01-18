package org.behnaz.rcsp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import priority.PropertyReader;

@Builder
public class Solver implements Containable {
	public 	static final boolean USE_EQUAL_SET_ON = true;
	public static final String FORMULA_NAME = "qaz";
	public static final String FORMULA_NAME_EQUAL = "qaz :=";
	public static final String SHUT = "shut";

	private ConstraintConnector connectorConstraint;
	private IOAwareStateValue initState;
	@Builder.Default
	private String reduceProgram = PropertyReader.reduceProgram();

	public List<IOAwareSolution> solve(int maxLimit) throws IOException {
		List<IOAwareStateValue> visitedStates = new ArrayList<>();
		List<IOAwareStateValue> explorableStates = new ArrayList<>();
		//TODO convert to treemap and fix contains ad delete issues
		int n = 0;
		IOAwareStateValue currentStatesValue = initState;
		final StateManager stateManager = new StateManager();
		List<IOAwareSolution> solutions = new ArrayList<>();
		long startTime0 = System.nanoTime();

		do {
			long startTime1 = System.nanoTime();

			visit(visitedStates, currentStatesValue);
			// Get solutions from current state
			List<IOAwareSolution> foundSolutions = doSolve(currentStatesValue, connectorConstraint);
			addToSolutions(solutions, foundSolutions);

			addToExplorableStates(visitedStates, explorableStates, stateManager, solutions);
			currentStatesValue = getNextUnexploredState(visitedStates, explorableStates);
			if (currentStatesValue != null)
				Starter.log("Step " + ++n + " from " + currentStatesValue.toString());
			
			
			long endTime1 = System.nanoTime();

			long duration = endTime1 - startTime1;
			Starter.log("One solution took in miliseconds: " + duration/1000000);
			
		} while (currentStatesValue != null && (maxLimit < 0 || n < maxLimit));
		long endTime0 = System.nanoTime();

		long duration = endTime0 - startTime0;
		Starter.log("whole solutions took in miliseconds: " + duration/1000000 + " #solutions: " 
		+ solutions.size() + " #cons len:" + connectorConstraint.getConstraint().length());
		Starter.log(".....done in step " + n);
		return solutions;
	}

	public void addToExplorableStates(List<IOAwareStateValue> visitedStates, List<IOAwareStateValue> explorableStates,
			StateManager stateManager, List<IOAwareSolution> solutions) {
	//Starter.log("B4 Updated explorable states: " + explorableStates.size() + " " + explorableStates.toString());
		List<IOAwareStateValue> nexts = stateManager.findNextStates(solutions, visitedStates, explorableStates);
		for (IOAwareStateValue state : nexts) {
		//	Starter.log("  " + state.toString() + " exporable  ");
			explorableStates.add(state);
		}
		//Starter.log("Updated explorable states: " + explorableStates.size() + " " + explorableStates.toString());
	}

	private void visit(@NonNull final List<IOAwareStateValue> visitedStates, @NonNull final IOAwareStateValue currentStatesValues) {
		//Starter.log("B4 visit states: " + visitedStates.size() + " " + visitedStates.toString());
		if (!contains(visitedStates, currentStatesValues))
			visitedStates.add(new IOAwareStateValue(currentStatesValues.getStateValue(), currentStatesValues.getIOs()));
		//Starter.log("After visit states: " + visitedStates.size() + " " + visitedStates.toString());
	}

	private void addToSolutions(@NonNull final List<IOAwareSolution> solutions, @NonNull final List<IOAwareSolution> stepSolutions) {
		//	IOAwareSolution temp = new IOAwareSolution(s.getSolution(), /*updateRequests(s.getSolution(),*/ s.getPreIOs());
		//if (!contains(solutions, temp)) {
		//Starter.log("Solution added "+temp.toString());
		//temp);
		//}
		solutions.addAll(stepSolutions);
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

	 public List<IOAwareSolution> doSolve(final IOAwareStateValue currentStatesValue, final ConstraintConnector cc) throws IOException {
		if (StringUtils.isBlank(reduceProgram)) {
			throw new RuntimeException("Reduce path not provided");
		}
		Starter.log("Solving the constraint using " + reduceProgram);
		final List<String> reduceOutput = executeReduce(cc.buildConstraint(currentStatesValue.getStateValue()));
		final String strReduceOutput = getOnlyAnswer(reduceOutput);
		final DNF dnf = new DNF(new ArrayList<>(cc.getVariables()));
		final List<Solution> solutions = dnf.extractSolutions(strReduceOutput);
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

	private List<String> executeReduce(final String constraints) throws IOException {
		Starter.log("Loading Reduce from " + reduceProgram);
		final Process process = Runtime.getRuntime().exec(reduceProgram);
		Starter.log("Reduce loaded ? " + process.isAlive());
		final OutputStream stdin = process.getOutputStream();
		Starter.log("Constraints to be solved: " + constraints);

		stdin.write(constraints.getBytes());
		stdin.flush();
		stdin.close();

		final List<String> output = new ArrayList<>();

		try (BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			String line;
			while ((line = out.readLine()) != null) {
				Starter.log("....solution line " + line);
				output.add(line);
			}
		}
		return output;
	}
}