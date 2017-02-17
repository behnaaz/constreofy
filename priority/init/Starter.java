package priority.init;

import java.util.List;

import priority.draw.Drawer;
import priority.solving.IOAwareSolution;
import priority.solving.Solver;

public class Starter {
	private Starter() {
	}
	
	public static void main(String[] args) throws Exception {
		List<IOAwareSolution> solutions = new Solver().solve(4, -1);
        new Drawer(solutions).draw();
	}
}