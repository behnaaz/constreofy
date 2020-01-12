package constraints;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import priority.connector.ConnectorFactory;
import priority.connector.ConstraintConnector;
import priority.draw.Drawer;
import priority.solving.IOAwareSolution;
import priority.solving.IOAwareStateValue;
import priority.solving.IOComponent;
import priority.solving.Solver;
import priority.states.StateValue;

public class DrawTest {
	private List<IOAwareSolution> solutions;

	@Before
	public void setUp() {
		ConnectorFactory factory = new ConnectorFactory();
		ConstraintConnector connector = factory.writer("W1", 1);
				
		ConstraintConnector repA1A2 = factory.sync("A1", "A2");
		connector.add(repA1A2, "W1", repA1A2.getVariableNames().get(0));//replicator bas bashe

		ConstraintConnector syncAB = factory.sync("AB1", "AB2");
		connector.add(syncAB, repA1A2.getVariableNames().get(1), syncAB.getName(1));

		final IOAwareStateValue initState = new IOAwareStateValue(StateValue.builder().build(), new IOComponent("a1", 1));
		try {
			solutions = Solver.builder()
					.connectorConstraint(connector)
					.initState(initState)
					.build()
					.solve(-1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void test4GoJS() {
		final String expected = "{\"nodeKeyProperty\":\"id\",\"nodeDataArray\":[],\"linkDataArray\":[{\"from\":\"0\",\"to\":\"0\",\"text\":\"\"},{\"from\":\"0\",\"to\":\"1\",\"text\":\"a1 a2 ab1 ab2 w1\"},{\"from\":\"1\",\"to\":\"1\",\"text\":\"a1 a2 ab1 ab2 w1\"},{\"from\":\"1\",\"to\":\"1\",\"text\":\" \"}]}";

		assertNotNull(solutions);		
		Drawer d = new Drawer(solutions);
        String real = d.toGoJS();
	//	assertEquals(expected, real);
	}
}
