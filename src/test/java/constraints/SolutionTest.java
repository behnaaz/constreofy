package constraints;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import priority.solving.Solution;

public class SolutionTest {
	private static final String TILDE = "tilde";
	private static final String XRING = "xring";
	@Test
	public void testSolution()  {
		Solution s = new Solution(new String[]{"a = 0", "b = 1"});
		assertEquals(s.getFlowVariables().size(), 0);
		assertEquals(s.getFromVariables().size(), 0);
		assertEquals(s.getToVariables().size(), 0);
		//assertEquals(s.getNextStateValue().toString(), "");
	}

	@Test
	public void testSolutionEmpty()  {
		Solution s = new Solution(new String[]{"a = 0", "b = 1"});
	//	assertEquals(s.toString(), "[] ------ {  } -------> ()");
		assertEquals(s.getFlowVariables().size(), 0);
		assertEquals(s.getFromVariables().size(), 0);
		assertEquals(s.getToVariables().size(), 0);
	//	assertEquals(s.getNextStateValue().toString(), "");

		s = new Solution(new String[]{"a" + TILDE + " = 0", "b" + TILDE + " = 1", "c"+ XRING + " = 1"
				, "d"+ XRING + " = 1",  "d"+ XRING + " = 1",  "c"+ XRING + " = 1"});
		//assertEquals(s.toString().trim(), "[] ------ {  btilde } -------> ([[cxringtrue ]])");
		assertEquals(s.getFlowVariables().size(), 1);
		assertEquals(s.getFromVariables().size(), 0);
		assertEquals(s.getToVariables().size(), 2);
		assertEquals(s.getNextStateValue().getVariableValues().size(), 2);
	//	assertEquals(s.getNextStateValue().toString(), "cringtrue,dringtrue");
	}

	@Test
	public void testBuildNextState() {
		Solution s = new Solution(new String[]{"a" + TILDE + " = 0", "b" + TILDE + " = 1", "c"+ XRING + " = 1"
				, "d"+ XRING + " = 1",  "d"+ XRING + " = 1",  "c"+ XRING + " = 1"});
		//assertEquals(s.toString().trim(), "[] ------ {  btilde } -------> ([[cxringtrue ]])");
		assertEquals(s.getFlowVariables().size(), 1);
		assertEquals(s.getFromVariables().size(), 0);
		assertEquals(s.getToVariables().size(), 2);
		assertEquals(s.getNextStateValue().getVariableValues().size(), 2);
	//	assertEquals(s.getNextStateValue().toString(), "cringtrue,dringtrue");

	//	assertEquals(s.buildNextStateValues().toString(),"cringtrue,dringtrue");
	}
}
