package priority.src.bk;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class PriorityColoring implements Connectable {
	boolean verbose = true;
	public PriorityColoring() {
		super();
	}

	@Override
	public Connector merger(String p1, String p2, String p3) {
		List<ArrayList<Character>> merger  = new ArrayList<ArrayList<Character>>();
		merger.add(Converter.convert('x', 'x', 'x'));
		merger.add(Converter.convert('x', 'o', '.'));
		merger.add(Converter.convert('o', 'x', '.'));
		merger.add(Converter.convert('o', 'o', '.'));
		merger.add(Converter.convert('.', '.', 'o'));
		return new Connector(new String[]{p1, p2, p3}, merger);
	}
	@Override
	public Connector prioritySync(String src, String snk) {
		List<ArrayList<Character>> priority  = new ArrayList<ArrayList<Character>>();
		priority.add(Converter.convert('.', '.'));
		return new Connector(new String[]{src, snk}, priority);
	}
	@Override
	public Connector syncDrain(String c1, String c2) {
		return sync(c1, c2);
	}
	
	@Override
	public Connector fullFifo(String src, String snk) {
		List<ArrayList<Character>> fullFifo  = new ArrayList<ArrayList<Character>>();
		fullFifo.add(Converter.convert('x', 'x'));
		fullFifo.add(Converter.convert('o', '.'));
		fullFifo.add(Converter.convert('.', 'o'));
		return new Connector(new String[]{src, snk}, fullFifo);
	}

	@Override
	public Connector router(String p1, String p2, String p3) {
		List<ArrayList<Character>> router  = new ArrayList<ArrayList<Character>>();
		router.add(Converter.convert('x', 'x', 'x'));
		router.add(Converter.convert('.', 'o', 'o'));
		router.add(Converter.convert('.', 'o', 'x'));
		router.add(Converter.convert('.', 'x', 'o'));
		return new Connector(new String[]{p1, p2, p3}, router);
	}

	@Override
	public Connector sync(String src, String snk) {
		List<ArrayList<Character>> sync  = new ArrayList<ArrayList<Character>>();
		sync.add(Converter.convert('x', 'x'));
		sync.add(Converter.convert('o', '.'));
		sync.add(Converter.convert('.', 'o'));
		return new Connector(new String[]{src, snk}, sync);
	}

	@Override
	public Connector lossySync(String src, String snk) {
		List<ArrayList<Character>> lossyDrain = new ArrayList<ArrayList<Character>>();
		lossyDrain.add(Converter.convert('x', 'x'));
		lossyDrain.add(Converter.convert('o', 'x'));
		lossyDrain.add(Converter.convert('x', 'o'));
		return new Connector(new String[] { src, snk }, lossyDrain);
	}
	
	boolean isCompatibel(Character c1, Character c2) {
		if (c1 == '.' && c2 == '.')
			return true;

		if (c1 == 'x' && c2 == 'x')
			return true;

		if (c1 == '.' && c2 == 'o')
			return true;

		if (c1 == 'o' && c2 == '.')
			return true;

		return false;
	}

	Connector add(Connector newTable, String newPortName, Connector existingConnector) {
		int newPort = newPortName == null ? -1 : newTable.getNames().indexOf(newPortName);
		int existing = (existingConnector  == null || existingConnector.getNames() == null) ? 
				-1 : existingConnector.getNames().indexOf(existingConnector.getNames());

		if (existingConnector == null) {
			return new Connector(newTable.getNames(), newTable.getModel());
		}

		if (existingConnector.getModel().size() > 0 && verbose)
			System.out.println("Connecting " + newPortName + " to " + existingConnector.getNames());

		List<ArrayList<Character>> newModel = new ArrayList<ArrayList<Character>>();
		for (int i = 0; i < existingConnector.getModel().size(); i++) {
			for (int j = 0; j < newTable.getModel().size(); j++) {
				if (isCompatibel(existingConnector.getModel().get(i).get(existing), newTable.getModel().get(j).get(newPort))) {
					newModel.add(concat(existingConnector.getModel().get(i), newTable.getModel().get(j)));
				}
			}
		}

		existingConnector.getNames().addAll(newTable.getNames());
		return new Connector(existingConnector.getNames(), newModel);
	}

	Connector connect(String name1, String name2, Connector connector) {
		int port1 = name1 == null ? -1 : connector.getNames().indexOf(name1);
		int port2 = name2 == null ? -1 : connector.getNames().indexOf(name2);
		List<ArrayList<Character>> result = new ArrayList<ArrayList<Character>>();

		for (int i = 0; i < connector.getModel().size(); i++) {
			boolean copy = true;
			if ((connector.getModel().get(i).get(port1) == 'x' && connector.getModel().get(i).get(port2) != 'x')
					|| (connector.getModel().get(i).get(port1) != 'x' && connector.getModel().get(i).get(port2) == 'x'))
				copy = false;

			if (copy)
				result.add(connector.getModel().get(i));
		}

		return new Connector(connector.getNames(), result);
	}

	ArrayList<Character> concat(ArrayList<Character> t1, List<Character> t2) {
		ArrayList<Character> result = new ArrayList<Character>();
		result.addAll(t1);
		result.addAll(t2);
		return result;
	}

	String spaced(int n, Optional<String> content) {
		return Stream.generate(() -> " ").limit(n).collect(joining()).concat(content.get());
	}

	Connector ground(Connector connector, String... boundaries) {
		List<ArrayList<Character>> result = new ArrayList<ArrayList<Character>>();
		for (int i = 0; i < connector.getModel().size(); i++) {
			boolean copy = true;
			for (int j = 0; j < boundaries.length; j++) {
				if (connector.getModel().get(i).get(connector.getNames().indexOf(boundaries[j])) == 'o') {
					copy = false;
					break;
				}
			}
			if (copy)
				result.add(connector.getModel().get(i));
		}
		return new Connector(connector.getNames(), result);
	}

	@Override
	public void output(Connector connector) {
		// TODO Auto-generated method stub
		
	}
}
