package priority.solving;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;

import com.google.common.collect.Lists;

import priority.common.Constants;
import priority.connector.ConstraintConnector;
import priority.init.ExampleMaker;
import priority.semantics.DNF;
import priority.states.StateManager;

public class Solver implements Constants, Containable {
	private static final String REDUCE_PROGRAM = "/Users/behnaz.changizi/Desktop/reduce/trunk/bin/redpsl";
	private String fileName;
	
	void solve(List<String> vars) throws TimeoutException {
		ISolver solver = new ModelIterator(SolverFactory.newMinOneSolver());
		Reader reader = new DimacsReader(solver);
		try {
			IProblem problem = reader.parseInstance(fileName);
			while (problem.isSatisfiable()) {
				int[] model = solver.model();
				printeq(model, true, vars);		
			} 
		} catch (ParseFormatException | IOException | ContradictionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void printeq(int[] model, boolean varName, List<String> vars) {
		for (int i : model)
			//if (i > 0)
				System.out.print((varName ? vars.get(Math.abs(i)-1) : Math.abs(i)) + "=" + (i>0?"T ":"F "));
		System.out.println();
	}
	
	public List<IOAwareSolution> solve(int exampleNo, int maxLimit, IOAwareStateValue initState) throws Exception {
		List<IOAwareStateValue> visitedStates = new ArrayList<>();
		List<IOAwareStateValue> explorableStates = new ArrayList<>();//TODO convert to trrmap and fix cpntains ad delete issues
		int n = 0;
		IOAwareStateValue currentStatesValue = initState;
		ExampleMaker exampleMaker = new ExampleMaker(exampleNo);
		StateManager stateManager = new StateManager();
		List<IOAwareSolution> solutions = new ArrayList<>();

		do {
			visitedStates = visit(visitedStates, currentStatesValue);

			ConstraintConnector cc = exampleMaker.getExample(currentStatesValue);

			// Get solutions from current state
			List<IOAwareSolution> foundSolutions = findSolutions(currentStatesValue, cc);
			solutions = updateSolutions(solutions, foundSolutions);

			explorableStates = updateExplorableStates(visitedStates, explorableStates, stateManager, solutions);
			currentStatesValue = getNextUnexploredState(visitedStates, explorableStates);
			if (currentStatesValue != null)
				System.out.println("Step " + ++n + " from " + currentStatesValue.toString());
		} while (currentStatesValue != null && (maxLimit < 0 || n < maxLimit));
		System.out.println(".....done in step " + n);
		return solutions;
	}

	public List<IOAwareStateValue> updateExplorableStates(List<IOAwareStateValue> visitedStates, List<IOAwareStateValue> explorableStates,
			StateManager stateManager, List<IOAwareSolution> solutions) {
		System.out.println("B4 Updated explorable states: " + explorableStates.size() + " " + explorableStates.toString());
		List<IOAwareStateValue> nexts = stateManager.findNextStates(solutions, visitedStates, explorableStates);
		for (IOAwareStateValue state : nexts) {
			System.out.println("  " + state.toString() + " exporable  ");
			explorableStates.add(state);
		}
		System.out.println("Updated explorable states: " + explorableStates.size() + " " + explorableStates.toString());
		return explorableStates;
	}

	private List<IOAwareStateValue> visit(final List<IOAwareStateValue> visitedStates, final IOAwareStateValue currentStatesValues) {
		System.out.println("B4 visit states: " + visitedStates.size() + " " + visitedStates.toString());
		if (!contains(visitedStates, currentStatesValues))
			visitedStates.add(new IOAwareStateValue(currentStatesValues.getStateValue(), currentStatesValues.getIOs()));
		System.out.println("After visit states: " + visitedStates.size() + " " + visitedStates.toString());
		return visitedStates;
	}

	private List<IOAwareSolution> updateSolutions(final List<IOAwareSolution> solutions, final List<IOAwareSolution> stepSolutions) {
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

	public List<IOAwareSolution> findSolutions(IOAwareStateValue currentStatesValue, ConstraintConnector cc) throws Exception{
	//long start = System.currentTimeMillis();
	//	System.out.println("in " + (new Date().getTime() - start) +"Constraint is: " + cc.getConstraint());

//		System.out.println("In " + (System.currentTimeMillis() - start) + " invoking reduce");
		//start = System.currentTimeMillis();
	//	writeToFile(
		List<String> reduceOutput = getReduceOutput(cc);
		String strReduceOutput = getOnlyAnswer(reduceOutput);
	//	System.out.println("reduce done In " + (System.currentTimeMillis() - start) + " wrote cnf file");
		
//		start = System.currentTimeMillis();
		DNF dnf = new DNF(Lists.newArrayList(cc.getVariables()), Lists.newArrayList(cc.getStates()),
				Lists.newArrayList(cc.getNextStates()));
		//dnf.solveByReduce(strReduceOutput);

	//	System.out.println("In " + (System.currentTimeMillis() - start) + " did sat4j prep");

		dnf.extractSolutions(strReduceOutput);

		return ioAwarify(dnf.getSolutions(), currentStatesValue.getIOs());
	}

	private String getOnlyAnswer(List<String> reduceOutput) {
		int formulaStart = -1;
		int resultStart = -1;
		int resultEnd = -1;
		for (int i = 0; i < reduceOutput.size() && resultStart == -1; i++) {
			if (reduceOutput.get(i).contains("qaz :="))//TODO
				formulaStart = i;
			if (formulaStart > -1 && 
				i + 3 < reduceOutput.size() &&
					isEmpty(reduceOutput.get(i)) && isEmpty(reduceOutput.get(i+1)) && isEmpty(reduceOutput.get(i+2))) {
						resultStart = i + 3;
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
		String end = "shut";
		return end.equals(s.trim());
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

	private List<String> getReduceOutput(ConstraintConnector cc) throws IOException {
		Process process = Runtime.getRuntime().exec(REDUCE_PROGRAM);
		OutputStream stdin = process.getOutputStream();
		stdin.write(cc.output().getBytes());
		stdin.flush();
		stdin.close();

		List<String> output = new ArrayList<>();

		BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		try {
			while ((line = out.readLine()) != null) { 
				System.out.println("....solution line " + line);
				output.add(line);
			}
		} catch (IOException ex) {
		}
		finally{ 
			//out.close();
		}
		return output;
	}
}