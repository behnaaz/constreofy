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
		connector.add(factory.lossyDrain("g2", "g1"), "g1","f2");
		connector.add(factory.syncDrain("h1", "h2"), "h1", "f3");
		connector.add(factory.prioritySync("i2", "i1"), "i1", "h2");
		connector.add(connector.connect("i2","a3"), "", "");
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

	ConstraintConnector getExample(Map<String, Boolean> currentStatesValues) throws IOException {
		ConstraintConnector example = null;
		if (n == 1)
			example = exampleOne(); 
		else if	(n == 2)
			example = exampleTwo();
		else if (n < 0)
			example = sequencer(Math.abs(n));
		else
			example = xaction(currentStatesValues);
		
		example.output(out);
		example.close();
		return example;
	}

	private ConstraintConnector xaction(Map<String, Boolean> currentStatesValues) {
		ConnectorFactory factory = new ConnectorFactory();
		connector = factory.fifo("AB1", "AB2", currentStatesValues.get(new ConnectorFactory().mem("ab1", "ab2")));
		connector.add(factory.replicator("b1", "b2", "b3"), "b1", "AB2");
		connector.states(factory.mem("AB1", "AB2"));
		connector.nextStates(factory.nextMem("AB1", "AB2"));
		return connector;

	}
	
	private ConstraintConnector xaction2() {
		ConnectorFactory factory = new ConnectorFactory();
		connector = factory.fifo("A1C", "A2C");
		connector.add(factory.replicator("b1", "b2", "b3"), "b1", "A2C");
		connector.add(factory.sync("B1C", "B2C"), "B1C", "b2");
		connector.add(factory.replicator("c1", "c2"), "c1", "B2C");
		connector.add(factory.fifo("C1C", "C2C"), "C1C", "c2");
		connector.add(factory.replicator("d1", "d2"), "d1", "C2C");
		connector.add(factory.fifo("D1C", "D2C"), "D1C", "d2");
		connector.add(factory.router("e1", "e2", "e3"), "e1", "D2C");
		connector.add(factory.fifo("E1C", "E2C"), "E1C", "e3");
		connector.add(factory.join("f1", "f3", "f2"), "f1", "E2C");
		
		connector.add(factory.fifo("F1C", "F2C"), "F1C", "f2");
//n
		connector.add(factory.sync("N1C", "N2C"), "N2C", "f3");
		
		connector.add(factory.router("m3", "m1", "m2"), "m1", "N2C");

		connector.add(factory.fifo("M1C", "M2C"), "M2C", "m3");


		return connector;
	}

	private ConstraintConnector sequencer(int n) {
		ConnectorFactory factory = new ConnectorFactory();
		for (int i=1; i<=n; i++) {
			if (i==1)
				connector = factory.fullFifo("a"+i, "b"+i);
			else
				connector.add(factory.fifo("a"+i, "b"+i), "a"+i, "e"+(i-1), true);
			connector.add(factory.replicator("c"+i, "d"+i, "e"+i), "c"+i, "b"+i, true);
		}
	//	connector.add(factory.replicator("c"+(n-1), "d"+(n-1), "e"+(n-1)), "c"+(n-1), "b"+(n-1), true);
		//connector.add(factory.fullFifo("a"+n, "b"+n), "a"+n, "e"+(n-1), true);
	//	connector.add(factory.replicator("c"+n, "d"+n, "e"+n), "c"+n, "b"+n, true);
		//connector.add(factory.merger("h", "i", "j"), "h", "g");
		return connector;
	}
}
