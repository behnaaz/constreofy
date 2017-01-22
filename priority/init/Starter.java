package priority.init;

import java.util.List;

import priority.common.Constants;
import priority.draw.Drawer;
import priority.semantics.AbstractSemantics;
import priority.solving.Solver;

public class Starter extends AbstractSemantics implements Constants {
	public static void main(String[] args) throws Exception {
		List<String> solutions = new Solver().solve(10);
         new Drawer(solutions).draw();
	}
}