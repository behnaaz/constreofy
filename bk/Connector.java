package bk;

import java.util.ArrayList;
import java.util.List;

public class Connector {
	protected List<String> names;
	protected List<ArrayList<Character>> model;
	public boolean verbose;

	public Connector(List<String> names, List<ArrayList<Character>> model) {
		this.names = names;
		this.model = model;
	}

	public Connector(String[] names, List<ArrayList<Character>> model) {
		this.names = Converter.convert(names);
		this.model = model;
	}

	public Connector() {
		names = new ArrayList<String>();
		model = new ArrayList<ArrayList<Character>>();
	}

	List<ArrayList<Character>> getModel() {
		return model;
	}

	List<String> getNames() {
		return names;
	}

	public void output(Connector connector) {
		// TODO Auto-generated method stub
		
	}
}
