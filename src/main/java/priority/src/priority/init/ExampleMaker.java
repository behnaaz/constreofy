package priority.src.priority.init;

import priority.src.priority.connector.ConnectorFactory;
import priority.src.priority.connector.ConstraintConnector;
import priority.src.priority.solving.IOAwareStateValue;

import static priority.src.priority.connector.AbstractConnector.TRUE;

class ExampleMaker {
	private ConstraintConnector connector;
	private int exampleChoice;
	 ExampleMaker(int exampleChoice) {
		this.exampleChoice = exampleChoice;
	}

	private ConstraintConnector exampleTwo() {
		ConnectorFactory factory = new ConnectorFactory();
		connector = factory.router("a1", "a2", "a3");
		connector.add(factory.getFIFOConstraint("b1", "b2"), "b1", "a2");//empty TODO
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

	private ConstraintConnector exampleOne() {
		ConnectorFactory factory = new ConnectorFactory();
		connector = factory.prioritySync("a", "b");
		connector.add(factory.merger("c", "d", "e"), "c", "b");
		connector.add(factory.sync("f", "g"), "f", "a");
		connector.add(factory.merger("h", "i", "j"), "h", "g");
		return connector;
	}

	ConstraintConnector getExample(IOAwareStateValue... currentStatesValues) {
		if (exampleChoice == 1)
			return exampleOne();
		if (exampleChoice == 2)
			return exampleTwo();
		if (exampleChoice == 3)
			return null;//wrongXaction(currentStatesValues[0].getStateValue(), currentStatesValues[0].getIOs());
		if (exampleChoice < 0)
			return sequencer(Math.abs(exampleChoice));
		
		return xaction(currentStatesValues);
	}

	private ConstraintConnector xaction(IOAwareStateValue... currentStatesValue) {
		ConnectorFactory factory = new ConnectorFactory();
		connector = factory.writer("w1", currentStatesValue[0].getIOs()[0].getRequests());
				
		ConstraintConnector repA1A2 = factory.sync("a1", "a2");
		connector.add(repA1A2, "w1", repA1A2.getVariableNames().get(0));//replicator bas bashe

		ConstraintConnector syncAB = factory.sync("ab1", "ab2");
		connector.add(syncAB, repA1A2.getVariableNames().get(1), syncAB.getName(0));
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
		// newer method: add connections first
		connector = new ConstraintConnector(TRUE);
		for (int i = 1; i <= n; i++) {
			if (i > 1)
				connector.addEquals("a" + i, "e" + (i - 1));
			connector.addEquals("c" + i, "b" + i);
		}
		if (n > 1)
			connector.addEquals("a1", "e" + n);

		ConnectorFactory factory = new ConnectorFactory();
		for (int i = 1; i <= n; i++) {
			if (i == 1)
				connector.add(factory.getFIFOConstraint("a" + i, "b" + i), null, null);
			else
				connector.add(factory.getFIFOConstraint("a" + i, "b" + i), "a" + i, "e" + (i - 1));

			connector.add(factory.replicator("c" + i, /*"d" + i,*/ "e" + i), "c" + i, "b" + i);
		}
		if (n > 1) {
			connector.add(null, "a1", "e" + n);
		}
		return connector;
	}

}