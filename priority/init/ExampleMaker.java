package priority.init;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import priority.connector.ConnectorFactory;
import priority.connector.ConstraintConnector;
import priority.primitives.FIFO;
import priority.solving.IOAwareStateValue;
import priority.solving.IOComponent;
import priority.states.StateValue;

public class ExampleMaker extends FileUser {
	private ConstraintConnector connector;
	private int n;
	private List<FIFO> fifos = new ArrayList<>();

	public ExampleMaker(int n) throws FileNotFoundException {
		this.n = n;
	}

	private ConstraintConnector exampleTwo() throws IOException {
		ConnectorFactory factory = new ConnectorFactory();
		connector = factory.router("a1", "a2", "a3");
		connector.add(factory.fifoNotInit("b1", "b2", Optional.of(Boolean.FALSE)), "b1", "a2");
		connector.add(factory.router("c1", "c2", "c3"), "c1", "b2");
		connector.add(factory.prioritySync("d1", "d2"), "d1", "c3");
		connector.add(factory.syncDrain("e1", "e2"), "e1", "d2");
		connector.add(factory.router("f2", "f1", "f3"), "f1", "e2");
		connector.add(factory.lossyDrain("g2", "g1"), "g1", "f2");
		connector.add(factory.syncDrain("h1", "h2"), "h1", "f3");
		connector.add(factory.prioritySync("i2", "i1"), "i1", "h2");
		connector.add(connector.connect("i2", "a3"), "", "");
		return connector;
	}

	private ConstraintConnector exampleOne() throws IOException {
		ConnectorFactory factory = new ConnectorFactory();
		connector = factory.prioritySync("a", "b");
		connector.add(factory.merger("c", "d", "e"), "c", "b");
		connector.add(factory.sync("f", "g"), "f", "a");
		connector.add(factory.merger("h", "i", "j"), "h", "g");
		return connector;
	}

	public ConstraintConnector getExample(IOAwareStateValue... currentStatesValues) throws IOException {
		if (n == 1)
			return exampleOne();
		if (n == 2)
			return exampleTwo();
		if (n == 3)
			return null;//wrongXaction(currentStatesValues[0].getStateValue(), currentStatesValues[0].getIOs());
		if (n < 0)
			return sequencer(Math.abs(n));
		
		return xaction(currentStatesValues);
	}

	private ConstraintConnector xaction(IOAwareStateValue... currentStatesValue) {
		ConnectorFactory factory = new ConnectorFactory();
		connector = factory.writer("w1", currentStatesValue[0].getIOs()[0].getRequests());
				
		ConstraintConnector repA1A2 = factory.sync("a1", "a2");
		connector.add(repA1A2, "w1", repA1A2.getNames().get(0));//replicator bas bashe

		ConstraintConnector syncAB = factory.sync("ab1", "ab2");
		connector.add(syncAB, repA1A2.getNames().get(1), syncAB.getName(0));
/*
		ConstraintConnector repB = factory.replicator("b1", "b2", "b3");
		connector.add(repB, "b1", "ab2");// syncAB.getName(0));
		connector.add(factory.sync("BC1", "BC2"), "b2", "bc2");
		connector.add(factory.sync("BJ3", "BJ2"), "b3", "bj3");
		connector.add(factory.sync("c1", "c2"), "c1", "bc1");//repl
		connector.add(factory.fifoNotInit("CD1", "CD2", Optional.of(Boolean.FALSE)), "c2", "cd2");
		connector.add(factory.sync("j1", "j2"), "bj2", "j2");//repl
		connector.add(factory.fifoNotInit("JK1", "JK2", Optional.of(Boolean.FALSE)), "c1", "jk1");
		connector.add(factory.fifoNotInit("DE2", "DE1", Optional.of(Boolean.FALSE)), "cd1", "de2");
*/
		return connector;
	}

	private ConstraintConnector sequencer(int n) {
		ConnectorFactory factory = new ConnectorFactory();
		for (int i = 1; i <= n; i++) {
			if (i == 1)
				connector = factory.fifoNotInit("a" + i, "b" + i, Optional.of(Boolean.FALSE));
			else
				connector.add(factory.fifoNotInit("a" + i, "b" + i, Optional.of(Boolean.FALSE)), "a" + i, "e" + (i - 1), true);
			connector.add(factory.replicator("c" + i, "d" + i, "e" + i), "c" + i, "b" + i, true);
		}
		// connector.add(factory.replicator("c"+(n-1), "d"+(n-1), "e"+(n-1)),
		// "c"+(n-1), "b"+(n-1), true);
		// connector.add(factory.fullFifo("a"+n, "b"+n), "a"+n, "e"+(n-1),
		// true);
		// connector.add(factory.replicator("c"+n, "d"+n, "e"+n), "c"+n, "b"+n,
		// true);
		// connector.add(factory.merger("h", "i", "j"), "h", "g");
		return connector;
	}

	public List<FIFO> fifos() {
		return fifos;
	}
}
