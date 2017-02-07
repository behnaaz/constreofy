package priority.states;

public class StateVariableValue implements Comparable<Object> {
	private String stateName;
	private Boolean value;
	public StateVariableValue(String name, Boolean value) {
		this.stateName(name);
		this.value(value);
	}
	String getStateName() {
		return stateName;
	}
	void stateName(String stateName) {
		this.stateName = stateName;
	}
	Boolean getValue() {
		return value;
	}
	void value(Boolean value) {
		this.value = value;
	}
	public String makeNextStateCurrent() {
		return getStateName().toLowerCase().replace("xring", "ring");//TODO constant;
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
		sb.append(stateName);
		sb.append(value);
		return sb.toString();
	}
}