package priority;

import java.util.ArrayList;
import java.util.List;

public class Coloring extends AbstractSemantics {
	public Coloring() {
		super();
	}
	ColoringConnector connector;

	ColoringConnector lossySync(String src, String snk) {
		List<ArrayList<Character>> lossySync  = new ArrayList<ArrayList<Character>>();
		lossySync.add(convert('x', 'x'));
		lossySync.add(convert('o', '.'));
		lossySync.add(convert('.', 'o'));
		return new ColoringConnector(new String[]{src, snk}, lossySync);
	}

	ColoringConnector prioritySync(String src, String snk) {
		List<ArrayList<Character>> priority  = new ArrayList<ArrayList<Character>>();
		priority.add(convert('.', '.'));
		return new ColoringConnector(new String[]{src, snk}, priority);
	}

	ColoringConnector syncDrain(String c1, String c2) {
		return sync(c1, c2);
	}
	
	ColoringConnector fullFifo(String src, String snk) {
		List<ArrayList<Character>> fullFifo  = new ArrayList<ArrayList<Character>>();
		fullFifo.add(convert('x', 'x'));
		fullFifo.add(convert('o', 'x'));
		fullFifo.add(convert('x', 'o'));
		return new ColoringConnector(new String[]{src, snk}, fullFifo);
	}

	ColoringConnector router(String p1, String p2, String p3) {
		List<ArrayList<Character>> router  = new ArrayList<ArrayList<Character>>();
		router.add(convert('x', 'x', 'x'));
		router.add(convert('.', 'o', 'o'));
		router.add(convert('.', 'o', 'x'));
		router.add(convert('.', 'x', 'o'));
		return new ColoringConnector(new String[]{p1, p2, p3}, router);
	}

	ColoringConnector sync(String src, String snk) {
		List<ArrayList<Character>> sync  = new ArrayList<ArrayList<Character>>();
		sync.add(convert('x', 'x'));
		sync.add(convert('o', '.'));
		sync.add(convert('.', 'o'));
		return new ColoringConnector(new String[]{src, snk}, sync);
	}

	ColoringConnector merger(String p1, String p2, String p3) {
		List<ArrayList<Character>> merger  = new ArrayList<ArrayList<Character>>();
		merger.add(convert('x', 'x', 'x'));
		merger.add(convert('x', 'o', '.'));
		merger.add(convert('o', 'x', '.'));
		merger.add(convert('o', 'o', '.'));
		merger.add(convert('.', '.', 'o'));
		return new ColoringConnector(new String[]{p1, p2, p3}, merger);
	}

	public static void main(String[] args) {
		Coloring coloring = new Coloring();
		//coloring.exampleOne();
		coloring.exampleTwo();
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
		connector.add(lossySync("g2", "g1"), "g1","f2");
		connector.output();
		connector.add(syncDrain("h1", "h2"), "h1", "f3");
		connector.output();
		connector.add(prioritySync("i2", "i1"), "i1", "h2");
		connector.output();
		connector = connector.connect("i2","a3");
		connector = connector.ground(new String[]{"a1", "c2", "g2"});
		connector.output();
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
		connector = connector.ground(new String[]{"d", "e", "i", "j"});
		connector.output();
	}
}
