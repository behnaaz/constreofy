package priority.states;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Builder
@EqualsAndHashCode
@ToString
public class StateValue implements Comparable<Object>, Cloneable {
	private Set<StateVariableValue> variableValues = new TreeSet<>();

	public Set<StateVariableValue> getVariableValues() {
		return variableValues;
	}

	public boolean containsKey(String m) {
		for (StateVariableValue v : variableValues) { 
			if (v.getStateName().equals(m))
				return true;
		}
		return false;
	}

	public Optional<Boolean> getValue(String m) {
		for (StateVariableValue v : getVariableValues()) { 
			if (v.getStateName().equals(m))
				return v.getValue();
		}
		return Optional.empty();
	}
	public void add(StateVariableValue stateVariableValue) {
		boolean found = false;
		for (StateVariableValue v : variableValues) {
			if (v.getStateName().equals(stateVariableValue.getStateName()) && v.getValue().equals(stateVariableValue.getValue()))
				found = true;
		}
		if (!found)
			variableValues.add(stateVariableValue);
	}

	@Override
	public int compareTo(Object o) {
		int result = 0;
		if (o instanceof StateValue) {
			StateValue compareTo = (StateValue)o;
			Iterator<StateVariableValue> iterator = variableValues.iterator();
			while (iterator.hasNext() && result == 0) {
				StateVariableValue item = iterator.next();
				if (!compareTo.getVariableValues().contains(item))
					result = 1;
			}
			iterator = compareTo.getVariableValues().iterator();
			while (iterator.hasNext() && result == 0) {
				if (!variableValues.contains(iterator.next()))
					result = -1;
			}
			return result;
		}
		return -1;
	}
}