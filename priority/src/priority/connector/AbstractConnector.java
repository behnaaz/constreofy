package priority.connector;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import priority.init.FileUser;

public abstract class AbstractConnector extends FileUser {
	boolean isTex = true;
	String newLine = isTex ? "\\\\ \r\n \\hline" : STRING_EMPTY;

	protected List<String> variableNames;

	public AbstractConnector(List<String> variableNames) {
		this.variableNames = variableNames;
	}

	public AbstractConnector(String... variableNames) {
		this.variableNames = convert(variableNames);
	}

	public List<String> getVariableNames() {
		return variableNames;
	}

	public String getName(int n) {
		if (variableNames == null || variableNames.isEmpty() || n >= variableNames.size() || n < 0)
			return null;
		
		return variableNames.get(n);
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
		String temp = Stream.generate(() -> AMPER).limit(1).collect(joining());
		if (content.isPresent())
			temp = temp.concat(content.get());
		return temp;
	}

	String spaced(int n, Optional<String> content) {
		if (isTex)
			return spaced(content);

		String temp = Stream.generate(() -> SPACE).limit(n).collect(joining());
		if (content.isPresent())
			temp = temp.concat(content.get());
		return temp;
	}
}