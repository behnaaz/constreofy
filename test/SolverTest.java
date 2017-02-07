package test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import priority.init.ExampleMaker;
import priority.solving.Solution;
import priority.solving.Solver;
import priority.states.StateManager;
import priority.states.StateValue;
import priority.states.StateVariableValue;

public class SolverTest {
	List<Solution> s;
	Solver solver;
	
	@Before
	public void setUp() throws Exception {
		solver = new Solver();
		s = solver.findSolutions(1, new StateValue(), new ExampleMaker(3));
	}
	@Test
	public void testFindSolutionFromBigState() throws Exception {
		StateValue v = new StateValue();
		v.add(new StateVariableValue("de1de2ring", true));
		v.add(new StateVariableValue("ij1ij2ring", true));
		v.add(new StateVariableValue("jk1jk2ring", true));

		s = solver.findSolutions(1, v, new ExampleMaker(3));
		assertEquals(s.size(), 4);

	}

	@Test
	public void testFindSolutions() throws Exception {
		assertEquals(s.size(), 4);

// [] ------ {  ab1tilde j2tilde jk1tilde } -------> (ab1ab2xringtrue jk1jk2xringtrue )  
		StateValue temp = new StateValue();
		temp.add(new StateVariableValue("ab1ab2ring", true));
		temp.add(new StateVariableValue("jk1jk2ring", true));
		assertEquals(temp, s.get(0).getNextStateValue());
		assertEquals(0, s.get(0).getFromVariables().size());
		assertEquals(2, s.get(0).getToVariables().size());

// [] ------ {  ab1tilde } -------> (ab1ab2xringtrue )  
		temp = new StateValue();
		temp.add(new StateVariableValue("ab1ab2ring", true));
		assertEquals(temp, s.get(1).getNextStateValue());
		assertEquals(0, s.get(1).getFromVariables().size());
		assertEquals(1, s.get(1).getToVariables().size());

// [] ------ {  j2tilde jk1tilde } -------> (jk1jk2xringtrue )  
		temp = new StateValue();
		temp.add(new StateVariableValue("jk1jk2ring", true));
		assertEquals(temp, s.get(2).getNextStateValue());
		assertEquals(0, s.get(2).getFromVariables().size());
		assertEquals(1, s.get(2).getToVariables().size());

// [] ------ {  } -------> ()  
		temp = new StateValue();
		assertEquals(temp, s.get(3).getNextStateValue());
		assertEquals(0, s.get(3).getFromVariables().size());
		assertEquals(0, s.get(3).getToVariables().size());
	}
	
	@Test
	public void testUpdateSolution() {
		List<StateValue> explorableStates = new ArrayList<>();
		StateManager stateManager = new StateManager();
		List<StateValue> visitedStates = new ArrayList<>();
		List<StateValue> t = solver.updateExplorableStates(visitedStates, explorableStates, stateManager, s);
		assertEquals(s.size(), 4);

		// [] ------ {  ab1tilde j2tilde jk1tilde } -------> (ab1ab2xringtrue jk1jk2xringtrue )  
		StateValue temp = new StateValue();
		temp.add(new StateVariableValue("ab1ab2ring", true));
		temp.add(new StateVariableValue("jk1jk2ring", true));
		assertEquals(temp, s.get(0).getNextStateValue());
		assertEquals(0, s.get(0).getFromVariables().size());
		assertEquals(2, s.get(0).getToVariables().size());

		// [] ------ {  ab1tilde } -------> (ab1ab2xringtrue )  
				temp = new StateValue();
				temp.add(new StateVariableValue("ab1ab2ring", true));
				assertEquals(temp, s.get(1).getNextStateValue());
				assertEquals(0, s.get(1).getFromVariables().size());
				assertEquals(1, s.get(1).getToVariables().size());

		// [] ------ {  j2tilde jk1tilde } -------> (jk1jk2xringtrue )  
				temp = new StateValue();
				temp.add(new StateVariableValue("jk1jk2ring", true));
				assertEquals(temp, s.get(2).getNextStateValue());
				assertEquals(0, s.get(2).getFromVariables().size());
				assertEquals(1, s.get(2).getToVariables().size());

		// [] ------ {  } -------> ()  
				temp = new StateValue();
				assertEquals(temp, s.get(3).getNextStateValue());
				assertEquals(0, s.get(3).getFromVariables().size());
				assertEquals(0, s.get(3).getToVariables().size());
	}
}
