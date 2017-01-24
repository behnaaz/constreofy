package priority.solving;

import java.util.HashSet;
import java.util.Set;

import priority.common.Constants;
import priority.states.StateValue;
import priority.states.StateVariableValue;

public class Solution  implements Constants  {
	private static final String ZERO = "0";
	private static final String NEG = "!";
	private Set<String> flow = new HashSet<>();
	private Set<String> priority = new HashSet<>();
	private Set<String> states = new HashSet<>();
	private Set<String> nextStates = new HashSet<>();
	private StateValue nextStateValues = new StateValue();

	public Solution(String[] terms) {
		for (String term : terms) {
			String[] atoms = term.trim().split(" = ");
			if (atoms[0].trim().endsWith(TILDE))
				flow.add((atoms[1].trim().equals(ZERO)?NEG:" ")+(atoms[0].trim()));
			else if (atoms[0].trim().endsWith(BULLET) || atoms[0].trim().endsWith(CIRC) && atoms[1].trim().equals("1"))
				priority.add(atoms[0].trim());
			else if (atoms[0].trim().endsWith(NEXT_MEMORY))
				nextStates.add((atoms[1].trim().equals(ZERO)?NEG:" ")+(atoms[0].trim()));
			else if (atoms[0].trim().endsWith(CURRENT_MEMORY))
				states.add((atoms[1].trim().equals(ZERO)?NEG:" ")+(atoms[0].trim()));
		}
	}
	
	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean withPriority) {
		StringBuilder sb = new StringBuilder();
		for (String nodeflow : flow) {
			if (!nodeflow.trim().startsWith(NEG)) {
				if (nodeflow.trim().endsWith(TILDE))
					sb.append(" ").append(nodeflow.trim());
				if (priority.contains(nodeflow.trim().replaceFirst(TILDE, BULLET)) || priority.contains(nodeflow.trim().replaceFirst(TILDE, CIRC)))
					sb.append(" ").append(nodeflow.trim().replaceFirst(TILDE, "PRIORITY"));
			}
		}
		
		StringBuilder from = new StringBuilder();
		from.append(" [");
		for (String state : states) {
				if (state.trim().endsWith(CURRENT_MEMORY)) {
					from.append(state.trim()).append(' ');
				}
			}
		from.append("] ------ { ");
		
		StringBuilder to = new StringBuilder();
		to.append(" } -------> (");
		for (String state : nextStates) {
				if (state.trim().endsWith(NEXT_MEMORY)) {
					boolean neg = state.trim().startsWith(NEG);
					to.append(state).append(' ');
					String name = neg?state.trim().substring(1, state.trim().length()):state.trim();
					nextStateValues.add(new StateVariableValue(name, !neg));
				}
		}
		to.append(") ");
	
		return from.append(sb.toString()).append(to.toString()).toString();
	}

	public StateValue nextStateValuess() {
		return nextStateValues;
	}
}
