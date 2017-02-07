package priority.init;

import java.util.List;

import priority.draw.Drawer;
import priority.solving.Solution;
import priority.solving.Solver;

public class Starter {
	private Starter() {
	}
	
	public static void main(String[] args) throws Exception {
		List<Solution> solutions = new Solver().solve(-1);
        new Drawer(solutions).draw();
	}
}