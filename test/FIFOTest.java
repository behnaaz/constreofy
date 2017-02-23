package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import priority.connector.ConnectorFactory;
import priority.connector.ConstraintConnector;
import priority.solving.IOAwareSolution;
import priority.solving.IOAwareStateValue;
import priority.solving.IOComponent;
import priority.solving.Solver;
import priority.states.StateValue;

public class FIFOTest {
	private ConstraintConnector cc;
	private List<IOAwareSolution> solutions;

	@Before
	public void setUp() {
		ConnectorFactory factory = new ConnectorFactory();
		ConstraintConnector connector = factory.writer("w1", 1);
/*
		ConstraintConnector repA1A2 = factory.sync("A1", "A2");
		connector.add(repA1A2, "W1", repA1A2.getNames().get(0));// replicator
																// bas bashe
		ConstraintConnector syncAB = factory.sync("AB1", "AB2");
		connector.add(syncAB, repA1A2.getNames().get(1), syncAB.getName(1));

*/
		ConstraintConnector fifoCD = factory.fifoNotInit("cd1", "cd2", Optional.of(Boolean.FALSE));
//		connector.add(fifoCD, syncAB.getNames().get(1), fifoCD.getName(0));

		connector.add(fifoCD, "w1", fifoCD.getName(0));

		cc = connector;
		IOAwareStateValue initState = new IOAwareStateValue(new StateValue(), new IOComponent("w1", 1));
		try {
			solutions = new Solver(cc, initState).solve(-1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testFIFO() {
		String allSolutions = "[[1w1] [] ------ {  cd1tilde w1tilde } -------> (cd1cd2xringtrue ) [0w1], [1w1] [] ------ {  } -------> () [1w1], [0w1] [] ------ {  } -------> () [0w1], [0w1] [] ------ {  cd1tilde w1tilde } -------> (cd1cd2xringtrue ) [0w1], [0w1] [] ------ {  } -------> () [0w1]]";
		assertNotNull(solutions);
		assertEquals(5, solutions.size());

		assertEquals(allSolutions, solutions.toString());
		
		IOAwareSolution ioAwareSolution = solutions.get(0);
		assertEquals("[cd1tilde, w1tilde]", ioAwareSolution.getSolution().getFlowVariables().toString());
		assertTrue(ioAwareSolution.getSolution().getFromVariables().isEmpty());
		assertEquals(1, ioAwareSolution.getSolution().getToVariables().size());
		assertEquals("[ cd1cd2xring]", ioAwareSolution.getSolution().getToVariables().toString());
		assertEquals(0, ioAwareSolution.getPostIOs().length);
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
