package test;

import org.junit.Before;

import priority.connector.ConnectorFactory;
import priority.connector.ConstraintConnector;

public class DrawTest {
	private ConstraintConnector cc;

	@Before
	private void setUp() {
		ConnectorFactory factory = new ConnectorFactory();
		ConstraintConnector connector = factory.writer("W1", 1);
				
		ConstraintConnector repA1A2 = factory.sync("A1", "A2");
		connector.add(repA1A2, "W1", repA1A2.getNames().get(0));//replicator bas bashe

		ConstraintConnector syncAB = factory.sync("AB1", "AB2");
		connector.add(syncAB, repA1A2.getNames().get(1), syncAB.getName(1));

		cc = connector;
	}
	
	public void test4GoJS() {
	//	cc.get
	}
}
