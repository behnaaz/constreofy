package priority;

public class Constraint extends AbstractSemantics {
	ConstraintConnector connector;

	public static void main(String[] args) {
		Constraint constraint = new Constraint();
		constraint.exampleOne();
		constraint.exampleTwo();
	}

	void exampleTwo() {
		connector = router("a1", "a2", "a3");
		connector.output();
		connector.add(fullFifo("b1", "b2"), "b1", "a2");
		connector.output();
		connector.add(router("c1", "c2", "c3"), "c1", "b2");
		connector.output();
		connector.add(prioritySync("d1", "d2"), "d1", "c3");
		connector.output();
		connector.add(syncDrain("e1", "e2"), "e1", "d2");
		connector.output();
		connector.add(router("f2", "f1", "f3"), "f1", "e2");
		connector.output();
		connector.add(lossyDrain("g2", "g1"), "g1","f2");
		connector.output();
		connector.add(syncDrain("h1", "h2"), "h1", "f3");
		connector.output();
		connector.add(prioritySync("i2", "i1"), "i1", "h2");
		connector.output();
		connector.add(connector.connect("i2","a3"), "", "");
		connector.output();	
	}

	private ConstraintConnector lossyDrain(String p1, String p2) {
		String lossyDrain = String.format("(p2 => p1)", p1);
		return new ConstraintConnector(lossyDrain, p1, p2);
	}

	private ConstraintConnector syncDrain(String p1, String p2) {
		String syncDrain = String.format("(not (p1 and p2))", p1);
		return new ConstraintConnector(syncDrain, p1, p2);
	}

	private ConstraintConnector prioritySync(String p1, String p2) {
		String prioritySync = String.format("(p1 <=> p2)", p1);
		return new ConstraintConnector(prioritySync, p1, p2);
	}

	private ConstraintConnector fullFifo(String p1, String p2) {
		String fullFifo = String.format("(not %s)", p1);
		return new ConstraintConnector(fullFifo, p1, p2);
	}

	private ConstraintConnector router(String p1, String p2, String p3) {
		String router = String.format("(%s <=> %s or %s) and (not (%s and %s))", 
				p1, p2, p3, p2, p3);
		return new ConstraintConnector(router, p1, p2, p3);
	}

	void exampleOne() {
		connector = prioritySync("a", "b");
		connector.output();
		connector.add(merger("c", "d", "e"), "c", "b");
		connector.output();
		connector.add(sync("f", "g"), "f", "a");
		connector.output();
		connector.add(merger("h", "i", "j"), "h", "g");
		connector.output();
	}

	private ConstraintConnector sync(String p1, String p2) {
		String sync = String.format("(%s <=> %s)", p1, p2);
		return new ConstraintConnector(sync, p1, p2);
	}

	private ConstraintConnector merger(String p1, String p2, String p3) {
		String merger = String.format("(%s <=> %s or %s) and (not (%s and %s))", 
				p3, p1, p2, p1, p2);
		return new ConstraintConnector(merger, p1, p2, p3);
	}
}
