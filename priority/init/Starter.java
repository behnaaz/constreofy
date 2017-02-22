package priority.init;

import java.util.List;

import priority.draw.Drawer;
import priority.solving.IOAwareSolution;
import priority.solving.IOAwareStateValue;
import priority.solving.IOComponent;
import priority.solving.Solver;
import priority.states.StateValue;

public class Starter {
	private Starter() {
	}
	
	public static void main(String[] args) throws Exception {
		List<IOAwareSolution> solutions = new Solver().solve(4, -1, new IOAwareStateValue(new StateValue(), new IOComponent("a1", 1)));
        Drawer d = new Drawer(solutions);
        d.draw();
        d.toGoJS();
	}
}