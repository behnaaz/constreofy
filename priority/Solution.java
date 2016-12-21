package priority;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static priority.Constraint.TILDE;
import static priority.Constraint.BULLET;
import static priority.Constraint.CIRC;
import static priority.Constraint.CURRENT_MEMORY;
import static priority.Constraint.NEXT_MEMORY;

public class Solution {
	private static final String NEG = "!";
	Set<String> flow = new HashSet<String>();
	Set<String> priority = new HashSet<String>();
	Set<String> states = new HashSet<String>();
	Set<String> nextStates = new HashSet<String>();
	private Map<String, Boolean> nextStateValues = new HashMap<String, Boolean>();

	public Solution(String[] terms) {
		for (String term : terms) {
			String[] atoms = term.trim().split(" = ");
			if (atoms[0].trim().endsWith(TILDE))
				flow.add((atoms[1].trim().equals("0")?NEG:" ")+(atoms[0].trim()));
			else if (atoms[0].trim().endsWith(BULLET) || atoms[0].trim().endsWith(CIRC) && atoms[1].trim().equals("1"))
				priority.add(atoms[0].trim());
			else if (atoms[0].trim().endsWith(NEXT_MEMORY))
				nextStates.add((atoms[1].trim().equals("0")?NEG:" ")+(atoms[0].trim()));
			else if (atoms[0].trim().endsWith(CURRENT_MEMORY))
				states.add((atoms[1].trim().equals("0")?NEG:" ")+(atoms[0].trim()));
		}
	}
	
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
		for (String state : states) {
		//	if (!state.trim().startsWith(NEG)) {
				if (state.trim().endsWith(CURRENT_MEMORY)) {
					sb.append(" [").append(state.trim()).append("] ");
				//states.add(e)	
				}
			}
	//	}
		for (String state : nextStates) {
		//	if (!state.trim().startsWith(NEG)) {
				if (state.trim().endsWith(NEXT_MEMORY)) {
					sb.append(" <").append(state.trim()).append("> ");
					boolean neg = state.trim().startsWith(NEG);
					String name = neg?state.trim().substring(1, state.trim().length()):state.trim();
					nextStateValues.put(name, neg);

					
					
				}
		//	}
		}
		return sb.toString();
	}

	Map<String, Boolean> nextStateValuess() {
		return nextStateValues;
	}
}
