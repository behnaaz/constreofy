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
	
	public List<Solution> solve(int maxLimit) throws Exception {
		List<StateValue> visitedStates = new ArrayList<>();
		List<StateValue> explorableStates = new ArrayList<>();//TODO convert to trrmap and fix cpntains ad delete issues
		int n = 0;
		int writer1 = 1;
		StateValue currentStatesValue = new StateValue();
		ExampleMaker exampleMaker = new ExampleMaker(3);
		StateManager stateManager = new StateManager();
		List<Solution> solutions = new ArrayList<>();

		do {
			if (n==6)
				System.out.println("ddddd");
			// Get solutions from current state
			solutions = updateSolutions(solutions, findSolutions(writer1, currentStatesValue, exampleMaker));
			writer1 = consumeWriteTokens(writer1);
			visitedStates = visit(visitedStates, currentStatesValue);

			explorableStates = updateExplorableStates(visitedStates, explorableStates, stateManager, solutions);
			currentStatesValue = getNextUnexploredState(visitedStates, explorableStates);
			if (currentStatesValue != null)
				System.out.println("Step " + ++n + " from " + currentStatesValue.toString());
		} while (currentStatesValue != null && (maxLimit < 0 || n < maxLimit));
		System.out.println(".....done in step " + n);
		return solutions;
	}

	public List<StateValue> updateExplorableStates(List<StateValue> visitedStates, List<StateValue> explorableStates,
			StateManager stateManager, List<Solution> solutions) {
		System.out.println("B4 Updated explorable states: " + explorableStates.size() + " " + explorableStates.toString());
		List<StateValue> nexts = stateManager.findNextStates(solutions);
		for (StateValue state : nexts) {
			System.out.println("Is  " + state.toString() + " exporable ? ");
			if (!contains(visitedStates, state) && !contains(explorableStates, state)) {
				explorableStates.add(state);
			System.out.println("YES");
			}
		}
		System.out.println("Updated explorable states: " + explorableStates.size() + " " + explorableStates.toString());
		return explorableStates;
	}

	private List<StateValue> visit(List<StateValue> visitedStates, StateValue currentStatesValues) {
		System.out.println("B4 visit states: " + visitedStates.size() + " " + visitedStates.toString());
		if (currentStatesValues.toString().trim().contains("de1de2ringtrue,ij1ij2ringtrue,jk1jk2ringtrue"))
			System.out.println("ddd" + currentStatesValues);
		if (!contains(visitedStates, currentStatesValues))
			visitedStates.add(currentStatesValues);
		System.out.println("After visit states: " + visitedStates.size() + " " + visitedStates.toString());
		return visitedStates;
	}

	private int consumeWriteTokens(int writer1) {
		return (writer1 > 0) ? writer1 - 1 : 0;
	}

	private List<Solution> updateSolutions(List<Solution> solutions, List<Solution> stepSolutions) {
		for (Solution temp : stepSolutions)
		{
			if (!contains(solutions, temp)) {
				System.out.println("Solution added "+temp.toString());
				solutions.add(temp);
			}
		}
		return solutions;
	}

	private StateValue getNextUnexploredState(List<StateValue> visitedStates, List<StateValue> explorableStates) {
		StateValue currentStatesValues = null;
		if (explorableStates.isEmpty())
			return currentStatesValues;
		
		do {
			currentStatesValues = explorableStates.remove(0);
			if (contains(visitedStates, currentStatesValues))
				currentStatesValues = null;
		} while (!explorableStates.isEmpty() && currentStatesValues == null);
		
		if (contains(visitedStates, currentStatesValues))
			currentStatesValues = null;
		return currentStatesValues;
	}

	public List<Solution> findSolutions(int writer1, StateValue currentStatesValues, ExampleMaker exampleMaker) throws Exception{
		File file = createFile(OUTPUTFILE);
		exampleMaker.out(new OutputStreamWriter(new FileOutputStream(file)));
		ConstraintConnector cc = exampleMaker.getExample(currentStatesValues, writer1);

		long start = System.currentTimeMillis();
	//	System.out.println("in " + (new Date().getTime() - start) +"Constraint is: " + cc.getConstraint());

//		System.out.println("In " + (System.currentTimeMillis() - start) + " invoking reduce");
		start = System.currentTimeMillis();
		writeToFile(CNFFILE, getReduceOutput(file, cc.getConstraint()));
	//	System.out.println("reduce done In " + (System.currentTimeMillis() - start) + " wrote cnf file");
		
		start = System.currentTimeMillis();
		DNF dnf = new DNF(CNFFILE, Lists.newArrayList(cc.variables()), Lists.newArrayList(cc.states()),
				Lists.newArrayList(cc.nextStates()));
		dnf.prepareForSat4j(new FileWriter(OUTPUTFILE));
	//	System.out.println("In " + (System.currentTimeMillis() - start) + " did sat4j prep");

		dnf.reportSolutions();
	//	if (writer1 > 0)
		//	dnf.reportVars();
		return dnf.getSolutions();
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
		while (out.ready()){ 
			line = out.readLine();
			System.out.println("....solution line " + line);
			output.add(line);
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
