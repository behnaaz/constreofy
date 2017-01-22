package priority.solving;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import priority.init.Starter;
import priority.semantics.DNF;
import priority.states.StateManager;

public class Solver implements Constants {
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
	
	public List<String> solve(int maxLimit) throws IOException, FileNotFoundException, Exception {
		List<Map<String, Boolean>> visited = new ArrayList<Map<String, Boolean>>();
		List<Map<String, Boolean>> explorableStates = new ArrayList<Map<String, Boolean>>();

		long start = System.currentTimeMillis();
		int n=0;
		int w1 = 1;
		Map<String, Boolean> currentStatesValues = new HashMap<>();
		File file = createFile(OUTPUTFILE);
		ExampleMaker exampleMaker = new ExampleMaker(3);
		StateManager stateManager = new StateManager();
		List<String> solutions = new ArrayList<>();

		do {
			n++;
			exampleMaker.out(new OutputStreamWriter(new FileOutputStream(file)));
			ConstraintConnector cc = exampleMaker.getExample(currentStatesValues, w1);

			System.out.println("in " + (new Date().getTime() - start) +"Constraint is: " + cc.constraint);
			visited.add(currentStatesValues);

			System.out.println("In " + (System.currentTimeMillis() - start) + " invoking reduce");
			start = System.currentTimeMillis();
			writeToFile(CNFFILE, getReduceOutput(file, new Starter()));
			System.out.println("reduce done In " + (System.currentTimeMillis() - start) + " wrote cnf file");
			
			start = System.currentTimeMillis();
			DNF dnf = new DNF(CNFFILE, Lists.newArrayList(cc.variables()), Lists.newArrayList(cc.states()),
					Lists.newArrayList(cc.nextStates()));
			dnf.prepareForSat4j(new FileWriter(OUTPUTFILE));
			System.out.println("In " + (System.currentTimeMillis() - start) + " did sat4j prep");
			start = System.currentTimeMillis();
			
			if (w1 > 0)
				dnf.reportVars();
			w1 = (w1 > 0) ? w1 - 1 : 0;

			dnf.reportSolutions(false);
			solutions.addAll(dnf.solutionsToList(false));
			//dnf.toString()
			System.out.println("In " + (System.currentTimeMillis() - start) + " reported solution");
			
		///////	dnf.printFlows();
			// dnf.printFlowsNPriority();
			List<Map<String, Boolean>> nexts = stateManager.stateValues(dnf.solutions());
			for (Map<String, Boolean> i : nexts) {
				Map<String, Boolean> temp = find(i, visited);
				if (temp == null)
					explorableStates.add(i);
			}

			currentStatesValues = (explorableStates.size() > 0) ? stateManager.makeItCurrent(explorableStates.remove(0)) : null;

			
			System.out.println("Step " + n);
		} while (currentStatesValues != null && (maxLimit < 0 || n < maxLimit));
		System.out.println(".....done in step " + n);
		return solutions;
	}

	private Map<String, Boolean> find(Map<String, Boolean> elem, List<Map<String, Boolean>> list) {
		Map<String, Boolean> result = null;
		for (Map<String, Boolean> t : list) {
			if (result == null)
				result = exists(elem, t);
		}
		return result;
	}

	private Map<String, Boolean> exists(Map<String, Boolean> elem, Map<String, Boolean> t) {
		for (String key : elem.keySet()) {
			if (!t.containsKey(key) || t.get(key) != elem.get(key)) {
				return null;
			}
		}
		return elem;
	}

	private byte[] readFile(File file) {
		try {
			if (file.exists() && file.canRead()) {
				byte[] lines = Files.readAllBytes(file.toPath());
				return lines;
			}
		} catch (IOException e) {
			// e.printStackTrace();
		}
		return null;
	}

	private List<String> getReduceOutput(File file, Starter constraint) throws IOException {
		Process process = Runtime.getRuntime().exec(REDUCE_PROGRAM);
		OutputStream stdin = process.getOutputStream();
		stdin.write(readFile(file));
		stdin.flush();
		stdin.close();

		List<String> output = new ArrayList<String>();
		BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		while ((line = out.readLine()) != null)
			output.add(line);
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
		System.out.println("Going to write into " + cnffile);
		File output = createFile(cnffile);
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(output));
		System.out.println("Going to extract solutions from " + content);
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
