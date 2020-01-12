package org.behnaz.rcsp;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Builder
@EqualsAndHashCode
@ToString
@Value
public class StateValue implements Cloneable {
	@Builder.Default
	private Set<StateVariableValue> variableValues = new HashSet<>();

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
}