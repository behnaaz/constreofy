package priority;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class Coloring extends AbstractConnector {
	public Coloring() {
		super();
	}

	class Connector extends AbstractConnector {
		public Connector(String[] names, List<ArrayList<Character>> model) {
			super(names, model);
		}

		public Connector(List<String> names, List<ArrayList<Character>> model) {
			super(names, model);
		}

		Connector ground(String... boundaries) {
			List<ArrayList<Character>> result = new ArrayList<ArrayList<Character>>();
			for (int i = 0; i < model.size(); i++) {
				boolean copy = true;
				for (int j = 0; j < boundaries.length; j++) {
					if (model.get(i).get(names.indexOf(boundaries[j])) == 'o') {
						copy = false;
						break;
					}
				}
				if (copy)
					result.add(model.get(i));
			}
			return new Connector(names, result);
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

		void output() {
			for (int i = 0; i < model.size(); i++) {
				if (i == 0) {
					System.out.println("Nodes : "+ model.get(i).size() + " Lines : " + model.size());
					for (int j = 0; j < model.get(i).size(); j++) {
						System.out.print(spaced(names.get(j).length(), Optional.of(names.get(j))));
					}
					System.out.println();
				}
				for (int j = 0; j < model.get(i).size(); j++) {
					System.out.print(spaced(names.get(j).length()+1, Optional.of(model.get(i).get(j).toString())));
				}
				System.out.println();
			}
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

		void add(Connector newTable, String newPortName, String existingPortName) {
			int newPort = newPortName == null ? -1 : newTable.getNames().indexOf(newPortName);
			int existing = existingPortName == null ? -1 : names.indexOf(existingPortName);

			if (model.size() == 0) {
				this.model = newTable.getModel();
				this.names = newTable.getNames();
				return;
			}
			
			if (model.size() > 0 && verbose )
				System.out.println("Connecting " +  newPortName + " to " + existingPortName);
			
			List<ArrayList<Character>> newModel = new ArrayList<ArrayList<Character>>();
			for (int i = 0; i < model.size(); i++) {
				for (int j = 0; j < newTable.getModel().size(); j++) {
					if (isCompatibel(model.get(i).get(existing), newTable.getModel().get(j).get(newPort))) {
						newModel.add(concat(model.get(i), newTable.getModel().get(j)));
					}
				}
			}
			
			this.model = newModel;
			this.names.addAll(newTable.getNames());
		}

		Connector connect(String name1, String name2) {
			int port1 = name1 == null ? -1 : names.indexOf(name1);
			int port2 = name2 == null ? -1 : names.indexOf(name2);
			List<ArrayList<Character>> result = new ArrayList<ArrayList<Character>>();

			for (int i = 0; i < model.size(); i++) {
				boolean copy = true;
				if ((model.get(i).get(port1) == 'x' && model.get(i).get(port2) != 'x') ||
					(model.get(i).get(port1) != 'x' && model.get(i).get(port2) == 'x'))
					copy = false;

				if (copy)
					result.add(model.get(i));
			}

			return new Connector(names, result);
		}
	}

	Connector connector;

	Connector lossyDrain(String src, String snk) {
		List<ArrayList<Character>> lossyDrain  = new ArrayList<ArrayList<Character>>();
		lossyDrain.add(convert('x', 'x'));
		lossyDrain.add(convert('o', 'x'));
		lossyDrain.add(convert('x', 'o'));
		return new Connector(new String[]{src, snk}, lossyDrain);
	}

	Connector prioritySync(String src, String snk) {
		List<ArrayList<Character>> priority  = new ArrayList<ArrayList<Character>>();
		priority.add(convert('.', '.'));
		return new Connector(new String[]{src, snk}, priority);
	}

	Connector syncDrain(String c1, String c2) {
		return sync(c1, c2);
	}
	
	Connector fullFifo(String src, String snk) {
		List<ArrayList<Character>> fullFifo  = new ArrayList<ArrayList<Character>>();
		fullFifo.add(convert('x', 'x'));
		fullFifo.add(convert('o', '.'));
		fullFifo.add(convert('.', 'o'));
		return new Connector(new String[]{src, snk}, fullFifo);
	}

	Connector router(String p1, String p2, String p3) {
		List<ArrayList<Character>> router  = new ArrayList<ArrayList<Character>>();
		router.add(convert('x', 'x', 'x'));
		router.add(convert('.', 'o', 'o'));
		router.add(convert('.', 'o', 'x'));
		router.add(convert('.', 'x', 'o'));
		return new Connector(new String[]{p1, p2, p3}, router);
	}

	Connector sync(String src, String snk) {
		List<ArrayList<Character>> sync  = new ArrayList<ArrayList<Character>>();
		sync.add(convert('x', 'x'));
		sync.add(convert('o', '.'));
		sync.add(convert('.', 'o'));
		return new Connector(new String[]{src, snk}, sync);
	}

	Connector merger(String p1, String p2, String p3) {
		List<ArrayList<Character>> merger  = new ArrayList<ArrayList<Character>>();
		merger.add(convert('x', 'x', 'x'));
		merger.add(convert('x', 'o', '.'));
		merger.add(convert('o', 'x', '.'));
		merger.add(convert('o', 'o', '.'));
		merger.add(convert('.', '.', 'o'));
		return new Connector(new String[]{p1, p2, p3}, merger);
	}
	
	ArrayList<Character> convert(Character... c) {
		ArrayList<Character> result = new ArrayList<Character>();
		for (int i = 0; i < c.length; i++) {
			result.add(c[i]);
		}
		return result;
	}
	public static void main(String[] args) {
		Coloring coloring = new Coloring();
		//coloring.exampleOne();
		coloring.exampleTwo();
	}

	private void exampleTwo() {
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
