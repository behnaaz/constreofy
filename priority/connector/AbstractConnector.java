package priority.connector;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class AbstractConnector {
	boolean isTex = true;
	String newLine = isTex ? "\\\\ \r\n \\hline" : "";

	protected List<String> names;

	public AbstractConnector(List<String> names) {
		this.names = names;
	}

	public AbstractConnector(String... names) {
		this.names = convert(names);
	}

	public List<String> getNames() {
		return names;
	}

	public String getName(int n) {
		if (names == null || names.isEmpty() || n >= names.size() || n < 0)
			return null;
		
		return names.get(n);
	}

	List<String> convert(String... c) {
		List<String> result = new ArrayList<>();
		for (int i = 0; i < c.length; i++) {
			if (!result.contains(c[i]))
				result.add(c[i]);
		}
		return result;
	}

	ArrayList<Character> concat(ArrayList<Character> t1, List<Character> t2) {
		ArrayList<Character> result = new ArrayList<>();
		result.addAll(t1);
		result.addAll(t2);
		return result;
	}

	String spaced(Optional<String> content) {
		String temp = Stream.generate(() -> "&").limit(1).collect(joining());
		if (content.isPresent())
			temp = temp.concat(content.get());
		return temp;
	}

	String spaced(int n, Optional<String> content) {
		if (isTex)
			return spaced(content);

		String temp = Stream.generate(() -> " ").limit(n).collect(joining());
		if (content.isPresent())
			temp = temp.concat(content.get());
		return temp;
	}
}