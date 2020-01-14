package org.behnaz.rcsp;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static org.behnaz.rcsp.AbstractConnector.*;

public class DNF {
	private List<String> variables;
	private List<Solution> solutions = new ArrayList<>();

	public DNF(final List<String> variables) throws IOException {
		this.variables = variables;
	}

	private void printFlows() {
		Starter.log("Flows~~~~~~~~~~~");
		for (Solution sol : solutions) {
			Starter.log(sol.toString());
		}
	}

	private void reportVars() {
		variables.forEach(v -> Starter.log(variables.indexOf(v.trim().toUpperCase()) + " " + v));
	}
	
	private void solveByReduce(final String constraint, final String outputFile) throws NotFeasibleException, IOException, VaiableNotFoundException {
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

	private String[] extractTerms(final String and) {
		String res = and.trim();
		int index = 0;
		if (!res.matches("^[a-zA-Z].*$"))
			index++;
		res = res.substring(index, and.trim().length()-(index));
		return res.split(AND.trim());
	}

	public List<Solution> extractSolutions(final String reduceOutput) {
		String[] ands = reduceOutput.split(OR.trim());

		for (String and : ands) {
			if (!and.trim().isEmpty()) {
			final String[] terms = and.trim().substring(1, and.trim().length()-1).split(AND.trim());
			final Solution newSol = new Solution(terms);//TODO
			Starter.log(newSol.toString() + " \r\n");

			if (!contains(solutions, newSol))
				solutions.add(newSol); //???TODO
			}
		}
		return solutions;
	}
	
	private boolean contains(final List<Solution> sols, final Solution s) {
		for (Solution t : sols)
			if (t.getFlowVariables().equals(s.getFlowVariables()) && t.getFromVariables().equals(s.getFromVariables()) && t.getToVariables().equals(s.getToVariables()))
				return true;
		return false;
	}
}