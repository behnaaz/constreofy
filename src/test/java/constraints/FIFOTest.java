package constraints;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Optional;

import org.behnaz.rcsp.*;
import org.junit.Test;

/**
 * Tests functionality of a FIFO connected to a writer
 */
public class FIFOTest {
	ConstraintConnector connector;

	@Test(expected = RuntimeException.class)
	public void testValidator() {
		FIFO fifo = new FIFO("", null);
	}

	private List<IOAwareSolution> initializeData(Optional<Boolean> full) {
		ConnectorFactory factory = new ConnectorFactory();
		connector = factory.writer("w1", 1);
		ConstraintConnector fifoCD = factory.getFIFOConstraint("cd1", "cd2");//TODO, full);
		//TODO [cd1, cd2, cd1cd2ring, cd1cd2xring] names to check
		connector.add(fifoCD, "w1", fifoCD.getName(0));

		IOAwareStateValue initState = new IOAwareStateValue(StateValue.builder().build(), new IOComponent("w1", 1));
		try {
			return Solver.builder().connectorConstraint(connector).initState(initState).build().solve(-1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
//TODO using uninit fifo must be prevented
	@Test
	public void nonInitializedFIFO() {
		String expectedConstraint = "(W1TILDE  or   not  W1TILDE)  and  (CD1TILDE  equiv  (CD1CD2XRING  and   not  CD1CD2RING))  and  (CD2TILDE  equiv  (CD1CD2RING  and   not  CD1CD2XRING))  and  (( not  (CD1TILDE  or  CD2TILDE))  impl  (CD1CD2RING  equiv  CD1CD2XRING))  and  ( not  (CD1TILDE  and  CD2TILDE))  and  ( W1TILDE  equiv  CD1TILDE )";
		List<IOAwareSolution> solutions = initializeData(Optional.empty());
		//assertEquals(expectedConstraint, connector.getConstraint());
		assertEquals(4, solutions.size());
		assertEquals("() ----{ cd1tilde, } ----> (cd1cd2xring, )", solutions.get(0).getSolution().readable());
		assertEquals("(cd1cd2ring, ) ----{ cd2tilde, } ----> ()", solutions.get(3).getSolution().readable());
		assertEquals("(cd1cd2ring, ) ----{ } ----> (cd1cd2xring, )", solutions.get(2).getSolution().readable());
		assertEquals("() ----{ } ----> ()", solutions.get(1).getSolution().readable());
/*
	}
	
	@Test
	public void solveEmptyFIFO() {
		List<IOAwareSolution> solutions = initializeData(Optional.of(Boolean.FALSE));
		//String allSolutions = "[[1w1] [] ------ {  cd1tilde w1tilde } -------> (cd1cd2xringtrue ) [0w1], [1w1] [] ------ {  } -------> () [1w1], [0w1] [] ------ {  } -------> () [0w1], [0w1] [] ------ {  cd1tilde w1tilde } -------> (cd1cd2xringtrue ) [0w1], [0w1] [] ------ {  } -------> () [0w1]]";
		assertNotNull(solutions);
		assertEquals(4, solutions.size());
		assertEquals(1, solutions.get(0).getPreIOs().length);
		assertEquals("w1", solutions.get(0).getPreIOs()[0].getNodeName());
		assertEquals(1, solutions.get(0).getPreIOs()[0].getRequests());
		assertEquals(0, solutions.get(0).getPreIOs()[0].consume());

		assertEquals("w1", solutions.get(0).getPostIOs()[0].getNodeName());
		assertEquals(1, solutions.get(0).getPostIOs()[0].getRequests());
		assertEquals(0, solutions.get(0).getPostIOs()[0].consume());

		assertEquals(1, solutions.get(0).getSolution().getNextStateValue().getVariableValues().size());
		assertEquals(StateVariableValue.builder().stateName("cd1cd2ring").value(Optional.ofNullable(true)).build(), solutions.get(0).getSolution().getNextStateValue().getVariableValues().toArray()[0]);

		assertEquals(1, solutions.get(0).getSolution().getToVariables().size());
		assertEquals("cd1cd2xring", solutions.get(0).getSolution().getToVariables().toArray()[0]);

		assertEquals("() ----{ cd1tilde, } ----> (cd1cd2xring, )", solutions.get(0).getSolution().readable());
		assertEquals("(cd1cd2ring, ) ----{ cd2tilde, } ----> ()", solutions.get(3).getSolution().readable());
		assertEquals("(cd1cd2ring, ) ----{ } ----> (cd1cd2xring, )", solutions.get(2).getSolution().readable());
		assertEquals("() ----{ } ----> ()", solutions.get(1).getSolution().readable());
/*
		assertEquals(allSolutions, solutions.toString());
		
		IOAwareSolution ioAwareSolution = solutions.get(0);
		assertEquals("[cd1tilde, w1tilde]", ioAwareSolution.getSolution().getFlowVariables().toString());
		assertTrue(ioAwareSolution.getSolution().getFromVariables().isEmpty());
		assertEquals(1, ioAwareSolution.getSolution().getToVariables().size());
		assertEquals("[ cd1cd2xring]", ioAwareSolution.getSolution().getToVariables().toString());
		assertEquals(1, ioAwareSolution.getPostIOs().length);
		assertEquals("w1", ioAwareSolution.getPreIOs()[0].getNodeName());
		assertEquals(1, ioAwareSolution.getPreIOs()[0].getRequests());
		assertEquals(1, ioAwareSolution.getPreIOs().length);
		assertEquals("1w1", ioAwareSolution.getPreIOs()[0].toString());

		ioAwareSolution = solutions.get(1);
		assertTrue(ioAwareSolution.getSolution().getFlowVariables().isEmpty());
		assertTrue(ioAwareSolution.getSolution().getFromVariables().isEmpty());
		assertTrue(ioAwareSolution.getSolution().getToVariables().isEmpty());
		assertEquals(1, ioAwareSolution.getPostIOs().length);
		assertEquals("1w1", ioAwareSolution.getPostIOs()[0].toString());
		assertEquals(1, ioAwareSolution.getPreIOs().length);
		assertEquals("1w1", ioAwareSolution.getPreIOs()[0].toString());*/
	}
}