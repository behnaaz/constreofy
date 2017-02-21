package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import priority.connector.ConnectorFactory;
import priority.connector.ConstraintConnector;

public class ConnectorTest {
	@Test
	public void replicatorNodes() {
		ConnectorFactory factory = new ConnectorFactory();
		ConstraintConnector repA1A2 = factory.sync("A1", "A2");
		assertEquals(repA1A2.getNames().get(0), "A1");
		assertEquals(repA1A2.getNames().get(1), "A2");
	}
}
