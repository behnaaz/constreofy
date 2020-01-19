package constraints;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

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

	private List<IOAwareSolution> initializeData(Boolean full) {
		ConnectorFactory factory = new ConnectorFactory();
		connector = factory.writer("w1", 1);
		ConstraintConnector fifoCD = factory.getFIFOConstraint("cd1", "cd2");//TODO, full);
		//TODO [cd1, cd2, cd1cd2ring, cd1cd2xring] names to check
		connector.add(fifoCD, "w1", fifoCD.getName(0));

		IOAwareStateValue initState = new IOAwareStateValue(StateValue.builder().build(), new IOComponent("w1", 1));
		try {
			return Solver.builder().initState(initState).build().solve(connector.getConstraint(),-1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Test
	public void solveEmptyFIFO() {
		List<IOAwareSolution> solutions = initializeData(Boolean.FALSE);
		//String allSolutions = "[[1w1] [] ------ {  cd1tilde w1tilde } -------> (cd1cd2xringtrue ) [0w1], [1w1] [] ------ {  } -------> () [1w1], [0w1] [] ------ {  } -------> () [0w1], [0w1] [] ------ {  cd1tilde w1tilde } -------> (cd1cd2xringtrue ) [0w1], [0w1] [] ------ {  } -------> () [0w1]]";
		assertNotNull(solutions);
		assertEquals(6, solutions.size());
		assertEquals(1, solutions.get(0).getPreIOs().length);
		assertEquals("w1", solutions.get(0).getPreIOs()[0].getNodeName());
		assertEquals(1, solutions.get(0).getPreIOs()[0].getRequests());
		assertEquals(0, solutions.get(0).getPreIOs()[0].consume());

		assertEquals("w1", solutions.get(0).getPostIOs()[0].getNodeName());
		assertEquals("w1", solutions.get(0).getPostIOs()[0].getNodeName());
		assertEquals(1, solutions.get(0).getPostIOs()[0].getRequests());
		assertEquals(0, solutions.get(0).getPostIOs()[0].consume());

		assertEquals(1, solutions.get(0).getSolution().getNextStateValue().getVariableValues().size());
		assertEquals(StateVariableValue.builder().stateName("cd1cd2ring").value(true).build(), solutions.get(0).getSolution().getNextStateValue().getVariableValues().toArray()[0]);

		assertEquals(1, solutions.get(0).getSolution().getToVariables().size());
		assertEquals("cd1cd2xring", solutions.get(0).getSolution().getToVariables().toArray()[0]);

		assertEquals("(!cd1cd2ring) ----{cd1} ----> (cd1cd2xring)", solutions.get(0).getSolution().readable());
		assertEquals("(cd1cd2ring) ----{cd2} ----> (!cd1cd2xring)", solutions.get(3).getSolution().readable());
		assertEquals("(cd1cd2ring) ----{} ----> (cd1cd2xring)", solutions.get(2).getSolution().readable());
		assertEquals("(!cd1cd2ring) ----{} ----> (!cd1cd2xring)", solutions.get(1).getSolution().readable());
		assertEquals("(!cd1cd2ring) ----{cd1} ----> (cd1cd2xring)", solutions.get(4).getSolution().readable());
		assertEquals("(!cd1cd2ring) ----{} ----> (!cd1cd2xring)", solutions.get(5).getSolution().readable());
	}
}