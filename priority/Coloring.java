package priority;

import java.util.ArrayList;
import java.util.List;

public class Coloring {
	class Connector {
		private List<String> names = new ArrayList<String>();
		public List<String> getNames() {
			return names;
		}

		public List<ArrayList<Character>> getModel() {
			return model;
		}

		private List<ArrayList<Character>> model = new ArrayList<ArrayList<Character>>();
		private boolean verbose = true;

		ArrayList<String> convert(String... c) {
			ArrayList<String> result = new ArrayList<String>();
			for (int i = 0; i < c.length; i++) {
				result.add(c[i]);
			}
			return result;
		}

		public Connector(String[] names, List<ArrayList<Character>> model) {
			this.names = convert(names);
			this.model = model;
		}

		public Connector(List<String> names, List<ArrayList<Character>> model) {
			this.names = names;
			this.model = model;
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

		ArrayList<Character> connect(ArrayList<Character> t1, List<Character> t2) {
			ArrayList<Character> result = new ArrayList<Character>();
			result.addAll(t1);
			result.addAll(t2);
			return result;
		}

		void output() {
			for (int i = 0; i < model.size(); i++) {
				if (i == 0) {
					System.out.println("Nodes : "+ model.get(i).size() + " Lines : " + model.size());
					for (int j = 0; j < model.get(i).size(); j++) {
						System.out.print(" " + names.get(j));
					}
					System.out.println();
				}
				for (int j = 0; j < model.get(i).size(); j++) {
					System.out.print(" " + model.get(i).get(j));
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
						newModel.add(connect(model.get(i), newTable.getModel().get(j)));
					}
				}
			}
			
			this.model = newModel;
			this.names.addAll(newTable.getNames());
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
		coloring.exampleOne();
		//coloring.clear();
		//coloring.exampleTwo();
	}

	private void exampleTwo() {
		/*add(router("a1", "a2", "a3"), -1, -1);
		add(fullFifo("b1", "b2"), 1, 0);
		add(router("c1", "c2", "c3"), 4, 0);
		add(prioritySync("d1", "d2"), 7, 0);
		add(syncDrain("e1", "e2"), 9, 0);
		add(router("f1", "f2", "f3"), 11, 0);
		add(lossyDrain("g2", "g1"), 13, 0);
		add(syncDrain("h1", "h2"), 16, 0);
		add(prioritySync("i2", "i1"), 18, 1);
		connect(2,20);
		model = ground(0, 6);
		output();*/
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
