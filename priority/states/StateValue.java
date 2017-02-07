package priority.states;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import priority.common.Constants;

public class StateValue implements Comparable<Object>, Cloneable, Constants {
	private Set<StateVariableValue> variableValues = new TreeSet<>();

	public Set<StateVariableValue> getVariableValues() {
		return variableValues;
	}
/*
	public StateValue makeItCurrent() {
		variableValues.forEach(s -> s.stateName(s.makeNextStateCurrent()));
		return this;
	}*/

	public boolean containsKey(String m) {
		for (StateVariableValue v : variableValues) { 
			if (v.getStateName().equals(m))
				return true;
		}
		return false;
	}

	public boolean getValue(String m) {
		for (StateVariableValue v : getVariableValues()) { 
			if (v.getStateName().equals(m))
				return v.getValue();
		}
		return false;
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
	public String toString() {
		StringBuilder sb = new StringBuilder();
		variableValues.forEach(e -> sb.append(e.getStateName()).append(e.getValue()).append(STRING_COMMA));
		return sb.toString().replaceAll(",$", "");
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
	
	@Override
	public boolean equals(Object o) {
		return this.compareTo(o) == 0;
	}

	@Override
	public int hashCode() {
		return variableValues.hashCode();//??
	}

	@Override
	public Object clone() throws CloneNotSupportedException {//TODO
		return super.clone();
	}
}