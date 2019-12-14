package priority.src.priority.connector;

import org.apache.commons.lang3.StringUtils;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractConnector {
	boolean isTex = true;
	private static final String AMPER =  "&";//???TODO
	public static final String SPACE =  " ";
	public static final String CIRC = "circ";
	public static final String IMPLIES = " impl ";
	public static final String RIGHTLEFTARROW = " equiv ";
	public static final String NOT = " not ";
	public static final String OR = " or ";
	public static final String AND = " and ";
	public static final String TRUE = " true ";
	static final String FALSE = " false ";

	List<String> variableNames;

	AbstractConnector(List<String> variableNames) {
		this.variableNames = variableNames;
	}

	AbstractConnector(String... variableNames) {
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

	private List<String> convert(final String... c) {
		final List<String> result = new ArrayList<>();
		for (String s : c) {
			if (!result.contains(s))
				result.add(s);
		}
		return result;
	}

	ArrayList<Character> concat(ArrayList<Character> t1, List<Character> t2) {
		ArrayList<Character> result = new ArrayList<>();
		result.addAll(t1);
		result.addAll(t2);
		return result;
	}

	private String spaced(final String content) {
		String temp = Stream.generate(() -> AMPER).limit(1).collect(joining());
		if (StringUtils.isNotBlank(content))
			return temp.concat(content);
		return temp;
	}

	String spaced(final int n, final String content) {
		if (isTex)
			return spaced(content);

		String temp = Stream.generate(() -> SPACE).limit(n).collect(joining());
		if (StringUtils.isNotBlank(content))
			return temp.concat(content);
		return temp;
	}
}