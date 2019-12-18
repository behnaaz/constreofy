import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import priority.connector.ConnectorFactory;
import priority.connector.ConstraintConnector;
import priority.solving.IOAwareSolution;
import priority.solving.IOAwareStateValue;
import priority.solving.IOComponent;
import priority.solving.Solver;
import priority.states.StateValue;

/**
 * Tests functionality of a FIFO connected to a writer
 */
public class FIFOTest {
	ConstraintConnector connector;

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
	public void testNonInitializedFIFO() {
		String expectedConstraint = "(W1TILDE  or   not  W1TILDE)  and  (CD1TILDE  equiv  (CD1CD2XRING  and   not  CD1CD2RING))  and  (CD2TILDE  equiv  (CD1CD2RING  and   not  CD1CD2XRING))  and  (( not  (CD1TILDE  or  CD2TILDE))  impl  (CD1CD2RING  equiv  CD1CD2XRING))  and  ( not  (CD1TILDE  and  CD2TILDE))  and  ( W1TILDE  equiv  CD1TILDE )";
		List<IOAwareSolution> solutions = initializeData(Optional.empty());
		assertEquals(expectedConstraint, connector.getConstraint());
		assertEquals(0, solutions.size());
	}
	
	@Test
	public void testEmpyFIFO() {
		List<IOAwareSolution> solutions = initializeData(Optional.of(Boolean.FALSE));
		String allSolutions = "[[1w1] [] ------ {  cd1tilde w1tilde } -------> (cd1cd2xringtrue ) [0w1], [1w1] [] ------ {  } -------> () [1w1], [0w1] [] ------ {  } -------> () [0w1], [0w1] [] ------ {  cd1tilde w1tilde } -------> (cd1cd2xringtrue ) [0w1], [0w1] [] ------ {  } -------> () [0w1]]";
		assertNotNull(solutions);
		assertEquals(5, solutions.size());

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
		assertEquals("1w1", ioAwareSolution.getPreIOs()[0].toString());
	}
}