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
			return wrongXaction(currentStatesValues[0].getStateValue(), currentStatesValues[0].getIOs());
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

		ConstraintConnector repB = factory.replicator("b1", "b2", "b3");
		connector.add(repB, "b1", "ab2");// syncAB.getName(0));
		connector.add(factory.sync("BC1", "BC2"), "b2", "bc2");
		connector.add(factory.sync("BJ3", "BJ2"), "b3", "bj3");
		connector.add(factory.sync("c1", "c2"), "c1", "bc1");//repl
		connector.add(factory.fifoNotInit("CD1", "CD2", Optional.of(Boolean.FALSE)), "c2", "cd2");
		connector.add(factory.sync("j1", "j2"), "bj2", "j2");//repl
		connector.add(factory.fifoNotInit("JK1", "JK2", Optional.of(Boolean.FALSE)), "c1", "jk1");
		connector.add(factory.fifoNotInit("DE2", "DE1", Optional.of(Boolean.FALSE)), "cd1", "de2");

		return connector;
	}

	private ConstraintConnector wrongXaction(StateValue currentStatesValues, IOComponent... ios) {
		ConnectorFactory connectorFactory = new ConnectorFactory();
		FIFO ab = buildFIFO("AB1", "AB2", currentStatesValues);
		connector = ab.generateConstraint();
		connector.add(connectorFactory.writer("AB1", ios[0].getRequests()), "ab1", "ab1");
		connector.add(connectorFactory.replicator("b1", "b2", "b3"), "b1", "AB2");
		connector.add(connectorFactory.replicator("c1", "c2"), "c1", "b2");//"BC2");
		FIFO cd = buildFIFO("CD1", "CD2", currentStatesValues);
		connector.add(cd.generateConstraint(), "CD1", "c2");
		FIFO de = buildFIFO("DE1", "DE2", currentStatesValues);
		connector.add(de.generateConstraint(),
				"DE1", "CD2");
		connector.add(connectorFactory.router("e1", "e2", "e3"), "e1", "DE2");
		FIFO el = buildFIFO("EL1", "EL2", currentStatesValues);
		connector.add(el.generateConstraint(),
				"EL1", "e2");
		//connector.add(factory.sync("EG1", "EG2"), "EG1", "e3");
		connector.add(connectorFactory.replicator("g3", "g1", "g2"), "g3", "e3");
		FIFO fg = buildFIFO("FG2", "FG1", currentStatesValues);
		connector.add(fg.generateConstraint(),
				"FG2", "g1");
		FIFO gh = buildFIFO("GH1", "GH2", currentStatesValues);
		connector.add(gh.generateConstraint(), "GH1", "g2");
		
		//connector.add(factory.replicator("h1", "h2"), "h1", "GH2");
		//connector.add(factory.sync("HP1", "HP2"), "HP1", "h2");// prio
		//connector.add(factory.sync("BI1", "BI2"), "BI1", "b3");
		connector.add(connectorFactory.router("i1", "i2", "i3"), "i1", "b3");
		FIFO ij = buildFIFO("IJ1", "IJ2", currentStatesValues);
		connector.add(ij.generateConstraint(), "IJ1", "i2");
	//	connector.add(factory.replicator("j1", "j2"), "j1", "IJ2");
		connector.add(buildFIFO("JK1", "JK2", currentStatesValues).generateConstraint(), "JK1", "j2");
		connector.add(connectorFactory.router("JK2", "k2", "k3"), "JK2", "JK2");
	//	connector.add(factory.sync("KL1", "KL2"), "KL1", "k2");
		connector.add(connectorFactory.join("EL2", "l3", "l2"), "EL2", "EL2");
		// kl2 l3
		connector.add(buildFIFO("LM1", "LM2", currentStatesValues).generateConstraint(), "LM1", "l2");
	//	connector.add(factory.syncDrain("KO1", "KO2"), "KO1", "k3");
	//	connector.add(factory.router("p2", "p1", "p3"), "p2", "HP2");
		connector.add(connectorFactory.router("p2", "p1", "p3"), "p2", "GH2");
/*
		connector.add(factory.sync("OP2", "OP1"), "OP2", "p1");
		connector.add(factory.syncDrain("IP2", "IP1"), "IP2", "p3");
		// i3 ip1
		 
		connector.add(connectorFactory.replicator("o2", "o1", "o3"), "o2", "p1");
		// ko2 o3
		connector.add(buildFIFO("ON1", "ON2", currentStatesValues).constraint(), "ON1", "o1");
	/*	connector.add(factory.sync("KL2", "l3"), "KL2", "l3");
		connector.add(factory.sync("IP1", "i3"), "IP1", "i3");
		connector.add(factory.sync("KO2", "o3"), "KO2", "o3");
*/
		
		connector.setStates(connectorFactory.memory("ab1", "ab2"), connectorFactory.memory("cd1", "cd2"), connectorFactory.memory("de1", "de2"),
				connectorFactory.memory("el1", "el2"), connectorFactory.memory("fg2", "fg1"), connectorFactory.memory("gh1", "gh2"),
				connectorFactory.memory("ij1", "ij2"), connectorFactory.memory("jk1", "jk2"), connectorFactory.memory("lm1", "lm2"),
				connectorFactory.memory("on1", "on2"));
		connector.setNextStates(connectorFactory.nextMemory("ab1", "ab2"), connectorFactory.nextMemory("cd1", "cd2"),
				connectorFactory.nextMemory("de1", "de2"), connectorFactory.nextMemory("el1", "el2"), connectorFactory.nextMemory("fg2", "fg1"),
				connectorFactory.nextMemory("gh1", "gh2"), connectorFactory.nextMemory("ij1", "ij2"), connectorFactory.nextMemory("jk1", "jk2"),
				connectorFactory.nextMemory("lm1", "lm2"), connectorFactory.nextMemory("on1", "on2"));
		return connector;
	}

	private FIFO buildFIFO(String p1, String p2, StateValue currentStatesValues) {
		FIFO fifo = new FIFO(p1, p2, currentStatesValues);
		fifos.add(fifo);
		return fifo;
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
