package org.behnaz.rcsp;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;

import java.util.Optional;

import static priority.Variable.CURRENT_MEMORY;
import static priority.Variable.NEXT_MEMORY;

@Builder
@EqualsAndHashCode
@ToString
@Value
public class StateVariableValue {
	@Getter
	private String stateName;
	@Getter
	private Optional<Boolean> value;

	public String makeNextStateCurrent() {
		return getStateName().toLowerCase().replace(NEXT_MEMORY, CURRENT_MEMORY);
	}
}