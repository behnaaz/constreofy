package priority;

import java.util.ArrayList;
import java.util.List;

public  abstract class AbstractConnector {
	protected List<String> names;
	protected List<ArrayList<Character>> model;
	public boolean verbose;

	public AbstractConnector(List<String> names, List<ArrayList<Character>> model) {
		this.names = names;
		this.model = model;
	}
	
	public AbstractConnector(String[] names, List<ArrayList<Character>> model) {
		this.names = convert(names);
		this.model = model;
	}

	public AbstractConnector() {
		names = new ArrayList<String>();
		model = new ArrayList<ArrayList<Character>>();
	}

	public List<String> getNames() {
		return names;
	}

	public List<ArrayList<Character>> getModel() {
		return model;
	}

	ArrayList<String> convert(String... c) {
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < c.length; i++) {
			result.add(c[i]);
		}
		return result;
	}
}