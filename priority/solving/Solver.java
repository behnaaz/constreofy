package priority.solving;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
import priority.primitives.Primitive;
import priority.semantics.DNF;
import priority.states.StateManager;
import priority.states.StateValue;

public class Solver implements Constants, Containable {
	static final String OUTPUTFILE = "abc" + new Date().toString().replaceAll("\\ |\\:", "") + ".txt";
	static final String CNFFILE = "/Users/behnaz.changizi/reoworkspace/priority/src/Users/behnaz.changizi/Desktop/Dropbox/sol.txt";

	private static final String REDUCE_PROGRAM = "/Users/behnaz.changizi/Desktop/reduce/trunk/bin/redpsl";

	void solve(String file, List<String> vars) throws TimeoutException {
		ISolver solver = new ModelIterator(SolverFactory.newMinOneSolver());
		Reader reader = new DimacsReader(solver);
		try {
			IProblem problem = reader.parseInstance(file);
			while (problem.isSatisfiable()) {
				int[] model = solver.model();
				printeq(model, true, vars, !true);		
			} 
		} catch (ParseFormatException | IOException | ContradictionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void printeq(int[] model, boolean varName, List<String> vars, boolean onlyTrue) {
		for (int i : model)
			//if (i > 0)
				System.out.print((varName ? vars.get(Math.abs(i)-1) : Math.abs(i)) + "=" + (i>0?"T ":"F "));
		System.out.println();
	}
	
	public List<IOAwareSolution> solve(int exampleNo, int maxLimit) throws Exception {
		List<IOAwareStateValue> visitedStates = new ArrayList<>();
		List<IOAwareStateValue> explorableStates = new ArrayList<>();//TODO convert to trrmap and fix cpntains ad delete issues
		int n = 0;
		IOAwareStateValue currentStatesValue = new IOAwareStateValue(new StateValue(), new IOComponent("a1", 1));
		ExampleMaker exampleMaker = new ExampleMaker(exampleNo);
		StateManager stateManager = new StateManager();
		List<IOAwareSolution> solutions = new ArrayList<>();

		do {
			visitedStates = visit(visitedStates, currentStatesValue);

			// Get solutions from current state
			List<IOAwareSolution> foundSolutions = findSolutions(currentStatesValue, exampleMaker);
			solutions = updateSolutions(solutions, foundSolutions);

			explorableStates = updateExplorableStates(visitedStates, explorableStates, stateManager, solutions);
			currentStatesValue = getNextUnexploredState(visitedStates, explorableStates);
			if (currentStatesValue != null)
				System.out.println("Step " + ++n + " from " + currentStatesValue.toString());
			/////writer1 = consumeWriteTokens(writer1);
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

	private IOComponent[] updateRequests(final Solution sol, final IOComponent... ios) {
		List<IOComponent> result =  new ArrayList<>();
		Set<String> flowVariables = sol.getFlowVariables();
		if (flowVariables == null || flowVariables.isEmpty())
			return ios;//??? TODO

		for (IOComponent io : result) {
			Primitive p = new Primitive();
			String flowNode = p.flow(io.getNodeName());
			if (flowVariables.contains(flowNode)) {
				result.add(new IOComponent(io.getNodeName(), io.consume()));//TODO
			} else
				result.add(new IOComponent(io.getNodeName(), io.getRequests()));
		}
		return result.toArray(new IOComponent[result.size()]);
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

	public List<IOAwareSolution> findSolutions(final IOAwareStateValue currentStatesValue, ExampleMaker exampleMaker) throws Exception{
		File file = createFile(OUTPUTFILE);
		exampleMaker.out(new OutputStreamWriter(new FileOutputStream(file)));
		ConstraintConnector cc = exampleMaker.getExample(currentStatesValue);

		//long start = System.currentTimeMillis();
	//	System.out.println("in " + (new Date().getTime() - start) +"Constraint is: " + cc.getConstraint());

//		System.out.println("In " + (System.currentTimeMillis() - start) + " invoking reduce");
		//start = System.currentTimeMillis();
		writeToFile(CNFFILE, getReduceOutput(file, cc.getConstraint()));
	//	System.out.println("reduce done In " + (System.currentTimeMillis() - start) + " wrote cnf file");
		
//		start = System.currentTimeMillis();
		DNF dnf = new DNF(CNFFILE, Lists.newArrayList(cc.variables()), Lists.newArrayList(cc.getStates()),
				Lists.newArrayList(cc.getNextStates()));
		dnf.prepareForSat4j(new FileWriter(OUTPUTFILE));
	//	System.out.println("In " + (System.currentTimeMillis() - start) + " did sat4j prep");

		dnf.reportSolutions();

		return ioAwarify(dnf.getSolutions(), currentStatesValue.getIOs());
	}

	private List<IOAwareSolution> ioAwarify(final List<Solution> solutions, final IOComponent[] iOs) {
		List<IOAwareSolution> temp = new ArrayList<>();
		solutions.forEach(s ->
			temp.add(new IOAwareSolution(s, iOs))////???? updateRequests(s, iOs)));
		);
		return temp;
	}

	private byte[] readFile(File file) {
		try {
			if (file.exists() && file.canRead()) {
				return Files.readAllBytes(file.toPath());
			}
		} catch (IOException e) {
			// e.printStackTrace();
		}
		return null;
	}

	private List<String> getReduceOutput(File file, String constraint) throws IOException {
		Process process = Runtime.getRuntime().exec(REDUCE_PROGRAM);
		OutputStream stdin = process.getOutputStream();
		stdin.write(readFile(file));
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

	private File createFile(String fileName) throws IOException {
		File file = new File(fileName);
		if (file.exists())
			file.delete();
		if (!file.exists())
			file.createNewFile();
		return file;
	}

	private void writeToFile(String cnffile, List<String> content) throws IOException {
	//	System.out.println("Going to write into " + cnffile);
		File output = createFile(cnffile);
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(output));
	//	System.out.println("Going to extract solutions from " + content);
		osw.write(extractSolution(content));
		osw.flush();
		osw.close();
	}

	private String extractSolution(List<String> content) {
		int start = findResultStart(content);
		if (start > -1) {
			StringBuilder sb = new StringBuilder();
			for (int i = start; i < content.size() - 1 && !content.get(i).contains(SHUT); i++) {
				if (content.get(i).trim().length() > 0)
					sb.append(content.get(i));
			}
			return sb.toString();
		}
		return null;
	}

	private int findResultStart(List<String> content) {
		for (int i = content.lastIndexOf(SHUT) - 1; i > -1; i--) {
			if (content.get(i - 1).trim().length() == 0 && content.get(i - 2).trim().length() == 0) {
				if (content.get(i).trim().endsWith(":")
						&& Integer.parseInt(content.get(i).replaceAll(":", "").trim()) > 0)
					i++;
				return i;
			}
		}

		return -1;
	}
}
