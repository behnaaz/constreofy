package priority.semantics;

import priority.solving.Solution;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static priority.connector.AbstractConnector.AND;
import static priority.connector.AbstractConnector.OR;

public class DNF {
	private List<String> variables;
	private List<Solution> solutions = new ArrayList<>();
	static final String CNFFILE = "/Users/behnaz.changizi/reoworkspace/priority/src/Users/behnaz.changizi/Desktop/Dropbox/sol.txt";

	public DNF(List<String> variables) throws IOException {
		this.variables = variables;
	}

	public void printFlows() {
		System.out.println("Flows~~~~~~~~~~~");
		for (Solution sol : solutions) {
			System.out.println(sol.toString());
		}
	}

	public void reportVars() {
		variables.forEach(v -> System.out.println(variables.indexOf(v.trim().toUpperCase()) + " " + v));
	}
	
	public void solveByReduce(final String constraint, final String outputFile) throws NotFeasibleException, IOException, VaiableNotFoundException {
		Writer writer = new FileWriter(outputFile);

		writer.write("c test\r\n");
		writer.write("c\r\n");
		
		if ("false".equalsIgnoreCase(constraint.trim())) {
			writer.close();
			throw new NotFeasibleException();
		}

		
		String[] ands = constraint.split(OR.trim());
		writer.write("p cnf " + variables.size() + " " + ands.length + "\r\n");

		for (String and : ands) {
			StringBuilder sb = new StringBuilder();
			String[] terms = extractTerms(and);
			for (String term : terms) {
				String[] atoms = term.trim().split(" = ");
				if (variables.indexOf(atoms[0].toUpperCase()) <= -1) {
					writer.close();
					throw new VaiableNotFoundException(atoms[0]);
				}
				sb.append(("0".equals(atoms[1].trim())?"-":"")+(variables.indexOf(atoms[0].toUpperCase())+1)+" ");
			}
			writer.write(sb.toString() + " 0\r\n");
		}
		writer.close();
		assert(constraint.contains(OR.trim()));//OTHERWISE not supported yet
	}

	private String[] extractTerms(String and) {
		String res = and.trim();
		int index = 0;
		if (!res.matches("^[a-zA-Z].*$"))
			index++;
		res = res.substring(index, and.trim().length()-(index));
		return res.split(AND.trim());
	}

	public List<Solution> extractSolutions(String reduceOutput) {
		String[] ands = reduceOutput.split(OR.trim());

		for (String and : ands) {
			if (!and.trim().isEmpty()) {
			String[] terms = and.trim().substring(1, and.trim().length()-1).split(AND.trim());
			Solution newSol = new Solution(terms);//TODO
			System.out.println(newSol.toString() + " \r\n");

			if (!contains(solutions, newSol))
				solutions.add(newSol); //???TODO
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
}