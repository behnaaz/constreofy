package priority.states;

import java.util.Optional;

import priority.common.Constants;

public class StateVariableValue implements Comparable<Object>, Constants {
	private String stateName;
	private Optional<Boolean> value;
	public StateVariableValue(String name, Optional<Boolean> value) {
		this.getStateName(name);
		this.setValue(value);
	}
	public String getStateName() {
		return stateName;
	}
	public void getStateName(String stateName) {
		this.stateName = stateName.trim().toLowerCase();
	}
	public Optional<Boolean> getValue() {
		return value;
	}
	
	public void setValue(Optional<Boolean> value) {
		this.value = value;
	}
	public String makeNextStateCurrent() {
		return getStateName().toLowerCase().replace(NEXT_MEMORY, CURRENT_MEMORY);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StateVariableValue) {
			StateVariableValue toCompare = (StateVariableValue)obj;
			return stateName.trim().equals(toCompare.getStateName().trim()) && value.equals(toCompare.getValue());
		}

		return false;	
	}

	@Override
	public int hashCode() {
		return 17 * stateName.hashCode() + 13 * value.hashCode();
	}
	@Override
	public int compareTo(Object o) {
		if (this.equals(o))
			return 0;
		return 1;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (value.isPresent()) {
			sb.append(stateName);
			sb.append(value.get());
		}
		return sb.toString();
	}
}