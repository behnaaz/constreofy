package priority.solving;
import java.io.IOException;
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

public class Solver {
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
	
	/*public static void main(String[] a) throws TimeoutException {
		Solver solver = new Solver();
		
		solver.solve("/Users/behnaz.changizi/reoworkspace/priority/jjjnnn.cnf.txt", ImmutableList.of("a", "b"));
	}*/
}
