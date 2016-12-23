package priority;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

public class ExampleMaker {
	ConstraintConnector connector;
	private int n;
	private OutputStreamWriter out;

	public ExampleMaker(int n, OutputStreamWriter out) {
		this.n = n;
		this.out = out;
	}

	private ConstraintConnector exampleTwo() throws IOException {
		ConnectorFactory factory = new ConnectorFactory();
		connector = factory.router("a1", "a2", "a3");
		connector.add(factory.fullFifo("b1", "b2"), "b1", "a2");
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

	ConstraintConnector getExample(Map<String, Boolean> currentStatesValues, int... ios) throws IOException {
		ConstraintConnector example = null;
		if (n == 1)
			example = exampleOne();
		else if (n == 2)
			example = exampleTwo();
		else if (n < 0)
			example = sequencer(Math.abs(n));
		else
			example = xaction(currentStatesValues, ios);

		example.output(out);
		example.close();
		return example;
	}
	
	private ConstraintConnector xaction(Map<String, Boolean> currentStatesValues, int... ios) {
		ConnectorFactory factory = new ConnectorFactory();
		connector = factory.fifo("AB1", "AB2", currentStatesValues.get(new ConnectorFactory().mem("ab1", "ab2")));
		connector.add(factory.writer("AB1", ios[0]), "ab1", "ab1");
		connector.add(factory.replicator("b1", "b2", "b3"), "b1", "AB2");
		//connector.add(factory.sync("BC1", "BC2"), "BC1", "b2");
		connector.add(factory.replicator("c1", "c2"), "c1", "b2");//"BC2");
		connector.add(factory.fifo("CD1", "CD2", currentStatesValues.get(new ConnectorFactory().mem("cd1", "cd2"))),
				"CD1", "c2");
	///	connector.add(factory.replicator("d1", "d2"), "d1", "CD2");
		connector.add(factory.fifo("DE1", "DE2", currentStatesValues.get(new ConnectorFactory().mem("de1", "de2"))),
				"DE1", "CD2");
		connector.add(factory.router("e1", "e2", "e3"), "e1", "DE2");
		connector.add(factory.fifo("EL1", "EL2", currentStatesValues.get(new ConnectorFactory().mem("el1", "el2"))),
				"EL1", "e2");
		//connector.add(factory.sync("EG1", "EG2"), "EG1", "e3");
		connector.add(factory.replicator("g3", "g1", "g2"), "g3", "e3");
		connector.add(factory.fifo("FG2", "FG1", currentStatesValues.get(new ConnectorFactory().mem("fg2", "fg1"))),
				"FG2", "g1");
		connector.add(factory.fifo("GH1", "GH2", currentStatesValues.get(new ConnectorFactory().mem("gh1", "gh2"))),
				"GH1", "g2");
		
		//connector.add(factory.replicator("h1", "h2"), "h1", "GH2");
		//connector.add(factory.sync("HP1", "HP2"), "HP1", "h2");// prio
		//connector.add(factory.sync("BI1", "BI2"), "BI1", "b3");
		connector.add(factory.router("i1", "i2", "i3"), "i1", "b3");
		connector.add(factory.fifo("IJ1", "IJ2", currentStatesValues.get(factory.mem("ij1", "ij2"))), "IJ1", "i2");
	//	connector.add(factory.replicator("j1", "j2"), "j1", "IJ2");
		connector.add(factory.fifo("JK1", "JK2", currentStatesValues.get(factory.mem("jk1", "jk2"))), "JK1", "j2");
		connector.add(factory.router("JK2", "k2", "k3"), "JK2", "JK2");
	//	connector.add(factory.sync("KL1", "KL2"), "KL1", "k2");
		connector.add(factory.join("EL2", "l3", "l2"), "EL2", "EL2");
		// kl2 l3
		connector.add(factory.fifo("LM1", "LM2", currentStatesValues.get(factory.mem("lm1", "lm2"))), "LM1", "l2");
	//	connector.add(factory.syncDrain("KO1", "KO2"), "KO1", "k3");
	//	connector.add(factory.router("p2", "p1", "p3"), "p2", "HP2");
		connector.add(factory.router("p2", "p1", "p3"), "p2", "GH2");
/*
		connector.add(factory.sync("OP2", "OP1"), "OP2", "p1");
		connector.add(factory.syncDrain("IP2", "IP1"), "IP2", "p3");
		// i3 ip1
		 */
		connector.add(factory.replicator("o2", "o1", "o3"), "o2", "p1");
		// ko2 o3
		connector.add(factory.fifo("ON1", "ON2", currentStatesValues.get(factory.mem("on1", "on2"))), "ON1", "o1");
	/*	connector.add(factory.sync("KL2", "l3"), "KL2", "l3");
		connector.add(factory.sync("IP1", "i3"), "IP1", "i3");
		connector.add(factory.sync("KO2", "o3"), "KO2", "o3");
*/
		
		connector.states(factory.mem("ab1", "ab2"), factory.mem("cd1", "cd2"), factory.mem("de1", "de2"),
				factory.mem("el1", "el2"), factory.mem("fg2", "fg1"), factory.mem("gh1", "gh2"),
				factory.mem("ij1", "ij2"), factory.mem("jk1", "jk2"), factory.mem("lm1", "lm2"),
				factory.mem("on1", "on2"));
		connector.nextStates(factory.nextMem("ab1", "ab2"), factory.nextMem("cd1", "cd2"),
				factory.nextMem("de1", "de2"), factory.nextMem("el1", "el2"), factory.nextMem("fg2", "fg1"),
				factory.nextMem("gh1", "gh2"), factory.nextMem("ij1", "ij2"), factory.nextMem("jk1", "jk2"),
				factory.nextMem("lm1", "lm2"), factory.nextMem("on1", "on2"));
		return connector;
	}


	private ConstraintConnector xaction2(Map<String, Boolean> currentStatesValues, int... ios) {
		ConnectorFactory factory = new ConnectorFactory();
		connector = factory.fifo("AB1", "AB2", currentStatesValues.get(new ConnectorFactory().mem("ab1", "ab2")));
		connector.add(factory.writer("o1", ios[0]), "o1", "ab1");
		connector.add(factory.replicator("b1", "b2", "b3"), "b1", "AB2");
		connector.add(factory.sync("BC1", "BC2"), "BC1", "b2");
		connector.add(factory.replicator("c1", "c2"), "c1", "BC2");
		connector.add(factory.fifo("CD1", "CD2", currentStatesValues.get(new ConnectorFactory().mem("cd1", "cd2"))),
				"CD1", "c2");
		connector.add(factory.replicator("d1", "d2"), "d1", "CD2");
		connector.add(factory.fifo("DE1", "DE2", currentStatesValues.get(new ConnectorFactory().mem("de1", "de2"))),
				"DE1", "d2");
		connector.add(factory.router("e1", "e2", "e3"), "e1", "DE2");
		connector.add(factory.fifo("EL1", "EL2", currentStatesValues.get(new ConnectorFactory().mem("el1", "el2"))),
				"EL1", "e2");
		connector.add(factory.sync("EG1", "EG2"), "EG1", "e3");
		connector.add(factory.replicator("g3", "g1", "g2"), "g3", "EG2");
		connector.add(factory.fifo("FG2", "FG1", currentStatesValues.get(new ConnectorFactory().mem("fg2", "fg1"))),
				"FG2", "g1");
		connector.add(factory.fifo("GH1", "GH2", currentStatesValues.get(new ConnectorFactory().mem("gh1", "gh2"))),
				"GH1", "g2");
		/*
		connector.add(factory.replicator("h1", "h2"), "h1", "GH2");
		connector.add(factory.sync("HP1", "HP2"), "HP1", "h2");// prio
		connector.add(factory.sync("BI1", "BI2"), "BI1", "b3");
		connector.add(factory.router("i1", "i2", "i3"), "i1", "BI2");
		connector.add(factory.fifo("IJ1", "IJ2", currentStatesValues.get(factory.mem("ij1", "ij2"))), "IJ1", "i2");
		connector.add(factory.replicator("j1", "j2"), "j1", "IJ2");
		connector.add(factory.fifo("JK1", "JK2", currentStatesValues.get(factory.mem("jk1", "jk2"))), "JK1", "j2");
		connector.add(factory.router("k1", "k2", "k3"), "k1", "JK2");
		connector.add(factory.sync("KL1", "KL2"), "KL1", "k2");
		connector.add(factory.join("l1", "l3", "l2"), "l1", "EL2");
		// kl2 l3
		connector.add(factory.fifo("LM1", "LM2", currentStatesValues.get(factory.mem("lm1", "lm2"))), "LM1", "l2");
		connector.add(factory.syncDrain("KO1", "KO2"), "KO1", "k3");
		connector.add(factory.router("p2", "p1", "p3"), "p2", "HP2");
		connector.add(factory.sync("OP2", "OP1"), "OP2", "p1");
		connector.add(factory.syncDrain("IP2", "IP1"), "IP2", "p3");
		// i3 ip1
		 */
		connector.add(factory.replicator("o2", "o1", "o3"), "o2", "OP1");
		// ko2 o3
		connector.add(factory.fifo("ON1", "ON2", currentStatesValues.get(factory.mem("on1", "on2"))), "ON1", "o1");
		connector.add(factory.sync("KL2", "l3"), "KL2", "l3");
		connector.add(factory.sync("IP1", "i3"), "IP1", "i3");
		connector.add(factory.sync("KO2", "o3"), "KO2", "o3");

		
		connector.states(factory.mem("ab1", "ab2"), factory.mem("cd1", "cd2"), factory.mem("de1", "de2"),
				factory.mem("el1", "el2"), factory.mem("fg2", "fg1"), factory.mem("gh1", "gh2"),
				factory.mem("ij1", "ij2"), factory.mem("jk1", "jk2"), factory.mem("lm1", "lm2"),
				factory.mem("on1", "on2"));
		connector.nextStates(factory.nextMem("ab1", "ab2"), factory.nextMem("cd1", "cd2"),
				factory.nextMem("de1", "de2"), factory.nextMem("el1", "el2"), factory.nextMem("fg2", "fg1"),
				factory.nextMem("gh1", "gh2"), factory.nextMem("ij1", "ij2"), factory.nextMem("jk1", "jk2"),
				factory.nextMem("lm1", "lm2"), factory.nextMem("on1", "on2"));
		return connector;
	}

	private ConstraintConnector sequencer(int n) {
		ConnectorFactory factory = new ConnectorFactory();
		for (int i = 1; i <= n; i++) {
			if (i == 1)
				connector = factory.fullFifo("a" + i, "b" + i);
			else
				connector.add(factory.fifo("a" + i, "b" + i), "a" + i, "e" + (i - 1), true);
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
}
