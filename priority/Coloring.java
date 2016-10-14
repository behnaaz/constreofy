package priority;

import java.util.ArrayList;
import java.util.List;

public class Coloring {
	List<ArrayList<Character>> model = new ArrayList<ArrayList<Character>>();

	List<ArrayList<Character>> lossyDrain() {
		List<ArrayList<Character>> lossyDrain  = new ArrayList<ArrayList<Character>>();
		lossyDrain.add(convert('x', 'x'));
		lossyDrain.add(convert('o', 'x'));
		lossyDrain.add(convert('x', 'o'));
		return lossyDrain;
	}

	List<ArrayList<Character>> prioritySync() {
		List<ArrayList<Character>> priority  = new ArrayList<ArrayList<Character>>();
		priority.add(convert('.', '.'));
		return priority;
	}

	List<ArrayList<Character>> syncDrain() {
		return sync();
	}
	
	List<ArrayList<Character>> fullFifo() {
		List<ArrayList<Character>> fullFifo  = new ArrayList<ArrayList<Character>>();
		fullFifo.add(convert('x', 'x'));
		fullFifo.add(convert('o', '.'));
		fullFifo.add(convert('.', 'o'));
		return fullFifo;
	}

	List<ArrayList<Character>> router() {
		List<ArrayList<Character>> router  = new ArrayList<ArrayList<Character>>();
		router.add(convert('x', 'x', 'x'));
		router.add(convert('.', 'o', 'o'));
		router.add(convert('.', 'o', 'x'));
		router.add(convert('.', 'x', 'o'));
		return router;
	}

	List<ArrayList<Character>> sync() {
		List<ArrayList<Character>> sync  = new ArrayList<ArrayList<Character>>();
		sync.add(convert('x', 'x'));
		sync.add(convert('o', '.'));
		sync.add(convert('.', 'o'));
		return sync;
	}

	List<ArrayList<Character>> merge() {
		List<ArrayList<Character>> priority  = new ArrayList<ArrayList<Character>>();
		priority.add(convert('x', 'x', 'x'));
		priority.add(convert('x', 'o', '.'));
		priority.add(convert('o', 'x', '.'));
		priority.add(convert('o', 'o', '.'));
		priority.add(convert('.', '.', 'o'));
		return priority;
	}
	private ArrayList<Character> convert(Character... c) {
		ArrayList<Character> result = new ArrayList<Character>();
		for (int i = 0; i < c.length; i++) {
			result.add(c[i]);
		}
		return result;
	}
	public static void main(String[] args) {
		Coloring coloring = new Coloring();
	//	coloring.exampleOne();
		//coloring.clear();
		coloring.exampleTwo();
	}

	private void exampleTwo() {
		add(router(), -1, -1);
		add(fullFifo(), 1, 0);
		add(router(), 4, 0);
		add(prioritySync(), 7, 0);
		add(syncDrain(), 9, 0);
		add(router(), 11, 0);
		add(lossyDrain(), 13, 0);
		add(syncDrain(), 16, 0);
		add(prioritySync(), 18, 1);
		output();
	}

	void clear() {
		model = new ArrayList<ArrayList<Character>>();
	}

	void exampleOne() {
		add(prioritySync(), -1, -1);
		add(merge(), 0, 0);//flip
		add(sync(), 0, 0);
		add(merge(), 6, 0);
		output();
	}

	private void output() {
		for (int i = 0; i < model.size(); i++) {
		//	System.out.println(i+1);
			for (int j = 0; j < model.get(i).size(); j++) {
				System.out.print(" " + model.get(i).get(j));
			}
			System.out.println(model.get(i).size());
		}
		System.out.println(model.size());
	}

	private void add(List<ArrayList<Character>> a, int j1, int j2) {
		if (model.size() == 0) {
			model = a;
			return;
		}
		
		List<ArrayList<Character>> newModel = new ArrayList<ArrayList<Character>>();
		for (int i = 0; i < model.size(); i++) {
			for (int j = 0; j < a.size(); j++) {
				if (isCompatibel(model.get(i).get(j1), a.get(j).get(j2))) {
					newModel.add(connect(model.get(i), a.get(j)));
				}
			}
		}
		
		model = newModel;
	}

	private ArrayList<Character> connect(final ArrayList<Character> t1, final List<Character> t2) {
		ArrayList<Character> result = new ArrayList<Character>();
		result.addAll(t1);
		result.addAll(t2);
		return result;
	}

	private boolean isCompatibel(Character c1, Character c2) {
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
}
