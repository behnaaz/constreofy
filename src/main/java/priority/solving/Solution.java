package priority.solving;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import priority.states.StateValue;
import priority.states.StateVariableValue;

import static priority.Variable.*;
import static priority.connector.AbstractConnector.CIRC;

@EqualsAndHashCode
public class Solution {
	private static final String ONE = "1";
	private static final String SPACE = " ";
	private static final String REGEX_EQUAL = " = ";
	private static final String ZERO = "0";
	private static final String NEG = "!";
	private static final String EMPTY = "";
	@Getter
	private Set<String> flowVariables = new HashSet<>();
	@Getter
	private Set<String> priority = new HashSet<>();
	@Getter
	private Set<String> fromVariables = new HashSet<>();
	@Getter
	private Set<String> toVariables = new HashSet<>();
	@Getter
	private StateValue nextStateValue;

	public Solution(String[] terms) {
		for (String term : terms) {
			String[] atoms = term.trim().split(REGEX_EQUAL);
			process(atoms);
		}
		nextStateValue = buildNextStateValues();
	}

	public StateValue buildNextStateValues() {//TODO eftezah
		StateValue res = StateValue.builder().build();
		for (String t : toVariables)
			res.add(makeStateVariable(t, true));
		return res;
	}

	private void process(String[] atoms) {//TODO
		if (atoms[0].trim().endsWith(TILDE) && !isNegative(atoms[1])) {
			String name = (atoms[1].trim().equals(ZERO)?NEG:SPACE)+(atoms[0].trim()).trim();
			name = name.trim();
			flowVariables.add(name);
		}
		else if (atoms[0].trim().endsWith(BULLET) || atoms[0].trim().endsWith(CIRC) && ONE.equals(atoms[1].trim()))
			priority.add(atoms[0].trim());
		else if (atoms[0].trim().endsWith(NEXT_MEMORY) && !isNegative(atoms[1])) {
			toVariables.add((atoms[1].trim().equals(ZERO)?NEG:EMPTY)+(atoms[0].trim()));
		}
		else if (atoms[0].trim().endsWith(CURRENT_MEMORY) && !isNegative(atoms[1]))
			fromVariables.add((atoms[1].trim().equals(ZERO)?NEG:EMPTY)+(atoms[0].trim()));
	}
	
	private boolean isNegative(String atom) {
		return atom.trim().equals(ZERO);
	}

	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean withPriority) {
		StringBuilder sb = new StringBuilder();		
		sb.append(" [");
		for (String state : fromVariables) {
			if (!state.trim().startsWith(NEG)) {
				//sb.append(state.trim()).append(' ');
				StateVariableValue temp = makeStateVariable(state);
			//	stateValues.add(temp);
				sb.append(temp.toString()).append(SPACE);
			}
		}
		sb.append("] ------ { ");
		
		for (String nodeflow : flowVariables) {
			if (!nodeflow.trim().startsWith(NEG)) {
				if (nodeflow.trim().endsWith(TILDE))
					sb.append(SPACE).append(nodeflow.trim());
				if (priority.contains(nodeflow.trim().replaceFirst(TILDE, BULLET)) || priority.contains(nodeflow.trim().replaceFirst(TILDE, CIRC)))
					sb.append(SPACE).append(nodeflow.trim().replaceFirst(TILDE, "PRIORITY"));
			}
		}

		sb.append(" } -------> (");
		for (String state : toVariables) {
			if (!state.trim().startsWith(NEG)) {
				StateVariableValue temp = makeStateVariable(state);//no
			///	nextStateValue.add(temp);
				sb.append(temp.toString()).append(SPACE);
			}
		}
		sb.append(") ");
	
		return sb.toString().replaceAll("\\ \\ ", "\\ ").replaceAll("\\ }", "}").replaceAll("\\ \\)", "\\)").replaceAll("\\ ]", "]").replaceAll("\\{ \\ ", "\\{");
	}

	public StateVariableValue makeStateVariable(String state) {
		return makeStateVariable(state.trim(), false);
	}
	
	public StateVariableValue makeStateVariable(String state, boolean convertNextToCurrent) {
		boolean neg = state.trim().startsWith(NEG);
		String name = neg ? EMPTY /*state.trim().substring(1, state.trim().length())*/:state.trim();
		if (convertNextToCurrent)
			name = name.replace(NEXT_MEMORY, CURRENT_MEMORY);
		Optional<Boolean> optTemp = neg ? Optional.of(Boolean.FALSE) : Optional.of(Boolean.TRUE);
		return StateVariableValue.builder().stateName(name.trim()).value(optTemp).build();
	}

	public String readable() {
		return new StringBuilder()
							.append("(")
							.append(fromVariables.stream().map(e -> e + ", ").collect(Collectors.joining()))
							.append(") ----{ ")
							.append(flowVariables.stream().map(e -> e + ", ").collect(Collectors.joining()))
							.append("} ----> (")
							.append(toVariables.stream().map(e -> e + ", ").collect(Collectors.joining()))
							.append(")")
				.toString();
	}
}
