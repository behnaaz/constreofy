package constraints;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.behnaz.rcsp.*;
import org.junit.Test;


public class ConnectorTest {
	@Test
	public void replicatorNodes() {
		ConnectorFactory factory = new ConnectorFactory();
		ConstraintConnector repA1A2 = factory.sync("A1", "A2");
		assertEquals(repA1A2.getName(0), "A1");
		assertEquals(repA1A2.getName(1), "A2");
		assertNull(repA1A2.getName(-1));
		assertNull(repA1A2.getName(2));
	}
}
