package priority;

import static java.util.stream.Collectors.joining;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class AbstractConnector {
	boolean isTex = true;
	String newLine = isTex ? "\\\\ \r\n \\hline" : "";

	protected List<String> names;
	public boolean verbose;

	public AbstractConnector(List<String> names) {
		this.names = names;
	}

	public AbstractConnector(String... names) {
		this.names = convert(names);
	}

	abstract void output(OutputStreamWriter out);

	public List<String> getNames() {
		return names;
	}

	List<String> convert(String... c) {
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < c.length; i++) {
			result.add(c[i]);
		}
		return result;
	}

	ArrayList<Character> concat(ArrayList<Character> t1, List<Character> t2) {
		ArrayList<Character> result = new ArrayList<Character>();
		result.addAll(t1);
		result.addAll(t2);
		return result;
	}

	String spaced(Optional<String> content) {
		return Stream.generate(() -> "&").limit(1).collect(joining()).concat(content.get());
	}

	String spaced(int n, Optional<String> content) {
		if (isTex)
			return spaced(content);

		return Stream.generate(() -> " ").limit(n).collect(joining()).concat(content.get());
	}
}