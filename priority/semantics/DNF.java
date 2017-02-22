package priority.semantics;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import priority.common.Constants;
import priority.init.FileUser;
import priority.solving.Solution;

public class DNF extends FileUser implements Constants {
	private List<String> variables;
	private List<Solution> solutions = new ArrayList<>();
	private List<String> states;
	private List<String> nextStates;
	static final String CNFFILE = "/Users/behnaz.changizi/reoworkspace/priority/src/Users/behnaz.changizi/Desktop/Dropbox/sol.txt";

	public DNF(List<String> variables, List<String> states, List<String> nextStates) throws IOException {
		this.variables = variables;
		this.states = states;
		this.nextStates = nextStates;
	}

	public void printFlows() {
		System.out.println("Flows~~~~~~~~~~~");
		for (Solution sol : solutions) {
			System.out.println(sol.toString());
		}
	}

	public List<String> getVariables() {
		return variables;
	}

	public void reportVars() {
		variables.forEach(v -> System.out.println(variables.indexOf(v.trim().toUpperCase()) + " " + v));
	}
	
	public void solveByReduce(String constraint) throws Exception {
		Writer writer = new FileWriter(OUTPUTFILE);

		writer.write("c test\r\n");
		writer.write("c\r\n");
		
		if ("false".equalsIgnoreCase(constraint.trim()))
			throw new Exception("NOT FEASIBLE");

		assert(constraint.contains(OR.trim()));//OTHERWISE not supported yet
		String[] ands = constraint.split(OR.trim());
		writer.write("p cnf " + variables.size() + " " + ands.length + "\r\n");

		for (String and : ands) {
			StringBuilder sb = new StringBuilder();
			String[] terms = extractTerms(and);
			for (String term : terms) {
				String[] atoms = term.trim().split(" = ");
				if (variables.indexOf(atoms[0].toUpperCase()) <= -1) 
					throw new Exception(atoms[0]+" not found");
				sb.append(("0".equals(atoms[1].trim())?"-":"")+(variables.indexOf(atoms[0].toUpperCase())+1)+" ");
			}
			writer.write(sb.toString() + " 0\r\n");
		}
		writer.close();
	}

	private String[] extractTerms(String and) {
		String res = and.trim();
		int index = 0;
		if (!res.matches("^[a-zA-Z].*$"))
			index++;
		res = res.substring(index, and.trim().length()-(index));
		return res.split(AND.trim());
	}

	public List<Solution> extractSolutions(String reduceOutput) throws IOException {
		String[] ands = reduceOutput.split(OR.trim());

		for (String and : ands) {
			if (!and.trim().isEmpty()) {
				
			//sb = new StringBuilder();
			String[] terms = and.trim().substring(1, and.trim().length()-1).split(AND.trim());
			
			//for (String term : terms) {
			//	String[] atoms = term.trim().split(" = ");
			//	if (variables.indexOf(atoms[0].toUpperCase()) <= -1) 
				//	throw new Exception(atoms[0]+" not found");
			//	sb.append((atoms[1].trim().equals("0")?"!":" ")+(atoms[0].trim())+" ");
			//}
			Solution newSol = new Solution(terms);//TODO
			System.out.println(newSol.toString() + " \r\n");

			if (!contains(solutions, newSol))
				solutions.add(newSol);
			}
		}
		return solutions;
	}
	
	private boolean contains(List<Solution> sols, Solution s) {
		for (Solution t : sols)
			if (t.getFlowVariables().equals(s.getFlowVariables()) && t.getFromVariables().equals(s.getFromVariables()) && t.getToVariables().equals(s.getToVariables()))
				return true;
		return false;
	}

	public void printFlowsNPriority() {
		for (Solution sol : solutions) {
			System.out.println(sol.toString(true));
		}
	}
}